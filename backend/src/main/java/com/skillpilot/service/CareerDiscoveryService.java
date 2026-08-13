package com.skillpilot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.response.CareerMatchResponse;
import com.skillpilot.entity.*;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareerDiscoveryService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserQuestionAnswerRepository userQuestionAnswerRepository;
    private final CareerMatchResultRepository careerMatchResultRepository;
    private final CareerScoringEngine careerScoringEngine;
    private final CareerMapper careerMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<CareerMatchResponse> calculateAndPersistCareerMatches(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Career> activeCareers = careerRepository.findByIsActiveTrue();
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        List<UserQuestionAnswer> answers = userQuestionAnswerRepository.findByUserId(userId);

        Map<String, Integer> userSkillMap = userSkills.stream()
                .collect(Collectors.toMap(
                        us -> us.getSkill() != null ? us.getSkill().getId() : "",
                        UserSkill::getLevel,
                        (existing, replacement) -> Math.max(existing, replacement)
                ));

        List<CalculatedMatch> matchResults = new ArrayList<>();

        for (Career career : activeCareers) {
            CareerScoringEngine.CalculationResult calc = careerScoringEngine.calculateMatch(career, userSkillMap, answers);
            matchResults.add(new CalculatedMatch(career, calc));
        }

        // Rank deterministically: score desc, careerId asc
        matchResults.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getCalc().getMatchScore(), a.getCalc().getMatchScore());
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return a.getCareer().getId().compareTo(b.getCareer().getId());
        });

        List<CareerMatchResult> entitiesToSave = new ArrayList<>();
        List<CareerMatchResponse> responses = new ArrayList<>();

        int rank = 1;
        for (CalculatedMatch cm : matchResults) {
            Career career = cm.getCareer();
            CareerScoringEngine.CalculationResult calc = cm.getCalc();

            String strengthsJson;
            String gapsJson;
            try {
                strengthsJson = objectMapper.writeValueAsString(calc.getKeyStrengths());
                gapsJson = objectMapper.writeValueAsString(calc.getKeyGaps());
            } catch (Exception e) {
                strengthsJson = "[]";
                gapsJson = "[]";
            }

            CareerMatchResult entity = careerMatchResultRepository
                    .findByUserIdAndCareerId(userId, career.getId())
                    .orElseGet(() -> CareerMatchResult.builder()
                            .id(UUID.randomUUID().toString())
                            .user(user)
                            .career(career)
                            .build());

            String configSnapshot = null;
            String reqsSnapshot = null;
            try {
                SystemConfig sysCfg = careerScoringEngine.getCurrentSystemConfig();
                if (sysCfg != null) {
                    configSnapshot = objectMapper.writeValueAsString(sysCfg);
                }
                if (career.getRequiredSkills() != null) {
                    reqsSnapshot = objectMapper.writeValueAsString(career.getRequiredSkills().stream().map(r -> Map.of(
                            "skillId", r.getSkill() != null ? r.getSkill().getId() : "",
                            "skillName", r.getSkill() != null ? r.getSkill().getName() : "",
                            "requiredLevel", r.getRequiredLevel(),
                            "isEssential", r.getIsEssential()
                    )).collect(Collectors.toList()));
                }
            } catch (Exception ignored) {}

            entity.setMatchScore(calc.getMatchScore());
            entity.setRankPosition(rank++);
            entity.setConfidenceLevel(calc.getConfidenceLevel());
            entity.setFitReason(calc.getFitReason());
            entity.setSystemCalculatedBadge(calc.getSystemCalculatedBadge());
            entity.setKeyStrengthsJson(strengthsJson);
            entity.setKeyGapsJson(gapsJson);
            entity.setScoringVersion(calc.getSystemCalculatedBadge().replace("Deterministic Algorithm ", ""));
            entity.setConfigSnapshot(configSnapshot);
            entity.setRequirementsSnapshot(reqsSnapshot);

            entitiesToSave.add(entity);

            responses.add(CareerMatchResponse.builder()
                    .career(careerMapper.toCareerResponse(career))
                    .matchScore(calc.getMatchScore())
                    .readinessScore(calc.getReadinessScore())
                    .isRecommended(calc.isRecommended())
                    .keyStrengths(calc.getKeyStrengths())
                    .keyGaps(calc.getKeyGaps())
                    .confidenceLevel(calc.getConfidenceLevel())
                    .fitReason(calc.getFitReason())
                    .systemCalculatedBadge(calc.getSystemCalculatedBadge())
                    .build());
        }

        careerMatchResultRepository.saveAll(entitiesToSave);
        return responses;
    }

    @Transactional
    public List<CareerMatchResponse> getUserCareerMatches(String userId) {
        List<CareerMatchResult> savedResults = careerMatchResultRepository.findByUserIdOrderByRankPositionAsc(userId);

        if (savedResults.isEmpty()) {
            return calculateAndPersistCareerMatches(userId);
        }

        int minScoreThreshold = 45;
        SystemConfig currentCfg = careerScoringEngine.getCurrentSystemConfig();
        if (currentCfg != null && currentCfg.getMinimumMatchThreshold() != null) {
            minScoreThreshold = currentCfg.getMinimumMatchThreshold();
        }

        List<CareerMatchResponse> responses = new ArrayList<>();
        for (CareerMatchResult cmr : savedResults) {
            List<String> strengths;
            List<String> gaps;
            try {
                strengths = objectMapper.readValue(cmr.getKeyStrengthsJson(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                gaps = objectMapper.readValue(cmr.getKeyGapsJson(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
            } catch (Exception e) {
                strengths = Collections.emptyList();
                gaps = Collections.emptyList();
            }

            int score = cmr.getMatchScore() != null ? cmr.getMatchScore() : 0;
            boolean isRecommended = score >= minScoreThreshold;

            responses.add(CareerMatchResponse.builder()
                    .career(careerMapper.toCareerResponse(cmr.getCareer()))
                    .matchScore(score)
                    .readinessScore(null)
                    .isRecommended(isRecommended)
                    .keyStrengths(strengths)
                    .keyGaps(gaps)
                    .confidenceLevel(cmr.getConfidenceLevel())
                    .fitReason(cmr.getFitReason())
                    .systemCalculatedBadge(cmr.getSystemCalculatedBadge())
                    .build());
        }

        return responses;
    }

    @lombok.Value
    private static class CalculatedMatch {
        Career career;
        CareerScoringEngine.CalculationResult calc;
    }
}
