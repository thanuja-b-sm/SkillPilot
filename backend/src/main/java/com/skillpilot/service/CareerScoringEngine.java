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

        double questionnaireBonus = 0.0;
        if (answers != null) {
            Set<String> careerSkillIds = (career.getRequiredSkills() != null)
                    ? career.getRequiredSkills().stream()
                    .map(r -> r.getSkill() != null ? r.getSkill().getId() : "")
                    .collect(Collectors.toSet())
                    : Collections.emptySet();

            for (UserQuestionAnswer uqa : answers) {
                if (uqa.getSelectedOptionIds() == null || uqa.getSelectedOptionIds().isBlank()) {
                    continue;
                }

                List<String> selectedOptionIds;
                try {
                    selectedOptionIds = objectMapper.readValue(uqa.getSelectedOptionIds(), new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    selectedOptionIds = Collections.emptyList();
                }

                Question question = uqa.getQuestion();
                if (question != null && question.getOptions() != null) {
                    for (QuestionOption opt : question.getOptions()) {
                        if (selectedOptionIds.contains(opt.getId()) && opt.getAssociatedSkills() != null) {
                            for (QuestionSkillMapping mapping : opt.getAssociatedSkills()) {
                                String mappedSkillId = mapping.getSkill() != null ? mapping.getSkill().getId() : "";
                                if (careerSkillIds.contains(mappedSkillId)) {
                                    double w = mapping.getWeight() != null ? mapping.getWeight() : 1.0;
                                    questionnaireBonus += (w / 5.0) * 4.0;
                                }
                            }
                        }
                    }
                }
            }
        }

        int rawPercentage = (int) Math.round((skillMatchRatio * techScale) + Math.min(questCap, questionnaireBonus));
        if (rawPercentage < minScore) {
            rawPercentage = minScore;
        }
        if (rawPercentage > 98) {
            rawPercentage = 98;
        }

        String confidenceLevel = "Moderate";
        if (rawPercentage >= 85) {
            confidenceLevel = "High";
        } else if (rawPercentage >= 70) {
            confidenceLevel = "Medium";
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
                confidenceLevel,
                fitReason,
                "Deterministic Algorithm v2.4",
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
        String confidenceLevel;
        String fitReason;
        String systemCalculatedBadge;
        List<String> keyStrengths;
        List<String> keyGaps;
    }
}
