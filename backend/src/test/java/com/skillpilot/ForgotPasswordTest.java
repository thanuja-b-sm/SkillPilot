package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.ForgotPasswordRequest;
import com.skillpilot.dto.request.LoginRequest;
import com.skillpilot.dto.request.ResetPasswordRequest;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ForgotPasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_EMAIL = "resetuser@skillpilot.io";
    private static final String ORIGINAL_PASSWORD = "OriginalPass123";
    private static final String NEW_PASSWORD = "UpdatedSecurePass123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Reset Test User")
                .email(TEST_EMAIL)
                .passwordHash(passwordEncoder.encode(ORIGINAL_PASSWORD))
                .role(UserRole.STUDENT)
                .build();

        userRepository.save(user);
    }

    @Test
    @DisplayName("Should successfully request reset code, update password, and login with new password")
    void testCompleteForgotPasswordWorkflow() throws Exception {
        // Step 1: Request Forgot Password
        ForgotPasswordRequest forgotReq = ForgotPasswordRequest.builder()
                .email(TEST_EMAIL)
                .build();

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("If an account with that email address is registered")))
                .andExpect(jsonPath("$.resetCode", nullValue()));

        String resetCode = AuthService.getResetCodeForTesting(TEST_EMAIL);
        assertNotNull(resetCode);
        assertEquals(6, resetCode.length());

        // Step 2: Attempt reset with WRONG reset code (should fail)
        ResetPasswordRequest wrongCodeReq = ResetPasswordRequest.builder()
                .email(TEST_EMAIL)
                .resetCode("999999")
                .newPassword(NEW_PASSWORD)
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongCodeReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid verification")));

        // Step 3: Reset password with CORRECT reset code
        ResetPasswordRequest validResetReq = ResetPasswordRequest.builder()
                .email(TEST_EMAIL)
                .resetCode(resetCode)
                .newPassword(NEW_PASSWORD)
                .build();

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validResetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Password updated successfully")));

        // Step 4: Login with OLD password (should fail)
        LoginRequest oldLoginReq = LoginRequest.builder()
                .email(TEST_EMAIL)
                .password(ORIGINAL_PASSWORD)
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oldLoginReq)))
                .andExpect(status().isUnauthorized());

        // Step 5: Login with NEW password (should succeed)
        LoginRequest newLoginReq = LoginRequest.builder()
                .email(TEST_EMAIL)
                .password(NEW_PASSWORD)
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newLoginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.userProfile.email", is(TEST_EMAIL)));
    }

    @Test
    @DisplayName("Should return generic success message without revealing email non-existence")
    void testNonExistentEmailReturnsGenericSuccessMessageWithoutLeaking() throws Exception {
        ForgotPasswordRequest forgotReq = ForgotPasswordRequest.builder()
                .email("nonexistent.user@unknown-domain.org")
                .build();

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("If an account with that email address is registered")))
                .andExpect(jsonPath("$.resetCode", nullValue()));
    }
}
