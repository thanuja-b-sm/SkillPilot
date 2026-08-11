package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.config.GeminiProperties;
import com.skillpilot.dto.request.AiEnhanceSummaryRequest;
import com.skillpilot.dto.response.*;
import com.skillpilot.service.ai.FallbackExplanationService;
import com.skillpilot.service.ai.GeminiExplanationService;
import com.skillpilot.service.ai.GeminiPromptBuilder;
import com.skillpilot.service.ai.GeminiResponseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class Phase9GeminiAiEnhancementTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GeminiProperties geminiProperties;

    @Autowired
    private GeminiPromptBuilder promptBuilder;

    @Autowired
    private GeminiResponseValidator responseValidator;

    @Autowired
    private FallbackExplanationService fallbackService;

    @Autowired
    private GeminiExplanationService explanationService;

    @BeforeEach
    void setUp() {
        // Ensure properties exist
        assertNotNull(geminiProperties);
    }

    @Test
    @DisplayName("Validator 1: Clean JSON markdown code fence")
    void testValidator_CleanCodeFence() {
        String raw = "```json\n{\"summary\": \"Great fit\", \"explanation\": \"Strong Python baseline\"}\n```";
        assertTrue(responseValidator.isValidExplanationResponse(raw));

        String clean = responseValidator.cleanJsonFence(raw);
        assertEquals("{\"summary\": \"Great fit\", \"explanation\": \"Strong Python baseline\"}", clean);
    }

    @Test
    @DisplayName("Validator 2: Malformed JSON or empty string rejected")
    void testValidator_MalformedJsonRejected() {
        assertFalse(responseValidator.isValidExplanationResponse(null));
        assertFalse(responseValidator.isValidExplanationResponse(""));
        assertFalse(responseValidator.isValidExplanationResponse("Not a json string"));
        assertFalse(responseValidator.isValidExplanationResponse("{\"invalid_key\": 123}"));
    }

    @Test
    @DisplayName("Fallback 1: Fallback service produces data-driven career explanation")
    void testFallback_CareerExplanation() {
        AiCareerExplanationResponse res = fallbackService.getCareerFallback(
                "AI & Machine Learning Engineer",
                84,
                List.of("Python Programming"),
                List.of("Cloud Computing")
        );

        assertEquals("AI & Machine Learning Engineer", res.getCareerTitle());
        assertEquals(84, res.getMatchScore()); // Match score strictly unchanged!
        assertEquals("system-calculated", res.getSource());
        assertTrue(res.getSummary().contains("84%"));
        assertTrue(res.getExplanation().contains("Python Programming"));
    }

    @Test
    @DisplayName("Fallback 2: Fallback service produces data-driven readiness explanation")
    void testFallback_ReadinessExplanation() {
        AiSkillGapExplanationResponse res = fallbackService.getSkillGapFallback(
                "AI & Machine Learning Engineer",
                42,
                List.of("Cloud Computing", "Deep Learning")
        );

        assertEquals(42, res.getReadinessScore()); // Readiness strictly unchanged!
        assertEquals("system-calculated", res.getSource());
        assertTrue(res.getSummary().contains("42%"));
        assertTrue(res.getExplanation().contains("Cloud Computing"));
    }

    @Test
    @DisplayName("Fallback 3: Fallback service produces data-driven roadmap summary")
    void testFallback_RoadmapSummary() {
        AiRoadmapExplanationResponse res = fallbackService.getRoadmapFallback(
                "AI & Machine Learning Engineer",
                "6 Months (Phased 4-Stage Plan)",
                84,
                List.of("Phase 1", "Phase 2", "Phase 3", "Phase 4")
        );

        assertEquals("6 Months (Phased 4-Stage Plan)", res.getOverallTimeline());
        assertEquals(84, res.getOverallReadiness());
        assertEquals("system-calculated", res.getSource());
        assertEquals(4, res.getStageHighlights().size());
    }

    @Test
    @DisplayName("Immutability: AI Explanation Layer does NOT alter deterministic business scores")
    void testImmutability_BusinessScoresPreserved() {
        int originalMatchScore = 78;
        int originalReadiness = 35;

        AiCareerExplanationResponse careerRes = explanationService.explainCareerMatch(
                null, "AI & Machine Learning Engineer", originalMatchScore, List.of("Python"), List.of("Docker"), "AI"
        );
        assertEquals(originalMatchScore, careerRes.getMatchScore());

        AiSkillGapExplanationResponse gapRes = explanationService.explainSkillGap(
                null, "AI & Machine Learning Engineer", originalReadiness, List.of("Docker")
        );
        assertEquals(originalReadiness, gapRes.getReadinessScore());
    }

    @Test
    @DisplayName("API 1: POST /api/ai/enhance-summary returns enhanced explanation")
    void testApi_EnhanceSummary() throws Exception {
        AiEnhanceSummaryRequest req = AiEnhanceSummaryRequest.builder()
                .careerTitle("AI & Machine Learning Engineer")
                .currentMatchScore(84)
                .keyStrengths(List.of("Python Programming"))
                .keyGaps(List.of("Cloud Computing"))
                .targetRoleGoal("Artificial Intelligence")
                .build();

        mockMvc.perform(post("/api/ai/enhance-summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enhancedExplanation").isString());
    }

    @Test
    @DisplayName("API 2: POST /api/ai/explain-career returns structured response")
    void testApi_ExplainCareer() throws Exception {
        Map<String, Object> req = Map.of(
                "careerTitle", "AI & Machine Learning Engineer",
                "matchScore", 84,
                "keyStrengths", List.of("Python Programming"),
                "keyGaps", List.of("Cloud Computing")
        );

        mockMvc.perform(post("/api/ai/explain-career")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerTitle").value("AI & Machine Learning Engineer"))
                .andExpect(jsonPath("$.matchScore").value(84))
                .andExpect(jsonPath("$.summary").isString());
    }

    @Test
    @DisplayName("API 3: POST /api/ai/explain-skill-gap returns structured response")
    void testApi_ExplainSkillGap() throws Exception {
        Map<String, Object> req = Map.of(
                "careerTitle", "AI & Machine Learning Engineer",
                "readinessScore", 42,
                "missingSkills", List.of("Cloud Computing", "Deep Learning")
        );

        mockMvc.perform(post("/api/ai/explain-skill-gap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerTitle").value("AI & Machine Learning Engineer"))
                .andExpect(jsonPath("$.readinessScore").value(42))
                .andExpect(jsonPath("$.summary").isString());
    }

    @Test
    @DisplayName("API 4: POST /api/ai/explain-roadmap returns structured response")
    void testApi_ExplainRoadmap() throws Exception {
        Map<String, Object> req = Map.of(
                "careerTitle", "AI & Machine Learning Engineer",
                "overallTimeline", "6 Months (Phased 4-Stage Plan)",
                "overallReadiness", 84,
                "phaseTitles", List.of("Phase 1", "Phase 2", "Phase 3", "Phase 4")
        );

        mockMvc.perform(post("/api/ai/explain-roadmap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerTitle").value("AI & Machine Learning Engineer"))
                .andExpect(jsonPath("$.overallTimeline").value("6 Months (Phased 4-Stage Plan)"))
                .andExpect(jsonPath("$.overallReadiness").value(84))
                .andExpect(jsonPath("$.summary").isString());
    }
}
