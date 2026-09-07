package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.LoginRequest;
import com.skillpilot.dto.request.ProfileUpdateRequest;
import com.skillpilot.dto.request.RegisterRequest;
import com.skillpilot.dto.request.ResendVerificationRequest;
import com.skillpilot.dto.request.VerifyEmailRequest;
import com.skillpilot.entity.EmailVerification;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.EmailVerificationRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RegistrationVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailService emailService;

    private static final String TEST_EMAIL = "student.verify@skillpilot.edu";
    private static final String TEST_PASSWORD = "SecurePassword123";

    @BeforeEach
    void setUp() {
        emailVerificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("1. Registration creates unverified user and persists 6-digit verification state in DB")
    void testRegistrationCreatesVerificationState() throws Exception {
        RegisterRequest registerReq = RegisterRequest.builder()
                .name("Alex Student")
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .education("Senior CS Major")
                .targetFocus("Artificial Intelligence")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requiresVerification", is(true)))
                .andExpect(jsonPath("$.email", is(TEST_EMAIL)))
                .andExpect(jsonPath("$.token").doesNotExist());

        User user = userRepository.findByEmailIgnoreCase(TEST_EMAIL).orElseThrow();
        assertFalse(user.getIsVerified(), "Newly registered user must be unverified");

        EmailVerification verification = emailVerificationRepository
                .findFirstByEmailIgnoreCaseAndIsVerifiedFalseOrderByCreatedAtDesc(TEST_EMAIL)
                .orElseThrow();

        assertEquals(user.getId(), verification.getUserId());
        assertNotNull(verification.getVerificationCode());
        assertEquals(6, verification.getVerificationCode().length());
        assertFalse(verification.getIsVerified());
        assertEquals(0, verification.getAttemptsCount());
        assertTrue(verification.getExpiresAt().isAfter(LocalDateTime.now()));

        verify(emailService).sendEmailVerificationCode(eq(TEST_EMAIL), any(), any());
    }

    @Test
    @DisplayName("2. Login is blocked before email verification is completed")
    void testLoginBlockedBeforeVerification() throws Exception {
        User unverifiedUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Unverified Alex")
                .email(TEST_EMAIL)
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .role(UserRole.STUDENT)
                .isVerified(false)
                .build();
        userRepository.save(unverifiedUser);

        LoginRequest loginReq = LoginRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Account email is not verified")));
    }

    @Test
    @DisplayName("3. Verification succeeds with valid code, activates user, and issues JWT session")
    void testVerificationSucceedsWithValidCode() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Verifying Alex")
                .email(TEST_EMAIL)
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .role(UserRole.STUDENT)
                .isVerified(false)
                .build();
        userRepository.save(user);

        String code = "654321";
        EmailVerification ev = EmailVerification.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .email(TEST_EMAIL)
                .verificationCode(code)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .attemptsCount(0)
                .isVerified(false)
                .build();
        emailVerificationRepository.save(ev);

        VerifyEmailRequest verifyReq = VerifyEmailRequest.builder()
                .email(TEST_EMAIL)
                .verificationCode(code)
                .build();

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.userRole", is("student")))
                .andExpect(jsonPath("$.requiresVerification", is(false)));

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(updatedUser.getIsVerified(), "User must be marked verified");

        EmailVerification updatedEv = emailVerificationRepository.findById(ev.getId()).orElseThrow();
        assertTrue(updatedEv.getIsVerified(), "Verification code must be marked verified/used");

        // Now login should succeed
        LoginRequest loginReq = LoginRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("4. Invalid verification code is rejected and increments attempt count")
    void testInvalidVerificationCodeRejected() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Alex Invalid")
                .email(TEST_EMAIL)
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .role(UserRole.STUDENT)
                .isVerified(false)
                .build();
        userRepository.save(user);

        EmailVerification ev = EmailVerification.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .email(TEST_EMAIL)
                .verificationCode("112233")
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .attemptsCount(0)
                .isVerified(false)
                .build();
        emailVerificationRepository.save(ev);

        VerifyEmailRequest verifyReq = VerifyEmailRequest.builder()
                .email(TEST_EMAIL)
                .verificationCode("999999")
                .build();

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid verification code")));

        EmailVerification updatedEv = emailVerificationRepository.findById(ev.getId()).orElseThrow();
        assertEquals(1, updatedEv.getAttemptsCount());
        assertFalse(updatedEv.getIsVerified());
    }

    @Test
    @DisplayName("5. Expired verification code is rejected")
    void testExpiredVerificationCodeRejected() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Alex Expired")
                .email(TEST_EMAIL)
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .role(UserRole.STUDENT)
                .isVerified(false)
                .build();
        userRepository.save(user);

        EmailVerification ev = EmailVerification.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .email(TEST_EMAIL)
                .verificationCode("445566")
                .expiresAt(LocalDateTime.now().minusMinutes(5)) // Expired
                .attemptsCount(0)
                .isVerified(false)
                .build();
        emailVerificationRepository.save(ev);

        VerifyEmailRequest verifyReq = VerifyEmailRequest.builder()
                .email(TEST_EMAIL)
                .verificationCode("445566")
                .build();

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("expired")));
    }

    @Test
    @DisplayName("6. Maximum attempts exceeded locks the verification code")
    void testMaxAttemptsExceededLockout() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Alex MaxAttempts")
                .email(TEST_EMAIL)
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .role(UserRole.STUDENT)
                .isVerified(false)
                .build();
        userRepository.save(user);

        EmailVerification ev = EmailVerification.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .email(TEST_EMAIL)
                .verificationCode("778899")
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .attemptsCount(4)
                .isVerified(false)
                .build();
        emailVerificationRepository.save(ev);

        VerifyEmailRequest verifyReq = VerifyEmailRequest.builder()
                .email(TEST_EMAIL)
                .verificationCode("000000") // 5th invalid attempt
                .build();

        mockMvc.perform(post("/api/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Maximum verification attempts exceeded")));

        EmailVerification updatedEv = emailVerificationRepository.findById(ev.getId()).orElseThrow();
        assertTrue(updatedEv.getIsVerified(), "Code must be invalidated after max attempts");
    }

    @Test
    @DisplayName("7. Resend verification invalidates previous code and creates new active code")
    void testResendVerificationInvalidatesPreviousCode() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Alex Resend")
                .email(TEST_EMAIL)
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .role(UserRole.STUDENT)
                .isVerified(false)
                .build();
        userRepository.save(user);

        EmailVerification ev1 = EmailVerification.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .email(TEST_EMAIL)
                .verificationCode("111111")
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .attemptsCount(0)
                .isVerified(false)
                .build();
        emailVerificationRepository.save(ev1);

        ResendVerificationRequest resendReq = ResendVerificationRequest.builder()
                .email(TEST_EMAIL)
                .build();

        mockMvc.perform(post("/api/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resendReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiresVerification", is(true)));

        EmailVerification updatedEv1 = emailVerificationRepository.findById(ev1.getId()).orElseThrow();
        assertTrue(updatedEv1.getIsVerified(), "Previous code must be invalidated");

        EmailVerification ev2 = emailVerificationRepository
                .findFirstByEmailIgnoreCaseAndIsVerifiedFalseOrderByCreatedAtDesc(TEST_EMAIL)
                .orElseThrow();
        assertNotEquals(ev1.getId(), ev2.getId(), "A new code record must be created");
        assertFalse(ev2.getIsVerified());
    }

    @Test
    @DisplayName("8. Pre-existing accounts remain verified and log in without verification prompts")
    void testExistingUsersRemainVerifiedAndLogin() throws Exception {
        User existingUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Existing Senior Student")
                .email("legacy.student@skillpilot.edu")
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .role(UserRole.STUDENT)
                .isVerified(true) // Simulates pre-existing database record with default TRUE
                .build();
        userRepository.save(existingUser);

        LoginRequest loginReq = LoginRequest.builder()
                .email("legacy.student@skillpilot.edu")
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.userRole", is("student")));
    }

    @Test
    @DisplayName("9. Date of birth persistence: valid date saved, future date rejected, impossible age rejected")
    void testDateOfBirthPersistenceAndValidation() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("DOB Test User")
                .email("dob.user@skillpilot.edu")
                .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
                .role(UserRole.STUDENT)
                .isVerified(true)
                .build();
        userRepository.save(user);

        // Authenticate user to obtain token
        LoginRequest loginReq = LoginRequest.builder()
                .email("dob.user@skillpilot.edu")
                .password(TEST_PASSWORD)
                .build();

        String loginResponseJson = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponseJson).get("token").asText();

        // 1. Valid DOB
        ProfileUpdateRequest validUpdate = ProfileUpdateRequest.builder()
                .dateOfBirth("2001-08-20")
                .build();

        mockMvc.perform(put("/api/user/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateOfBirth", is("2001-08-20")));

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(LocalDate.of(2001, 8, 20), updatedUser.getDateOfBirth());

        // 2. Future DOB rejected
        ProfileUpdateRequest futureUpdate = ProfileUpdateRequest.builder()
                .dateOfBirth(LocalDate.now().plusDays(5).toString())
                .build();

        mockMvc.perform(put("/api/user/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("future")));

        // 3. Underage DOB rejected (< 13 years old)
        ProfileUpdateRequest tooYoungUpdate = ProfileUpdateRequest.builder()
                .dateOfBirth(LocalDate.now().minusYears(5).toString())
                .build();

        mockMvc.perform(put("/api/user/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooYoungUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("between 13 and 120 years")));
    }
}
