package com.skillpilot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.dto.response.RoadmapMilestoneResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.dto.response.SkillGapItemResponse;
import com.skillpilot.entity.*;
import com.skillpilot.exception.BadRequestException;
import com.skillpilot.exception.ForbiddenException;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final UserTargetCareerRepository userTargetCareerRepository;
    private final RoadmapTemplateRepository roadmapTemplateRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapMilestoneRepository roadmapMilestoneRepository;
    private final UserRepository userRepository;
    private final SkillGapService skillGapService;
    private final RoadmapGenerationEngine roadmapGenerationEngine;
    private final ObjectMapper objectMapper;

    @Transactional
    public CareerRoadmapResponse generateAndPersistRoadmap(String userId, Integer durationMonths) {
        int duration = durationMonths != null ? durationMonths : 6;
        if (duration < 6 || duration > 12) {
            throw new BadRequestException("Roadmap duration must be between 6 and 12 months.");
        }

        UserTargetCareer targetCareer = userTargetCareerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TargetCareer", "userId", userId));

        Career career = targetCareer.getCareer();
        if (!Boolean.TRUE.equals(career.getIsActive())) {
            throw new BadRequestException("Target career is inactive and roadmap cannot be generated.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        SkillGapAnalysisResponse gapAnalysis = skillGapService.getSkillGapForTargetCareer(userId);
        RoadmapTemplate template = roadmapTemplateRepository.findByCareerId(career.getId()).orElse(null);

        CareerRoadmapResponse generatedResponse = roadmapGenerationEngine.generateRoadmap(career, gapAnalysis, template, duration);

        Roadmap roadmap = roadmapRepository.findByUserIdAndCareerId(userId, career.getId())
                .orElseGet(() -> Roadmap.builder()
                        .id(UUID.randomUUID().toString())
                        .user(user)
                        .career(career)
                        .build());

        roadmap.setOverallTimeline(generatedResponse.getOverallTimeline());
        roadmap.setOverallReadiness(generatedResponse.getOverallReadiness());
        roadmap.setAiExplanation(generatedResponse.getAiExplanation());
        roadmap.setStatus("active");

        if (roadmap.getPhases() == null) {
            roadmap.setPhases(new ArrayList<>());
        } else {
            roadmap.getPhases().clear();
        }

        int order = 1;
        for (RoadmapMilestoneResponse mr : generatedResponse.getPhases()) {
            String goalsJson;
            String coursesJson;
            try {
                goalsJson = objectMapper.writeValueAsString(mr.getGoals());
                coursesJson = objectMapper.writeValueAsString(mr.getRecommendedCourses());
            } catch (Exception e) {
                goalsJson = "[]";
                coursesJson = "[]";
            }

            RoadmapMilestone ms = RoadmapMilestone.builder()
                    .id(UUID.randomUUID().toString())
                    .roadmap(roadmap)
                    .phaseOrder(order++)
                    .monthRange(mr.getMonthRange())
                    .phaseTitle(mr.getPhaseTitle())
                    .focusArea(mr.getFocusArea())
                    .expectedOutcome(mr.getExpectedOutcome())
                    .goals(goalsJson)
                    .recommendedCourses(coursesJson)
                    .status(mr.getStatus() != null ? mr.getStatus() : "not_started")
                    .build();

            roadmap.getPhases().add(ms);
        }

        Roadmap saved = roadmapRepository.save(roadmap);

        return toResponse(saved);
    }

    @Transactional
    public CareerRoadmapResponse getRoadmapForUser(String userId) {
        UserTargetCareer targetCareer = userTargetCareerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TargetCareer", "userId", userId));

        Optional<Roadmap> existing = roadmapRepository.findByUserIdAndCareerId(userId, targetCareer.getCareer().getId());
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        return generateAndPersistRoadmap(userId, 6);
    }

    @Transactional(readOnly = true)
    public CareerRoadmapResponse getRoadmapById(String userId, String roadmapId) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", "id", roadmapId));

        if (!roadmap.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to view this roadmap.");
        }

        return toResponse(roadmap);
    }

    private CareerRoadmapResponse toResponse(Roadmap roadmap) {
        List<RoadmapMilestoneResponse> milestoneResponses = new ArrayList<>();
        if (roadmap.getPhases() != null) {
            for (RoadmapMilestone ms : roadmap.getPhases()) {
                List<String> goals;
                List<String> courses;
                try {
                    goals = objectMapper.readValue(ms.getGoals(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                    courses = objectMapper.readValue(ms.getRecommendedCourses(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                } catch (Exception e) {
                    goals = Collections.emptyList();
                    courses = Collections.emptyList();
                }

                milestoneResponses.add(RoadmapMilestoneResponse.builder()
                        .id(ms.getId())
                        .monthRange(ms.getMonthRange())
                        .phaseTitle(ms.getPhaseTitle())
                        .focusArea(ms.getFocusArea())
                        .goals(goals)
                        .expectedOutcome(ms.getExpectedOutcome())
                        .recommendedCourses(courses)
                        .status(ms.getStatus())
                        .build());
            }
        }

        return CareerRoadmapResponse.builder()
                .id(roadmap.getId())
                .careerId(roadmap.getCareer().getId())
                .careerTitle(roadmap.getCareer().getTitle())
                .overallTimeline(roadmap.getOverallTimeline())
                .overallReadiness(roadmap.getOverallReadiness())
                .aiExplanation(roadmap.getAiExplanation())
                .phases(milestoneResponses)
                .build();
    }
}
