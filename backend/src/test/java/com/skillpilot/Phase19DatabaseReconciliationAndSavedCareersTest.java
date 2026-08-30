package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.DemandLevel;
import com.skillpilot.entity.SavedCareer;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.repository.SavedCareerRepository;
import com.skillpilot.repository.UserRepository;
import com.skillpilot.security.JwtTokenProvider;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class Phase19DatabaseReconciliationAndSavedCareersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private SavedCareerRepository savedCareerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User userA;
    private User userB;
    private Career career1;
    private Career career2;
    private String tokenA;
    private String tokenB;

    @BeforeEach
    void setUp() throws Exception {
        savedCareerRepository.deleteAll();

        userA = userRepository.findByEmailIgnoreCase("alex.student@skillpilot.io")
                .orElseGet(() -> userRepository.save(User.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Alex Student")
                        .email("alex.student@skillpilot.io")
                        .passwordHash(passwordEncoder.encode("Password123!"))
                        .role(UserRole.STUDENT)
                        .title("Full Stack Aspirant")
                        .experienceYears(1)
                        .build()));

        userB = userRepository.findByEmailIgnoreCase("bianca.learner@skillpilot.io")
                .orElseGet(() -> userRepository.save(User.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Bianca Learner")
                        .email("bianca.learner@skillpilot.io")
                        .passwordHash(passwordEncoder.encode("Password123!"))
                        .role(UserRole.STUDENT)
                        .title("Data Science Explorer")
                        .experienceYears(2)
                        .build()));

        List<Career> allCareers = careerRepository.findAll();
        career1 = allCareers.get(0);
        career2 = allCareers.size() > 1 ? allCareers.get(1) : allCareers.get(0);

        tokenA = "Bearer " + obtainJwtToken(userA.getEmail(), "Password123!");
        tokenB = "Bearer " + obtainJwtToken(userB.getEmail(), "Password123!");
    }


    private String obtainJwtToken(String email, String password) throws Exception {
        Map<String, String> loginReq = Map.of("email", email, "password", password);
        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> resp = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        return (String) resp.get("token");
    }


    @Test
    @DisplayName("1. Student saves career and lists all saved careers")
    void test1_SaveCareerAndListForUser() throws Exception {
        mockMvc.perform(post("/api/user/saved-careers/" + career1.getId())
                        .header("Authorization", tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("notes", "Top choice for my transition"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.careerId", is(career1.getId())))
                .andExpect(jsonPath("$.title", is(career1.getTitle())))
                .andExpect(jsonPath("$.notes", is("Top choice for my transition")));

        // Verify MySQL persistence
        List<SavedCareer> savedList = savedCareerRepository.findByUserIdOrderByCreatedAtDesc(userA.getId());
        assertEquals(1, savedList.size());
        assertEquals(career1.getId(), savedList.get(0).getCareer().getId());

        // GET /api/user/saved-careers
        mockMvc.perform(get("/api/user/saved-careers")
                        .header("Authorization", tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].careerId", is(career1.getId())))
                .andExpect(jsonPath("$[0].title", is(career1.getTitle())));
    }

    @Test
    @DisplayName("2. Duplicate save is idempotent and updates notes")
    void test2_DuplicateSaveIsIdempotent() throws Exception {
        mockMvc.perform(post("/api/user/saved-careers/" + career1.getId())
                        .header("Authorization", tokenA))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/user/saved-careers/" + career1.getId())
                        .header("Authorization", tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("notes", "Updated note on second save"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes", is("Updated note on second save")));

        assertEquals(1, savedCareerRepository.countByUserId(userA.getId()));
    }

    @Test
    @DisplayName("3. Unsave career removes record from database")
    void test3_UnsaveCareerRemovesRecord() throws Exception {
        mockMvc.perform(post("/api/user/saved-careers/" + career1.getId())
                        .header("Authorization", tokenA))
                .andExpect(status().isOk());

        assertEquals(1, savedCareerRepository.countByUserId(userA.getId()));

        mockMvc.perform(delete("/api/user/saved-careers/" + career1.getId())
                        .header("Authorization", tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saved", is(false)));

        assertEquals(0, savedCareerRepository.countByUserId(userA.getId()));
    }

    @Test
    @DisplayName("4. Saved careers are strictly isolated between users")
    void test4_UserIsolation() throws Exception {
        // User A saves career 1
        mockMvc.perform(post("/api/user/saved-careers/" + career1.getId())
                        .header("Authorization", tokenA))
                .andExpect(status().isOk());

        // User B saves career 2
        mockMvc.perform(post("/api/user/saved-careers/" + career2.getId())
                        .header("Authorization", tokenB))
                .andExpect(status().isOk());

        // User A lists saved careers -> should only see career 1
        mockMvc.perform(get("/api/user/saved-careers")
                        .header("Authorization", tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].careerId", is(career1.getId())));

        // User B lists saved careers -> should only see career 2
        mockMvc.perform(get("/api/user/saved-careers")
                        .header("Authorization", tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].careerId", is(career2.getId())));
    }

    @Test
    @DisplayName("5. Status endpoint accurately reflects saved state")
    void test5_IsCareerSavedStatus() throws Exception {
        mockMvc.perform(get("/api/user/saved-careers/" + career1.getId() + "/status")
                        .header("Authorization", tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saved", is(false)));

        mockMvc.perform(post("/api/user/saved-careers/" + career1.getId())
                        .header("Authorization", tokenA))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/saved-careers/" + career1.getId() + "/status")
                        .header("Authorization", tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saved", is(true)));
    }
}
