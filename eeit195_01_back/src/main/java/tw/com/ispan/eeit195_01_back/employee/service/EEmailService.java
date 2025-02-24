package tw.com.ispan.eeit195_01_back.employee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // 發送簡單郵件
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    // 修改密碼發送郵件
    public void sendPasswordResetEmail(String recipientEmail, String resetUrl,String token) {
        // 建立郵件內容
        String subject = "新生活飯店--員工密碼重設請求";
        String body = "您的驗證碼: " + token + "\n\n" + "點擊以下連結重設密碼:\n" + resetUrl;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("no-reply@example.com"); // 這裡填寫發件人郵件地址

        // 發送郵件
        javaMailSender.send(message);
    }
}
