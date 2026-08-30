package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.ForgotPasswordRequest;
import com.skillpilot.dto.request.ResetPasswordRequest;
import com.skillpilot.entity.PasswordResetCode;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.PasswordResetCodeRepository;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class Phase18BrevoForgotPasswordIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetCodeRepository passwordResetCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SpyBean
    private EmailService emailService;

    private User registeredUser;
    private final String targetEmail = "thanujasm61@gmail.com";

    @BeforeEach
    void setUp() {
        passwordResetCodeRepository.deleteAll();

        registeredUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Thanuja SM")
                .email(targetEmail)
                .passwordHash(passwordEncoder.encode("InitialSecretPass2026"))
                .role(UserRole.STUDENT)
                .title("Software Engineer Candidate")
                .education("B.E. Computer Science")
                .experienceYears(2)
                .location("Bangalore, India")
                .targetFocus("Full Stack Development")
                .bio("Candidate for Brevo SMTP verification")
                .completionPercentage(80)
                .build();
        userRepository.save(registeredUser);
    }

    @Test
    @DisplayName("1. Forgot password inserts DB record with user_id, 15m expiration, and invokes EmailService")
    void test1_ForgotPasswordInsertsDbAndInvokesEmailService() throws Exception {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email(targetEmail)
                .build();

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("verification code has been sent")));

        List<PasswordResetCode> codes = passwordResetCodeRepository.findByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(targetEmail);
        assertEquals(1, codes.size());
        PasswordResetCode saved = codes.get(0);

        assertEquals(registeredUser.getId(), saved.getUserId());
        assertEquals(targetEmail, saved.getEmail());
        assertEquals(6, saved.getResetCode().length());
        assertEquals(0, saved.getAttemptsCount());
        assertFalse(saved.getIsUsed());
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now()));

        verify(emailService).sendPasswordResetEmail(eq(targetEmail), eq(saved.getResetCode()), eq(saved.getId()), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("2. Requesting new reset code (Resend) invalidates prior unused codes")
    void test2_ResendInvalidatesPriorCodes() throws Exception {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email(targetEmail)
                .build();

        // 1st request
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 2nd request (resend)
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<PasswordResetCode> activeCodes = passwordResetCodeRepository.findByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(targetEmail);
        assertEquals(1, activeCodes.size(), "Only the newest code should remain active/unused");
    }

    @Test
    @DisplayName("3. Failed reset attempts increment counter and lock out at 5 attempts")
    void test3_AttemptCountAndLockout() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(targetEmail))))
                .andExpect(status().isOk());

        ResetPasswordRequest wrongReq = ResetPasswordRequest.builder()
                .email(targetEmail)
                .resetCode("000000")
                .newPassword("UpdatedValidPassword123")
                .build();

        for (int i = 1; i <= 4; i++) {
            mockMvc.perform(post("/api/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongReq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Invalid verification reset code")));
        }

        // 5th attempt burns code
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Maximum verification attempts exceeded")));

        List<PasswordResetCode> activeCodes = passwordResetCodeRepository.findByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(targetEmail);
        assertTrue(activeCodes.isEmpty());
    }

    @Test
    @DisplayName("4. Successful reset updates BCrypt hash and marks code is_used=true")
    void test4_SuccessfulReset() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(targetEmail))))
                .andExpect(status().isOk());

        PasswordResetCode activeCode = passwordResetCodeRepository
                .findFirstByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(targetEmail)
                .orElseThrow();

        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .email(targetEmail)
                .resetCode(activeCode.getResetCode())
                .newPassword("BrandNewSecurePassword2026")
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Password updated successfully")));

        User updatedUser = userRepository.findByEmailIgnoreCase(targetEmail).orElseThrow();
        assertTrue(passwordEncoder.matches("BrandNewSecurePassword2026", updatedUser.getPasswordHash()));

        PasswordResetCode refreshedCode = passwordResetCodeRepository.findById(activeCode.getId()).orElseThrow();
        assertTrue(refreshedCode.getIsUsed());
    }

    @Test
    @DisplayName("5. Mail Health Diagnostics returns Brevo SMTP configuration metadata")
    void test5_MailHealthDiagnostics() {
        var diagnostics = emailService.getMailHealthDiagnostics();
        assertNotNull(diagnostics);
        assertEquals("Brevo SMTP", diagnostics.get("provider"));
        assertEquals("smtp-relay.brevo.com", diagnostics.get("host"));
        assertEquals(587, diagnostics.get("port"));
        assertTrue((Boolean) diagnostics.get("authentication"));
    }
}
