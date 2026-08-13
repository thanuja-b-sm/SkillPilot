package com.skillpilot;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class Phase15AlgorithmV2ValidationTest {

    @Autowired
    private CareerDiscoveryService careerDiscoveryService;

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private TargetCareerService targetCareerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserQuestionAnswerRepository userQuestionAnswerRepository;

    @Autowired
    private CareerMatchResultRepository careerMatchResultRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userQuestionAnswerRepository.deleteAllInBatch();
        careerMatchResultRepository.deleteAllInBatch();
        userSkillRepository.deleteAllInBatch();
        userSkillRepository.flush();

        testUser = userRepository.findByEmail("student@skillpilot.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .id(UUID.randomUUID().toString())
                        .email("persona-tester@skillpilot.com")
                        .name("Persona Tester")
                        .passwordHash("hashed")
                        .role(UserRole.STUDENT)
                        .build()));
    }

    private void setUserSkills(Map<String, Integer> skillsMap) {
        userSkillRepository.deleteAllInBatch();
        userSkillRepository.flush();
        for (Map.Entry<String, Integer> entry : skillsMap.entrySet()) {
            Skill skill = skillRepository.findById(entry.getKey()).orElseThrow(
                    () -> new IllegalArgumentException("Skill not found: " + entry.getKey())
            );
            userSkillRepository.save(UserSkill.builder()
                    .id(UUID.randomUUID().toString())
                    .user(testUser)
                    .skill(skill)
                    .level(entry.getValue())
                    .build());
        }
        userSkillRepository.flush();
    }

    @Test
    @DisplayName("Persona 1: Zero-skill Candidate — True 0% floor across all active careers")
    void testPersona1_ZeroSkillCandidate() {
        setUserSkills(Collections.emptyMap());

        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());

        assertThat(matches).hasSizeGreaterThanOrEqualTo(30);
        for (CareerMatchResponse match : matches) {
            assertEquals(0, match.getMatchScore(), "Zero-skill candidate must receive 0% match score");
            assertEquals(0, match.getReadinessScore(), "Zero-skill candidate must receive 0% readiness score");
            assertEquals(Boolean.FALSE, match.getIsRecommended(), "Zero-skill candidate must not be recommended");
        }
    }

    @Test
    @DisplayName("Persona 2: Software-focused Candidate — Ranks software careers above non-software careers")
    void testPersona2_SoftwareFocusedCandidate() {
        setUserSkills(Map.of(
                "java-spring", 5,
                "sql-db", 4,
                "system-design", 4,
                "microservices-arch", 4,
                "docker-k8s", 3,
                "git", 4
        ));

        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(matches).isNotEmpty();

        List<String> top5Titles = matches.stream().limit(5).map(m -> m.getCareer().getTitle()).toList();
        assertThat(top5Titles).anyMatch(t -> t.contains("Backend") || t.contains("Software") || t.contains("DevOps") || t.contains("SRE") || t.contains("Cloud"));

        CareerMatchResponse topMatch = matches.get(0);
        assertThat(topMatch.getMatchScore()).isGreaterThanOrEqualTo(60);
    }

    @Test
    @DisplayName("Persona 3: Data/AI-focused Candidate — Ranks AI and Data careers at the top")
    void testPersona3_DataAiFocusedCandidate() {
        setUserSkills(Map.of(
                "python", 5,
                "machine-learning", 5,
                "deep-learning", 4,
                "sql-advanced", 5,
                "bigquery-snowflake", 4,
                "apache-spark", 4,
                "nlp-spacy-huggingface", 5
        ));

        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(matches).isNotEmpty();

        CareerMatchResponse topMatch = matches.get(0);
        assertThat(topMatch.getCareer().getCategory()).containsAnyOf("Data & Artificial Intelligence", "Artificial Intelligence", "Data Engineering");
        assertThat(topMatch.getMatchScore()).isGreaterThanOrEqualTo(70);
    }

    @Test
    @DisplayName("Persona 5: Finance-focused Candidate — Ranks Banking & Finance careers at the top")
    void testPersona5_FinanceFocusedCandidate() {
        setUserSkills(Map.of(
                "financial-statement-analysis", 5,
                "corporate-valuation-dcf", 5,
                "mergers-acquisitions", 4,
                "excel-financial-modeling", 5,
                "corporate-taxation-audit", 4
        ));

        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(matches).isNotEmpty();

        CareerMatchResponse topMatch = matches.get(0);
        assertThat(topMatch.getCareer().getCategory()).containsAnyOf("Business & Finance", "Finance & Banking");
        assertThat(topMatch.getMatchScore()).isGreaterThanOrEqualTo(85);

        CareerMatchResponse softwareMatch = matches.stream()
                .filter(m -> m.getCareer().getTitle().contains("Backend"))
                .findFirst().orElseThrow();

        assertThat(softwareMatch.getMatchScore()).isLessThan(topMatch.getMatchScore());
    }

    @Test
    @DisplayName("Persona 6: Generalist Candidate — Balanced distribution without score flatline")
    void testPersona6_GeneralistCandidate() {
        setUserSkills(Map.of(
                "python", 2,
                "sql-db", 2,
                "communication", 3,
                "problem-solving", 3,
                "git", 3,
                "financial-statement-analysis", 2,
                "scrum-agile-framework", 2
        ));

        List<CareerMatchResponse> matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        assertThat(matches).isNotEmpty();

        List<Integer> scores = matches.stream().map(CareerMatchResponse::getMatchScore).toList();
        int maxScore = Collections.max(scores);
        int minScore = Collections.min(scores);

        assertThat(maxScore).isBetween(20, 65);
        assertThat(minScore).isLessThan(maxScore);
    }

    @Test
    @DisplayName("Persona 8: Perfect Candidate — Reaches true 100% Match Score and 100% Readiness")
    void testPersona8_PerfectCandidate() {
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
        assertEquals(100, topMatch.getMatchScore());
        assertEquals(100, topMatch.getReadinessScore());
        assertEquals(Boolean.TRUE, topMatch.getIsRecommended());
    }

    @Test
    @DisplayName("Invariant B: Monotonicity — Skill level increase from 1 to 5 never decreases match score")
    void testInvariantB_SkillProgressionMonotonicity() {
        setUserSkills(Map.of("java-spring", 1));
        List<CareerMatchResponse> level1Matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        int scoreL1 = level1Matches.stream().filter(m -> m.getCareer().getTitle().contains("Backend")).findFirst().orElseThrow().getMatchScore();

        setUserSkills(Map.of("java-spring", 5));
        List<CareerMatchResponse> level5Matches = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
        int scoreL5 = level5Matches.stream().filter(m -> m.getCareer().getTitle().contains("Backend")).findFirst().orElseThrow().getMatchScore();

        assertThat(scoreL5).isGreaterThanOrEqualTo(scoreL1);
    }

    @Test
    @DisplayName("Invariant G: Roadmap Consistency — Perfect user receives 0 missing essential skill gaps")
    void testInvariantG_RoadmapConsistency() {
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

        targetCareerService.setTargetCareer(testUser.getId(), "backend-systems-engineer");
        SkillGapAnalysisResponse gapAnalysis = skillGapService.getSkillGapForTargetCareer(testUser.getId());

        assertThat(gapAnalysis.getMissingSkills()).isEmpty();

        CareerRoadmapResponse roadmap = roadmapService.generateAndPersistRoadmap(testUser.getId(), 6);
        assertThat(roadmap).isNotNull();
        assertThat(roadmap.getPhases()).isNotEmpty();
    }

    @Test
    @DisplayName("Invariant H: Determinism — 100 consecutive runs produce 100% identical results")
    void testInvariantH_Determinism() {
        setUserSkills(Map.of("java-spring", 4, "sql-db", 3));

        List<CareerMatchResponse> firstRun = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());

        for (int i = 0; i < 100; i++) {
            List<CareerMatchResponse> currentRun = careerDiscoveryService.calculateAndPersistCareerMatches(testUser.getId());
            assertEquals(firstRun.size(), currentRun.size());
            assertEquals(firstRun.get(0).getMatchScore(), currentRun.get(0).getMatchScore());
            assertEquals(firstRun.get(0).getCareer().getId(), currentRun.get(0).getCareer().getId());
        }
    }
}
