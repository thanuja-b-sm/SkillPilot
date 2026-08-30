package com.skillpilot.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from-email:${spring.mail.username:thanujasm61@gmail.com}}")
    private String fromEmail;

    @Value("${spring.mail.from-name:SkillPilot}")
    private String fromName;

    @Value("${spring.mail.host:smtp-relay.brevo.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    // Diagnostics state
    private volatile LocalDateTime lastSuccessfulEmail = null;
    private volatile String lastFailure = null;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendPasswordResetEmail(String recipientEmail, String resetCode) {
        return sendPasswordResetEmail(recipientEmail, resetCode, null, null);
    }

    public boolean sendPasswordResetEmail(String recipientEmail, String resetCode, String resetRequestId, LocalDateTime expiresAt) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            logger.warn("Password reset email dispatch aborted: recipient email is null or empty");
            lastFailure = "Recipient email is null or empty";
            return false;
        }

        if (mailSender == null) {
            logger.warn("JavaMailSender is not configured. Skipping email dispatch for recipient: {}", recipientEmail);
            lastFailure = "JavaMailSender not configured";
            return false;
        }

        try {
            logger.info("Initiating password reset verification email dispatch via Brevo SMTP to recipient: {} [ResetRequestID: {}, ExpiresAt: {}]", 
                    recipientEmail, resetRequestId != null ? resetRequestId : "N/A", 
                    expiresAt != null ? expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "N/A");
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(new InternetAddress(fromEmail, fromName, "UTF-8"));
            helper.setReplyTo(new InternetAddress(fromEmail, fromName, "UTF-8"));
            helper.setTo(recipientEmail);
            helper.setSubject("SkillPilot — Password Reset Verification Code");

            String htmlBody = buildPasswordResetHtml(resetCode);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            String messageId = message.getMessageID();
            this.lastSuccessfulEmail = LocalDateTime.now();
            this.lastFailure = null;
            logger.info("Password reset verification HTML email successfully delivered via Brevo SMTP to recipient: {} (MessageID: {})", 
                    recipientEmail, messageId != null ? messageId : "generated");
            return true;
        } catch (Exception e) {
            this.lastFailure = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            logger.error("Failed to send password reset verification email via Brevo SMTP to recipient {}: {}", recipientEmail, e.getMessage(), e);
            return false;
        }
    }

    public Map<String, Object> getMailHealthDiagnostics() {
        Map<String, Object> diagnostics = new HashMap<>();
        diagnostics.put("provider", "Brevo SMTP");
        diagnostics.put("host", mailHost);
        diagnostics.put("port", mailPort);
        diagnostics.put("sender", String.format("%s <%s>", fromName, fromEmail));
        diagnostics.put("configured", mailSender != null);
        diagnostics.put("authentication", true);
        diagnostics.put("lastSuccessfulEmail", lastSuccessfulEmail != null ? lastSuccessfulEmail.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        diagnostics.put("lastFailure", lastFailure);
        return diagnostics;
    }


    private String buildPasswordResetHtml(String resetCode) {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='utf-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<meta http-equiv='X-UA-Compatible' content='IE=edge'>" +
                "<title>SkillPilot — Password Verification Code</title>" +
                "<style type='text/css'>" +
                "  body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }" +
                "  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #0f172a; color: #0f172a; margin: 0; padding: 32px 12px; }" +
                "  .wrapper { width: 100%; max-width: 560px; margin: 0 auto; background-color: #ffffff; border-radius: 20px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2), 0 8px 10px -6px rgba(0, 0, 0, 0.1); }" +
                "  .header { background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); padding: 36px 32px; text-align: center; border-bottom: 1px solid #334155; }" +
                "  .logo-badge { display: inline-block; background: rgba(59, 130, 246, 0.15); border: 1px solid rgba(96, 165, 250, 0.3); border-radius: 12px; padding: 8px 16px; margin-bottom: 12px; }" +
                "  .brand { font-size: 24px; font-weight: 800; color: #ffffff; letter-spacing: -0.5px; margin: 0; }" +
                "  .brand-blue { color: #60a5fa; }" +
                "  .tagline { font-size: 13px; color: #94a3b8; margin: 6px 0 0 0; font-weight: 500; }" +
                "  .content { padding: 36px 32px; text-align: left; background-color: #ffffff; }" +
                "  .title { font-size: 20px; font-weight: 800; color: #0f172a; margin: 0 0 12px 0; letter-spacing: -0.3px; }" +
                "  .text { font-size: 15px; color: #475569; line-height: 1.6; margin: 0 0 24px 0; }" +
                "  .code-container { background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%); border: 2px dashed #93c5fd; border-radius: 16px; padding: 24px 20px; text-align: center; margin: 0 0 28px 0; }" +
                "  .code-label { font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 1.5px; color: #3b82f6; margin: 0 0 10px 0; }" +
                "  .code { font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace; font-size: 38px; font-weight: 900; letter-spacing: 10px; color: #1d4ed8; margin: 0; text-shadow: 0 1px 2px rgba(29, 78, 216, 0.15); }" +
                "  .expiry-badge { display: inline-block; background-color: #dbeafe; color: #1e40af; font-size: 13px; font-weight: 700; padding: 6px 14px; border-radius: 9999px; margin-top: 14px; }" +
                "  .notice-box { background-color: #f8fafc; border-left: 4px solid #3b82f6; border-radius: 4px 10px 10px 4px; padding: 16px 18px; margin: 0 0 24px 0; }" +
                "  .notice-title { font-size: 13px; font-weight: 700; color: #1e293b; margin: 0 0 4px 0; }" +
                "  .notice-text { font-size: 13px; color: #64748b; line-height: 1.5; margin: 0; }" +
                "  .footer { background-color: #f8fafc; padding: 24px 32px; text-align: center; border-top: 1px solid #e2e8f0; font-size: 12px; color: #64748b; line-height: 1.6; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "  <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                "    <tr>" +
                "      <td align='center'>" +
                "        <div class='wrapper'>" +
                "          <div class='header'>" +
                "            <div class='logo-badge'>" +
                "              <div class='brand'>Skill<span class='brand-blue'>Pilot</span></div>" +
                "            </div>" +
                "            <p class='tagline'>AI-Powered Career Intelligence & Learning Roadmap Platform</p>" +
                "          </div>" +
                "          <div class='content'>" +
                "            <h1 class='title'>Password Reset Request</h1>" +
                "            <p class='text'>We received a request to reset your password for your SkillPilot account. Enter the 6-digit verification code below to authorize this request and set a new password:</p>" +
                "            <div class='code-container'>" +
                "              <div class='code-label'>Your Verification Code</div>" +
                "              <div class='code'>" + resetCode + "</div>" +
                "              <div class='expiry-badge'>⏱ Expires in 15 minutes</div>" +
                "            </div>" +
                "            <div class='notice-box'>" +
                "              <p class='notice-title'>Security Notice</p>" +
                "              <p class='notice-text'>If you did not request a password reset, you can safely ignore this email. Your current password remains secure and will not change until this code is verified.</p>" +
                "            </div>" +
                "          </div>" +
                "          <div class='footer'>" +
                "            <p style='margin:0 0 6px 0; font-weight:600; color:#334155;'>SkillPilot Security Operations</p>" +
                "            <p style='margin:0 0 6px 0;'>Autonomous Career Intelligence Platform • MySQL & Spring Boot Engine</p>" +
                "            <p style='margin:0; font-size:11px; color:#94a3b8;'>This is an automated system message. Please do not reply directly to this email.</p>" +
                "          </div>" +
                "        </div>" +
                "      </td>" +
                "    </tr>" +
                "  </table>" +
                "</body>" +
                "</html>";
    }
}

