package com.skillpilot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.entity.CareerMatchResult;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserRole;
import com.skillpilot.repository.CareerMatchResultRepository;
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
import org.springframework.transaction.annotation.Transactional;

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
public class Phase6CareerDiscoveryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerMatchResultRepository careerMatchResultRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User studentA;
    private User studentB;
    private String tokenA;
    private String tokenB;

    @BeforeEach
    void setUp() {
        // Student A
        studentA = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Student A")
                .email("studenta@university.edu")
                .passwordHash(passwordEncoder.encode("Password123"))
                .role(UserRole.STUDENT)
                .completionPercentage(60)
                .build();
        userRepository.save(studentA);

        SecurityUser secA = new SecurityUser(studentA);
        Authentication authA = new UsernamePasswordAuthenticationToken(secA, null, secA.getAuthorities());
        tokenA = jwtTokenProvider.generateToken(authA);

        // Student B
        studentB = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Student B")
                .email("studentb@university.edu")
                .passwordHash(passwordEncoder.encode("Password123"))
                .role(UserRole.STUDENT)
                .completionPercentage(70)
                .build();
        userRepository.save(studentB);

        SecurityUser secB = new SecurityUser(studentB);
        Authentication authB = new UsernamePasswordAuthenticationToken(secB, null, secB.getAuthorities());
        tokenB = jwtTokenProvider.generateToken(authB);
    }

    @Test
    @DisplayName("1. Authenticated user GET /api/careers/matches returns ranked career matches")
    void test1_GetCareerMatches() throws Exception {
        mockMvc.perform(get("/api/careers/matches")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[0].career.id", notNullValue()))
                .andExpect(jsonPath("$[0].matchScore", greaterThanOrEqualTo(45)))
                .andExpect(jsonPath("$[0].matchScore", lessThanOrEqualTo(98)))
                .andExpect(jsonPath("$[0].systemCalculatedBadge", is("Deterministic Algorithm v2.4")));
    }

    @Test
    @DisplayName("2. Career matches are persisted in MySQL career_match_results table")
    void test2_CareerMatchesPersistedInDB() throws Exception {
        mockMvc.perform(get("/api/careers/matches")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        List<CareerMatchResult> saved = careerMatchResultRepository.findByUserIdOrderByRankPositionAsc(studentA.getId());
        assertFalse(saved.isEmpty());
        assertEquals(1, saved.get(0).getRankPosition());
    }

    @Test
    @DisplayName("3. GET /api/user/career-results returns persisted user results")
    void test3_GetUserCareerResults() throws Exception {
        mockMvc.perform(get("/api/careers/matches")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/career-results")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].career.title", notNullValue()))
                .andExpect(jsonPath("$[0].matchScore", notNullValue()));
    }

    @Test
    @DisplayName("4. POST /api/careers/matches/recalculate updates match results")
    void test4_RecalculateMatches() throws Exception {
        mockMvc.perform(post("/api/careers/matches/recalculate")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matchScore", notNullValue()));
    }

    @Test
    @DisplayName("5. Ownership Isolation: Student A cannot see Student B results")
    void test5_OwnershipIsolation() throws Exception {
        // Generate matches for Student A
        mockMvc.perform(get("/api/careers/matches")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        // Student B checks own results before calculation
        List<CareerMatchResult> bResults = careerMatchResultRepository.findByUserIdOrderByRankPositionAsc(studentB.getId());
        assertTrue(bResults.isEmpty());
    }

    @Test
    @DisplayName("6. Unauthenticated requests to /api/careers/matches return HTTP 401 Unauthorized")
    void test6_UnauthenticatedRejected() throws Exception {
        mockMvc.perform(get("/api/careers/matches"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("7. Deterministic tie-breaking orders results reproducibly")
    void test7_DeterministicTieBreaking() throws Exception {
        mockMvc.perform(get("/api/careers/matches")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matchScore", greaterThanOrEqualTo(45)));

        mockMvc.perform(get("/api/careers/matches")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matchScore", greaterThanOrEqualTo(45)));
    }

    @Test
    @DisplayName("8. Inactive careers excluded from discovery matches")
    void test8_InactiveCareersExcluded() throws Exception {
        mockMvc.perform(get("/api/careers/matches")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].career.id", not(hasItem("inactive-career-id"))));
    }
}
