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
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class Phase18ForgotPasswordFlowTest {

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

    @MockBean
    private EmailService emailService;

    private User registeredUser;
    private final String userEmail = "student.reset.test@skillpilot.io";

    @BeforeEach
    void setUp() {
        passwordResetCodeRepository.deleteAll();

        registeredUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Reset Candidate Test")
                .email(userEmail)
                .passwordHash(passwordEncoder.encode("OldPassword123"))
                .role(UserRole.STUDENT)
                .title("Student Profile")
                .education("B.S. Software Engineering")
                .experienceYears(1)
                .location("Boston, MA")
                .targetFocus("Cloud Engineering")
                .bio("Test user for password reset")
                .completionPercentage(75)
                .build();
        userRepository.save(registeredUser);
    }

    @Test
    @DisplayName("1. Forgot Password Request stores 6-digit code in MySQL and invokes EmailService")
    void test1_ForgotPasswordSuccessAndPersistence() throws Exception {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email(userEmail)
                .build();

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("verification code has been sent")))
                .andExpect(jsonPath("$.resetCode").doesNotExist());

        // Verify MySQL persistence
        List<PasswordResetCode> codes = passwordResetCodeRepository.findByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(userEmail);
        assertEquals(1, codes.size());
        PasswordResetCode savedCode = codes.get(0);

        assertNotNull(savedCode.getResetCode());
        assertEquals(6, savedCode.getResetCode().length());
        assertTrue(savedCode.getResetCode().matches("^\\d{6}$"));
        assertFalse(savedCode.getIsUsed());
        assertEquals(0, savedCode.getAttemptsCount());
        assertTrue(savedCode.getExpiresAt().isAfter(LocalDateTime.now()));

        // Verify EmailService was called with generated code and metadata
        verify(emailService).sendPasswordResetEmail(eq(userEmail), eq(savedCode.getResetCode()), eq(savedCode.getId()), org.mockito.ArgumentMatchers.any(LocalDateTime.class));
    }



    @Test
    @DisplayName("2. Unknown email returns generic success response without storing code or sending email (anti-enumeration)")
    void test2_UnknownEmailAntiEnumeration() throws Exception {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("unknown.unregistered@skillpilot.io")
                .build();

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("verification code has been sent")));

        List<PasswordResetCode> codes = passwordResetCodeRepository.findByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc("unknown.unregistered@skillpilot.io");
        assertTrue(codes.isEmpty());
    }

    @Test
    @DisplayName("3. Requesting a new code replaces and invalidates previous unused codes")
    void test3_NewCodeInvalidatesOldCodes() throws Exception {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email(userEmail)
                .build();

        // First request
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Second request
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Only 1 active unused code should exist
        List<PasswordResetCode> activeCodes = passwordResetCodeRepository.findByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(userEmail);
        assertEquals(1, activeCodes.size());
    }

    @Test
    @DisplayName("4. Reset Password with valid code updates password and marks code as used")
    void test4_ResetPasswordSuccess() throws Exception {
        // Request code
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(userEmail))))
                .andExpect(status().isOk());

        PasswordResetCode activeCode = passwordResetCodeRepository
                .findFirstByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(userEmail)
                .orElseThrow();

        // Submit reset
        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .email(userEmail)
                .resetCode(activeCode.getResetCode())
                .newPassword("BrandNewSecurePassword2026")
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Password updated successfully")));

        // Verify password hash updated in User repository
        User updatedUser = userRepository.findByEmailIgnoreCase(userEmail).orElseThrow();
        assertTrue(passwordEncoder.matches("BrandNewSecurePassword2026", updatedUser.getPasswordHash()));
        assertFalse(passwordEncoder.matches("OldPassword123", updatedUser.getPasswordHash()));

        // Verify code is marked as used
        PasswordResetCode refreshedCode = passwordResetCodeRepository.findById(activeCode.getId()).orElseThrow();
        assertTrue(refreshedCode.getIsUsed());
    }

    @Test
    @DisplayName("5. Reset Password with invalid code increments attempt counter and rejects (400)")
    void test5_InvalidCodeRejection() throws Exception {
        // Generate valid code
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(userEmail))))
                .andExpect(status().isOk());

        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .email(userEmail)
                .resetCode("000000") // wrong code
                .newPassword("BrandNewSecurePassword2026")
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid verification reset code")));

        PasswordResetCode activeCode = passwordResetCodeRepository
                .findFirstByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(userEmail)
                .orElseThrow();
        assertEquals(1, activeCode.getAttemptsCount());
    }

    @Test
    @DisplayName("6. Reset Password with expired code is rejected (400) and invalidated")
    void test6_ExpiredCodeRejection() throws Exception {
        // Save expired code
        PasswordResetCode expiredCode = PasswordResetCode.builder()
                .id(UUID.randomUUID().toString())
                .email(userEmail)
                .resetCode("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1)) // expired
                .attemptsCount(0)
                .isUsed(false)
                .build();
        passwordResetCodeRepository.save(expiredCode);

        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .email(userEmail)
                .resetCode("123456")
                .newPassword("BrandNewSecurePassword2026")
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("expired")));

        PasswordResetCode refreshedCode = passwordResetCodeRepository.findById(expiredCode.getId()).orElseThrow();
        assertTrue(refreshedCode.getIsUsed());
    }

    @Test
    @DisplayName("7. Max attempts limit (5 attempts) invalidates code and prevents brute force")
    void test7_MaxAttemptsLimit() throws Exception {
        // Generate valid code
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ForgotPasswordRequest(userEmail))))
                .andExpect(status().isOk());

        ResetPasswordRequest wrongReq = ResetPasswordRequest.builder()
                .email(userEmail)
                .resetCode("999999") // wrong code
                .newPassword("BrandNewSecurePassword2026")
                .build();

        // 5 wrong attempts
        for (int i = 1; i <= 4; i++) {
            mockMvc.perform(post("/api/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongReq)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("Invalid verification reset code")));
        }

        // 5th attempt hits max attempts threshold
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Maximum verification attempts exceeded")));

        // Verify active code list is empty (invalidated)
        List<PasswordResetCode> activeCodes = passwordResetCodeRepository.findByEmailIgnoreCaseAndIsUsedFalseOrderByCreatedAtDesc(userEmail);
        assertTrue(activeCodes.isEmpty());
    }
}
