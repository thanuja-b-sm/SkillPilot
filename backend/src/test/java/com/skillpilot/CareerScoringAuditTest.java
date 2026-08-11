package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.entity.*;
import com.skillpilot.service.CareerScoringEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CareerScoringAuditTest {

    private CareerScoringEngine scoringEngine;
    private ObjectMapper objectMapper;

    private Skill pythonSkill;
    private Skill mlSkill;
    private Skill cloudSkill;
    private Skill problemSolvingSkill;
    private Career aiCareer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        scoringEngine = new CareerScoringEngine(objectMapper);

        pythonSkill = Skill.builder().id("python").name("Python Programming").category("Technical").build();
        mlSkill = Skill.builder().id("machine-learning").name("Machine Learning & AI").category("Technical").build();
        cloudSkill = Skill.builder().id("cloud-aws").name("Cloud Computing (AWS/GCP)").category("Technical").build();
        problemSolvingSkill = Skill.builder().id("problem-solving").name("Critical Problem Solving").category("Soft Skills").build();

        List<CareerSkillRequirement> reqs = new ArrayList<>();
        reqs.add(CareerSkillRequirement.builder().id("csr-1").skill(pythonSkill).requiredLevel(5).isEssential(true).build()); // wt 10
        reqs.add(CareerSkillRequirement.builder().id("csr-2").skill(mlSkill).requiredLevel(4).isEssential(true).build()); // wt 8
        reqs.add(CareerSkillRequirement.builder().id("csr-4").skill(cloudSkill).requiredLevel(3).isEssential(true).build()); // wt 6
        reqs.add(CareerSkillRequirement.builder().id("csr-6").skill(problemSolvingSkill).requiredLevel(4).isEssential(true).build()); // wt 8

        // Total required weight = 10 + 8 + 6 + 8 = 32

        aiCareer = Career.builder()
                .id("ai-software-engineer")
                .title("AI & Machine Learning Engineer")
                .category("Artificial Intelligence")
                .requiredSkills(reqs)
                .build();
    }

    @Test
    @DisplayName("CASE A: User has maximum skill levels (5/5) for every required skill")
    void testCaseA_MaximumSkillLevels() {
        Map<String, Integer> maxSkills = Map.of(
                "python", 5,
                "machine-learning", 5,
                "cloud-aws", 5,
                "problem-solving", 5
        );

        CareerScoringEngine.CalculationResult res = scoringEngine.calculateMatch(aiCareer, maxSkills, Collections.emptyList());
        // Max technical ratio = 32 / 32 = 1.0. 1.0 * 75 = 75%
        assertEquals(75, res.getMatchScore());
        assertEquals("Medium", res.getConfidenceLevel());
    }

    @Test
    @DisplayName("CASE B: User has minimum skill levels (0/5) for every required skill")
    void testCaseB_MinimumSkillLevels() {
        Map<String, Integer> minSkills = Collections.emptyMap();
        CareerScoringEngine.CalculationResult res = scoringEngine.calculateMatch(aiCareer, minSkills, Collections.emptyList());
        // Raw score 0% -> clamped to 45%
        assertEquals(45, res.getMatchScore());
        assertEquals("Moderate", res.getConfidenceLevel());
    }

    @Test
    @DisplayName("CASE C & D: Improving and decreasing a required skill above 45% threshold changes score dynamically")
    void testCaseC_D_SkillIncreaseAndDecrease() {
        // User starts with Python 5 and Problem Solving 4 -> earned 18 / 32 = 0.5625 * 75 = 42.18% -> clamped to 45%
        Map<String, Integer> baseSkills = Map.of("python", 5, "problem-solving", 4);
        CareerScoringEngine.CalculationResult baseRes = scoringEngine.calculateMatch(aiCareer, baseSkills, Collections.emptyList());
        assertEquals(45, baseRes.getMatchScore());

        // User adds Machine Learning level 4 -> earned 18 + 8 = 26 / 32 = 0.8125 * 75 = 60.93% -> rounded to 61%
        Map<String, Integer> improvedSkills = Map.of("python", 5, "problem-solving", 4, "machine-learning", 4);
        CareerScoringEngine.CalculationResult improvedRes = scoringEngine.calculateMatch(aiCareer, improvedSkills, Collections.emptyList());
        assertEquals(61, improvedRes.getMatchScore());
        assertTrue(improvedRes.getMatchScore() > baseRes.getMatchScore());

        // Decreasing Machine Learning back to 0 reduces score back to 45%
        CareerScoringEngine.CalculationResult decreasedRes = scoringEngine.calculateMatch(aiCareer, baseSkills, Collections.emptyList());
        assertEquals(45, decreasedRes.getMatchScore());
    }

    @Test
    @DisplayName("CASE E & F: Questionnaire alignment increases score by up to 23 percentage points")
    void testCaseE_F_QuestionnaireAlignment() {
        Map<String, Integer> baseSkills = Map.of("python", 5, "problem-solving", 4, "machine-learning", 4); // 61%

        Question q1 = Question.builder().id("q1").build();
        QuestionOption opt1 = QuestionOption.builder()
                .id("q1-ai")
                .question(q1)
                .associatedSkills(List.of(
                        QuestionSkillMapping.builder().skill(pythonSkill).weight(5).build(),
                        QuestionSkillMapping.builder().skill(mlSkill).weight(5).build()
                ))
                .build();
        q1.setOptions(List.of(opt1));

        UserQuestionAnswer ansAI = UserQuestionAnswer.builder()
                .question(q1)
                .selectedOptionIds("[\"q1-ai\"]")
                .build();

        // 61% base + questionnaire bonus ( (5/5)*4 + (5/5)*4 = 8 pts ) = 69%
        CareerScoringEngine.CalculationResult aiRes = scoringEngine.calculateMatch(aiCareer, baseSkills, List.of(ansAI));
        assertEquals(69, aiRes.getMatchScore());

        // Poorly aligned questionnaire (e.g. unselected or non-matching skills)
        CareerScoringEngine.CalculationResult noBonusRes = scoringEngine.calculateMatch(aiCareer, baseSkills, Collections.emptyList());
        assertEquals(61, noBonusRes.getMatchScore());
    }

    @Test
    @DisplayName("Audit: 100% Determinism across 500 consecutive runs")
    void testAudit_Determinism() {
        Map<String, Integer> skills = Map.of("python", 5, "machine-learning", 3);
        CareerScoringEngine.CalculationResult first = scoringEngine.calculateMatch(aiCareer, skills, Collections.emptyList());

        for (int i = 0; i < 500; i++) {
            CareerScoringEngine.CalculationResult current = scoringEngine.calculateMatch(aiCareer, skills, Collections.emptyList());
            assertEquals(first.getMatchScore(), current.getMatchScore());
            assertEquals(first.getConfidenceLevel(), current.getConfidenceLevel());
            assertEquals(first.getFitReason(), current.getFitReason());
            assertEquals(first.getKeyStrengths(), current.getKeyStrengths());
            assertEquals(first.getKeyGaps(), current.getKeyGaps());
        }
    }
}
