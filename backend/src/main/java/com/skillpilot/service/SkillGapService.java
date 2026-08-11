package com.skillpilot.service;

import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.UserSkill;
import com.skillpilot.entity.UserTargetCareer;
import com.skillpilot.exception.BadRequestException;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.UserSkillRepository;
import com.skillpilot.repository.UserTargetCareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillGapService {

    private final UserTargetCareerRepository userTargetCareerRepository;
    private final CareerRepository careerRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillGapAnalysisEngine skillGapAnalysisEngine;

    @Transactional(readOnly = true)
    public SkillGapAnalysisResponse getSkillGapForTargetCareer(String userId) {
        UserTargetCareer targetCareer = userTargetCareerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TargetCareer", "userId", userId));

        return computeAnalysis(userId, targetCareer.getCareer());
    }

    @Transactional(readOnly = true)
    public SkillGapAnalysisResponse getSkillGapForCareer(String userId, String careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new ResourceNotFoundException("Career", "id", careerId));

        if (!Boolean.TRUE.equals(career.getIsActive())) {
            throw new BadRequestException("Requested career is inactive.");
        }

        return computeAnalysis(userId, career);
    }

    private SkillGapAnalysisResponse computeAnalysis(String userId, Career career) {
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        Map<String, Integer> userSkillMap = userSkills.stream()
                .collect(Collectors.toMap(
                        us -> us.getSkill() != null ? us.getSkill().getId() : "",
                        UserSkill::getLevel,
                        (existing, replacement) -> Math.max(existing, replacement)
                ));

        return skillGapAnalysisEngine.analyze(career, userSkillMap);
    }
}
