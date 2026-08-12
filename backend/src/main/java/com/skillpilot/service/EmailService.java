package com.skillpilot.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
            logger.warn("JavaMailSender is not configured. Skipping email dispatch for recipient {}", recipientEmail);
            return false;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("SkillPilot — Password Reset Verification Code");

            String htmlBody = buildPasswordResetHtml(resetCode);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            logger.info("Password reset verification HTML email successfully sent to recipient");
            return true;
        } catch (Exception e) {
            logger.error("Failed to send password reset verification email: {}", e.getMessage());
            return false;
        }
    }

    private String buildPasswordResetHtml(String resetCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='utf-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>SkillPilot Password Reset</title>" +
                "<style>" +
                "  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #0f172a; margin: 0; padding: 24px; }" +
                "  .container { max-width: 520px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; border: 1px solid #e2e8f0; overflow: hidden; shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1); }" +
                "  .header { background-color: #0f172a; padding: 28px; text-align: center; color: #ffffff; }" +
                "  .brand { font-size: 22px; font-weight: 800; tracking-tight: -0.025em; letter-spacing: -0.5px; }" +
                "  .brand-blue { color: #2563eb; }" +
                "  .content { padding: 32px 28px; text-align: left; }" +
                "  .title { font-size: 18px; font-weight: 700; color: #0f172a; margin-top: 0; margin-bottom: 12px; }" +
                "  .text { font-size: 14px; color: #475569; line-height: 1.6; margin-bottom: 24px; }" +
                "  .code-box { background-color: #eff6ff; border: 1px solid #bfdbfe; border-radius: 12px; padding: 20px; text-align: center; margin-bottom: 24px; }" +
                "  .code { font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; font-size: 32px; font-weight: 800; letter-spacing: 6px; color: #1d4ed8; margin: 0; }" +
                "  .expiry { font-size: 12px; font-weight: 600; color: #64748b; margin-top: 8px; margin-bottom: 0; }" +
                "  .warning { background-color: #fefce8; border: 1px solid #fef08a; border-radius: 10px; padding: 14px; font-size: 12px; color: #713f12; line-height: 1.5; margin-bottom: 24px; }" +
                "  .footer { background-color: #f1f5f9; padding: 20px 28px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <div class='brand'>Skill<span class='brand-blue'>Pilot</span></div>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <h1 class='title'>Password Reset Request</h1>" +
                "      <p class='text'>We received a request to reset the password for your SkillPilot account. Use the 6-digit verification code below to complete your password reset:</p>" +
                "      <div class='code-box'>" +
                "        <div class='code'>" + resetCode + "</div>" +
                "        <p class='expiry'>⏱ Expires in 15 minutes</p>" +
                "      </div>" +
                "      <div class='warning'>" +
                "        <strong>Security Notice:</strong> If you did not request a password reset, please ignore this email or contact support if you suspect unauthorized access." +
                "      </div>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p style='margin:0 0 6px 0;'>SkillPilot — Autonomous Career Intelligence Platform</p>" +
                "      <p style='margin:0;'>Need help? Contact support@skillpilot.io</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
