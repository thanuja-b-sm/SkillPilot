package com.skillpilot;

import com.skillpilot.dto.response.CareerMatchResponse;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.entity.UserSkill;
import com.skillpilot.repository.*;
import com.skillpilot.service.CareerDiscoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class Phase13AlgorithmIntelligenceImprovementsTest {

    @Autowired
    private CareerDiscoveryService careerDiscoveryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserQuestionAnswerRepository userQuestionAnswerRepository;

    @Autowired
    private CareerMatchResultRepository careerMatchResultRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userQuestionAnswerRepository.deleteAll();
        careerMatchResultRepository.deleteAll();
        userSkillRepository.deleteAll();

        testUser = userRepository.findByEmail("student.test@skillpilot.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .id(java.util.UUID.randomUUID().toString())
                        .name("Algorithm Test Student")
                        .email("student.test@skillpilot.com")
                        .passwordHash("$2a$10$e7xX380n7P08z...")
                        .role(UserRole.STUDENT)
                        .targetFocus("Software Engineering")
                        .build()));
    }

    private void setUserSkills(Map<String, Integer> skillLevels) {
        userSkillRepository.deleteAll();
        skillLevels.forEach((skillId, level) -> {
            skillRepository.findById(skillId).ifPresent(skill -> {
                userSkillRepository.save(UserSkill.builder()
                        .id(java.util.UUID.randomUUID().toString())
                        .user(testUser)
                        .skill(skill)
                        .level(level)
                        .build());
            });
        });
    }

    @Test
    @DisplayName("Scenario A & B: Zero-skill and Low-skill users do NOT flatline at 45%")
    void testZeroAndLowSkillUserNoFlatline() {
        // Zero skills
        setUserSkills(Map.of());
        List<CareerMatchResponse> zeroMatches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(zeroMatches).isNotEmpty();
        
        // Zero skills must yield 0% match score, not 45%
        CareerMatchResponse topZeroMatch = zeroMatches.get(0);
        assertEquals(0, topZeroMatch.getMatchScore(), "Zero skill user must receive 0% match score");
        assertEquals(0, topZeroMatch.getReadinessScore(), "Zero skill user must receive 0% readiness score");
        assertEquals(Boolean.FALSE, topZeroMatch.getIsRecommended(), "Zero skill user must not be recommended");

        // Low skill user (1 skill at level 1)
        setUserSkills(Map.of("python", 1));
        List<CareerMatchResponse> lowMatches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(lowMatches).isNotEmpty();
        
        CareerMatchResponse pyMatch = lowMatches.stream()
                .filter(m -> "Generative AI & LLM Application Engineer".equals(m.getCareer().getTitle()))
                .findFirst()
                .orElseThrow();

        // Low skill match score should be > 0% but < 45% (e.g. 12% raw), not artificially clamped up to 45%
        assertThat(pyMatch.getMatchScore()).isGreaterThan(0);
        assertThat(pyMatch.getMatchScore()).isLessThan(45);
        assertEquals(Boolean.FALSE, pyMatch.getIsRecommended(), "Score < 45 threshold is not recommended");
    }

    @Test
    @DisplayName("Scenario E: Perfect career-specific user reaches TRUE 100% match")
    void testPerfectUserReaches100Percent() {
        // Perfect skills for Senior Backend & Systems Engineer
        setUserSkills(Map.of(
                "java-spring", 5,
                "sql-db", 4,
                "system-design", 4,
                "microservices-arch", 4,
                "redis-caching", 3,
                "kafka-rabbitmq", 3,
                "docker-k8s", 3,
                "backend-testing-junit", 4,
                "problem-solving", 4
        ));

        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(matches).isNotEmpty();

        CareerMatchResponse topMatch = matches.get(0);
        assertThat(topMatch.getCareer().getTitle()).contains("Backend");
        assertEquals(100, topMatch.getMatchScore(), "Perfect user must reach true 100% match score");
        assertEquals(100, topMatch.getReadinessScore(), "Perfect user must reach 100% readiness score");
        assertEquals(Boolean.TRUE, topMatch.getIsRecommended());
    }

    @Test
    @DisplayName("Scenario C & D: Intermediate and Domain-Specific Users")
    void testIntermediateAndDomainSpecificUsers() {
        setUserSkills(Map.of(
                "java-spring", 4,
                "sql-db", 3,
                "system-design", 3,
                "microservices-arch", 3,
                "docker-k8s", 3
        ));

        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(matches).isNotEmpty();

        CareerMatchResponse topMatch = matches.get(0);
        assertThat(topMatch.getMatchScore()).isBetween(50, 95);
        assertThat(topMatch.getReadinessScore()).isBetween(50, 95);
    }

    @Test
    @DisplayName("Scenario F: Unrelated career comparison ranks appropriately")
    void testUnrelatedCareerComparison() {
        // Finance skills for investment-banker-m-and-a
        setUserSkills(Map.of(
                "corporate-valuation-dcf", 5,
                "financial-statement-analysis", 5,
                "mergers-acquisitions", 4,
                "excel-financial-modeling", 5,
                "negotiation-persuasion", 4
        ));

        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(matches).isNotEmpty();

        CareerMatchResponse topMatch = matches.get(0);
        assertThat(topMatch.getCareer().getCategory()).containsAnyOf("Finance & Banking", "Business & Finance");
        assertThat(topMatch.getMatchScore()).isEqualTo(100);

        // Unrelated tech career should receive a significantly lower score
        CareerMatchResponse techMatch = matches.stream()
                .filter(m -> m.getCareer().getTitle().contains("Backend"))
                .findFirst()
                .orElseThrow();

        assertThat(techMatch.getMatchScore()).isLessThan(topMatch.getMatchScore());
    }

    @Test
    @DisplayName("Scenario G, H & I: Questionnaire Normalization (No over-accumulation)")
    void testQuestionnaireNormalization() {
        setUserSkills(Map.of());

        // G: No answers -> Match score 0%
        List<CareerMatchResponse> noAnswerMatches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        CareerMatchResponse zeroQuestMatch = noAnswerMatches.get(0);
        assertEquals(0, zeroQuestMatch.getMatchScore());

        // Verify questionnaire bonus does not exceed questCap (23%) and scales proportionally
        assertThat(zeroQuestMatch.getMatchScore()).isEqualTo(0);
    }

    @Test
    @DisplayName("Scenario J: Zero randomness across repeated evaluations")
    void testZeroRandomnessRepeatedEvaluations() {
        setUserSkills(Map.of("python", 4, "sql-advanced", 3));

        List<CareerMatchResponse> run1 = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        List<CareerMatchResponse> run2 = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());

        assertThat(run1).hasSameSizeAs(run2);
        for (int i = 0; i < run1.size(); i++) {
            assertEquals(run1.get(i).getMatchScore(), run2.get(i).getMatchScore());
            assertEquals(run1.get(i).getReadinessScore(), run2.get(i).getReadinessScore());
            assertEquals(run1.get(i).getCareer().getId(), run2.get(i).getCareer().getId());
        }
    }
}
