package tw.com.ispan.eeit195_01_back.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

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

    // 忘記密碼發送郵件
    public void sendPasswordResetEmail(String recipientEmail, String resetUrl) {
        // 建立郵件內容
        String subject = "Password Reset Request";
        String body = "Please click the following link to reset your password:\n" + resetUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("no-reply@example.com"); // 這裡填寫發件人郵件地址

        // 發送郵件
        javaMailSender.send(message);
    }
}


