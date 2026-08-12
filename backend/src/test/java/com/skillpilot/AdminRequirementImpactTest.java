package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.CareerSkillRequirementRequest;
import com.skillpilot.dto.response.CareerMatchResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.entity.*;
import com.skillpilot.repository.*;
import com.skillpilot.security.JwtTokenProvider;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.CareerDiscoveryService;
import com.skillpilot.service.SkillGapService;
import com.skillpilot.service.TargetCareerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminRequirementImpactTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private CareerSkillRequirementRepository requirementRepository;

    @Autowired
    private CareerMatchResultRepository careerMatchResultRepository;

    @Autowired
    private CareerDiscoveryService careerDiscoveryService;

    @Autowired
    private SkillGapService skillGapService;

    @Autowired
    private TargetCareerService targetCareerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private User adminUser;
    private User student1;
    private User student2;
    private Career cloudCareer;
    private Skill k8sSkill;
    private Skill terraformSkill;
    private String adminToken;
    private String student1Token;
    private String student2Token;

    @BeforeEach
    void setUp() {
        // 1. Create Admin
        adminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Impact Admin")
                .email("impact.admin@skillpilot.io")
                .passwordHash(passwordEncoder.encode("AdminPass123"))
                .role(UserRole.ADMIN)
                .title("System Admin")
                .education("M.S.")
                .experienceYears(10)
                .location("Chicago")
                .targetFocus("DevOps")
                .bio("")
                .completionPercentage(100)
                .build();
        userRepository.save(adminUser);

        SecurityUser secAdmin = new SecurityUser(adminUser);
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(secAdmin, null, secAdmin.getAuthorities());
        adminToken = jwtTokenProvider.generateToken(adminAuth);

        // 2. Create Student 1 & Student 2
        student1 = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Student One")
                .email("student1.impact@skillpilot.io")
                .passwordHash(passwordEncoder.encode("StudentPass123"))
                .role(UserRole.STUDENT)
                .title("Junior DevOps")
                .education("B.S.")
                .experienceYears(1)
                .location("Boston")
                .targetFocus("Cloud")
                .bio("")
                .completionPercentage(20)
                .build();
        userRepository.save(student1);

        SecurityUser secStudent1 = new SecurityUser(student1);
        Authentication s1Auth = new UsernamePasswordAuthenticationToken(secStudent1, null, secStudent1.getAuthorities());
        student1Token = jwtTokenProvider.generateToken(s1Auth);

        student2 = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Student Two")
                .email("student2.impact@skillpilot.io")
                .passwordHash(passwordEncoder.encode("StudentPass123"))
                .role(UserRole.STUDENT)
                .title("Sophomore")
                .education("B.S.")
                .experienceYears(0)
                .location("New York")
                .targetFocus("Cloud")
                .bio("")
                .completionPercentage(10)
                .build();
        userRepository.save(student2);

        SecurityUser secStudent2 = new SecurityUser(student2);
        Authentication s2Auth = new UsernamePasswordAuthenticationToken(secStudent2, null, secStudent2.getAuthorities());
        student2Token = jwtTokenProvider.generateToken(s2Auth);

        // 3. Create Career & Skills
        cloudCareer = Career.builder()
                .id("impact-cloud-arch-" + UUID.randomUUID().toString().substring(0, 8))
                .title("Cloud Infrastructure Architect")
                .category("Cloud Computing")
                .description("Architects enterprise cloud solutions")
                .averageSalary("$140,000")
                .growthRate("+22%")
                .demandLevel(DemandLevel.HIGH)
                .isActive(true)
                .build();
        careerRepository.save(cloudCareer);

        k8sSkill = Skill.builder()
                .id("skill-k8s-" + UUID.randomUUID().toString().substring(0, 8))
                .name("Kubernetes Orchestration")
                .category("DevOps & Cloud")
                .description("Container orchestration")
                .isActive(true)
                .build();
        skillRepository.save(k8sSkill);

        terraformSkill = Skill.builder()
                .id("skill-terraform-" + UUID.randomUUID().toString().substring(0, 8))
                .name("Terraform IaC")
                .category("DevOps & Cloud")
                .description("Infrastructure as Code")
                .isActive(true)
                .build();
        skillRepository.save(terraformSkill);

        // Initial Requirement: Kubernetes Level 3 Essential
        CareerSkillRequirement initReq = CareerSkillRequirement.builder()
                .id(UUID.randomUUID().toString())
                .career(cloudCareer)
                .skill(k8sSkill)
                .requiredLevel(3)
                .isEssential(true)
                .build();
        requirementRepository.saveAndFlush(initReq);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Admin Requirement Update: Future skill gap reflects changes while historical snapshots remain unchanged")
    void testAdminRequirementChangesPropagateToFutureGapAnalysisAndPreserveHistory() throws Exception {
        // Phase 1: Student 1 calculates career match & selects target career
        targetCareerService.setTargetCareer(student1.getId(), cloudCareer.getId());
        List<CareerMatchResponse> s1Matches = careerDiscoveryService.calculateAndPersistCareerMatches(student1.getId());
        assertNotNull(s1Matches);

        // Fetch Student 1 persisted historical snapshot entity
        CareerMatchResult s1ResultBefore = careerMatchResultRepository
                .findByUserIdAndCareerId(student1.getId(), cloudCareer.getId())
                .orElseThrow();
        String s1HistoricalReqsSnapshot = s1ResultBefore.getRequirementsSnapshot();
        assertNotNull(s1HistoricalReqsSnapshot);
        assertTrue(s1HistoricalReqsSnapshot.contains(k8sSkill.getName()));

        // Student 1 skill gap analysis before admin change (Requires level 3 for K8s)
        SkillGapAnalysisResponse s1GapBefore = skillGapService.getSkillGapForCareer(student1.getId(), cloudCareer.getId());
        assertNotNull(s1GapBefore.getSkills());
        assertEquals(1, s1GapBefore.getSkills().size());
        assertEquals(3, s1GapBefore.getSkills().get(0).getRequiredLevel());

        // Phase 2: Admin updates Kubernetes requiredLevel to 5 AND adds Terraform level 4 essential requirement
        CareerSkillRequirementRequest updateK8sReq = CareerSkillRequirementRequest.builder()
                .skillId(k8sSkill.getId())
                .requiredLevel(5)
                .isEssential(true)
                .build();

        mockMvc.perform(post("/api/admin/careers/" + cloudCareer.getId() + "/requirements")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateK8sReq)))
                .andExpect(status().isOk());

        CareerSkillRequirementRequest addTerraformReq = CareerSkillRequirementRequest.builder()
                .skillId(terraformSkill.getId())
                .requiredLevel(4)
                .isEssential(true)
                .build();

        mockMvc.perform(post("/api/admin/careers/" + cloudCareer.getId() + "/requirements")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTerraformReq)))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        // Phase 3: Future Student 2 performs skill gap analysis on the updated career requirements
        SkillGapAnalysisResponse s2GapAfter = skillGapService.getSkillGapForCareer(student2.getId(), cloudCareer.getId());
        assertNotNull(s2GapAfter);
        assertNotNull(s2GapAfter.getSkills());
        assertEquals(2, s2GapAfter.getSkills().size());

        // Confirm updated requirement levels in Student 2 gap analysis
        boolean foundK8sLevel5 = s2GapAfter.getSkills().stream()
                .anyMatch(g -> g.getSkillId().equals(k8sSkill.getId()) && g.getRequiredLevel() == 5);
        boolean foundTerraformLevel4 = s2GapAfter.getSkills().stream()
                .anyMatch(g -> g.getSkillId().equals(terraformSkill.getId()) && g.getRequiredLevel() == 4);

        assertTrue(foundK8sLevel5, "Future skill gap should reflect updated K8s required level 5");
        assertTrue(foundTerraformLevel4, "Future skill gap should reflect new Terraform requirement level 4");

        // Phase 4: Verify Student 1 historical snapshot remains unchanged
        CareerMatchResult s1ResultAfter = careerMatchResultRepository
                .findByUserIdAndCareerId(student1.getId(), cloudCareer.getId())
                .orElseThrow();
        assertEquals(s1HistoricalReqsSnapshot, s1ResultAfter.getRequirementsSnapshot(),
                "Historical requirementsSnapshot for Student 1 must remain untouched after admin requirement changes");
    }
}
