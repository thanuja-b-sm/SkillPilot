package com.skillpilot;

import com.skillpilot.dto.request.LoginRequest;
import com.skillpilot.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class Phase11AdminCareerIntelligenceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Autowired
    private com.skillpilot.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() throws Exception {
        String adminEmail = "admin.phase11@skillpilot.com";
        userRepository.findByEmail(adminEmail).orElseGet(() ->
                userRepository.save(com.skillpilot.entity.User.builder()
                        .id(java.util.UUID.randomUUID().toString())
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode("AdminPassword123"))
                        .name("Phase11 Admin User")
                        .role(com.skillpilot.entity.UserRole.ADMIN)
                        .build())
        );

        String studentEmail = "student.phase11@skillpilot.com";
        userRepository.findByEmail(studentEmail).orElseGet(() ->
                userRepository.save(com.skillpilot.entity.User.builder()
                        .id(java.util.UUID.randomUUID().toString())
                        .email(studentEmail)
                        .passwordHash(passwordEncoder.encode("Password123"))
                        .name("Phase11 Student User")
                        .role(com.skillpilot.entity.UserRole.STUDENT)
                        .build())
        );

        // Admin Auth
        LoginRequest adminAuth = LoginRequest.builder()
                .email(adminEmail)
                .password("AdminPassword123")
                .build();

        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminAuth)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse adminResp = objectMapper.readValue(
                adminResult.getResponse().getContentAsString(), AuthResponse.class);
        this.adminToken = adminResp.getToken();

        // Student Auth
        LoginRequest studentAuth = LoginRequest.builder()
                .email(studentEmail)
                .password("Password123")
                .build();

        MvcResult studentResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentAuth)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse studentResp = objectMapper.readValue(
                studentResult.getResponse().getContentAsString(), AuthResponse.class);
        this.studentToken = studentResp.getToken();
    }

    @Test
    @DisplayName("Admin can fetch career impact analysis")
    void testCareerImpactAnalysis() throws Exception {
        mockMvc.perform(get("/api/admin/careers/cloud-architect/impact")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerId").value("cloud-architect"))
                .andExpect(jsonPath("$.requiredSkillCount").exists())
                .andExpect(jsonPath("$.isConfigurationComplete").value(true));
    }

    @Test
    @DisplayName("Student user is rejected from career impact analysis")
    void testStudentRejectedFromCareerImpact() throws Exception {
        mockMvc.perform(get("/api/admin/careers/cloud-architect/impact")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Admin can fetch skill dependency impact analysis")
    void testSkillImpactAnalysis() throws Exception {
        mockMvc.perform(get("/api/admin/skills/cloud-aws/impact")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillId").value("cloud-aws"))
                .andExpect(jsonPath("$.careerCount").exists());
    }

    @Test
    @DisplayName("Admin can run system health check audit")
    void testSystemHealthCheck() throws Exception {
        mockMvc.perform(get("/api/admin/health-check")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.activeCareersCount").exists())
                .andExpect(jsonPath("$.activeSkillsCount").exists());
    }
}
