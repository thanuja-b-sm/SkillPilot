package com.skillpilot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.MilestoneProgressUpdateRequest;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.dto.response.RoadmapMilestoneResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.entity.*;
import com.skillpilot.exception.BadRequestException;
import com.skillpilot.exception.ForbiddenException;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final UserTargetCareerRepository userTargetCareerRepository;
    private final RoadmapTemplateRepository roadmapTemplateRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapMilestoneRepository roadmapMilestoneRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final SkillGapService skillGapService;
    private final RoadmapGenerationEngine roadmapGenerationEngine;
    private final ObjectMapper objectMapper;

    @Transactional
    public CareerRoadmapResponse generateAndPersistRoadmap(String userId, Integer durationMonths) {
        int duration = durationMonths != null ? durationMonths : 6;
        if (duration != 3 && duration != 6 && duration != 12) {
            throw new BadRequestException("Roadmap duration must be 3, 6, or 12 months.");
        }

        UserTargetCareer targetCareer = userTargetCareerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TargetCareer", "userId", userId));

        Career career = targetCareer.getCareer();
        if (!Boolean.TRUE.equals(career.getIsActive())) {
            throw new BadRequestException("Target career is inactive and roadmap cannot be generated.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Preserve previous milestone progress by targetSkillId / phaseOrder before clearing
        Optional<Roadmap> existingRoadmapOpt = roadmapRepository.findByUserIdAndCareerId(userId, career.getId());
        Map<String, RoadmapMilestone> previousSkillProgressMap = new HashMap<>();
        Map<Integer, RoadmapMilestone> previousOrderProgressMap = new HashMap<>();

        if (existingRoadmapOpt.isPresent()) {
            Roadmap oldRm = existingRoadmapOpt.get();
            if (oldRm.getPhases() != null) {
                for (RoadmapMilestone ms : oldRm.getPhases()) {
                    if (ms.getTargetSkillId() != null) {
                        previousSkillProgressMap.put(ms.getTargetSkillId(), ms);
                    }
                    previousOrderProgressMap.put(ms.getPhaseOrder(), ms);
                }
            }
        }

        SkillGapAnalysisResponse gapAnalysis = skillGapService.getSkillGapForTargetCareer(userId);
        RoadmapTemplate template = roadmapTemplateRepository.findByCareerId(career.getId()).orElse(null);

        CareerRoadmapResponse generatedResponse = roadmapGenerationEngine.generateRoadmap(career, gapAnalysis, template, duration);

        Roadmap roadmap = existingRoadmapOpt.orElseGet(() -> Roadmap.builder()
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

            // Restore progress if previous equivalent milestone existed
            RoadmapMilestone prevMs = mr.getTargetSkillId() != null ? previousSkillProgressMap.get(mr.getTargetSkillId()) : null;
            if (prevMs == null) {
                prevMs = previousOrderProgressMap.get(order);
            }

            String status = mr.getStatus() != null ? mr.getStatus() : "not_started";
            int completionPercentage = 0;
            String notes = null;
            LocalDateTime completedAt = null;

            if (prevMs != null) {
                status = prevMs.getStatus();
                completionPercentage = prevMs.getCompletionPercentage() != null ? prevMs.getCompletionPercentage() : 0;
                notes = prevMs.getNotes();
                completedAt = prevMs.getCompletedAt();
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
                    .status(status)
                    .completionPercentage(completionPercentage)
                    .targetSkillId(mr.getTargetSkillId())
                    .currentLevel(mr.getCurrentLevel())
                    .requiredLevel(mr.getRequiredLevel())
                    .gapSeverity(mr.getGapSeverity())
                    .notes(notes)
                    .completedAt(completedAt)
                    .build();

            roadmap.getPhases().add(ms);
        }

        Roadmap saved = roadmapRepository.save(roadmap);
        return toResponse(saved, false);
    }

    @Transactional
    public CareerRoadmapResponse getRoadmapForUser(String userId) {
        UserTargetCareer targetCareer = userTargetCareerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TargetCareer", "userId", userId));

        Optional<Roadmap> existing = roadmapRepository.findByUserIdAndCareerId(userId, targetCareer.getCareer().getId());
        if (existing.isPresent()) {
            Roadmap r = existing.get();
            boolean isStale = checkIsStale(userId, r);
            return toResponse(r, isStale);
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

        boolean isStale = checkIsStale(userId, roadmap);
        return toResponse(roadmap, isStale);
    }

    @Transactional
    public RoadmapMilestoneResponse updateMilestoneProgress(String userId, String roadmapId, String milestoneId, MilestoneProgressUpdateRequest request) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", "id", roadmapId));

        if (!roadmap.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to modify this roadmap.");
        }

        RoadmapMilestone milestone = roadmapMilestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("RoadmapMilestone", "id", milestoneId));

        if (!milestone.getRoadmap().getId().equals(roadmapId)) {
            throw new BadRequestException("Milestone does not belong to the specified roadmap.");
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            milestone.setStatus(request.getStatus().trim().toLowerCase());
            if ("completed".equalsIgnoreCase(milestone.getStatus())) {
                milestone.setCompletionPercentage(100);
                milestone.setCompletedAt(LocalDateTime.now());
            } else if ("not_started".equalsIgnoreCase(milestone.getStatus())) {
                milestone.setCompletionPercentage(0);
                milestone.setCompletedAt(null);
            }
        }

        if (request.getCompletionPercentage() != null) {
            milestone.setCompletionPercentage(Math.min(100, Math.max(0, request.getCompletionPercentage())));
            if (milestone.getCompletionPercentage() == 100) {
                milestone.setStatus("completed");
                milestone.setCompletedAt(LocalDateTime.now());
            } else if (milestone.getCompletionPercentage() > 0 && "not_started".equalsIgnoreCase(milestone.getStatus())) {
                milestone.setStatus("in_progress");
            }
        }

        if (request.getNotes() != null) {
            milestone.setNotes(request.getNotes().trim());
        }

        RoadmapMilestone saved = roadmapMilestoneRepository.save(milestone);
        return toMilestoneResponse(saved);
    }

    private boolean checkIsStale(String userId, Roadmap roadmap) {
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        if (userSkills != null && !userSkills.isEmpty() && roadmap.getUpdatedAt() != null) {
            for (UserSkill us : userSkills) {
                if (us.getUpdatedAt() != null && us.getUpdatedAt().isAfter(roadmap.getUpdatedAt())) {
                    return true;
                }
            }
        }
        return false;
    }

    private CareerRoadmapResponse toResponse(Roadmap roadmap, boolean isStale) {
        List<RoadmapMilestoneResponse> milestoneResponses = new ArrayList<>();
        int completedCount = 0;

        if (roadmap.getPhases() != null) {
            for (RoadmapMilestone ms : roadmap.getPhases()) {
                RoadmapMilestoneResponse mr = toMilestoneResponse(ms);
                milestoneResponses.add(mr);
                if ("completed".equalsIgnoreCase(ms.getStatus())) {
                    completedCount++;
                }
            }
        }

        int duration = 6;
        if (roadmap.getOverallTimeline() != null && roadmap.getOverallTimeline().contains("3 Months")) {
            duration = 3;
        } else if (roadmap.getOverallTimeline() != null && roadmap.getOverallTimeline().contains("12 Months")) {
            duration = 12;
        }

        return CareerRoadmapResponse.builder()
                .id(roadmap.getId())
                .careerId(roadmap.getCareer().getId())
                .careerTitle(roadmap.getCareer().getTitle())
                .overallTimeline(roadmap.getOverallTimeline())
                .durationMonths(duration)
                .overallReadiness(roadmap.getOverallReadiness())
                .completedMilestonesCount(completedCount)
                .totalMilestonesCount(milestoneResponses.size())
                .isStale(isStale)
                .status(roadmap.getStatus())
                .aiExplanation(roadmap.getAiExplanation())
                .phases(milestoneResponses)
                .build();
    }

    private RoadmapMilestoneResponse toMilestoneResponse(RoadmapMilestone ms) {
        List<String> goals;
        List<String> courses;
        try {
            goals = objectMapper.readValue(ms.getGoals(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
            courses = objectMapper.readValue(ms.getRecommendedCourses(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
        } catch (Exception e) {
            goals = Collections.emptyList();
            courses = Collections.emptyList();
        }

        return RoadmapMilestoneResponse.builder()
                .id(ms.getId())
                .phaseOrder(ms.getPhaseOrder())
                .monthRange(ms.getMonthRange())
                .phaseTitle(ms.getPhaseTitle())
                .focusArea(ms.getFocusArea())
                .goals(goals)
                .expectedOutcome(ms.getExpectedOutcome())
                .recommendedCourses(courses)
                .status(ms.getStatus())
                .completionPercentage(ms.getCompletionPercentage() != null ? ms.getCompletionPercentage() : 0)
                .targetSkillId(ms.getTargetSkillId())
                .currentLevel(ms.getCurrentLevel())
                .requiredLevel(ms.getRequiredLevel())
                .gapSeverity(ms.getGapSeverity())
                .notes(ms.getNotes())
                .completedAt(ms.getCompletedAt() != null ? ms.getCompletedAt().toString() : null)
                .build();
    }
}
