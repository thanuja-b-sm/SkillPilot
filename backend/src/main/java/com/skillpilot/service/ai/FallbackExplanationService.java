package com.skillpilot.service.ai;

import com.skillpilot.dto.response.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FallbackExplanationService {

    public AiCareerExplanationResponse getCareerFallback(String careerTitle, Integer matchScore, List<String> keyStrengths, List<String> keyGaps) {
        int score = matchScore != null ? matchScore : 75;
        String strengthsText = (keyStrengths != null && !keyStrengths.isEmpty())
                ? String.join(", ", keyStrengths)
                : "your foundational skill baseline";
        String gapsText = (keyGaps != null && !keyGaps.isEmpty())
                ? String.join(", ", keyGaps)
                : "specialized technical requirements";

        String summary = String.format("System Summary: %s is calculated at %d%% match alignment based on your current assessment.", careerTitle, score);
        String explanation = String.format("Your strongest aligned competencies include %s. Addressing development areas in %s will accelerate your career readiness score.", strengthsText, gapsText);

        List<String> focus = new ArrayList<>();
        if (keyGaps != null && !keyGaps.isEmpty()) {
            focus.addAll(keyGaps.stream().limit(2).toList());
        } else {
            focus.add("Advanced System Architecture");
            focus.add("Production Workflow Optimization");
        }

        return AiCareerExplanationResponse.builder()
                .careerTitle(careerTitle)
                .matchScore(score)
                .summary(summary)
                .explanation(explanation)
                .focusAreas(focus)
                .source("system-calculated")
                .build();
    }

    public AiSkillGapExplanationResponse getSkillGapFallback(String careerTitle, Integer readinessScore, List<String> missingSkills) {
        int readiness = readinessScore != null ? readinessScore : 0;
        String gapsText = (missingSkills != null && !missingSkills.isEmpty())
                ? String.join(", ", missingSkills)
                : "target role competencies";

        String summary = String.format("System Summary: Current readiness for %s is calculated at %d%%.", careerTitle, readiness);
        String explanation = String.format("The primary skill gaps requiring attention include %s. Systematic development through structured milestones will progressively elevate your readiness.", gapsText);

        List<String> priorityGaps = (missingSkills != null && !missingSkills.isEmpty())
                ? missingSkills.stream().limit(3).toList()
                : List.of("Core Technical Prerequisites", "Systemic Skill Practice");

        return AiSkillGapExplanationResponse.builder()
                .careerTitle(careerTitle)
                .readinessScore(readiness)
                .summary(summary)
                .explanation(explanation)
                .priorityGaps(priorityGaps)
                .source("system-calculated")
                .build();
    }

    public AiRoadmapExplanationResponse getRoadmapFallback(String careerTitle, String overallTimeline, Integer overallReadiness, List<String> phaseTitles) {
        int readiness = overallReadiness != null ? overallReadiness : 0;
        String timeline = overallTimeline != null ? overallTimeline : "6 Months";

        String summary = String.format("System Summary: Milestone plan tailored for %s across %s (%d%% Baseline Readiness).", careerTitle, timeline, readiness);
        String explanation = "The 4-stage milestone sequence structures your development chronologically, focusing on foundational gap remediation before advancing to applied portfolio production.";

        List<String> highlights = (phaseTitles != null && !phaseTitles.isEmpty())
                ? phaseTitles.stream().limit(4).toList()
                : List.of("Stage 1: Foundational Skill Acquisition", "Stage 2: Applied Project Workflows", "Stage 3: Systems Hardening", "Stage 4: Portfolio Defense");

        return AiRoadmapExplanationResponse.builder()
                .careerTitle(careerTitle)
                .overallTimeline(timeline)
                .overallReadiness(readiness)
                .summary(summary)
                .explanation(explanation)
                .stageHighlights(highlights)
                .source("system-calculated")
                .build();
    }
}
