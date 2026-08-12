package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.LoginRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SessionLifecycleAuditTest {

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

    private User adminUser;
    private User studentUser;
    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        // Create Admin User
        adminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Lifecycle Admin")
                .email("lifecycle.admin@skillpilot.io")
                .passwordHash(passwordEncoder.encode("AdminPass123"))
                .role(UserRole.ADMIN)
                .title("System Admin")
                .education("M.S. Computer Science")
                .experienceYears(8)
                .location("Chicago, IL")
                .targetFocus("Infrastructure")
                .bio("Admin bio")
                .completionPercentage(100)
                .build();
        userRepository.save(adminUser);

        SecurityUser secAdmin = new SecurityUser(adminUser);
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(secAdmin, null, secAdmin.getAuthorities());
        adminToken = jwtTokenProvider.generateToken(adminAuth);

        // Create Student User
        studentUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Lifecycle Student")
                .email("lifecycle.student@skillpilot.io")
                .passwordHash(passwordEncoder.encode("StudentPass123"))
                .role(UserRole.STUDENT)
                .title("Student")
                .education("B.S. Software Engineering")
                .experienceYears(1)
                .location("Boston, MA")
                .targetFocus("Full Stack")
                .bio("Student bio")
                .completionPercentage(40)
                .build();
        userRepository.save(studentUser);

        SecurityUser secStudent = new SecurityUser(studentUser);
        Authentication studentAuth = new UsernamePasswordAuthenticationToken(secStudent, null, secStudent.getAuthorities());
        studentToken = jwtTokenProvider.generateToken(studentAuth);
    }

    @Test
    @DisplayName("Session Restoration: GET /api/auth/me for ADMIN returns userRole='admin'")
    void testAuthMeForAdminReturnsAdminRole() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(adminUser.getId())))
                .andExpect(jsonPath("$.email", is("lifecycle.admin@skillpilot.io")))
                .andExpect(jsonPath("$.userRole", is("admin")))
                .andExpect(jsonPath("$.role", is("admin")));
    }

    @Test
    @DisplayName("Session Restoration: GET /api/auth/me for STUDENT returns userRole='student'")
    void testAuthMeForStudentReturnsStudentRole() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(studentUser.getId())))
                .andExpect(jsonPath("$.email", is("lifecycle.student@skillpilot.io")))
                .andExpect(jsonPath("$.userRole", is("student")))
                .andExpect(jsonPath("$.role", is("student")));
    }

    @Test
    @DisplayName("Error Semantics: Unauthenticated GET /api/auth/me returns 401 Unauthorized")
    void testAuthMeUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Authorization Semantics: Student accessing /api/admin/stats returns 403 Forbidden without destroying session")
    void testStudentAccessingAdminEndpointReturns403() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Authorization Semantics: Admin accessing /api/admin/stats returns 200 OK")
    void testAdminAccessingAdminEndpointReturns200() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCareers", notNullValue()));
    }
}
