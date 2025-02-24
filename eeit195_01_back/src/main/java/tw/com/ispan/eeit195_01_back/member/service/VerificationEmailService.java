package tw.com.ispan.eeit195_01_back.member.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.VerificationEmailBean;
import tw.com.ispan.eeit195_01_back.member.exception.MemberNotFoundException;
import tw.com.ispan.eeit195_01_back.member.exception.VerificationCodeInvalidException;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.member.repository.VerificationEmailRepository;

@Service
public class VerificationEmailService {

    @Autowired
    private VerificationEmailRepository verificationEmailRepository;

    @Autowired
    private EmailService emailService; // 注入發送郵件的服務

    @Autowired
    private MemberRepository memberRepository;

    // 驗證碼驗證
    public boolean verifyCode(String email, String inputCode) {
        // 先清理過期的驗證碼
        cleanExpiredVerificationCodes();

        // 查找會員
        MemberBean member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("Member with email " + email + " not found"));

        // 查找該會員的有效驗證碼
        List<VerificationEmailBean> verifications = verificationEmailRepository.findByMember(member);

        for (VerificationEmailBean verification : verifications) {
            if (verification.getVerificationCode().equals(inputCode) &&
                    verification.getExpirationDate().isAfter(LocalDateTime.now())) {
                // 驗證成功
                member.setIsVerified(true);
                memberRepository.save(member);

                // 刪除已驗證的驗證碼
                verificationEmailRepository.delete(verification);
                return true;
            }
        }

        throw new VerificationCodeInvalidException("Verification code is invalid or expired");
    }

    // 發送驗證碼郵件
    public void sendVerificationEmail(String email) {
        // 查找會員
        MemberBean member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("Member with email " + email + " not found"));

        // 檢查會員是否已經驗證過
        if (member.getIsVerified()) {
            throw new RuntimeException("This email is already verified.");
        }

        // 檢查會員最近是否已經發送過驗證郵件
        // LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        // List<VerificationEmailBean> recentVerifications = verificationEmailRepository
        // .findByMemberAndSentTimeAfter(member, oneHourAgo);
        // if (!recentVerifications.isEmpty()) {
        // throw new RuntimeException("You can only request a verification code once
        // every hour.");
        // }

        // 檢查是否有未過期的驗證碼
        // List<VerificationEmailBean> existingVerifications =
        // verificationEmailRepository.findByMember(member);
        // for (VerificationEmailBean verification : existingVerifications) {
        // if (verification.getExpirationDate().isAfter(LocalDateTime.now())) {
        // throw new RuntimeException("A verification code is already active for this
        // email.");
        // }
        // }

        // 生成驗證碼並保存
        String verificationCode = generateVerificationCode();
        VerificationEmailBean verificationEmail = new VerificationEmailBean();
        verificationEmail.setVerificationCode(verificationCode);
        verificationEmail.setSentTime(LocalDateTime.now());
        verificationEmail.setExpirationDate(LocalDateTime.now().plusHours(24));
        verificationEmail.setMember(member);
        verificationEmailRepository.save(verificationEmail);

        // 發送郵件
        sendEmail(email, verificationCode);
    }

    // 清理過期的驗證碼
    @Scheduled(cron = "0 0 * * * *")  // 每小時清理一次過期的驗證碼
    public void cleanExpiredVerificationCodes() {
        List<VerificationEmailBean> expiredVerifications = verificationEmailRepository.findAll()
                .stream()
                .filter(verification -> verification.getExpirationDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        
        if (!expiredVerifications.isEmpty()) {
            verificationEmailRepository.deleteAll(expiredVerifications);
        }
    }

    public boolean hasUnverifiedEmail(MemberBean member) {
        return verificationEmailRepository.existsByMember(member);
    }

    // 隨機生成驗證碼
    private String generateVerificationCode() {
        Random random = new Random(); 
        int code = random.nextInt(999999); 
        return String.format("%06d", code); // 6 位數字，前補零 
    }

    // 發送郵件
    private void sendEmail(String email, String verificationCode) {
        String subject = "會員驗證碼";
        String text = "Dear User,\n\nYour verification code is: " + verificationCode +
                "\nPlease enter this code in the application to verify your email.\n" +
                "This code will expire in 24 hours.\n\nBest regards,\nYour Application Team";

        emailService.sendSimpleMessage(email, subject, text);
    }
}