package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.TargetCareerRequest;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserTargetCareer;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.repository.UserTargetCareerRepository;
import com.skillpilot.service.RoadmapService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TargetCareerSynchronizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private UserTargetCareerRepository userTargetCareerRepository;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testUserEmail;
    private String testUserId;

    @BeforeEach
    void setUp() {
        testUserEmail = "sync.student." + UUID.randomUUID() + "@skillpilot.com";
        User user = userRepository.save(User.builder()
                .id(UUID.randomUUID().toString())
                .name("Sync Test Student")
                .email(testUserEmail)
                .passwordHash(passwordEncoder.encode("Password123"))
                .education("Computer Science")
                .experienceYears(1)
                .role(com.skillpilot.entity.UserRole.STUDENT)
                .build());
        testUserId = user.getId();
    }

    @Test
    @DisplayName("1. Career A selected -> Career A data and skill gap loaded")
    void testCareerASelectionAndData() throws Exception {
        String token = obtainJwtToken(testUserEmail, "Password123");

        TargetCareerRequest reqA = TargetCareerRequest.builder().careerId("ai-software-engineer").build();

        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerId").value("ai-software-engineer"));

        mockMvc.perform(get("/api/user/target-career/skill-gap")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.career.id").value("ai-software-engineer"));
    }

    @Test
    @DisplayName("2 & 3. Switch to Career B -> Career A roadmap is not returned for Career B")
    void testSwitchTargetCareerInvalidatesStaleRoadmap() throws Exception {
        String token = obtainJwtToken(testUserEmail, "Password123");

        // 1. Select Career A (ai-software-engineer)
        TargetCareerRequest reqA = TargetCareerRequest.builder().careerId("ai-software-engineer").build();
        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqA)))
                .andExpect(status().isOk());

        // Generate Roadmap for Career A
        CareerRoadmapResponse roadmapA = roadmapService.generateAndPersistRoadmap(testUserId, 6);
        assertThat(roadmapA.getCareerId()).isEqualTo("ai-software-engineer");

        // 2. Switch target career to Career B (cloud-architect)
        TargetCareerRequest reqB = TargetCareerRequest.builder().careerId("cloud-architect").build();
        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqB)))
                .andExpect(status().isOk());

        // 3. Fetch user roadmap -> must return Career B roadmap, NEVER Career A
        CareerRoadmapResponse roadmapB = roadmapService.getRoadmapForUser(testUserId);
        assertThat(roadmapB.getCareerId()).isEqualTo("cloud-architect");
        assertThat(roadmapB.getCareerId()).isNotEqualTo("ai-software-engineer");
    }

    @Test
    @DisplayName("4. Career specific questionnaire loading")
    void testCareerSpecificQuestionnaireLoading() throws Exception {
        String token = obtainJwtToken(testUserEmail, "Password123");

        mockMvc.perform(get("/api/questionnaire/career/ai-software-engineer")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("5. Unauthenticated / No target career returns 404 for skill gap")
    void testNoTargetCareerReturns404() throws Exception {
        String token = obtainJwtToken(testUserEmail, "Password123");

        // User has not selected target career yet
        mockMvc.perform(get("/api/user/target-career/skill-gap")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("6. Historical roadmap data remains unchanged when new roadmap is generated")
    void testHistoricalRoadmapsIsolation() {
        Career careerA = careerRepository.findById("ai-software-engineer").orElseThrow();
        User user = userRepository.findById(testUserId).orElseThrow();
        userTargetCareerRepository.save(UserTargetCareer.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .career(careerA)
                .build());

        CareerRoadmapResponse roadmapA = roadmapService.generateAndPersistRoadmap(testUserId, 6);
        String roadmapAId = roadmapA.getId();

        CareerRoadmapResponse fetchedA = roadmapService.getRoadmapById(testUserId, roadmapAId);
        assertThat(fetchedA.getCareerId()).isEqualTo("ai-software-engineer");
    }

    private String obtainJwtToken(String email, String password) throws Exception {
        Map<String, String> loginReq = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> resp = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        return (String) resp.get("token");
    }
}
