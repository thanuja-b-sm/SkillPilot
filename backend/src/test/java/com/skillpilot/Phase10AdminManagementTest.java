package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.*;
import com.skillpilot.dto.response.AuthResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.Skill;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.SkillRepository;
import com.skillpilot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class Phase10AdminManagementTest {

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
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create Admin user if not exists
        String adminEmail = "admin.phase10@skillpilot.com";
        User adminUser = userRepository.findByEmail(adminEmail).orElseGet(() ->
                userRepository.save(User.builder()
                        .id(UUID.randomUUID().toString())
                        .email(adminEmail)
                        .passwordHash(passwordEncoder.encode("AdminPassword123"))
                        .name("Admin User")
                        .role(UserRole.ADMIN)
                        .build())
        );

        // Create Student user if not exists
        String studentEmail = "student.phase10@skillpilot.com";
        User studentUser = userRepository.findByEmail(studentEmail).orElseGet(() ->
                userRepository.save(User.builder()
                        .id(UUID.randomUUID().toString())
                        .email(studentEmail)
                        .passwordHash(passwordEncoder.encode("StudentPassword123"))
                        .name("Student User")
                        .role(UserRole.STUDENT)
                        .build())
        );

        // Obtain Admin JWT
        LoginRequest adminLogin = LoginRequest.builder().email(adminEmail).password("AdminPassword123").build();
        MvcResult adminRes = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();
        adminToken = objectMapper.readValue(adminRes.getResponse().getContentAsString(), AuthResponse.class).getToken();

        // Obtain Student JWT
        LoginRequest studentLogin = LoginRequest.builder().email(studentEmail).password("StudentPassword123").build();
        MvcResult studentRes = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentLogin)))
                .andExpect(status().isOk())
                .andReturn();
        studentToken = objectMapper.readValue(studentRes.getResponse().getContentAsString(), AuthResponse.class).getToken();
    }

    @Test
    @DisplayName("Security 1: ADMIN can access admin endpoints (200 OK)")
    void testSecurity_AdminAccessAllowed() throws Exception {
        mockMvc.perform(get("/api/admin/careers")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Security 2: STUDENT accessing admin endpoint receives HTTP 403 Forbidden")
    void testSecurity_StudentForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/careers")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Security 3: Unauthenticated user accessing admin endpoint receives HTTP 401 Unauthorized")
    void testSecurity_UnauthenticatedUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/careers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Security 4: STUDENT cannot update system configuration (HTTP 403 Forbidden)")
    void testSecurity_StudentCannotUpdateConfig() throws Exception {
        SystemConfigUpdateRequest req = SystemConfigUpdateRequest.builder()
                .technicalWeight(new BigDecimal("0.600"))
                .build();

        mockMvc.perform(put("/api/admin/config")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Careers CRUD: ADMIN can create, update, and de-activate careers")
    void testCareers_AdminCrud() throws Exception {
        String testCareerId = UUID.randomUUID().toString();
        String uniqueTitle = "Quantum Computing Engineer " + UUID.randomUUID().toString().substring(0, 5);
        CareerRequest createReq = CareerRequest.builder()
                .id(testCareerId)
                .title(uniqueTitle)
                .category("Specialized Engineering")
                .description("Builds quantum algorithms and software")
                .averageSalary("$180,000 / yr")
                .growthRate("+35%")
                .demandLevel("HIGH")
                .typicalRoles(List.of("Quantum Developer"))
                .recommendedPrerequisites(List.of("Physics", "Linear Algebra"))
                .isActive(true)
                .build();

        // 1. Create
        mockMvc.perform(post("/api/admin/careers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testCareerId))
                .andExpect(jsonPath("$.title").value(uniqueTitle));

        // 2. Update
        createReq.setDescription("Updated description for Quantum Computing Engineer");
        mockMvc.perform(put("/api/admin/careers/" + testCareerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description for Quantum Computing Engineer"));

        // 3. De-activate
        mockMvc.perform(delete("/api/admin/careers/" + testCareerId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify soft deactivation (isActive = false)
        Career deactivated = careerRepository.findById(testCareerId).orElse(null);
        assertNotNull(deactivated);
        assertFalse(deactivated.getIsActive());
    }

    @Test
    @DisplayName("Skills CRUD: ADMIN can create, update, and de-activate skills")
    void testSkills_AdminCrud() throws Exception {
        String testSkillId = UUID.randomUUID().toString();
        String uniqueSkillName = "Quantum Physics " + UUID.randomUUID().toString().substring(0, 5);
        SkillRequest createReq = SkillRequest.builder()
                .id(testSkillId)
                .name(uniqueSkillName)
                .category("Physics")
                .description("Quantum mechanical foundations")
                .isActive(true)
                .build();

        // 1. Create
        mockMvc.perform(post("/api/admin/skills")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testSkillId))
                .andExpect(jsonPath("$.name").value(uniqueSkillName));

        // 2. Update
        String updatedName = "Quantum Physics " + UUID.randomUUID().toString().substring(0, 5);
        createReq.setName(updatedName);
        mockMvc.perform(put("/api/admin/skills/" + testSkillId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedName));

        // 3. De-activate
        mockMvc.perform(delete("/api/admin/skills/" + testSkillId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Skill deactivated = skillRepository.findById(testSkillId).orElse(null);
        assertNotNull(deactivated);
        assertFalse(deactivated.getIsActive());
    }

    @Test
    @DisplayName("Career Requirement Validation: Rejects requiredLevel outside 1-5")
    void testCareerRequirement_InvalidLevelRejected() throws Exception {
        CareerSkillRequirementRequest req = CareerSkillRequirementRequest.builder()
                .skillId("python")
                .requiredLevel(99) // Invalid level > 5
                .isEssential(true)
                .build();

        mockMvc.perform(post("/api/admin/careers/career-ai-ml/requirements")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Config & Stats: ADMIN can retrieve system config and live stats")
    void testConfigAndStats_AdminGet() throws Exception {
        mockMvc.perform(get("/api/admin/config")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicalWeight").exists())
                .andExpect(jsonPath("$.questionnaireWeight").exists());

        mockMvc.perform(get("/api/admin/stats")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCareers").isNumber())
                .andExpect(jsonPath("$.activeSkills").isNumber())
                .andExpect(jsonPath("$.totalUsers").isNumber());
    }
}
