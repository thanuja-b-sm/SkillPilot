package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.RoadmapGenerateRequest;
import com.skillpilot.dto.request.TargetCareerRequest;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.dto.response.RoadmapMilestoneResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.dto.response.SkillGapItemResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.CareerSkillRequirement;
import com.skillpilot.entity.RoadmapTemplate;
import com.skillpilot.entity.Skill;
import com.skillpilot.service.CareerMapper;
import com.skillpilot.service.RoadmapGenerationEngine;
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
public class Phase8RoadmapGenerationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CareerMapper careerMapper;

    private RoadmapGenerationEngine engine;

    private Skill pySkill;
    private Skill mlSkill;
    private Skill awsSkill;
    private Career aiCareer;

    @BeforeEach
    void setUp() {
        engine = new RoadmapGenerationEngine(objectMapper);

        pySkill = Skill.builder().id("python").name("Python Programming").category("Technical").build();
        mlSkill = Skill.builder().id("machine-learning").name("Machine Learning & AI").category("Technical").build();
        awsSkill = Skill.builder().id("cloud-aws").name("Cloud Computing (AWS/GCP)").category("Technical").build();

        List<CareerSkillRequirement> reqs = List.of(
                CareerSkillRequirement.builder().id("csr-1").skill(pySkill).requiredLevel(5).isEssential(true).build(),
                CareerSkillRequirement.builder().id("csr-2").skill(mlSkill).requiredLevel(4).isEssential(true).build(),
                CareerSkillRequirement.builder().id("csr-3").skill(awsSkill).requiredLevel(3).isEssential(false).build()
        );

        aiCareer = Career.builder()
                .id("ai-software-engineer")
                .title("AI & Machine Learning Engineer")
                .category("Artificial Intelligence")
                .isActive(true)
                .requiredSkills(reqs)
                .build();
    }

    @Test
    @DisplayName("Engine 1: 6-month roadmap generation produces 4 distributed milestones")
    void testEngine_SixMonthRoadmap() {
        SkillGapAnalysisResponse gapRes = SkillGapAnalysisResponse.builder()
                .career(careerMapper.toCareerResponse(aiCareer))
                .readinessScore(20)
                .missingSkills(List.of(
                        SkillGapItemResponse.builder().skillId("machine-learning").skillName("Machine Learning & AI").category("Technical").currentLevel(0).requiredLevel(4).gapAmount(4).severity("critical").isEssential(true).build(),
                        SkillGapItemResponse.builder().skillId("cloud-aws").skillName("Cloud Computing (AWS/GCP)").category("Technical").currentLevel(0).requiredLevel(3).gapAmount(3).severity("critical").isEssential(false).build()
                ))
                .skills(Collections.emptyList())
                .strengths(Collections.emptyList())
                .totalRequiredSkills(3)
                .completedSkills(1)
                .build();

        CareerRoadmapResponse res = engine.generateRoadmap(aiCareer, gapRes, null, 6);

        assertEquals("6 Months (Phased 4-Stage Plan)", res.getOverallTimeline());
        assertEquals(20, res.getOverallReadiness());
        assertEquals(4, res.getPhases().size());
        assertEquals("Months 1 – 1", res.getPhases().get(0).getMonthRange());
        assertEquals("Months 2 – 3", res.getPhases().get(1).getMonthRange());
        assertEquals("Months 4 – 4", res.getPhases().get(2).getMonthRange());
        assertEquals("Months 5 – 6", res.getPhases().get(3).getMonthRange());
    }

    @Test
    @DisplayName("Engine 2: 12-month roadmap duration distribution")
    void testEngine_TwelveMonthRoadmap() {
        SkillGapAnalysisResponse gapRes = SkillGapAnalysisResponse.builder()
                .career(careerMapper.toCareerResponse(aiCareer))
                .readinessScore(50)
                .missingSkills(Collections.emptyList())
                .skills(Collections.emptyList())
                .strengths(Collections.emptyList())
                .totalRequiredSkills(3)
                .completedSkills(3)
                .build();

        CareerRoadmapResponse res = engine.generateRoadmap(aiCareer, gapRes, null, 12);

        assertEquals("12 Months (Phased 4-Stage Plan)", res.getOverallTimeline());
        assertEquals(4, res.getPhases().size());
        assertEquals("Months 1 – 3", res.getPhases().get(0).getMonthRange());
        assertEquals("Months 4 – 6", res.getPhases().get(1).getMonthRange());
        assertEquals("Months 7 – 9", res.getPhases().get(2).getMonthRange());
        assertEquals("Months 10 – 12", res.getPhases().get(3).getMonthRange());
    }

    @Test
    @DisplayName("Engine 3: 100% readiness user produces consolidation roadmap")
    void testEngine_FullReadinessUser() {
        SkillGapAnalysisResponse gapRes = SkillGapAnalysisResponse.builder()
                .career(careerMapper.toCareerResponse(aiCareer))
                .readinessScore(100)
                .missingSkills(Collections.emptyList())
                .skills(Collections.emptyList())
                .strengths(List.of("Python Programming (Level 5/5)"))
                .totalRequiredSkills(3)
                .completedSkills(3)
                .build();

        CareerRoadmapResponse res = engine.generateRoadmap(aiCareer, gapRes, null, 6);

        assertEquals(100, res.getOverallReadiness());
        assertTrue(res.getAiExplanation().contains("100% Readiness"));
    }

    @Test
    @DisplayName("API 1: Generate 6-month roadmap via POST /api/user/roadmaps/generate")
    void testApi_GenerateRoadmap() throws Exception {
        String token = obtainJwtToken("alex.rivera@university.edu", "Password123");

        // Set target career first
        TargetCareerRequest targetReq = TargetCareerRequest.builder().careerId("ai-software-engineer").build();
        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(targetReq)))
                .andExpect(status().isOk());

        // Generate roadmap
        RoadmapGenerateRequest genReq = RoadmapGenerateRequest.builder().durationMonths(6).build();
        mockMvc.perform(post("/api/user/roadmaps/generate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerId").value("ai-software-engineer"))
                .andExpect(jsonPath("$.overallTimeline").value("6 Months (Phased 4-Stage Plan)"))
                .andExpect(jsonPath("$.phases").isArray())
                .andExpect(jsonPath("$.phases.length()").value(4));
    }

    @Test
    @DisplayName("API 2: Invalid duration (< 6 or > 12) rejected with 400 Bad Request")
    void testApi_InvalidDurationRejected() throws Exception {
        String token = obtainJwtToken("alex.rivera@university.edu", "Password123");

        TargetCareerRequest targetReq = TargetCareerRequest.builder().careerId("ai-software-engineer").build();
        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(targetReq)))
                .andExpect(status().isOk());

        // Duration 4
        RoadmapGenerateRequest lowReq = RoadmapGenerateRequest.builder().durationMonths(4).build();
        mockMvc.perform(post("/api/user/roadmaps/generate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowReq)))
                .andExpect(status().isBadRequest());

        // Duration 14
        RoadmapGenerateRequest highReq = RoadmapGenerateRequest.builder().durationMonths(14).build();
        mockMvc.perform(post("/api/user/roadmaps/generate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(highReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("API 3: User roadmap retrieval via GET /api/user/roadmaps")
    void testApi_GetUserRoadmap() throws Exception {
        String token = obtainJwtToken("alex.rivera@university.edu", "Password123");

        TargetCareerRequest targetReq = TargetCareerRequest.builder().careerId("ai-software-engineer").build();
        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(targetReq)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/roadmaps")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerId").value("ai-software-engineer"))
                .andExpect(jsonPath("$.phases.length()").value(4));
    }

    @Test
    @DisplayName("API 4: Ownership Isolation — User B cannot retrieve User A's private roadmap by ID")
    void testApi_RoadmapOwnershipIsolation() throws Exception {
        String tokenAlex = obtainJwtToken("alex.rivera@university.edu", "Password123");
        String tokenMarcus = obtainJwtToken("marcus.vance@techcorp.com", "Password123");

        // Alex generates roadmap
        TargetCareerRequest targetReq = TargetCareerRequest.builder().careerId("ai-software-engineer").build();
        mockMvc.perform(put("/api/user/target-career")
                        .header("Authorization", "Bearer " + tokenAlex)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(targetReq)))
                .andExpect(status().isOk());

        MvcResult alexGenResult = mockMvc.perform(post("/api/user/roadmaps/generate")
                        .header("Authorization", "Bearer " + tokenAlex)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(RoadmapGenerateRequest.builder().durationMonths(6).build())))
                .andExpect(status().isOk())
                .andReturn();

        CareerRoadmapResponse alexRoadmap = objectMapper.readValue(alexGenResult.getResponse().getContentAsString(), CareerRoadmapResponse.class);
        assertNotNull(alexRoadmap.getId());

        // Marcus attempts to retrieve Alex's roadmap by ID (rejected with 403 Forbidden)
        mockMvc.perform(get("/api/user/roadmaps/" + alexRoadmap.getId())
                        .header("Authorization", "Bearer " + tokenMarcus))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("API 5: Unauthenticated request to /api/user/roadmaps rejected with 401 Unauthorized")
    void testApi_UnauthenticatedRequestRejected() throws Exception {
        mockMvc.perform(get("/api/user/roadmaps"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/user/roadmaps/generate"))
                .andExpect(status().isUnauthorized());
    }

    private String obtainJwtToken(String email, String password) throws Exception {
        Map<String, String> loginReq = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andReturn();

        if (result.getResponse().getStatus() == 200) {
            Map<?, ?> resp = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
            return (String) resp.get("token");
        }

        // Fallback: register new user
        Map<String, String> regReq = Map.of(
                "email", email,
                "password", password,
                "name", "Test User " + UUID.randomUUID().toString().substring(0, 5)
        );
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> resp = objectMapper.readValue(regResult.getResponse().getContentAsString(), Map.class);
        return (String) resp.get("token");
    }
}
