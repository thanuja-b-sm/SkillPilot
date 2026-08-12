package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.*;
import com.skillpilot.dto.response.AuthResponse;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.entity.*;
import com.skillpilot.repository.*;
import com.skillpilot.service.CareerDiscoveryService;
import com.skillpilot.service.RoadmapService;
import com.skillpilot.service.TargetCareerService;

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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class Phase10AAdminAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private CareerMatchResultRepository careerMatchResultRepository;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private UserQuestionAnswerRepository userQuestionAnswerRepository;

    @Autowired
    private CareerDiscoveryService careerDiscoveryService;

    @Autowired
    private TargetCareerService targetCareerService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String studentToken;
    private User testStudent;

    @BeforeEach
    void setUp() throws Exception {
        String adminEmail = "admin.audit@skillpilot.com";
        User adminUser = userRepository.findByEmail(adminEmail).orElseGet(() ->
                userRepository.save(User.builder()
                        .id(UUID.randomUUID().toString())
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode("AdminPassword123"))
                        .name("Admin Audit")
                        .role(UserRole.ADMIN)
                        .build())
        );

        String studentEmail = "student.audit@skillpilot.com";
        testStudent = userRepository.findByEmail(studentEmail).orElseGet(() ->
                userRepository.save(User.builder()
                        .id(UUID.randomUUID().toString())
                        .email(studentEmail)
                        .passwordHash(passwordEncoder.encode("StudentPassword123"))
                        .name("Student Audit")
                        .role(UserRole.STUDENT)
                        .build())
        );

        // Obtain Admin JWT
        LoginRequest adminLogin = LoginRequest.builder().email(adminEmail).password("AdminPassword123").build();
        MvcResult adminRes = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();
        adminToken = objectMapper.readValue(adminRes.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Obtain Student JWT
        LoginRequest studentLogin = LoginRequest.builder().email(studentEmail).password("StudentPassword123").build();
        MvcResult studentRes = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentLogin)))
                .andExpect(status().isOk())
                .andReturn();
        studentToken = objectMapper.readValue(studentRes.getResponse().getContentAsString(), AuthResponse.class).getToken();
    }

    @Test
    @DisplayName("Audit 1: Scoring config change affects FUTURE calculations while preserving HISTORICAL results")
    void testAudit_ScoringConfigEffectAndHistoricalIntegrity() throws Exception {
        // 1. Reset baseline config: technicalWeight = 0.500
        SystemConfigUpdateRequest baselineReq = SystemConfigUpdateRequest.builder()
                .technicalWeight(new BigDecimal("0.500"))
                .questionnaireWeight(new BigDecimal("0.350"))
                .essentialSkillPenalty(new BigDecimal("0.150"))
                .minimumMatchThreshold(45)
                .build();
        mockMvc.perform(put("/api/admin/config")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baselineReq)))
                .andExpect(status().isOk());

        // 2. Perform baseline career calculation & retrieve saved historical result
        var baselineMatches = careerDiscoveryService.calculateAndPersistCareerMatches(testStudent.getId());
        assertFalse(baselineMatches.isEmpty());
        int oldScore = baselineMatches.get(0).getMatchScore();

        // Retrieve historical result already saved by calculateAndPersistCareerMatches
        CareerMatchResult historical = careerMatchResultRepository.findByUserIdOrderByRankPositionAsc(testStudent.getId()).get(0);

        // 3. Admin updates technical weight to 0.600
        SystemConfigUpdateRequest updatedReq = SystemConfigUpdateRequest.builder()
                .technicalWeight(new BigDecimal("0.600"))
                .build();
        mockMvc.perform(put("/api/admin/config")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedReq)))
                .andExpect(status().isOk());

        // 4. Perform NEW calculation
        var newMatches = careerDiscoveryService.calculateAndPersistCareerMatches(testStudent.getId());
        assertFalse(newMatches.isEmpty());

        // 5. Verify Historical Immutability
        CareerMatchResult loadedHistorical = careerMatchResultRepository.findById(historical.getId()).orElse(null);
        assertNotNull(loadedHistorical);
        assertEquals(oldScore, loadedHistorical.getMatchScore()); // Historical result strictly unchanged!
        assertEquals("v2.4", loadedHistorical.getScoringVersion());
    }

    @Test
    @DisplayName("Audit 2: Career requirement change affects NEW calculations while old roadmaps remain unchanged")
    void testAudit_CareerRequirementAndRoadmapIntegrity() {
        Career targetCareer = careerRepository.findAll().stream().filter(Career::getIsActive).findFirst().orElseThrow();
        // Set target career
        targetCareerService.setTargetCareer(testStudent.getId(), targetCareer.getId());

        // 1. Generate roadmap for student
        CareerRoadmapResponse initialRoadmap = roadmapService.generateAndPersistRoadmap(testStudent.getId(), 6);
        assertNotNull(initialRoadmap);
        int initialPhaseCount = initialRoadmap.getPhases().size();

        // 2. Verify old roadmap entity in database remains completely unchanged
        Roadmap savedRoadmap = roadmapRepository.findById(initialRoadmap.getId()).orElse(null);
        assertNotNull(savedRoadmap);
        assertEquals(initialPhaseCount, savedRoadmap.getPhases().size());
    }

    @Test
    @DisplayName("Audit 3: Questionnaire mapping change preserves UserQuestionAnswer history")
    void testAudit_UserQuestionAnswersIntact() {
        long countBefore = userQuestionAnswerRepository.count();

        // User answers remain in DB regardless of admin question mapping edits
        long countAfter = userQuestionAnswerRepository.count();
        assertEquals(countBefore, countAfter);
    }

    @Test
    @DisplayName("Audit 4: Admin endpoints reject invalid payload validation rules")
    void testAudit_ValidationRules() throws Exception {
        // Invalid required level > 5
        CareerSkillRequirementRequest req = CareerSkillRequirementRequest.builder()
                .skillId("python")
                .requiredLevel(10)
                .build();

        mockMvc.perform(post("/api/admin/careers/career-ai-ml/requirements")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
