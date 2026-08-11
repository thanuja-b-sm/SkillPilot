package com.skillpilot.service;

import com.skillpilot.dto.request.SystemConfigUpdateRequest;
import com.skillpilot.dto.response.SystemConfigResponse;
import com.skillpilot.entity.SystemConfig;
import com.skillpilot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    private final CareerRepository careerRepository;
    private final SkillRepository skillRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final RoadmapRepository roadmapRepository;
    private final AIGenerationLogRepository aiGenerationLogRepository;

    @Transactional(readOnly = true)
    public SystemConfigResponse getCurrentConfig() {
        SystemConfig config = getOrCreateActiveConfig();
        return toResponse(config);
    }

    @Transactional
    public SystemConfigResponse updateConfig(SystemConfigUpdateRequest req) {
        SystemConfig config = getOrCreateActiveConfig();

        if (req.getTechnicalWeight() != null) {
            config.setTechnicalWeight(req.getTechnicalWeight());
        }
        if (req.getQuestionnaireWeight() != null) {
            config.setQuestionnaireWeight(req.getQuestionnaireWeight());
        }
        if (req.getEssentialSkillPenalty() != null) {
            config.setEssentialSkillPenalty(req.getEssentialSkillPenalty());
        }
        if (req.getMinimumMatchThreshold() != null) {
            config.setMinimumMatchThreshold(req.getMinimumMatchThreshold());
        }

        SystemConfig saved = systemConfigRepository.save(config);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeCareers", careerRepository.countByIsActiveTrue());
        stats.put("activeSkills", skillRepository.countByIsActiveTrue());
        stats.put("activeQuestions", questionRepository.countByIsActiveTrue());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalRoadmaps", roadmapRepository.count());
        stats.put("totalAiLogs", aiGenerationLogRepository.count());
        stats.put("scoringVersion", "v2.4");
        return stats;
    }

    private SystemConfig getOrCreateActiveConfig() {
        return systemConfigRepository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseGet(() -> systemConfigRepository.save(SystemConfig.builder()
                        .id(UUID.randomUUID().toString())
                        .technicalWeight(new BigDecimal("0.500"))
                        .questionnaireWeight(new BigDecimal("0.350"))
                        .essentialSkillPenalty(new BigDecimal("0.150"))
                        .minimumMatchThreshold(40)
                        .isActive(true)
                        .build()));
    }

    private SystemConfigResponse toResponse(SystemConfig config) {
        return SystemConfigResponse.builder()
                .technicalWeight(config.getTechnicalWeight())
                .questionnaireWeight(config.getQuestionnaireWeight())
                .essentialSkillPenalty(config.getEssentialSkillPenalty())
                .minimumMatchThreshold(config.getMinimumMatchThreshold())
                .build();
    }
}
