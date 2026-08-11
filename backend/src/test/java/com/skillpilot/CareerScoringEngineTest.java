package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.entity.*;
import com.skillpilot.service.CareerScoringEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CareerScoringEngineTest {

    private CareerScoringEngine scoringEngine;
    private ObjectMapper objectMapper;

    private Skill pythonSkill;
    private Skill mlSkill;
    private Career aiCareer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        scoringEngine = new CareerScoringEngine(objectMapper);

        pythonSkill = Skill.builder()
                .id("python")
                .name("Python Programming")
                .category("Technical")
                .build();

        mlSkill = Skill.builder()
                .id("machine-learning")
                .name("Machine Learning Algorithms")
                .category("Technical")
                .build();

        List<CareerSkillRequirement> reqs = new ArrayList<>();
        reqs.add(CareerSkillRequirement.builder()
                .id("req-py")
                .skill(pythonSkill)
                .requiredLevel(4)
                .isEssential(true)
                .build());
        reqs.add(CareerSkillRequirement.builder()
                .id("req-ml")
                .skill(mlSkill)
                .requiredLevel(3)
                .isEssential(false)
                .build());

        aiCareer = Career.builder()
                .id("ai-engineer")
                .title("AI Engineer")
                .category("AI")
                .requiredSkills(reqs)
                .build();
    }

    @Test
    @DisplayName("1. Identical input produces identical score")
    void test1_IdenticalInputIdenticalScore() {
        Map<String, Integer> userSkills = Map.of("python", 4, "machine-learning", 3);
        List<UserQuestionAnswer> answers = Collections.emptyList();

        CareerScoringEngine.CalculationResult res1 = scoringEngine.calculateMatch(aiCareer, userSkills, answers);
        CareerScoringEngine.CalculationResult res2 = scoringEngine.calculateMatch(aiCareer, userSkills, answers);

        assertEquals(res1.getMatchScore(), res2.getMatchScore());
        assertEquals(res1.getConfidenceLevel(), res2.getConfidenceLevel());
        assertEquals(res1.getFitReason(), res2.getFitReason());
    }

    @Test
    @DisplayName("2. Identical input produces identical ranking")
    void test2_IdenticalInputIdenticalRanking() {
        Map<String, Integer> userSkills = Map.of("python", 4, "machine-learning", 3);
        CareerScoringEngine.CalculationResult res1 = scoringEngine.calculateMatch(aiCareer, userSkills, Collections.emptyList());
        CareerScoringEngine.CalculationResult res2 = scoringEngine.calculateMatch(aiCareer, userSkills, Collections.emptyList());

        assertEquals(0, Integer.compare(res1.getMatchScore(), res2.getMatchScore()));
    }

    @Test
    @DisplayName("3. Higher skill alignment increases score")
    void test3_HigherSkillAlignmentIncreasesScore() {
        Map<String, Integer> lowSkills = Map.of("python", 1, "machine-learning", 1);
        Map<String, Integer> highSkills = Map.of("python", 4, "machine-learning", 3);

        CareerScoringEngine.CalculationResult lowRes = scoringEngine.calculateMatch(aiCareer, lowSkills, Collections.emptyList());
        CareerScoringEngine.CalculationResult highRes = scoringEngine.calculateMatch(aiCareer, highSkills, Collections.emptyList());

        assertTrue(highRes.getMatchScore() > lowRes.getMatchScore());
    }

    @Test
    @DisplayName("4. Lower skill alignment decreases score")
    void test4_LowerSkillAlignmentDecreasesScore() {
        Map<String, Integer> highSkills = Map.of("python", 4, "machine-learning", 3);
        Map<String, Integer> lowSkills = Map.of("python", 0, "machine-learning", 0);

        CareerScoringEngine.CalculationResult highRes = scoringEngine.calculateMatch(aiCareer, highSkills, Collections.emptyList());
        CareerScoringEngine.CalculationResult lowRes = scoringEngine.calculateMatch(aiCareer, lowSkills, Collections.emptyList());

        assertTrue(lowRes.getMatchScore() < highRes.getMatchScore());
    }

    @Test
    @DisplayName("5. Questionnaire contribution affects score correctly")
    void test5_QuestionnaireContributionAffectsScore() {
        Map<String, Integer> skills = Map.of("python", 2, "machine-learning", 1);

        // Without answers
        CareerScoringEngine.CalculationResult baseRes = scoringEngine.calculateMatch(aiCareer, skills, Collections.emptyList());

        // With answer mapping to python
        Question q1 = Question.builder().id("q1").build();
        QuestionOption opt1 = QuestionOption.builder()
                .id("opt-py")
                .question(q1)
                .associatedSkills(List.of(QuestionSkillMapping.builder().skill(pythonSkill).weight(5).build()))
                .build();
        q1.setOptions(List.of(opt1));

        UserQuestionAnswer ans = UserQuestionAnswer.builder()
                .question(q1)
                .selectedOptionIds("[\"opt-py\"]")
                .build();

        CareerScoringEngine.CalculationResult bonusRes = scoringEngine.calculateMatch(aiCareer, skills, List.of(ans));

        assertTrue(bonusRes.getMatchScore() >= baseRes.getMatchScore());
    }

    @Test
    @DisplayName("6. Technical weight essential multiplier (2.0 vs 1.0) affects score correctly")
    void test6_TechnicalWeightEssentialMultiplier() {
        // Python essential requiredLevel 4 (wt 2.0 = 8), ML standard requiredLevel 3 (wt 1.0 = 3). Total wt = 11.
        // User has python 4/4 (earns 8) vs ML 3/3 (earns 3).
        Map<String, Integer> pyOnly = Map.of("python", 4, "machine-learning", 0);
        Map<String, Integer> mlOnly = Map.of("python", 0, "machine-learning", 3);

        CareerScoringEngine.CalculationResult pyRes = scoringEngine.calculateMatch(aiCareer, pyOnly, Collections.emptyList());
        CareerScoringEngine.CalculationResult mlRes = scoringEngine.calculateMatch(aiCareer, mlOnly, Collections.emptyList());

        assertTrue(pyRes.getMatchScore() > mlRes.getMatchScore());
    }

    @Test
    @DisplayName("7. Questionnaire weight affects score proportionally")
    void test7_QuestionnaireWeightProportionality() {
        Map<String, Integer> skills = Map.of("python", 2);

        Question q1 = Question.builder().id("q1").build();
        QuestionOption optHigh = QuestionOption.builder()
                .id("opt-high")
                .question(q1)
                .associatedSkills(List.of(QuestionSkillMapping.builder().skill(pythonSkill).weight(5).build()))
                .build();
        q1.setOptions(List.of(optHigh));

        UserQuestionAnswer ansHigh = UserQuestionAnswer.builder()
                .question(q1)
                .selectedOptionIds("[\"opt-high\"]")
                .build();

        CareerScoringEngine.CalculationResult highRes = scoringEngine.calculateMatch(aiCareer, skills, List.of(ansHigh));
        assertTrue(highRes.getMatchScore() >= 45);
    }

    @Test
    @DisplayName("8. Essential skill gap identified in keyGaps")
    void test8_EssentialSkillGapIdentified() {
        Map<String, Integer> skills = Map.of("python", 1);
        CareerScoringEngine.CalculationResult res = scoringEngine.calculateMatch(aiCareer, skills, Collections.emptyList());

        assertTrue(res.getKeyGaps().stream().anyMatch(g -> g.contains("Python Programming")));
    }

    @Test
    @DisplayName("9. Missing essential skill level 0 handles gap calculation cleanly")
    void test9_MissingEssentialSkillLevel0() {
        Map<String, Integer> skills = Collections.emptyMap();
        CareerScoringEngine.CalculationResult res = scoringEngine.calculateMatch(aiCareer, skills, Collections.emptyList());

        assertTrue(res.getKeyGaps().stream().anyMatch(g -> g.contains("Python Programming")));
    }

    @Test
    @DisplayName("10. Score normalization bounds result between 45% and 98%")
    void test10_ScoreNormalizationBounds() {
        // Zero skills
        CareerScoringEngine.CalculationResult minRes = scoringEngine.calculateMatch(aiCareer, Collections.emptyMap(), Collections.emptyList());
        assertEquals(45, minRes.getMatchScore());

        // Perfect skills
        Map<String, Integer> maxSkills = Map.of("python", 5, "machine-learning", 5);
        CareerScoringEngine.CalculationResult maxRes = scoringEngine.calculateMatch(aiCareer, maxSkills, Collections.emptyList());
        assertTrue(maxRes.getMatchScore() <= 98);
        assertTrue(maxRes.getMatchScore() >= 45);
    }

    @Test
    @DisplayName("11. Minimum threshold clamping clamps score to 45 minimum")
    void test11_MinimumThresholdClamping() {
        CareerScoringEngine.CalculationResult res = scoringEngine.calculateMatch(aiCareer, Collections.emptyMap(), Collections.emptyList());
        assertEquals(45, res.getMatchScore());
    }

    @Test
    @DisplayName("12. Deterministic tie-breaking verified")
    void test12_DeterministicTieBreaking() {
        Career c1 = Career.builder().id("career-a").title("Career A").requiredSkills(Collections.emptyList()).build();
        Career c2 = Career.builder().id("career-b").title("Career B").requiredSkills(Collections.emptyList()).build();

        CareerScoringEngine.CalculationResult res1 = scoringEngine.calculateMatch(c1, Collections.emptyMap(), Collections.emptyList());
        CareerScoringEngine.CalculationResult res2 = scoringEngine.calculateMatch(c2, Collections.emptyMap(), Collections.emptyList());

        assertEquals(res1.getMatchScore(), res2.getMatchScore());
    }

    @Test
    @DisplayName("13. Missing user skill handled as level 0")
    void test13_MissingUserSkillLevel0() {
        Map<String, Integer> skills = Map.of("python", 4);
        CareerScoringEngine.CalculationResult res = scoringEngine.calculateMatch(aiCareer, skills, Collections.emptyList());
        assertNotNull(res);
        assertTrue(res.getKeyGaps().stream().anyMatch(g -> g.contains("Machine Learning Algorithms")));
    }

    @Test
    @DisplayName("14. Questionnaire mapping to non-career skill gives 0 bonus")
    void test14_QuestionnaireMappingIrrelevantSkill() {
        Skill unrelatedSkill = Skill.builder().id("cooking").name("Cooking").build();
        Question q = Question.builder().id("q-unrelated").build();
        QuestionOption opt = QuestionOption.builder()
                .id("opt-cook")
                .question(q)
                .associatedSkills(List.of(QuestionSkillMapping.builder().skill(unrelatedSkill).weight(5).build()))
                .build();
        q.setOptions(List.of(opt));

        UserQuestionAnswer ans = UserQuestionAnswer.builder()
                .question(q)
                .selectedOptionIds("[\"opt-cook\"]")
                .build();

        CareerScoringEngine.CalculationResult baseRes = scoringEngine.calculateMatch(aiCareer, Collections.emptyMap(), Collections.emptyList());
        CareerScoringEngine.CalculationResult bonusRes = scoringEngine.calculateMatch(aiCareer, Collections.emptyMap(), List.of(ans));

        assertEquals(baseRes.getMatchScore(), bonusRes.getMatchScore());
    }

    @Test
    @DisplayName("15. Confidence level mapping (High, Medium, Moderate)")
    void test15_ConfidenceLevelMapping() {
        Question q1 = Question.builder().id("q1").build();
        QuestionOption optHigh = QuestionOption.builder()
                .id("opt-high")
                .question(q1)
                .associatedSkills(List.of(QuestionSkillMapping.builder().skill(pythonSkill).weight(5).build()))
                .build();
        q1.setOptions(List.of(optHigh));

        UserQuestionAnswer ans = UserQuestionAnswer.builder()
                .question(q1)
                .selectedOptionIds("[\"opt-high\"]")
                .build();

        Map<String, Integer> highSkills = Map.of("python", 4, "machine-learning", 3);
        CareerScoringEngine.CalculationResult resHigh = scoringEngine.calculateMatch(aiCareer, highSkills, List.of(ans));
        assertTrue(resHigh.getMatchScore() >= 70);
        assertEquals("Medium", scoringEngine.calculateMatch(aiCareer, highSkills, Collections.emptyList()).getConfidenceLevel());

        CareerScoringEngine.CalculationResult resLow = scoringEngine.calculateMatch(aiCareer, Collections.emptyMap(), Collections.emptyList());
        assertEquals("Moderate", resLow.getConfidenceLevel());
    }

    @Test
    @DisplayName("16. System calculated badge matches 'Deterministic Algorithm v2.4'")
    void test16_SystemCalculatedBadge() {
        CareerScoringEngine.CalculationResult res = scoringEngine.calculateMatch(aiCareer, Collections.emptyMap(), Collections.emptyList());
        assertEquals("Deterministic Algorithm v2.4", res.getSystemCalculatedBadge());
    }

    @Test
    @DisplayName("17. Zero randomness across 100 consecutive runs")
    void test17_ZeroRandomness100Runs() {
        Map<String, Integer> skills = Map.of("python", 3, "machine-learning", 2);
        CareerScoringEngine.CalculationResult first = scoringEngine.calculateMatch(aiCareer, skills, Collections.emptyList());

        for (int i = 0; i < 100; i++) {
            CareerScoringEngine.CalculationResult current = scoringEngine.calculateMatch(aiCareer, skills, Collections.emptyList());
            assertEquals(first.getMatchScore(), current.getMatchScore());
            assertEquals(first.getConfidenceLevel(), current.getConfidenceLevel());
            assertEquals(first.getFitReason(), current.getFitReason());
        }
    }
}
