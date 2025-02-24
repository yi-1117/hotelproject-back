package tw.com.ispan.eeit195_01_back.employee.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import tw.com.ispan.eeit195_01_back.employee.bean.EPasswordResetBean;
import tw.com.ispan.eeit195_01_back.employee.bean.EmployeeBean;
import tw.com.ispan.eeit195_01_back.employee.repository.EPasswordResetRepository;
import tw.com.ispan.eeit195_01_back.employee.repository.EmployeeRepository;
import tw.com.ispan.eeit195_01_back.member.exception.MemberNotFoundException;

@Service
public class EmployeePasswordResetService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 6;
    private static final SecureRandom secureRandom = new SecureRandom();

    private final EPasswordResetRepository epasswordResetRepository;
    private final EmployeeRepository employeeRepository;
    private final EEmailService emailService;

    // constructor
    public EmployeePasswordResetService(EPasswordResetRepository epasswordResetRepository,
            EmployeeRepository employeeRepository,
            EEmailService emailService) {
        this.epasswordResetRepository = epasswordResetRepository;
        this.employeeRepository = employeeRepository;
        this.emailService = emailService; // 會員裡面的emailService
    }

    // 發送密碼重設請求
    public EPasswordResetBean requestPasswordReset(Integer employeeId) {
        // 驗證會員是否存在
        EmployeeBean employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new MemberNotFoundException("Employee not found with ID: " + employeeId));

        // 標記舊 Token 為已使用
        epasswordResetRepository.findByEmployeeAndIsUsedFalse(employee)
                .ifPresent(request -> {
                    request.setIsUsed(true);
                    epasswordResetRepository.save(request);
                });

        // 生成新密碼重設 Token
        String resetToken = generateAlphaNumericToken();

        // 設定過期時間為1小時後
        LocalDateTime expireTime = LocalDateTime.now().plusHours(1);

        // 建立密碼重設請求
        EPasswordResetBean resetRequest = new EPasswordResetBean();
        resetRequest.setEmployeeBean(employee); // 確保 memberBean 已正確設定
        resetRequest.setResetToken(resetToken);
        resetRequest.setRequestTime(LocalDateTime.now());
        resetRequest.setExpireTime(expireTime); // 設定過期時間
        resetRequest.setIsUsed(false);

        // 儲存請求
        epasswordResetRepository.save(resetRequest);

        // 發送重設密碼郵件
        String resetUrl = "http://192.168.23.148:8080/employee/password/reset?token=" + resetToken;
        emailService.sendPasswordResetEmail(employee.getEmail(), resetUrl, resetToken);

        return resetRequest;
    }

    public static String generateAlphaNumericToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(randomIndex));
        }
        return token.toString();
    }

    public boolean validateToken(String resetToken) {
        return epasswordResetRepository.findByResetToken(resetToken)
                .map(resetRequest -> !resetRequest.getIsUsed()
                        && resetRequest.getExpireTime().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    // 根據 Token 重設密碼
    public void resetPassword(String resetToken, String newPassword) {
        EPasswordResetBean resetRequest = epasswordResetRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        // 確認 Token 未過期
        if (resetRequest.getExpireTime().isBefore(LocalDateTime.now())) {
            // 刪除過期請求
            epasswordResetRepository.delete(resetRequest);
            throw new RuntimeException("This reset token has expired and has been deleted.");
        }

        // 確認 Token 未被使用
        if (resetRequest.getIsUsed()) {
            throw new RuntimeException("This reset token has already been used.");
        }

        // 更新員工密碼
        EmployeeBean employee = resetRequest.getEmployeeBean();
        employee.setPassword(newPassword);

        // 標記 Token 為已使用
        resetRequest.setIsUsed(true);

        // 儲存變更
        epasswordResetRepository.save(resetRequest);
        employeeRepository.save(employee);
    }
}
