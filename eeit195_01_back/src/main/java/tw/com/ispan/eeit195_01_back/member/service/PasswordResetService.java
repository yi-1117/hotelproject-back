package tw.com.ispan.eeit195_01_back.member.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.PasswordResetBean;
import tw.com.ispan.eeit195_01_back.member.exception.MemberNotFoundException;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.member.repository.PasswordResetRepository;

@Service
public class PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    public PasswordResetService(PasswordResetRepository passwordResetRepository,
            MemberRepository memberRepository,
            EmailService emailService) {
        this.passwordResetRepository = passwordResetRepository;
        this.memberRepository = memberRepository;
        this.emailService = emailService;
    }

    // 發送密碼重設請求
    public PasswordResetBean requestPasswordReset(Integer memberId) {
        // 驗證會員是否存在
        MemberBean member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + memberId));

        // 標記舊 Token 為已使用
        passwordResetRepository.findByMemberBeanAndIsUsedFalse(member)
                .ifPresent(request -> {
                    request.setIsUsed(true);
                    passwordResetRepository.save(request);
                });

        // 生成新密碼重設 Token
        String resetToken = UUID.randomUUID().toString();

        // 設定過期時間為1小時後
        LocalDateTime expireTime = LocalDateTime.now().plusHours(1);

        // 建立密碼重設請求
        PasswordResetBean resetRequest = new PasswordResetBean();
        resetRequest.setMemberBean(member); // 確保 memberBean 已正確設定
        resetRequest.setResetToken(resetToken);
        resetRequest.setRequestTime(LocalDateTime.now());
        resetRequest.setExpireTime(expireTime); // 設定過期時間
        resetRequest.setIsUsed(false);

        // 儲存請求
        passwordResetRepository.save(resetRequest);

        // 發送重設密碼郵件
        String resetUrl = "http://localhost:8080/api/members/password/reset?token=" + resetToken;
        emailService.sendPasswordResetEmail(member.getEmail(), resetUrl);

        return resetRequest;
    }

    public boolean validateToken(String resetToken) {
        return passwordResetRepository.findByResetToken(resetToken)
                .map(resetRequest -> !resetRequest.getIsUsed()
                        && resetRequest.getExpireTime().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    // 根據 Token 重設密碼
    public void resetPassword(String resetToken, String newPassword) {
        PasswordResetBean resetRequest = passwordResetRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        // 確認 Token 未過期
        if (resetRequest.getExpireTime().isBefore(LocalDateTime.now())) {
            // 刪除過期請求
            passwordResetRepository.delete(resetRequest);
            throw new RuntimeException("This reset token has expired and has been deleted.");
        }

        // 確認 Token 未被使用
        if (resetRequest.getIsUsed()) {
            passwordResetRepository.delete(resetRequest); // 刪除已使用請求
            throw new RuntimeException("This reset token has already been used.");
        }

        // 更新會員密碼
        MemberBean member = resetRequest.getMemberBean();
        member.setPassword(newPassword);

        // 標記 Token 為已使用
        resetRequest.setIsUsed(true);

        // 儲存變更
        passwordResetRepository.save(resetRequest);
        memberRepository.save(member);

        // 刪除舊的 token
        passwordResetRepository.deleteByMemberBeanAndIsUsedFalse(member); // 刪除所有未被使用的舊 token
    }

}
