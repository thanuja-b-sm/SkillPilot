package com.skillpilot.service;

import com.skillpilot.dto.response.CareerResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.dto.response.SkillGapItemResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.CareerSkillRequirement;
import com.skillpilot.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SkillGapAnalysisEngine {

    private final CareerMapper careerMapper;

    public SkillGapAnalysisResponse analyze(Career career, Map<String, Integer> userSkillMap) {
        return analyze(career, userSkillMap, null);
    }

    public SkillGapAnalysisResponse analyze(Career career, Map<String, Integer> userSkillMap, User user) {
        List<CareerSkillRequirement> requirements = career.getRequiredSkills();
        if (requirements == null || requirements.isEmpty()) {
            CareerResponse careerResponse = careerMapper.toCareerResponse(career);
            return SkillGapAnalysisResponse.builder()
                    .career(careerResponse)
                    .readinessScore(100)
                    .skillReadiness(100)
                    .experienceAlignment(100)
                    .educationAlignment(100)
                    .overallReadiness(100)
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

        int relevantExp = user != null && user.getRelevantExperienceYears() != null ? user.getRelevantExperienceYears() : 
                         (user != null && user.getExperienceYears() != null ? user.getExperienceYears() : 0);

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

            String classification;
            String severity;
            boolean experienceSupported = false;

            if (gapAmount == 0) {
                classification = "SATISFIED";
                severity = "low";
            } else if (gapAmount == 1 && relevantExp >= 3) {
                classification = "EXPERIENCE_SUPPORTED";
                severity = "medium";
                experienceSupported = true;
            } else if (gapAmount >= 3) {
                classification = "CRITICAL";
                severity = "critical";
            } else if (gapAmount == 2) {
                classification = "IMPORTANT";
                severity = "high";
            } else {
                classification = "MINOR";
                severity = "medium";
            }

            String recommendedAction;
            if (gapAmount == 0) {
                recommendedAction = "Maintain skill practice.";
                completedSkills++;
                strengths.add(String.format("%s (Level %d/%d)", skillName, currentLevel, requiredLevel));
            } else if (experienceSupported) {
                recommendedAction = String.format("Domain experience (%d yrs) buffers this minor gap. Refine specific %s advanced practices.", relevantExp, skillName);
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
                    .classification(classification)
                    .experienceSupported(experienceSupported)
                    .isEssential(isEssential)
                    .recommendedAction(recommendedAction)
                    .build();

            skillItems.add(item);
            if (gapAmount > 0) {
                missingSkills.add(item);
            }
        }

        double weightedRatio = totalWeight > 0 ? (totalWeightedFulfillment / totalWeight) : 0.0;
        int skillReadiness = (int) Math.round(weightedRatio * 100.0);
        skillReadiness = Math.min(100, Math.max(0, skillReadiness));

        // Compute Experience Alignment
        int experienceAlignment;
        if (relevantExp >= 5) experienceAlignment = 100;
        else if (relevantExp >= 3) experienceAlignment = 85;
        else if (relevantExp >= 1) experienceAlignment = 65;
        else experienceAlignment = 40;

        // Compute Education Alignment
        int educationAlignment = 60;
        if (user != null) {
            String major = user.getMajorFieldOfStudy() != null ? user.getMajorFieldOfStudy().toLowerCase() : "";
            String category = career.getCategory() != null ? career.getCategory().toLowerCase() : "";
            if (!major.isBlank() && (category.contains("software") || category.contains("data") || category.contains("tech")) &&
                    (major.contains("computer") || major.contains("engineering") || major.contains("data") || major.contains("science"))) {
                educationAlignment = 90;
            } else if (!major.isBlank() && (category.contains("finance") || category.contains("business")) &&
                    (major.contains("finance") || major.contains("business") || major.contains("economics") || major.contains("accounting"))) {
                educationAlignment = 90;
            }
        }

        int overallReadiness = (int) Math.round((0.60 * skillReadiness) + (0.25 * experienceAlignment) + (0.15 * educationAlignment));
        overallReadiness = Math.min(100, Math.max(0, overallReadiness));

        // Sort skills deterministically by gap severity (critical -> high -> medium -> low), then skillId
        skillItems.sort(getSkillComparator());
        missingSkills.sort(getSkillComparator());

        return SkillGapAnalysisResponse.builder()
                .career(careerMapper.toCareerResponse(career))
                .readinessScore(skillReadiness)
                .skillReadiness(skillReadiness)
                .experienceAlignment(experienceAlignment)
                .educationAlignment(educationAlignment)
                .overallReadiness(overallReadiness)
                .skills(skillItems)
                .strengths(strengths)
                .missingSkills(missingSkills)
                .totalRequiredSkills(requirements.size())
                .completedSkills(completedSkills)
                .build();
    }

    private Comparator<SkillGapItemResponse> getSkillComparator() {
        return (a, b) -> {
            int sevCompare = Integer.compare(getSeverityRank(b.getSeverity()), getSeverityRank(a.getSeverity()));
            if (sevCompare != 0) return sevCompare;
            return a.getSkillId().compareTo(b.getSkillId());
        };
    }

    private int getSeverityRank(String severity) {
        if ("critical".equalsIgnoreCase(severity)) return 4;
        if ("high".equalsIgnoreCase(severity)) return 3;
        if ("medium".equalsIgnoreCase(severity)) return 2;
        return 1;
    }
}
