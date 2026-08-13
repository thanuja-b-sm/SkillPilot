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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class Phase17UserIntelligenceRoadmapValidationTest {

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

    private User personaUser;
    private User attackerUser;
    private List<Career> testCareers;

    @BeforeEach
    void setUp() {
        personaUser = userRepository.save(User.builder()
                .id("validation-persona-user-id")
                .name("Persona Validation Candidate")
                .email("persona.val@example.com")
                .passwordHash("hashed")
                .build());

        attackerUser = userRepository.save(User.builder()
                .id("validation-attacker-user-id")
                .name("Attacker User")
                .email("attacker@example.com")
                .passwordHash("hashed")
                .build());

        testCareers = careerRepository.findByIsActiveTrue();
        assertTrue(testCareers.size() >= 5, "At least 5 active careers must exist for validation");
    }

    @Test
    @DisplayName("1. Profile Persistence & Completeness Influence Validation")
    void testProfilePersistenceAndCompleteness() {
        // Step A: Incomplete Profile
        UserProfileResponse initialProfile = userProfileService.getUserProfile(personaUser.getId());
        int baseCompleteness = initialProfile.getCompletionPercentage();

        // Step B: Complete Profile with Education, Experience, Location, and Preferences
        ProfileUpdateRequest fullReq = ProfileUpdateRequest.builder()
                .name("Persona Candidate")
                .title("Software Engineer")
                .institutionName("MIT")
                .degreeLevel("Bachelor's Degree")
                .majorFieldOfStudy("Computer Science")
                .graduationYear(2024)
                .educationStatus("Completed / Graduated")
                .employmentStatus("Employed Full-Time")
                .currentJobTitle("Software Developer")
                .currentIndustry("Technology")
                .experienceYears(3)
                .relevantExperienceYears(3)
                .location("Boston, MA")
                .country("United States")
                .preferredWorkMode("Hybrid")
                .preferredEmploymentType("Full-Time")
                .careerGoal("Become Tech Lead")
                .weeklyHoursAvailable(15)
                .preferredLearningPace("Accelerated")
                .preferredRoadmapDuration(6)
                .build();

        UserProfileResponse updatedProfile = userProfileService.updateUserProfile(personaUser.getId(), fullReq);

        assertNotNull(updatedProfile);
        assertEquals("MIT", updatedProfile.getInstitutionName());
        assertEquals("Computer Science", updatedProfile.getMajorFieldOfStudy());
        assertEquals(3, updatedProfile.getExperienceYears());
        assertTrue(updatedProfile.getCompletionPercentage() > baseCompleteness, "Completeness must increase after adding intelligence fields");

        // Step C: Retrieve & Verify Persistence
        UserProfileResponse fetched = userProfileService.getUserProfile(personaUser.getId());
        assertEquals("Boston, MA", fetched.getLocation());
        assertEquals("Hybrid", fetched.getPreferredWorkMode());
    }

    @Test
    @DisplayName("2. Personas A-H Multi-Dimensional Readiness & Invariants Validation")
    void testPersonasReadinessValidation() {
        Career aiCareer = testCareers.stream()
                .filter(c -> c.getId().equalsIgnoreCase("ai-software-engineer") || c.getTitle().toLowerCase().contains("ai"))
                .findFirst().orElse(testCareers.get(0));

        // Persona A: Beginner / Zero-Skill
        userProfileService.updateUserProfile(personaUser.getId(), ProfileUpdateRequest.builder().relevantExperienceYears(0).build());
        SkillGapAnalysisResponse zeroGap = skillGapService.getSkillGapForCareer(personaUser.getId(), aiCareer.getId());
        assertTrue(zeroGap.getSkillReadiness() <= 20, "Zero-skill user skill readiness must be low");
        assertTrue(zeroGap.getOverallReadiness() <= 35, "Zero-skill user overall readiness must be low");

        // Persona B: Experienced (5 yrs) but Zero Skill Rating
        userProfileService.updateUserProfile(personaUser.getId(), ProfileUpdateRequest.builder().relevantExperienceYears(5).build());
        SkillGapAnalysisResponse expNoSkillGap = skillGapService.getSkillGapForCareer(personaUser.getId(), aiCareer.getId());
        assertEquals(100, expNoSkillGap.getExperienceAlignment(), "5 yrs experience gives 100% experience alignment");
        assertTrue(expNoSkillGap.getSkillReadiness() <= 20, "Experience must NOT artificially boost raw skill readiness");
        assertTrue(expNoSkillGap.getMissingSkills().size() > 0, "Experience must NOT hide genuine skill gaps");

        // Persona C: Relevant CS Education Alignment
        userProfileService.updateUserProfile(personaUser.getId(), ProfileUpdateRequest.builder().majorFieldOfStudy("Computer Science").build());
        SkillGapAnalysisResponse eduGap = skillGapService.getSkillGapForCareer(personaUser.getId(), aiCareer.getId());
        assertEquals(90, eduGap.getEducationAlignment(), "CS major for AI career gives 90% education alignment");

        // Invariant: Determinism (10 consecutive calls yield identical readiness scores)
        for (int i = 0; i < 10; i++) {
            SkillGapAnalysisResponse r = skillGapService.getSkillGapForCareer(personaUser.getId(), aiCareer.getId());
            assertEquals(eduGap.getOverallReadiness(), r.getOverallReadiness());
        }
    }

    @Test
    @DisplayName("3. Gap Quality Across 5 Real Careers")
    void testGapQualityAcrossRealCareers() {
        int evaluatedCareers = 0;
        for (Career career : testCareers) {
            if (evaluatedCareers >= 5) break;

            SkillGapAnalysisResponse gap = skillGapService.getSkillGapForCareer(personaUser.getId(), career.getId());
            assertNotNull(gap);
            assertNotNull(gap.getSkills());
            assertNotNull(gap.getMissingSkills());

            gap.getSkills().forEach(item -> {
                assertNotNull(item.getSkillId());
                assertNotNull(item.getSeverity());
                assertNotNull(item.getRecommendedAction());
                assertTrue(item.getGapAmount() >= 0);
            });

            evaluatedCareers++;
        }
        assertEquals(5, evaluatedCareers, "Must evaluate gap quality for 5 distinct careers");
    }

    @Test
    @DisplayName("4. Roadmap Duration Strategy Validation (3, 6, and 12 Months)")
    void testRoadmapDurationStrategyValidation() {
        Career targetCareer = testCareers.get(0);
        userTargetCareerRepository.save(UserTargetCareer.builder()
                .id("utc-val-1")
                .user(personaUser)
                .career(targetCareer)
                .build());

        CareerRoadmapResponse rm3 = roadmapService.generateAndPersistRoadmap(personaUser.getId(), 3);
        assertEquals(3, rm3.getDurationMonths());
        assertEquals(3, rm3.getPhases().size());
        assertTrue(rm3.getOverallTimeline().contains("3 Months"));
        assertTrue(rm3.getPhases().get(0).getPhaseTitle().contains("Phase 1: Urgent"), "3-month strategy prioritizes urgent gaps");

        CareerRoadmapResponse rm6 = roadmapService.generateAndPersistRoadmap(personaUser.getId(), 6);
        assertEquals(6, rm6.getDurationMonths());
        assertEquals(4, rm6.getPhases().size());
        assertTrue(rm6.getOverallTimeline().contains("6 Months"));

        CareerRoadmapResponse rm12 = roadmapService.generateAndPersistRoadmap(personaUser.getId(), 12);
        assertEquals(12, rm12.getDurationMonths());
        assertEquals(5, rm12.getPhases().size());
        assertTrue(rm12.getOverallTimeline().contains("12 Months"));
        assertTrue(rm12.getPhases().get(4).getPhaseTitle().contains("Phase 5: Technical Interview"), "12-month strategy includes executive positioning");
    }

    @Test
    @DisplayName("5. Milestone Progress Persistence & Tracking")
    void testMilestoneProgressPersistence() {
        Career targetCareer = testCareers.get(0);
        userTargetCareerRepository.save(UserTargetCareer.builder()
                .id("utc-val-2")
                .user(personaUser)
                .career(targetCareer)
                .build());

        CareerRoadmapResponse rm = roadmapService.generateAndPersistRoadmap(personaUser.getId(), 6);
        String milestoneId = rm.getPhases().get(0).getId();

        MilestoneProgressUpdateRequest req = MilestoneProgressUpdateRequest.builder()
                .status("in_progress")
                .completionPercentage(50)
                .notes("Completed introductory modules")
                .build();

        RoadmapMilestoneResponse updated = roadmapService.updateMilestoneProgress(personaUser.getId(), rm.getId(), milestoneId, req);

        assertNotNull(updated);
        assertEquals("in_progress", updated.getStatus());
        assertEquals(50, updated.getCompletionPercentage());
        assertEquals("Completed introductory modules", updated.getNotes());

        // Re-read roadmap to confirm persistence
        CareerRoadmapResponse fetched = roadmapService.getRoadmapForUser(personaUser.getId());
        assertEquals("in_progress", fetched.getPhases().get(0).getStatus());
        assertEquals(50, fetched.getPhases().get(0).getCompletionPercentage());
    }

    @Test
    @DisplayName("6. Regeneration Safety & Stale State Validation")
    void testRegenerationSafetyAndStaleState() {
        Career targetCareer = testCareers.get(0);
        userTargetCareerRepository.save(UserTargetCareer.builder()
                .id("utc-val-3")
                .user(personaUser)
                .career(targetCareer)
                .build());

        CareerRoadmapResponse initialRm = roadmapService.generateAndPersistRoadmap(personaUser.getId(), 6);
        assertFalse(initialRm.getIsStale(), "Initial roadmap must not be stale");

        // Mark milestone 1 completed
        String msId = initialRm.getPhases().get(0).getId();
        roadmapService.updateMilestoneProgress(personaUser.getId(), initialRm.getId(), msId,
                MilestoneProgressUpdateRequest.builder().status("completed").completionPercentage(100).notes("Preserved notes").build());

        // Trigger profile update AFTER roadmap creation
        userProfileService.updateUserProfile(personaUser.getId(), ProfileUpdateRequest.builder().title("Updated Senior Lead").build());

        // Check stale detection
        CareerRoadmapResponse staleCheck = roadmapService.getRoadmapForUser(personaUser.getId());
        assertTrue(staleCheck.getIsStale(), "Roadmap should be flagged stale after profile update");

        // Regenerate roadmap
        CareerRoadmapResponse regeneratedRm = roadmapService.generateAndPersistRoadmap(personaUser.getId(), 6);
        assertFalse(regeneratedRm.getIsStale(), "Regenerated roadmap should clear stale flag");
        assertEquals("completed", regeneratedRm.getPhases().get(0).getStatus(), "Completed status preserved across regeneration");
        assertEquals("Preserved notes", regeneratedRm.getPhases().get(0).getNotes(), "Milestone notes preserved across regeneration");
    }

    @Test
    @DisplayName("7. Security & User Isolation Validation")
    void testSecurityUserIsolation() {
        Career targetCareer = testCareers.get(0);
        userTargetCareerRepository.save(UserTargetCareer.builder()
                .id("utc-val-4")
                .user(personaUser)
                .career(targetCareer)
                .build());

        CareerRoadmapResponse rm = roadmapService.generateAndPersistRoadmap(personaUser.getId(), 6);

        // Attacker attempts to read User A's roadmap by ID
        assertThrows(ForbiddenException.class, () -> {
            roadmapService.getRoadmapById(attackerUser.getId(), rm.getId());
        });

        // Attacker attempts to update User A's milestone progress
        assertThrows(ForbiddenException.class, () -> {
            roadmapService.updateMilestoneProgress(attackerUser.getId(), rm.getId(), rm.getPhases().get(0).getId(),
                    MilestoneProgressUpdateRequest.builder().status("completed").build());
        });
    }
}
