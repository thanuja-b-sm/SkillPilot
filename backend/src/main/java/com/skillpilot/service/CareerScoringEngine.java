package com.skillpilot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.entity.*;
import com.skillpilot.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CareerScoringEngine {

    private final ObjectMapper objectMapper;
    private final SystemConfigRepository systemConfigRepository;

    @Autowired
    public CareerScoringEngine(ObjectMapper objectMapper, SystemConfigRepository systemConfigRepository) {
        this.objectMapper = objectMapper;
        this.systemConfigRepository = systemConfigRepository;
    }

    public CareerScoringEngine(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.systemConfigRepository = null;
    }

    public CalculationResult calculateMatch(
            Career career,
            Map<String, Integer> userSkillMap,
            List<UserQuestionAnswer> answers
    ) {
        double techScale = 75.0;
        double questCap = 23.0;
        int minScore = 45;

        SystemConfig config = null;
        if (systemConfigRepository != null) {
            try {
                config = systemConfigRepository.findFirstByIsActiveTrueOrderByCreatedAtDesc().orElse(null);
                if (config != null) {
                    if (config.getTechnicalWeight() != null) {
                        techScale = config.getTechnicalWeight().doubleValue() * 150.0;
                    }
                    if (config.getQuestionnaireWeight() != null) {
                        questCap = config.getQuestionnaireWeight().doubleValue() * 65.714;
                    }
                    if (config.getMinimumMatchThreshold() != null) {
                        minScore = config.getMinimumMatchThreshold();
                    }
                }
            } catch (Exception ignored) {}
        }

        double essentialWeightMultiplier = 2.0;
        if (config != null && config.getEssentialSkillPenalty() != null) {
            essentialWeightMultiplier = 1.0 + (config.getEssentialSkillPenalty().doubleValue() * 6.666);
        }

        double totalRequiredWeight = 0.0;
        double earnedScore = 0.0;
        List<String> keyStrengths = new ArrayList<>();
        List<String> keyGaps = new ArrayList<>();

        if (career.getRequiredSkills() != null) {
            for (CareerSkillRequirement req : career.getRequiredSkills()) {
                double weight = Boolean.TRUE.equals(req.getIsEssential()) ? essentialWeightMultiplier : 1.0;
                int reqLevel = req.getRequiredLevel() != null ? req.getRequiredLevel() : 1;
                totalRequiredWeight += reqLevel * weight;

                String skillId = req.getSkill() != null ? req.getSkill().getId() : "";
                String skillName = req.getSkill() != null ? req.getSkill().getName() : skillId;

                int userLevel = userSkillMap.getOrDefault(skillId, 0);

                double scoreForSkill = Math.min(userLevel, reqLevel) * weight;
                earnedScore += scoreForSkill;

                if (userLevel >= reqLevel) {
                    keyStrengths.add(skillName + " (Level " + userLevel + "/" + reqLevel + ")");
                } else {
                    int gap = reqLevel - userLevel;
                    keyGaps.add(skillName + " (Needs +" + gap + " level increase)");
                }
            }
        }

        double skillMatchRatio = totalRequiredWeight > 0.0 ? (earnedScore / totalRequiredWeight) : 0.0;
        int readinessScore = (int) Math.round(skillMatchRatio * 100.0);
        if (readinessScore < 0) readinessScore = 0;
        if (readinessScore > 100) readinessScore = 100;

        // ANOM-03: Normalized questionnaire contribution proportional to relevant questions answered
        double questionnaireBonus = 0.0;
        if (answers != null && career.getRequiredSkills() != null && !career.getRequiredSkills().isEmpty()) {
            Set<String> careerSkillIds = career.getRequiredSkills().stream()
                    .map(r -> r.getSkill() != null ? r.getSkill().getId() : "")
                    .filter(id -> !id.isBlank())
                    .collect(Collectors.toSet());

            int relevantQuestionsCount = 0;
            double totalEarnedQuestionnaireScore = 0.0;

            for (UserQuestionAnswer uqa : answers) {
                Question question = uqa.getQuestion();
                if (question == null || question.getOptions() == null || question.getOptions().isEmpty()) {
                    continue;
                }

                // A question is relevant if at least one option maps to a skill required by this career
                boolean isQuestionRelevantToCareer = false;
                for (QuestionOption opt : question.getOptions()) {
                    if (opt.getAssociatedSkills() != null) {
                        for (QuestionSkillMapping mapping : opt.getAssociatedSkills()) {
                            String mappedSkillId = mapping.getSkill() != null ? mapping.getSkill().getId() : "";
                            if (careerSkillIds.contains(mappedSkillId)) {
                                isQuestionRelevantToCareer = true;
                                break;
                            }
                        }
                    }
                    if (isQuestionRelevantToCareer) break;
                }

                if (!isQuestionRelevantToCareer) {
                    continue;
                }

                relevantQuestionsCount++;

                if (uqa.getSelectedOptionIds() == null || uqa.getSelectedOptionIds().isBlank()) {
                    continue;
                }

                List<String> selectedOptionIds;
                try {
                    selectedOptionIds = objectMapper.readValue(uqa.getSelectedOptionIds(), new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    selectedOptionIds = Collections.emptyList();
                }

                double maxOptionWeightForQuestion = 0.0;
                for (QuestionOption opt : question.getOptions()) {
                    if (selectedOptionIds.contains(opt.getId()) && opt.getAssociatedSkills() != null) {
                        for (QuestionSkillMapping mapping : opt.getAssociatedSkills()) {
                            String mappedSkillId = mapping.getSkill() != null ? mapping.getSkill().getId() : "";
                            if (careerSkillIds.contains(mappedSkillId)) {
                                double w = mapping.getWeight() != null ? mapping.getWeight() : 1.0;
                                if (w > maxOptionWeightForQuestion) {
                                    maxOptionWeightForQuestion = w;
                                }
                            }
                        }
                    }
                }

                totalEarnedQuestionnaireScore += (maxOptionWeightForQuestion / 5.0);
            }

            if (relevantQuestionsCount > 0) {
                techScale = (config != null && config.getTechnicalWeight() != null)
                        ? config.getTechnicalWeight().doubleValue() * 150.0 : 75.0;
                questCap = (config != null && config.getQuestionnaireWeight() != null)
                        ? config.getQuestionnaireWeight().doubleValue() * 71.42857 : 25.0;

                double questionnaireNormalizedRatio = totalEarnedQuestionnaireScore / relevantQuestionsCount;
                questionnaireBonus = questionnaireNormalizedRatio * questCap;
            }
        }

        int rawPercentage;
        if (answers != null && !answers.isEmpty() && questionnaireBonus > 0) {
            rawPercentage = (int) Math.round((skillMatchRatio * techScale) + questionnaireBonus);
        } else {
            // Skill-only evaluation when no relevant questionnaire answers exist
            rawPercentage = (int) Math.round(skillMatchRatio * 100.0);
        }
        
        // ANOM-01: Remove artificial minScore flatline from calculated matchScore.
        if (rawPercentage < 0) {
            rawPercentage = 0;
        }

        // ANOM-02: Allow true 100% match for perfect candidates.
        if (rawPercentage > 100) {
            rawPercentage = 100;
        }

        boolean isRecommended = (rawPercentage >= minScore);

        String confidenceLevel = "Low";
        if (rawPercentage >= 85) {
            confidenceLevel = "High";
        } else if (rawPercentage >= 70) {
            confidenceLevel = "Medium";
        } else if (rawPercentage >= minScore) {
            confidenceLevel = "Moderate";
        }

        String fitReason;
        if (rawPercentage >= 80) {
            String strengthsText = !keyStrengths.isEmpty()
                    ? String.join(", ", keyStrengths.subList(0, Math.min(2, keyStrengths.size())))
                    : "core skill requirements";
            fitReason = "System calculated a " + rawPercentage + "% match due to strong proficiency in " + strengthsText + ".";
        } else {
            String gapsText = !keyGaps.isEmpty()
                    ? String.join(", ", keyGaps.subList(0, Math.min(2, keyGaps.size())))
                    : "key skills";
            fitReason = "System calculated a " + rawPercentage + "% match. Developing " + gapsText + " will significantly improve alignment.";
        }

        return new CalculationResult(
                rawPercentage,
                readinessScore,
                isRecommended,
                confidenceLevel,
                fitReason,
                "Deterministic Algorithm v2.5",
                keyStrengths,
                keyGaps
        );
    }

    public SystemConfig getCurrentSystemConfig() {
        if (systemConfigRepository != null) {
            return systemConfigRepository.findFirstByIsActiveTrueOrderByCreatedAtDesc().orElse(null);
        }
        return null;
    }

    @lombok.Value
    public static class CalculationResult {
        int matchScore;
        int readinessScore;
        boolean isRecommended;
        String confidenceLevel;
        String fitReason;
        String systemCalculatedBadge;
        List<String> keyStrengths;
        List<String> keyGaps;
    }
}
