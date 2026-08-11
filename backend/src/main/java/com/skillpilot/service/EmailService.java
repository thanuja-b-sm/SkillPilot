package com.skillpilot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:thanujasm61@gmail.com}")
    private String fromEmail;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendPasswordResetEmail(String recipientEmail, String resetCode) {
        if (mailSender == null) {
            logger.warn("JavaMailSender is not configured. Verification reset code for {} is {}", recipientEmail, resetCode);
            return false;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("SkillPilot — Password Reset Verification Code");
            message.setText(
                    "Hello,\n\n" +
                    "Your 6-digit verification code to reset your SkillPilot account password is:\n\n" +
                    "   " + resetCode + "\n\n" +
                    "This code will expire in 15 minutes.\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "SkillPilot Career Intelligence Team"
            );

            mailSender.send(message);
            logger.info("Password reset verification email successfully sent via Gmail SMTP to {}", recipientEmail);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send password reset verification email to {}: {}", recipientEmail, e.getMessage());
            return false;
        }
    }
}
