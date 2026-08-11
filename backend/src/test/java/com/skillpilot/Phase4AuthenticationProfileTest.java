package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.LoginRequest;
import com.skillpilot.dto.request.ProfileUpdateRequest;
import com.skillpilot.dto.request.RegisterRequest;
import com.skillpilot.dto.request.UserSkillUpdateRequest;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.security.JwtTokenProvider;
import com.skillpilot.security.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class Phase4AuthenticationProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testStudentUser;
    private User testAdminUser;
    private String studentToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Create test student
        testStudentUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Student User Alpha")
                .email("student.alpha@test.com")
                .passwordHash(passwordEncoder.encode("StudentPass123"))
                .role(UserRole.STUDENT)
                .title("Student Profile")
                .education("B.S. Computer Science")
                .experienceYears(1)
                .location("New York, NY")
                .targetFocus("Artificial Intelligence")
                .bio("Test student bio")
                .completionPercentage(50)
                .build();
        userRepository.save(testStudentUser);

        SecurityUser secStudent = new SecurityUser(testStudentUser);
        Authentication studentAuth = new UsernamePasswordAuthenticationToken(secStudent, null, secStudent.getAuthorities());
        studentToken = jwtTokenProvider.generateToken(studentAuth);

        // Create test admin
        testAdminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Admin User Omega")
                .email("admin.omega@test.com")
                .passwordHash(passwordEncoder.encode("AdminPass123"))
                .role(UserRole.ADMIN)
                .title("System Administrator")
                .education("M.S. Cybersecurity")
                .experienceYears(10)
                .location("Washington, D.C.")
                .targetFocus("Security")
                .bio("Test admin bio")
                .completionPercentage(100)
                .build();
        userRepository.save(testAdminUser);

        SecurityUser secAdmin = new SecurityUser(testAdminUser);
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(secAdmin, null, secAdmin.getAuthorities());
        adminToken = jwtTokenProvider.generateToken(adminAuth);
    }

    @Test
    @DisplayName("1. Successful Registration creates STUDENT and returns AuthResponse")
    void test1_SuccessfulRegistration() throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .name("New Student Candidate")
                .email("new.candidate@university.edu")
                .password("SecurePass2026")
                .education("Computer Science Junior")
                .targetFocus("Full Stack Web")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.userRole", is("student")))
                .andExpect(jsonPath("$.userProfile.email", is("new.candidate@university.edu")))
                .andExpect(jsonPath("$.userProfile.password").doesNotExist())
                .andExpect(jsonPath("$.userProfile.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("2. Duplicate Email Registration returns HTTP 409 Conflict")
    void test2_DuplicateEmailRegistration() throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .name("Duplicate Candidate")
                .email("student.alpha@test.com")
                .password("SecurePass2026")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already registered")));
    }

    @Test
    @DisplayName("3. Invalid Email Format returns HTTP 400 Bad Request")
    void test3_InvalidEmailFormat() throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .name("Invalid Email Guy")
                .email("not-an-email-format")
                .password("SecurePass2026")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. Weak Password returns HTTP 400 Bad Request")
    void test4_WeakPassword() throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .name("Weak Password Guy")
                .email("weak.pass@test.com")
                .password("weak")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("5. Successful Login returns valid JWT and AuthResponse")
    void test5_SuccessfulLogin() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .email("student.alpha@test.com")
                .password("StudentPass123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.userRole", is("student")))
                .andExpect(jsonPath("$.userProfile.name", is("Student User Alpha")));
    }

    @Test
    @DisplayName("6. Wrong Password returns HTTP 401 Unauthorized")
    void test6_WrongPassword() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .email("student.alpha@test.com")
                .password("WrongPassword123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid email or password")));
    }

    @Test
    @DisplayName("7. Unknown Email returns HTTP 401 Unauthorized without account enumeration")
    void test7_UnknownEmail() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .email("nonexistent.user@test.com")
                .password("StudentPass123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid email or password")));
    }

    @Test
    @DisplayName("8. Authenticated GET /api/auth/me returns current user profile")
    void test8_AuthenticatedAuthMe() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testStudentUser.getId())))
                .andExpect(jsonPath("$.email", is("student.alpha@test.com")))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("9. Unauthenticated GET /api/auth/me returns HTTP 401 Unauthorized")
    void test9_UnauthenticatedAuthMe() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("10. GET /api/user/profile returns authenticated user profile")
    void test10_GetOwnProfile() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Student User Alpha")))
                .andExpect(jsonPath("$.education", is("B.S. Computer Science")));
    }

    @Test
    @DisplayName("11. PUT /api/user/profile updates personal details and recalculates completion")
    void test11_UpdateOwnProfile() throws Exception {
        ProfileUpdateRequest req = ProfileUpdateRequest.builder()
                .name("Alex Rivera Updated")
                .title("AI Engineer Specialist")
                .education("M.S. Artificial Intelligence")
                .bio("Updated bio narrative.")
                .build();

        mockMvc.perform(put("/api/user/profile")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alex Rivera Updated")))
                .andExpect(jsonPath("$.title", is("AI Engineer Specialist")))
                .andExpect(jsonPath("$.bio", is("Updated bio narrative.")));
    }

    @Test
    @DisplayName("12. GET /api/user/skills returns user skills list")
    void test12_GetOwnSkills() throws Exception {
        mockMvc.perform(get("/api/user/skills")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("13. PUT /api/user/skills updates skill level (0 to 5)")
    void test13_UpdateUserSkill() throws Exception {
        UserSkillUpdateRequest req = UserSkillUpdateRequest.builder()
                .skillId("python")
                .level(4)
                .build();

        mockMvc.perform(put("/api/user/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillId", is("python")))
                .andExpect(jsonPath("$.level", is(4)));
    }

    @Test
    @DisplayName("14. Invalid Skill Level (>5) returns HTTP 400 Bad Request")
    void test14_InvalidSkillLevelHigh() throws Exception {
        UserSkillUpdateRequest req = UserSkillUpdateRequest.builder()
                .skillId("python")
                .level(9)
                .build();

        mockMvc.perform(put("/api/user/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("15. Invalid Skill Level (<0) returns HTTP 400 Bad Request")
    void test15_InvalidSkillLevelLow() throws Exception {
        UserSkillUpdateRequest req = UserSkillUpdateRequest.builder()
                .skillId("python")
                .level(-1)
                .build();

        mockMvc.perform(put("/api/user/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("16. Nonexistent Skill ID returns HTTP 404 Not Found")
    void test16_InvalidSkillId() throws Exception {
        UserSkillUpdateRequest req = UserSkillUpdateRequest.builder()
                .skillId("nonexistent-skill-id-999")
                .level(3)
                .build();

        mockMvc.perform(put("/api/user/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("17. Student user CANNOT access Admin endpoint (HTTP 403 Forbidden)")
    void test17_StudentCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("18. Admin user CAN access Admin endpoint (HTTP 200 OK)")
    void test18_AdminCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("authorized")));
    }

    @Test
    @DisplayName("19. User Ownership Isolation: Authenticated Principal identity used for profile queries")
    void test19_OwnershipProtection() throws Exception {
        // Calling /api/user/profile with User A's token ALWAYS returns User A's profile
        MvcResult res = mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn();

        String body = res.getResponse().getContentAsString();
        assertTrue(body.contains("Student User Alpha"));
        assertFalse(body.contains("Admin User Omega"));
    }
}
