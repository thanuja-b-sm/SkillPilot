package com.skillpilot;

import com.skillpilot.dto.request.MilestoneProgressUpdateRequest;
import com.skillpilot.dto.request.ProfileUpdateRequest;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.dto.response.RoadmapMilestoneResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.dto.response.UserProfileResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserTargetCareer;
import com.skillpilot.exception.ForbiddenException;

import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.repository.UserTargetCareerRepository;
import com.skillpilot.service.RoadmapService;
import com.skillpilot.service.SkillGapService;
import com.skillpilot.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class Phase16UserIntelligenceAndRoadmapOverhaulTest {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private UserTargetCareerRepository userTargetCareerRepository;

    private User testUser;
    private User secondUser;
    private Career testCareer;

    @BeforeEach
    void setUp() {
        testUser = userRepository.findAll().stream().findFirst().orElseThrow();

        secondUser = userRepository.save(User.builder()
                .id("test-user-b-uuid")
                .name("Second Test User")
                .email("userb@example.com")
                .passwordHash("hashed")
                .build());

        testCareer = careerRepository.findByIsActiveTrue().stream().findFirst().orElseThrow();

        userTargetCareerRepository.save(UserTargetCareer.builder()
                .id("utc-test-1")
                .user(testUser)
                .career(testCareer)
                .build());
    }

    @Test
    @DisplayName("1. User Profile Intelligence Expansion & Completeness Calculation")
    void testProfileIntelligenceExpansion() {
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .name("Updated Test User")
                .title("Senior Systems Engineer")
                .institutionName("Stanford University")
                .degreeLevel("Master's Degree")
                .majorFieldOfStudy("Computer Science")
                .graduationYear(2025)
                .educationStatus("Completed / Graduated")
                .employmentStatus("Employed Full-Time")
                .currentJobTitle("Software Developer")
                .currentIndustry("Cloud Computing")
                .experienceYears(5)
                .relevantExperienceYears(4)
                .location("San Francisco, CA")
                .country("United States")
                .preferredWorkMode("Remote")
                .preferredEmploymentType("Full-Time")
                .careerGoal("Architect distributed systems")
                .weeklyHoursAvailable(15)
                .preferredLearningPace("Accelerated")
                .preferredRoadmapDuration(6)
                .build();

        UserProfileResponse response = userProfileService.updateUserProfile(testUser.getId(), request);

        assertNotNull(response);
        assertEquals("Updated Test User", response.getName());
        assertEquals("Stanford University", response.getInstitutionName());
        assertEquals("Computer Science", response.getMajorFieldOfStudy());
        assertEquals(5, response.getExperienceYears());
        assertEquals(4, response.getRelevantExperienceYears());
        assertEquals("Remote", response.getPreferredWorkMode());
        assertTrue(response.getCompletionPercentage() > 50, "Profile completeness should exceed 50%");
    }

    @Test
    @DisplayName("2. Multi-Dimensional Readiness & Experience-Aware Gap Analysis")
    void testExperienceAwareSkillGapAnalysis() {
        // Give testUser 4 years relevant experience
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .relevantExperienceYears(4)
                .majorFieldOfStudy("Computer Science")
                .build();
        userProfileService.updateUserProfile(testUser.getId(), request);

        SkillGapAnalysisResponse gap = skillGapService.getSkillGapForTargetCareer(testUser.getId());

        assertNotNull(gap);
        assertNotNull(gap.getSkillReadiness());
        assertNotNull(gap.getExperienceAlignment());
        assertNotNull(gap.getEducationAlignment());
        assertNotNull(gap.getOverallReadiness());
        assertTrue(gap.getOverallReadiness() >= 0 && gap.getOverallReadiness() <= 100);

        // Verify experience-supported classification logic runs without error
        assertNotNull(gap.getSkills());
    }

    @Test
    @DisplayName("3. Duration-Aware Roadmap Generation (3, 6, and 12 Months)")
    void testDurationAwareRoadmapGeneration() {
        CareerRoadmapResponse rm3 = roadmapService.generateAndPersistRoadmap(testUser.getId(), 3);
        assertNotNull(rm3);
        assertEquals(3, rm3.getDurationMonths());
        assertEquals(3, rm3.getPhases().size(), "3-month roadmap must have 3 phases");

        CareerRoadmapResponse rm6 = roadmapService.generateAndPersistRoadmap(testUser.getId(), 6);
        assertNotNull(rm6);
        assertEquals(6, rm6.getDurationMonths());
        assertEquals(4, rm6.getPhases().size(), "6-month roadmap must have 4 phases");

        CareerRoadmapResponse rm12 = roadmapService.generateAndPersistRoadmap(testUser.getId(), 12);
        assertNotNull(rm12);
        assertEquals(12, rm12.getDurationMonths());
        assertEquals(5, rm12.getPhases().size(), "12-month roadmap must have 5 phases");
    }

    @Test
    @DisplayName("4. Roadmap Milestone Progress Tracking Persistence")
    void testRoadmapMilestoneProgressPersistence() {
        CareerRoadmapResponse rm = roadmapService.generateAndPersistRoadmap(testUser.getId(), 6);
        String milestoneId = rm.getPhases().get(0).getId();

        MilestoneProgressUpdateRequest updateReq = MilestoneProgressUpdateRequest.builder()
                .status("completed")
                .completionPercentage(100)
                .notes("Mastered Spring Boot dependency injection and REST annotations")
                .build();

        RoadmapMilestoneResponse updatedMs = roadmapService.updateMilestoneProgress(
                testUser.getId(), rm.getId(), milestoneId, updateReq);

        assertNotNull(updatedMs);
        assertEquals("completed", updatedMs.getStatus());
        assertEquals(100, updatedMs.getCompletionPercentage());
        assertEquals("Mastered Spring Boot dependency injection and REST annotations", updatedMs.getNotes());

        // Verify retrieval persists across reads
        CareerRoadmapResponse retrieved = roadmapService.getRoadmapForUser(testUser.getId());
        assertEquals(1, retrieved.getCompletedMilestonesCount());
    }

    @Test
    @DisplayName("5. Roadmap Regeneration Safety & Progress Preservation")
    void testRoadmapRegenerationSafety() {
        CareerRoadmapResponse rmInitial = roadmapService.generateAndPersistRoadmap(testUser.getId(), 6);
        String milestoneId = rmInitial.getPhases().get(0).getId();

        roadmapService.updateMilestoneProgress(
                testUser.getId(), rmInitial.getId(), milestoneId,
                MilestoneProgressUpdateRequest.builder().status("completed").completionPercentage(100).notes("Preserved Note").build());

        // Regenerate roadmap for same user & target career
        CareerRoadmapResponse rmRegenerated = roadmapService.generateAndPersistRoadmap(testUser.getId(), 6);

        assertNotNull(rmRegenerated);
        // The first phase should retain completed status and notes
        assertEquals("completed", rmRegenerated.getPhases().get(0).getStatus());
        assertEquals("Preserved Note", rmRegenerated.getPhases().get(0).getNotes());
    }

    @Test
    @DisplayName("6. User Isolation & Security Enforcement")
    void testUserIsolationEnforcement() {
        CareerRoadmapResponse rmUserA = roadmapService.generateAndPersistRoadmap(testUser.getId(), 6);
        String milestoneId = rmUserA.getPhases().get(0).getId();

        // Second user attempts to access or modify User A's roadmap
        assertThrows(ForbiddenException.class, () -> {
            roadmapService.getRoadmapById(secondUser.getId(), rmUserA.getId());
        });

        assertThrows(ForbiddenException.class, () -> {
            roadmapService.updateMilestoneProgress(
                    secondUser.getId(), rmUserA.getId(), milestoneId,
                    MilestoneProgressUpdateRequest.builder().status("completed").build());
        });
    }
}
