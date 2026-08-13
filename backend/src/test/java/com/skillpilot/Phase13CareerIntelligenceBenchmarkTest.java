package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class Phase13CareerIntelligenceBenchmarkTest {

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
    private UserQuestionAnswerRepository userQuestionAnswerRepository;

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

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.builder()
                .id(UUID.randomUUID().toString())
                .name("Intelligence Benchmark User")
                .email("benchmark.user." + UUID.randomUUID() + "@skillpilot.com")
                .passwordHash(passwordEncoder.encode("Password123"))
                .education("Computer Science")
                .experienceYears(2)
                .role(UserRole.STUDENT)
                .build());
    }

    private void clearUserSkills() {
        List<UserSkill> userSkills = userSkillRepository.findByUserId(testUser.getId());
        userSkillRepository.deleteAll(userSkills);
    }

    @Test
    @DisplayName("1. Persona Match Differentiation & Domain Ranking Test")
    void testPersonaMatchDifferentiation() {
        Map<String, List<String>> personaSkills = Map.of(
                "SOFTWARE", List.of("java-spring", "csharp-dotnet", "react", "sql-db", "microservices-arch", "docker-k8s"),
                "DATA_AI", List.of("python", "python-pandas-numpy", "sql-advanced", "deep-learning", "rag-architecture", "apache-spark"),
                "DESIGN", List.of("figma-ui", "accessibility-wcag", "storybook-design-tokens", "communication"),
                "BUSINESS_FINANCE", List.of("corporate-valuation-dcf", "financial-statement-analysis", "mergers-acquisitions", "excel-financial-modeling", "risk-management")
        );

        System.out.println("\n=== 1. PERSONA MATCH DIFFERENTIATION BENCHMARK ===");

        for (Map.Entry<String, List<String>> entry : personaSkills.entrySet()) {
            String personaName = entry.getKey();
            List<String> skills = entry.getValue();

            clearUserSkills();

            for (String skillId : skills) {
                Optional<Skill> sOpt = skillRepository.findById(skillId);
                if (sOpt.isPresent()) {
                    userSkillRepository.save(UserSkill.builder()
                            .id(UUID.randomUUID().toString())
                            .user(testUser)
                            .skill(sOpt.get())
                            .level(5)
                            .build());
                }
            }

            List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
            assertThat(matches).isNotEmpty();

            // Find top match among careers that actually exceeded minimum threshold (if any) or top match
            CareerMatchResponse topMatch = matches.stream()
                    .filter(m -> m.getMatchScore() > 45)
                    .findFirst()
                    .orElse(matches.get(0));

            System.out.printf("Persona: %-16s -> Top Differentiated Match: %-35s (Score: %d%%, Category: %s)%n",
                    personaName, topMatch.getCareer().getTitle(), topMatch.getMatchScore(), topMatch.getCareer().getCategory());

            if (topMatch.getMatchScore() > 45) {
                if ("SOFTWARE".equals(personaName)) {
                    assertThat(topMatch.getCareer().getCategory()).containsAnyOf("Software Engineering & Architecture", "Infrastructure, DevOps & Cloud", "Software");
                } else if ("DATA_AI".equals(personaName)) {
                    assertThat(topMatch.getCareer().getCategory()).containsAnyOf("Data Engineering, Analytics & AI", "Data & AI", "Engineering", "Data & Analytics");
                } else if ("BUSINESS_FINANCE".equals(personaName)) {
                    assertThat(topMatch.getCareer().getCategory()).containsAnyOf("Finance & Banking", "Business & Consulting", "Management", "Business & Finance");
                }
            }
        }
    }

    @Test
    @DisplayName("2. Single Skill Level Step-Up Sensitivity (Test A)")
    void testSingleSkillLevelSensitivity() {
        System.out.println("\n=== 2. SINGLE SKILL LEVEL STEP-UP SENSITIVITY ===");
        clearUserSkills();

        Skill pythonSkill = skillRepository.findById("python").orElseThrow();
        int previousReadiness = 0;

        for (int level = 1; level <= 5; level++) {
            Optional<UserSkill> existing = userSkillRepository.findByUserIdAndSkillId(testUser.getId(), "python");
            if (existing.isPresent()) {
                UserSkill us = existing.get();
                us.setLevel(level);
                userSkillRepository.save(us);
            } else {
                userSkillRepository.save(UserSkill.builder()
                        .id(UUID.randomUUID().toString())
                        .user(testUser)
                        .skill(pythonSkill)
                        .level(level)
                        .build());
            }

            targetCareerService.setTargetCareer(testUser.getId(), "ai-prompt-llm-engineer");
            SkillGapAnalysisResponse gap = skillGapService.getSkillGapForTargetCareer(testUser.getId());
            List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
            CareerMatchResponse aiMlMatch = matches.stream()
                    .filter(m -> "ai-prompt-llm-engineer".equals(m.getCareer().getId()))
                    .findFirst()
                    .orElse(matches.get(0));

            System.out.printf("Python Skill Level %d -> Career '%s' Match Score: %d%% (Readiness: %d%%)%n",
                    level, aiMlMatch.getCareer().getTitle(), aiMlMatch.getMatchScore(), gap.getReadinessScore());

            assertThat(gap.getReadinessScore()).isGreaterThanOrEqualTo(previousReadiness);
            previousReadiness = gap.getReadinessScore();
        }
    }

    @Test
    @DisplayName("3. Essential Skill Sensitivity (Test B)")
    void testEssentialSkillSensitivity() {
        System.out.println("\n=== 3. ESSENTIAL SKILL SENSITIVITY BENCHMARK ===");
        clearUserSkills();

        Career career = careerRepository.findById("backend-systems-engineer").orElseThrow();
        targetCareerService.setTargetCareer(testUser.getId(), career.getId());

        List<CareerSkillRequirement> reqs = requirementRepository.findByCareerId(career.getId());

        SkillGapAnalysisResponse gapLow = skillGapService.getSkillGapForTargetCareer(testUser.getId());
        List<CareerMatchResponse> matchesLow = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        int scoreLow = matchesLow.stream().filter(m -> career.getId().equals(m.getCareer().getId())).findFirst().map(CareerMatchResponse::getMatchScore).orElse(0);

        CareerSkillRequirement essentialReq = reqs.stream().filter(r -> Boolean.TRUE.equals(r.getIsEssential())).findFirst().orElseThrow();
        userSkillRepository.save(UserSkill.builder()
                .id(UUID.randomUUID().toString())
                .user(testUser)
                .skill(essentialReq.getSkill())
                .level(5)
                .build());

        SkillGapAnalysisResponse gapHigh = skillGapService.getSkillGapForTargetCareer(testUser.getId());
        List<CareerMatchResponse> matchesHigh = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        int scoreHigh = matchesHigh.stream().filter(m -> career.getId().equals(m.getCareer().getId())).findFirst().map(CareerMatchResponse::getMatchScore).orElse(0);

        System.out.printf("Essential Skill ('%s') Level 0 -> Match: %d%%, Readiness: %d%%%n",
                essentialReq.getSkill().getName(), scoreLow, gapLow.getReadinessScore());
        System.out.printf("Essential Skill ('%s') Level 5 -> Match: %d%%, Readiness: %d%%%n",
                essentialReq.getSkill().getName(), scoreHigh, gapHigh.getReadinessScore());

        assertThat(gapHigh.getReadinessScore()).isGreaterThan(gapLow.getReadinessScore());
    }

    @Test
    @DisplayName("4. Questionnaire Answer Sensitivity (Test C)")
    void testQuestionnaireAnswerSensitivity() {
        System.out.println("\n=== 4. QUESTIONNAIRE ANSWER SENSITIVITY BENCHMARK ===");
        clearUserSkills();

        List<Question> questions = questionRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        assertThat(questions).isNotEmpty();

        List<CareerMatchResponse> baseMatches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        int baseScore = baseMatches.get(0).getMatchScore();

        List<QuestionAnswerRequest.AnswerItem> answersVar1 = new ArrayList<>();
        for (Question q : questions) {
            List<QuestionOption> opts = optionRepository.findByQuestionIdOrderByDisplayOrderAsc(q.getId());
            if (!opts.isEmpty()) {
                answersVar1.add(QuestionAnswerRequest.AnswerItem.builder()
                        .questionId(q.getId())
                        .selectedOptionIds(List.of(opts.get(0).getId()))
                        .build());
            }
        }
        questionnaireService.saveUserAnswers(testUser.getId(), QuestionAnswerRequest.builder().answers(answersVar1).build());

        List<CareerMatchResponse> matchesVar1 = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        int var1Score = matchesVar1.get(0).getMatchScore();

        System.out.printf("Baseline Score (No Answers): %d%% -> Questionnaire Variant 1 Score: %d%%%n", baseScore, var1Score);
        assertThat(var1Score).isGreaterThanOrEqualTo(baseScore);
    }

    @Test
    @DisplayName("5. Target Career Switching & Isolation (Test D)")
    void testTargetCareerSwitchingIsolation() {
        System.out.println("\n=== 5. TARGET CAREER SWITCHING BENCHMARK ===");

        targetCareerService.setTargetCareer(testUser.getId(), "backend-systems-engineer");
        SkillGapAnalysisResponse gapA = skillGapService.getSkillGapForTargetCareer(testUser.getId());
        CareerRoadmapResponse roadmapA = roadmapService.generateAndPersistRoadmap(testUser.getId(), 6);

        assertThat(gapA.getCareer().getId()).isEqualTo("backend-systems-engineer");
        assertThat(roadmapA.getCareerId()).isEqualTo("backend-systems-engineer");

        targetCareerService.setTargetCareer(testUser.getId(), "ai-prompt-llm-engineer");
        SkillGapAnalysisResponse gapB = skillGapService.getSkillGapForTargetCareer(testUser.getId());
        CareerRoadmapResponse roadmapB = roadmapService.generateAndPersistRoadmap(testUser.getId(), 6);

        assertThat(gapB.getCareer().getId()).isEqualTo("ai-prompt-llm-engineer");
        assertThat(roadmapB.getCareerId()).isEqualTo("ai-prompt-llm-engineer");

        System.out.printf("Switched Target Career: '%s' -> '%s' (Roadmap Phase 1 Focus: %s)%n",
                gapA.getCareer().getTitle(), gapB.getCareer().getTitle(), roadmapB.getPhases().get(0).getFocusArea());
    }

    @Test
    @DisplayName("6. 100% Deterministic Execution Test")
    void testDeterminism() {
        System.out.println("\n=== 6. DETERMINISM BENCHMARK ===");
        clearUserSkills();

        targetCareerService.setTargetCareer(testUser.getId(), "backend-systems-engineer");

        List<Integer> scores = new ArrayList<>();
        List<Integer> readinessScores = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
            SkillGapAnalysisResponse gap = skillGapService.getSkillGapForTargetCareer(testUser.getId());

            scores.add(matches.get(0).getMatchScore());
            readinessScores.add(gap.getReadinessScore());
        }

        System.out.println("5 Consecutive Match Scores: " + scores);
        System.out.println("5 Consecutive Readiness Scores: " + readinessScores);

        assertThat(new HashSet<>(scores)).hasSize(1);
        assertThat(new HashSet<>(readinessScores)).hasSize(1);
    }
}
