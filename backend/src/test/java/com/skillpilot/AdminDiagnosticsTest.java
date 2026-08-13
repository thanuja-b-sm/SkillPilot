package com.skillpilot;

import com.skillpilot.dto.response.SystemHealthResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.DemandLevel;
import com.skillpilot.repository.CareerRepository;
import com.skillpilot.service.SystemConfigService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AdminDiagnosticsTest {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private CareerRepository careerRepository;

    @Test
    @DisplayName("Verify getSystemHealth returns valid health score and warning diagnostics")
    void testGetSystemHealthDiagnostics() {
        SystemHealthResponse response = systemConfigService.getSystemHealth();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isNotNull();
        assertThat(response.getHealthScore()).isGreaterThanOrEqualTo(0).isLessThanOrEqualTo(100);
        assertThat(response.getWarnings()).isNotNull();
        assertThat(response.getErrors()).isNotNull();
    }

    @Test
    @DisplayName("Verify active career with 0 requirements triggers warning diagnostic")
    void testCareerWithoutRequirementsTriggersWarning() {
        Career emptyCareer = Career.builder()
                .id(UUID.randomUUID().toString())
                .title("Diagnostic Test Career " + UUID.randomUUID())
                .category("Engineering")
                .description("Test career for diagnostics")
                .averageSalary("$120,000 / yr")
                .growthRate("+20%")
                .demandLevel(DemandLevel.HIGH)
                .isActive(true)
                .build();

        careerRepository.saveAndFlush(emptyCareer);

        SystemHealthResponse health = systemConfigService.getSystemHealth();

        assertThat(health.getWarnings())
                .anyMatch(w -> w.contains(emptyCareer.getTitle()) && w.contains("0 required skills"));
    }
}
