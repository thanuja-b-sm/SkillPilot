package com.skillpilot.controller;

import com.skillpilot.dto.request.AiEnhanceSummaryRequest;
import com.skillpilot.dto.response.*;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.ai.GeminiExplanationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GeminiExplanationService geminiExplanationService;

    @PostMapping("/enhance-summary")
    public ResponseEntity<AiEnhanceSummaryResponse> enhanceSummary(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody AiEnhanceSummaryRequest request) {
        String userId = securityUser != null ? securityUser.getId() : null;
        AiEnhanceSummaryResponse response = geminiExplanationService.enhanceSummary(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/explain-career")
    public ResponseEntity<AiCareerExplanationResponse> explainCareer(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody Map<String, Object> body) {
        String userId = securityUser != null ? securityUser.getId() : null;
        String careerTitle = (String) body.getOrDefault("careerTitle", "Target Career");
        Integer matchScore = body.get("matchScore") != null ? ((Number) body.get("matchScore")).intValue() : 75;
        List<String> keyStrengths = (List<String>) body.get("keyStrengths");
        List<String> keyGaps = (List<String>) body.get("keyGaps");
        String targetRoleGoal = (String) body.get("targetRoleGoal");

        AiCareerExplanationResponse response = geminiExplanationService.explainCareerMatch(userId, careerTitle, matchScore, keyStrengths, keyGaps, targetRoleGoal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/explain-skill-gap")
    public ResponseEntity<AiSkillGapExplanationResponse> explainSkillGap(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody Map<String, Object> body) {
        String userId = securityUser != null ? securityUser.getId() : null;
        String careerTitle = (String) body.getOrDefault("careerTitle", "Target Career");
        Integer readinessScore = body.get("readinessScore") != null ? ((Number) body.get("readinessScore")).intValue() : 0;
        List<String> missingSkills = (List<String>) body.get("missingSkills");

        AiSkillGapExplanationResponse response = geminiExplanationService.explainSkillGap(userId, careerTitle, readinessScore, missingSkills);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/explain-roadmap")
    public ResponseEntity<AiRoadmapExplanationResponse> explainRoadmap(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody Map<String, Object> body) {
        String userId = securityUser != null ? securityUser.getId() : null;
        String careerTitle = (String) body.getOrDefault("careerTitle", "Target Career");
        String overallTimeline = (String) body.getOrDefault("overallTimeline", "6 Months");
        Integer overallReadiness = body.get("overallReadiness") != null ? ((Number) body.get("overallReadiness")).intValue() : 0;
        List<String> phaseTitles = (List<String>) body.get("phaseTitles");

        AiRoadmapExplanationResponse response = geminiExplanationService.explainRoadmap(userId, careerTitle, overallTimeline, overallReadiness, phaseTitles);
        return ResponseEntity.ok(response);
    }
}
