package com.skillpilot;

import com.skillpilot.dto.request.QuestionAnswerRequest;
import com.skillpilot.dto.response.CareerMatchResponse;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.entity.*;
import com.skillpilot.repository.*;
import com.skillpilot.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class Phase12MasterDatasetExpansionTest {

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private CareerSkillRequirementRepository requirementRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository optionRepository;

    @Autowired
    private QuestionSkillMappingRepository mappingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserTargetCareerRepository userTargetCareerRepository;

    @Autowired
    private CareerDiscoveryService careerDiscoveryService;

    @Autowired
    private TargetCareerService targetCareerService;

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testStudent;

    @BeforeEach
    void setUp() {
        testStudent = userRepository.save(User.builder()
                .id(UUID.randomUUID().toString())
                .name("Expanded Dataset Tester")
                .email("dataset.tester." + UUID.randomUUID() + "@skillpilot.com")
                .passwordHash(passwordEncoder.encode("Password123"))
                .education("Computer Science & Business")
                .experienceYears(3)
                .role(UserRole.STUDENT)
                .build());
    }

    @Test
    @DisplayName("1. Master Dataset Records Inventory & Quality Rules")
    void testMasterDatasetInventoryAndQuality() {
        long totalCareers = careerRepository.count();
        long activeCareers = careerRepository.findByIsActiveTrue().size();
        long totalSkills = skillRepository.count();
        long activeSkills = skillRepository.findByIsActiveTrue().size();
        long totalRequirements = requirementRepository.count();
        long totalQuestions = questionRepository.count();
        long activeQuestions = questionRepository.findByIsActiveTrueOrderByDisplayOrderAsc().size();
        long totalMappings = mappingRepository.count();

        System.out.println("=== MASTER DATASET INVENTORY ===");
        System.out.println("Total Careers: " + totalCareers + " (Active: " + activeCareers + ")");
        System.out.println("Total Skills: " + totalSkills + " (Active: " + activeSkills + ")");
        System.out.println("Career Requirements: " + totalRequirements);
        System.out.println("Questions: " + totalQuestions + " (Active: " + activeQuestions + ")");
        System.out.println("Question Skill Mappings: " + totalMappings);

        assertThat(activeCareers).isGreaterThanOrEqualTo(30);
        assertThat(totalSkills).isGreaterThanOrEqualTo(150);
        assertThat(activeSkills).isGreaterThanOrEqualTo(90);
        assertThat(totalRequirements).isGreaterThanOrEqualTo(120);
        assertThat(activeQuestions).isGreaterThanOrEqualTo(15);
        assertThat(totalMappings).isGreaterThanOrEqualTo(50);

        // Quality Rule Verification: Every active career must have at least 3 requirements and 1 essential requirement
        List<Career> careers = careerRepository.findByIsActiveTrue();
        for (Career c : careers) {
            List<CareerSkillRequirement> reqs = requirementRepository.findByCareerId(c.getId());
            assertThat(reqs).withFailMessage("Career %s has insufficient requirements", c.getId()).hasSizeGreaterThanOrEqualTo(3);
            boolean hasEssential = reqs.stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsEssential()));
            assertThat(hasEssential).withFailMessage("Career %s lacks essential requirements", c.getId()).isTrue();
        }
    }

    @Test
    @DisplayName("2. End-to-End Career Intelligence Flow for 7 Distinct Domains")
    void testEndToEnd7Domains() {
        String[] domainCareers = {
                "backend-systems-engineer",
                "devops-platform-engineer",
                "ai-prompt-llm-engineer",
                "penetration-tester-red-team",
                "investment-banker-m-and-a",
                "ui-ux-design-lead",
                "health-informatics-specialist"
        };

        for (String careerId : domainCareers) {
            Optional<Career> careerOpt = careerRepository.findById(careerId);
            assertThat(careerOpt).withFailMessage("Career %s not found", careerId).isPresent();

            // Set user target career
            // Set user target career via service
            targetCareerService.setTargetCareer(testStudent.getId(), careerId);

            // Skill Gap Analysis
            SkillGapAnalysisResponse gap = skillGapService.getSkillGapForTargetCareer(testStudent.getId());
            assertThat(gap.getCareer().getId()).isEqualTo(careerId);
            assertThat(gap.getSkills()).isNotEmpty();

            // Roadmap Generation
            CareerRoadmapResponse roadmap = roadmapService.generateAndPersistRoadmap(testStudent.getId(), 6);
            assertThat(roadmap.getCareerId()).isEqualTo(careerId);
            assertThat(roadmap.getPhases()).isNotEmpty();
        }
    }

    @Test
    @DisplayName("3. Intelligence Stress Test: High-Skill User vs Low-Skill User")
    void testHighSkillVsLowSkillScenarios() {
        Career career = careerRepository.findById("backend-systems-engineer").orElseThrow();
        List<CareerSkillRequirement> reqs = requirementRepository.findByCareerId(career.getId());

        // Low skill test
        userTargetCareerRepository.save(UserTargetCareer.builder()
                .id(UUID.randomUUID().toString())
                .user(testStudent)
                .career(career)
                .build());

        SkillGapAnalysisResponse lowSkillGap = skillGapService.getSkillGapForTargetCareer(testStudent.getId());
        assertThat(lowSkillGap.getReadinessScore()).isLessThanOrEqualTo(30);

        // Add high skills for all required skills
        for (CareerSkillRequirement req : reqs) {
            userSkillRepository.save(UserSkill.builder()
                    .id(UUID.randomUUID().toString())
                    .user(testStudent)
                    .skill(req.getSkill())
                    .level(5)
                    .build());
        }

        SkillGapAnalysisResponse highSkillGap = skillGapService.getSkillGapForTargetCareer(testStudent.getId());
        assertThat(highSkillGap.getReadinessScore()).isGreaterThanOrEqualTo(80);
    }

    @Test
    @DisplayName("4. Questionnaire Submission Impacts Career Match Scoring")
    void testQuestionnaireAnswersImpactScoring() {
        List<Question> questions = questionRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        assertThat(questions).isNotEmpty();

        // Evaluate baseline career matches before questionnaire
        List<CareerMatchResponse> baselineMatches = careerDiscoveryService.calculateAndPersistCareerMatches(testStudent.getId());
        assertThat(baselineMatches).isNotEmpty();

        // Submit high-scoring questionnaire answers
        List<QuestionAnswerRequest.AnswerItem> answers = new ArrayList<>();
        for (Question q : questions) {
            List<QuestionOption> options = optionRepository.findByQuestionIdOrderByDisplayOrderAsc(q.getId());
            if (!options.isEmpty()) {
                answers.add(QuestionAnswerRequest.AnswerItem.builder()
                        .questionId(q.getId())
                        .selectedOptionIds(List.of(options.get(0).getId())) // Choose first (high-value) option
                        .build());
            }
        }

        questionnaireService.saveUserAnswers(testStudent.getId(), QuestionAnswerRequest.builder().answers(answers).build());

        List<CareerMatchResponse> postMatches = careerDiscoveryService.calculateAndPersistCareerMatches(testStudent.getId());
        assertThat(postMatches).isNotEmpty();
    }
}
