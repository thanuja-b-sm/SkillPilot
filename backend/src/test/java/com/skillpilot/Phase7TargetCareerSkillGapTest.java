package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.TargetCareerRequest;
import com.skillpilot.dto.request.UserSkillUpdateRequest;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.dto.response.SkillGapItemResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.CareerSkillRequirement;
import com.skillpilot.entity.Skill;
import com.skillpilot.service.CareerMapper;
import com.skillpilot.service.SkillGapAnalysisEngine;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class Phase7TargetCareerSkillGapTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CareerMapper careerMapper;

    private SkillGapAnalysisEngine engine;

    private Skill pySkill;
    private Skill mlSkill;
    private Skill awsSkill;
    private Career aiCareer;

    @BeforeEach
    void setUp() {
        engine = new SkillGapAnalysisEngine(careerMapper);

        pySkill = Skill.builder().id("python").name("Python Programming").category("Technical").build();
        mlSkill = Skill.builder().id("machine-learning").name("Machine Learning & AI").category("Technical").build();
        awsSkill = Skill.builder().id("cloud-aws").name("Cloud Computing (AWS/GCP)").category("Technical").build();

        List<CareerSkillRequirement> reqs = List.of(
                CareerSkillRequirement.builder().id("csr-1").skill(pySkill).requiredLevel(5).isEssential(true).build(),  // weight 2.0
                CareerSkillRequirement.builder().id("csr-2").skill(mlSkill).requiredLevel(4).isEssential(true).build(),  // weight 2.0
                CareerSkillRequirement.builder().id("csr-3").skill(awsSkill).requiredLevel(3).isEssential(false).build()  // weight 1.0
        ); // Total weight = 2.0 + 2.0 + 1.0 = 5.0

        aiCareer = Career.builder()
                .id("ai-software-engineer")
                .title("AI & Machine Learning Engineer")
                .category("Artificial Intelligence")
                .isActive(true)
                .requiredSkills(reqs)
                .build();
    }

    @Test
    @DisplayName("Engine 1: Exact skill match (current >= required -> gap = 0, severity = low)")
    void testEngine_ExactMatch() {
        Map<String, Integer> userSkills = Map.of("python", 5, "machine-learning", 4, "cloud-aws", 3);
        SkillGapAnalysisResponse res = engine.analyze(aiCareer, userSkills);

        assertEquals(100, res.getReadinessScore());
        assertEquals(3, res.getCompletedSkills());
        assertEquals(0, res.getMissingSkills().size());
        assertTrue(res.getSkills().stream().allMatch(s -> s.getGapAmount() == 0 && "low".equals(s.getSeverity())));
    }

    @Test
    @DisplayName("Engine 2: Partial skill match and missing skill default to 0")
    void testEngine_PartialAndMissingSkills() {
        // User has Python level 5 (fulfilled), Machine Learning level 2 (req 4 -> gap 2), AWS missing (0/3 -> gap 3)
        Map<String, Integer> userSkills = Map.of("python", 5, "machine-learning", 2);
        SkillGapAnalysisResponse res = engine.analyze(aiCareer, userSkills);

        // Fulfillment:
        // Python: 5/5 = 1.0 * 2.0 = 2.0
        // ML: 2/4 = 0.5 * 2.0 = 1.0
        // AWS: 0/3 = 0.0 * 1.0 = 0.0
        // Weighted = 3.0 / 5.0 = 0.60 -> readiness 60%
        assertEquals(60, res.getReadinessScore());

        SkillGapItemResponse awsGap = res.getSkills().stream().filter(s -> "cloud-aws".equals(s.getSkillId())).findFirst().orElseThrow();
        assertEquals(0, awsGap.getCurrentLevel());
        assertEquals(3, awsGap.getRequiredLevel());
        assertEquals(3, awsGap.getGapAmount());
        assertEquals("critical", awsGap.getSeverity());

        SkillGapItemResponse mlGap = res.getSkills().stream().filter(s -> "machine-learning".equals(s.getSkillId())).findFirst().orElseThrow();
        assertEquals(2, mlGap.getCurrentLevel());
        assertEquals(2, mlGap.getGapAmount());
        assertEquals("high", mlGap.getSeverity());
    }

    @Test
    @DisplayName("Engine 3: No skills result in 0% readiness")
    void testEngine_NoSkills() {
        SkillGapAnalysisResponse res = engine.analyze(aiCareer, Collections.emptyMap());
        assertEquals(0, res.getReadinessScore());
        assertEquals(0, res.getCompletedSkills());
        assertEquals(3, res.getMissingSkills().size());
    }

    @Test
    @DisplayName("API 1: Authenticated target career selection and retrieval")
    void testApi_TargetCareerSelection() throws Exception {
        String token = obtainJwtToken("alex.rivera@university.edu", "Password123");

        TargetCareerRequest req = TargetCareerRequest.builder().careerId("ai-software-engineer").build();

        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerId").value("ai-software-engineer"))
                .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get("/api/user/target-career")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerId").value("ai-software-engineer"));
    }

    @Test
    @DisplayName("API 2: Target career selection with invalid or inactive career rejected")
    void testApi_InvalidCareerSelection() throws Exception {
        String token = obtainJwtToken("alex.rivera@university.edu", "Password123");

        TargetCareerRequest invalidReq = TargetCareerRequest.builder().careerId("nonexistent-career-id").build();

        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("API 3: Skill gap API returns accurate readiness and skill gap matrix")
    void testApi_SkillGapAnalysis() throws Exception {
        String token = obtainJwtToken("alex.rivera@university.edu", "Password123");

        // First set target career
        TargetCareerRequest targetReq = TargetCareerRequest.builder().careerId("ai-software-engineer").build();
        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(targetReq)))
                .andExpect(status().isOk());

        // Call target career skill gap endpoint
        mockMvc.perform(get("/api/user/target-career/skill-gap")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.career.id").value("ai-software-engineer"))
                .andExpect(jsonPath("$.readinessScore").exists())
                .andExpect(jsonPath("$.skills").isArray())
                .andExpect(jsonPath("$.totalRequiredSkills").value(6));
    }

    @Test
    @DisplayName("API 4: Skill update dynamically changes subsequent skill gap analysis and readiness")
    void testApi_SkillUpdateChangesReadiness() throws Exception {
        String token = obtainJwtToken("alex.rivera@university.edu", "Password123");

        // Fetch initial skill gap for AI career
        MvcResult initialResult = mockMvc.perform(get("/api/careers/ai-software-engineer/skill-gap")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        SkillGapAnalysisResponse initialResp = objectMapper.readValue(
                initialResult.getResponse().getContentAsString(), SkillGapAnalysisResponse.class);
        int initialReadiness = initialResp.getReadinessScore();

        // Update Python skill to 5
        UserSkillUpdateRequest skillReq = UserSkillUpdateRequest.builder().skillId("python").level(5).build();
        mockMvc.perform(put("/api/user/skills")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillReq)))
                .andExpect(status().isOk());

        // Fetch updated skill gap for AI career
        MvcResult updatedResult = mockMvc.perform(get("/api/careers/ai-software-engineer/skill-gap")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        SkillGapAnalysisResponse updatedResp = objectMapper.readValue(
                updatedResult.getResponse().getContentAsString(), SkillGapAnalysisResponse.class);

        assertTrue(updatedResp.getReadinessScore() >= initialReadiness);
    }

    @Test
    @DisplayName("API 5: User ownership isolation — Unauthenticated access rejected")
    void testApi_UnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/user/target-career"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/user/target-career/skill-gap"))
                .andExpect(status().isUnauthorized());
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
