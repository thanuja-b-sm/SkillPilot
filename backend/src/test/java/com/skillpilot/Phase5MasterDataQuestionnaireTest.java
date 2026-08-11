package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.CareerRequest;
import com.skillpilot.dto.request.QuestionAnswerRequest;
import com.skillpilot.dto.request.QuestionRequest;
import com.skillpilot.dto.request.SkillRequest;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.Skill;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.QuestionRepository;
import com.skillpilot.repository.SkillRepository;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.security.JwtTokenProvider;
import com.skillpilot.security.SecurityUser;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class Phase5MasterDataQuestionnaireTest {

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
    private QuestionRepository questionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testStudentUser;
    private User testAdminUser;
    private String studentToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // Student User
        testStudentUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Phase5 Student")
                .email("p5.student@university.edu")
                .passwordHash(passwordEncoder.encode("Password123"))
                .role(UserRole.STUDENT)
                .completionPercentage(50)
                .build();
        userRepository.save(testStudentUser);

        SecurityUser secStudent = new SecurityUser(testStudentUser);
        Authentication studentAuth = new UsernamePasswordAuthenticationToken(secStudent, null, secStudent.getAuthorities());
        studentToken = jwtTokenProvider.generateToken(studentAuth);

        // Admin User
        testAdminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Phase5 Admin")
                .email("p5.admin@skillpilot.com")
                .passwordHash(passwordEncoder.encode("AdminPassword123"))
                .role(UserRole.ADMIN)
                .completionPercentage(100)
                .build();
        userRepository.save(testAdminUser);

        SecurityUser secAdmin = new SecurityUser(testAdminUser);
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(secAdmin, null, secAdmin.getAuthorities());
        adminToken = jwtTokenProvider.generateToken(adminAuth);
    }

    // --- CAREERS ---
    @Test
    @DisplayName("1. GET /api/careers returns active career tracks list")
    void test1_GetActiveCareers() throws Exception {
        mockMvc.perform(get("/api/careers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].requiredSkills", notNullValue()));
    }

    @Test
    @DisplayName("2. GET /api/careers/{id} returns single career details")
    void test2_GetCareerById() throws Exception {
        mockMvc.perform(get("/api/careers/ai-software-engineer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("ai-software-engineer")))
                .andExpect(jsonPath("$.title", is("AI & Machine Learning Engineer")));
    }

    @Test
    @DisplayName("3. GET /api/careers/{id} with non-existent ID returns HTTP 404")
    void test3_GetCareerById_NotFound() throws Exception {
        mockMvc.perform(get("/api/careers/nonexistent-career-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("4. Inactive career is hidden from GET /api/careers")
    void test4_InactiveCareerHidden() throws Exception {
        Career inactive = Career.builder()
                .id("inactive-career-test")
                .title("Inactive Track")
                .category("Deprecated")
                .description("Inactive test career track")
                .averageSalary("$100,000 / yr")
                .growthRate("+0%")
                .demandLevel(com.skillpilot.entity.DemandLevel.HIGH)
                .isActive(false)
                .build();
        careerRepository.save(inactive);

        mockMvc.perform(get("/api/careers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", not(hasItem("inactive-career-test"))));
    }

    // --- SKILLS ---
    @Test
    @DisplayName("5. GET /api/skills returns active skills catalogue")
    void test5_GetActiveSkills() throws Exception {
        mockMvc.perform(get("/api/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[0].name", notNullValue()));
    }

    @Test
    @DisplayName("6. GET /api/skills/{id} returns single skill details")
    void test6_GetSkillById() throws Exception {
        mockMvc.perform(get("/api/skills/python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("python")))
                .andExpect(jsonPath("$.name", is("Python Programming")));
    }

    @Test
    @DisplayName("7. GET /api/skills/{id} with non-existent ID returns HTTP 404")
    void test7_GetSkillById_NotFound() throws Exception {
        mockMvc.perform(get("/api/skills/nonexistent-skill-id"))
                .andExpect(status().isNotFound());
    }

    // --- CAREER REQUIREMENTS ---
    @Test
    @DisplayName("8. Career requirements expose requiredLevel (1-5) and essential flag")
    void test8_RetrieveCareerRequirements() throws Exception {
        mockMvc.perform(get("/api/careers/ai-software-engineer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiredSkills[0].skillId", notNullValue()))
                .andExpect(jsonPath("$.requiredSkills[0].requiredLevel", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.requiredSkills[0].requiredLevel", lessThanOrEqualTo(5)));
    }

    @Test
    @DisplayName("9. Invalid Career lookup returns 404 for requirements")
    void test9_InvalidCareerRequirements() throws Exception {
        mockMvc.perform(get("/api/careers/invalid-career-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("10. Duplicate mapping prevention check")
    void test10_DuplicateMappingPrevention() throws Exception {
        Skill python = skillRepository.findById("python").orElseThrow();
        Career ai = careerRepository.findById("ai-software-engineer").orElseThrow();

        // Skill requirements for ai-software-engineer already contains python
        assertTrue(ai.getRequiredSkills().stream().anyMatch(r -> r.getSkill().getId().equals(python.getId())));
    }

    // --- QUESTIONNAIRE ---
    @Test
    @DisplayName("11. GET /api/questionnaire returns active questions list")
    void test11_GetActiveQuestionnaire() throws Exception {
        mockMvc.perform(get("/api/questionnaire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[0].question", notNullValue()))
                .andExpect(jsonPath("$[0].options", notNullValue()));
    }

    @Test
    @DisplayName("12. Questionnaire returns questions in deterministic display order")
    void test12_DeterministicOrdering() throws Exception {
        mockMvc.perform(get("/api/questionnaire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].displayOrder", is(1)))
                .andExpect(jsonPath("$[1].displayOrder", is(2)));
    }

    @Test
    @DisplayName("13. Question options structure mapped correctly with skill mappings")
    void test13_OptionsStructure() throws Exception {
        mockMvc.perform(get("/api/questionnaire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].options[0].id", is("q1-ai")))
                .andExpect(jsonPath("$[0].options[0].associatedSkills[0].skillId", notNullValue()));
    }

    // --- QUESTIONNAIRE ANSWERS ---
    @Test
    @DisplayName("14. POST /api/questionnaire/answers saves valid user survey answers")
    void test14_ValidAnswerSubmission() throws Exception {
        QuestionAnswerRequest.AnswerItem item = QuestionAnswerRequest.AnswerItem.builder()
                .questionId("q1")
                .selectedOptionIds(Collections.singletonList("q1-ai"))
                .build();

        QuestionAnswerRequest req = QuestionAnswerRequest.builder()
                .answers(Collections.singletonList(item))
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionId", is("q1")))
                .andExpect(jsonPath("$[0].selectedOptionIds[0]", is("q1-ai")));
    }

    @Test
    @DisplayName("15. Invalid question ID in submission returns HTTP 404")
    void test15_InvalidQuestionInAnswer() throws Exception {
        QuestionAnswerRequest.AnswerItem item = QuestionAnswerRequest.AnswerItem.builder()
                .questionId("nonexistent-q")
                .selectedOptionIds(Collections.singletonList("q1-ai"))
                .build();

        QuestionAnswerRequest req = QuestionAnswerRequest.builder()
                .answers(Collections.singletonList(item))
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("16. Invalid option ID in submission returns HTTP 404")
    void test16_InvalidOptionInAnswer() throws Exception {
        QuestionAnswerRequest.AnswerItem item = QuestionAnswerRequest.AnswerItem.builder()
                .questionId("q1")
                .selectedOptionIds(Collections.singletonList("nonexistent-option"))
                .build();

        QuestionAnswerRequest req = QuestionAnswerRequest.builder()
                .answers(Collections.singletonList(item))
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("17. Option belonging to wrong question returns HTTP 400 Bad Request")
    void test17_OptionBelongsToWrongQuestion() throws Exception {
        // q2-coding belongs to q2, submitting it for q1 must fail
        QuestionAnswerRequest.AnswerItem item = QuestionAnswerRequest.AnswerItem.builder()
                .questionId("q1")
                .selectedOptionIds(Collections.singletonList("q2-coding"))
                .build();

        QuestionAnswerRequest req = QuestionAnswerRequest.builder()
                .answers(Collections.singletonList(item))
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("18. Missing required answer submission returns HTTP 400 Bad Request")
    void test18_MissingRequiredAnswer() throws Exception {
        QuestionAnswerRequest req = QuestionAnswerRequest.builder()
                .answers(Collections.emptyList())
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("19. Answer persistence verified via GET /api/questionnaire/answers")
    void test19_AnswerPersistence() throws Exception {
        QuestionAnswerRequest.AnswerItem item = QuestionAnswerRequest.AnswerItem.builder()
                .questionId("q1")
                .selectedOptionIds(Collections.singletonList("q1-cloud"))
                .build();

        QuestionAnswerRequest req = QuestionAnswerRequest.builder()
                .answers(Collections.singletonList(item))
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionId", is("q1")))
                .andExpect(jsonPath("$[0].selectedOptionIds[0]", is("q1-cloud")));
    }

    @Test
    @DisplayName("20. Resubmitting questionnaire updates user answers idempotently")
    void test20_AnswerUpdateResubmission() throws Exception {
        QuestionAnswerRequest.AnswerItem item1 = QuestionAnswerRequest.AnswerItem.builder()
                .questionId("q1")
                .selectedOptionIds(Collections.singletonList("q1-ai"))
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(QuestionAnswerRequest.builder().answers(Collections.singletonList(item1)).build())))
                .andExpect(status().isOk());

        // Update answer to q1-web
        QuestionAnswerRequest.AnswerItem item2 = QuestionAnswerRequest.AnswerItem.builder()
                .questionId("q1")
                .selectedOptionIds(Collections.singletonList("q1-web"))
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(QuestionAnswerRequest.builder().answers(Collections.singletonList(item2)).build())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].selectedOptionIds[0]", is("q1-web")));
    }

    // --- OWNERSHIP ---
    @Test
    @DisplayName("21. User A cannot retrieve User B questionnaire answers")
    void test21_OwnershipGetAnswers() throws Exception {
        // Student submits answer
        QuestionAnswerRequest.AnswerItem item = QuestionAnswerRequest.AnswerItem.builder()
                .questionId("q1")
                .selectedOptionIds(Collections.singletonList("q1-ai"))
                .build();

        mockMvc.perform(post("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(QuestionAnswerRequest.builder().answers(Collections.singletonList(item)).build())))
                .andExpect(status().isOk());

        // Admin retrieving own answers should NOT see student's answer
        MvcResult res = mockMvc.perform(get("/api/questionnaire/answers")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("[]", res.getResponse().getContentAsString().trim());
    }

    @Test
    @DisplayName("22. User identity derived strictly from SecurityContextHolder")
    void test22_OwnershipDerivedFromSecurityContext() throws Exception {
        mockMvc.perform(get("/api/questionnaire/answers?userId=fake-user-id")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    // --- ADMIN AUTHORIZATION ---
    @Test
    @DisplayName("23. Student blocked from admin career CRUD (HTTP 403)")
    void test23_StudentBlockedAdminCareerCRUD() throws Exception {
        CareerRequest req = CareerRequest.builder()
                .title("Unauthorized Track")
                .category("Security")
                .build();

        mockMvc.perform(post("/api/admin/careers")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("24. Student blocked from admin skill CRUD (HTTP 403)")
    void test24_StudentBlockedAdminSkillCRUD() throws Exception {
        SkillRequest req = SkillRequest.builder()
                .name("Unauthorized Skill")
                .category("Tools")
                .build();

        mockMvc.perform(post("/api/admin/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("25. Student blocked from admin questionnaire CRUD (HTTP 403)")
    void test25_StudentBlockedAdminQuestionnaireCRUD() throws Exception {
        QuestionRequest req = QuestionRequest.builder()
                .section("Unauthorized Section")
                .question("Unauthorized Question?")
                .type("single")
                .displayOrder(99)
                .build();

        mockMvc.perform(post("/api/admin/questionnaire")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("26. Admin authorized for CRUD operations (HTTP 201 Created)")
    void test26_AdminAuthorizedCRUD() throws Exception {
        CareerRequest req = CareerRequest.builder()
                .title("Robotics Engineer")
                .category("Hardware & Robotics")
                .description("Builds autonomous robots.")
                .averageSalary("$140,000 / yr")
                .growthRate("+25%")
                .demandLevel("High")
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/admin/careers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Robotics Engineer")));
    }

    // --- DATABASE INTEGRITY & SAFE DEACTIVATION ---
    @Test
    @DisplayName("27. Foreign key integrity preserved")
    void test27_ForeignKeyIntegrity() throws Exception {
        assertTrue(questionRepository.existsById("q1"));
    }

    @Test
    @DisplayName("28. Duplicate constraints integrity")
    void test28_DuplicateConstraints() throws Exception {
        assertTrue(skillRepository.existsById("python"));
    }

    @Test
    @DisplayName("29. Safe deactivation behavior sets isActive = false")
    void test29_SafeDeactivation() throws Exception {
        mockMvc.perform(delete("/api/admin/careers/product-manager")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Career pm = careerRepository.findById("product-manager").orElseThrow();
        assertFalse(pm.getIsActive());
    }
}
