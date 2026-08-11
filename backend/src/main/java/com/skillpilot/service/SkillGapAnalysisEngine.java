package com.skillpilot.service;

import com.skillpilot.dto.response.CareerResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.dto.response.SkillGapItemResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.CareerSkillRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SkillGapAnalysisEngine {

    private final CareerMapper careerMapper;

    public SkillGapAnalysisResponse analyze(Career career, Map<String, Integer> userSkillMap) {
        List<CareerSkillRequirement> requirements = career.getRequiredSkills();
        if (requirements == null || requirements.isEmpty()) {
            CareerResponse careerResponse = careerMapper.toCareerResponse(career);
            return SkillGapAnalysisResponse.builder()
                    .career(careerResponse)
                    .readinessScore(100)
                    .skills(Collections.emptyList())
                    .strengths(Collections.emptyList())
                    .missingSkills(Collections.emptyList())
                    .totalRequiredSkills(0)
                    .completedSkills(0)
                    .build();
        }

        List<SkillGapItemResponse> skillItems = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        List<SkillGapItemResponse> missingSkills = new ArrayList<>();

        double totalWeightedFulfillment = 0.0;
        double totalWeight = 0.0;
        int completedSkills = 0;

        for (CareerSkillRequirement req : requirements) {
            String skillId = req.getSkill() != null ? req.getSkill().getId() : "";
            String skillName = req.getSkill() != null ? req.getSkill().getName() : "Unknown Skill";
            String category = req.getSkill() != null ? req.getSkill().getCategory() : "Technical";

            int requiredLevel = req.getRequiredLevel() != null ? req.getRequiredLevel() : 1;
            int currentLevel = userSkillMap.getOrDefault(skillId, 0);

            int gapAmount = Math.max(0, requiredLevel - currentLevel);
            boolean isEssential = Boolean.TRUE.equals(req.getIsEssential());
            double weight = isEssential ? 2.0 : 1.0;

            double fulfillment = Math.min(1.0, (double) currentLevel / requiredLevel);
            totalWeightedFulfillment += fulfillment * weight;
            totalWeight += weight;

            String severity;
            if (gapAmount >= 3) {
                severity = "critical";
            } else if (gapAmount == 2) {
                severity = "high";
            } else if (gapAmount == 1) {
                severity = "medium";
            } else {
                severity = "low";
            }

            String recommendedAction;
            if (gapAmount == 0) {
                recommendedAction = "Maintain skill practice.";
                completedSkills++;
                strengths.add(String.format("%s (Level %d/%d)", skillName, currentLevel, requiredLevel));
            } else {
                if ("Technical".equalsIgnoreCase(category)) {
                    recommendedAction = String.format("Complete hands-on coding modules & build portfolio projects in %s.", skillName);
                } else if ("Tools & Frameworks".equalsIgnoreCase(category)) {
                    recommendedAction = String.format("Practice workflow integrations and environment setups for %s.", skillName);
                } else if ("Domain Knowledge".equalsIgnoreCase(category)) {
                    recommendedAction = String.format("Read case studies and complete industry certification modules for %s.", skillName);
                } else {
                    recommendedAction = String.format("Engage in peer reviews, presentations, and group problem-solving for %s.", skillName);
                }
            }

            SkillGapItemResponse item = SkillGapItemResponse.builder()
                    .skillId(skillId)
                    .skillName(skillName)
                    .category(category)
                    .currentLevel(currentLevel)
                    .requiredLevel(requiredLevel)
                    .gapAmount(gapAmount)
                    .severity(severity)
                    .isEssential(isEssential)
                    .recommendedAction(recommendedAction)
                    .build();

            skillItems.add(item);
            if (gapAmount > 0) {
                missingSkills.add(item);
            }
        }

        double weightedRatio = totalWeight > 0 ? (totalWeightedFulfillment / totalWeight) : 0.0;
        int readinessScore = (int) Math.round(weightedRatio * 100.0);
        if (readinessScore < 0) readinessScore = 0;
        if (readinessScore > 100) readinessScore = 100;

        // Sort skills deterministically by gap severity (critical -> high -> medium -> low), then skillId
        skillItems.sort((a, b) -> {
            int sevCompare = Integer.compare(getSeverityRank(b.getSeverity()), getSeverityRank(a.getSeverity()));
            if (sevCompare != 0) return sevCompare;
            return a.getSkillId().compareTo(b.getSkillId());
        });

        missingSkills.sort((a, b) -> {
            int sevCompare = Integer.compare(getSeverityRank(b.getSeverity()), getSeverityRank(a.getSeverity()));
            if (sevCompare != 0) return sevCompare;
            return a.getSkillId().compareTo(b.getSkillId());
        });

        return SkillGapAnalysisResponse.builder()
                .career(careerMapper.toCareerResponse(career))
                .readinessScore(readinessScore)
                .skills(skillItems)
                .strengths(strengths)
                .missingSkills(missingSkills)
                .totalRequiredSkills(requirements.size())
                .completedSkills(completedSkills)
                .build();
    }

    private int getSeverityRank(String severity) {
        if ("critical".equalsIgnoreCase(severity)) return 4;
        if ("high".equalsIgnoreCase(severity)) return 3;
        if ("medium".equalsIgnoreCase(severity)) return 2;
        return 1;
    }
}
