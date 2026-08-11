package com.skillpilot;

import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.CareerSkillRequirement;
import com.skillpilot.entity.Skill;
import com.skillpilot.service.CareerMapper;
import com.skillpilot.service.SkillGapAnalysisEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class Phase7AReadinessAuditTest {

    @Autowired
    private CareerMapper careerMapper;

    private SkillGapAnalysisEngine engine;

    private Skill pythonSkill;
    private Skill mlSkill;
    private Skill deepSkill;
    private Skill cloudSkill;
    private Skill dockerSkill;
    private Skill problemSkill;

    private Career aiCareer;

    @BeforeEach
    void setUp() {
        engine = new SkillGapAnalysisEngine(careerMapper);

        pythonSkill = Skill.builder().id("python").name("Python Programming").category("Technical").build();
        mlSkill = Skill.builder().id("machine-learning").name("Machine Learning & AI").category("Technical").build();
        deepSkill = Skill.builder().id("deep-learning").name("Deep Learning & PyTorch").category("Technical").build();
        cloudSkill = Skill.builder().id("cloud-aws").name("Cloud Computing (AWS/GCP)").category("Technical").build();
        dockerSkill = Skill.builder().id("docker-k8s").name("Docker & Kubernetes").category("Tools & Frameworks").build();
        problemSkill = Skill.builder().id("problem-solving").name("Critical Problem Solving").category("Soft Skills").build();

        // 6 required skills matching MySQL seed
        List<CareerSkillRequirement> reqs = List.of(
                CareerSkillRequirement.builder().id("csr-1").skill(pythonSkill).requiredLevel(5).isEssential(true).build(),   // wt 2.0
                CareerSkillRequirement.builder().id("csr-2").skill(mlSkill).requiredLevel(4).isEssential(true).build(),       // wt 2.0
                CareerSkillRequirement.builder().id("csr-3").skill(deepSkill).requiredLevel(4).isEssential(false).build(),    // wt 1.0
                CareerSkillRequirement.builder().id("csr-4").skill(cloudSkill).requiredLevel(3).isEssential(true).build(),     // wt 2.0
                CareerSkillRequirement.builder().id("csr-5").skill(dockerSkill).requiredLevel(3).isEssential(false).build(),    // wt 1.0
                CareerSkillRequirement.builder().id("csr-6").skill(problemSkill).requiredLevel(4).isEssential(true).build()    // wt 2.0
        ); // Total weight = 10.0

        aiCareer = Career.builder()
                .id("ai-software-engineer")
                .title("AI & Machine Learning Engineer")
                .category("Artificial Intelligence")
                .requiredSkills(reqs)
                .build();
    }

    @Test
    @DisplayName("Audit 1: Python level 0 vs 1 vs 3 vs 5 readiness responsiveness")
    void testPythonMonotonicResponsiveness() {
        // Python = 0 -> readiness = 0%
        SkillGapAnalysisResponse res0 = engine.analyze(aiCareer, Collections.emptyMap());
        assertEquals(0, res0.getReadinessScore());

        // Python = 1 -> fulfillment 1/5 = 0.2 * 2.0 = 0.4 / 10.0 = 4%
        SkillGapAnalysisResponse res1 = engine.analyze(aiCareer, Map.of("python", 1));
        assertEquals(4, res1.getReadinessScore());

        // Python = 3 -> fulfillment 3/5 = 0.6 * 2.0 = 1.2 / 10.0 = 12%
        SkillGapAnalysisResponse res3 = engine.analyze(aiCareer, Map.of("python", 3));
        assertEquals(12, res3.getReadinessScore());

        // Python = 5 -> fulfillment 5/5 = 1.0 * 2.0 = 2.0 / 10.0 = 20%
        SkillGapAnalysisResponse res5 = engine.analyze(aiCareer, Map.of("python", 5));
        assertEquals(20, res5.getReadinessScore());

        assertTrue(res1.getReadinessScore() > res0.getReadinessScore());
        assertTrue(res3.getReadinessScore() > res1.getReadinessScore());
        assertTrue(res5.getReadinessScore() > res3.getReadinessScore());
    }

    @Test
    @DisplayName("Audit 2: Controlled test case with known weights")
    void testControlledWeighting() {
        // Skill A (wt 2.0, req 5), Skill B (wt 1.0, req 5)
        CareerSkillRequirement reqA = CareerSkillRequirement.builder().skill(pythonSkill).requiredLevel(5).isEssential(true).build();
        CareerSkillRequirement reqB = CareerSkillRequirement.builder().skill(dockerSkill).requiredLevel(5).isEssential(false).build();

        Career testCareer = Career.builder()
                .id("test-career")
                .title("Test Track")
                .requiredSkills(List.of(reqA, reqB))
                .build();

        // 0 / 0 -> 0%
        assertEquals(0, engine.analyze(testCareer, Collections.emptyMap()).getReadinessScore());

        // Skill A = 5, Skill B = 0 -> (1.0*2 + 0.0*1)/3.0 = 2/3 = 66.67% -> 67%
        assertEquals(67, engine.analyze(testCareer, Map.of("python", 5)).getReadinessScore());

        // Skill A = 5, Skill B = 5 -> 100%
        assertEquals(100, engine.analyze(testCareer, Map.of("python", 5, "docker-k8s", 5)).getReadinessScore());
    }

    @Test
    @DisplayName("Audit 3: Completed skills definition (gap == 0 only)")
    void testCompletedSkillsDefinition() {
        // User has Python 3 (req 5) -> partial, NOT completed
        SkillGapAnalysisResponse resPartial = engine.analyze(aiCareer, Map.of("python", 3));
        assertEquals(0, resPartial.getCompletedSkills());
        assertTrue(resPartial.getReadinessScore() > 0); // Partial fulfillment contributes to readiness!

        // User has Python 5 (req 5) -> completed
        SkillGapAnalysisResponse resComplete = engine.analyze(aiCareer, Map.of("python", 5));
        assertEquals(1, resComplete.getCompletedSkills());
    }

    @Test
    @DisplayName("Audit 4: 100-run readiness calculation repeatability")
    void testRepeatability100Runs() {
        Map<String, Integer> skills = Map.of("python", 4, "machine-learning", 3, "cloud-aws", 2);
        SkillGapAnalysisResponse first = engine.analyze(aiCareer, skills);

        for (int i = 0; i < 100; i++) {
            SkillGapAnalysisResponse current = engine.analyze(aiCareer, skills);
            assertEquals(first.getReadinessScore(), current.getReadinessScore());
            assertEquals(first.getCompletedSkills(), current.getCompletedSkills());
            assertEquals(first.getTotalRequiredSkills(), current.getTotalRequiredSkills());
            assertEquals(first.getSkills().size(), current.getSkills().size());
        }
    }
}
