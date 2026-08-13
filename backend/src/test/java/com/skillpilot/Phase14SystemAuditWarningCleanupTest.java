package com.skillpilot;

import com.skillpilot.dto.response.SystemHealthResponse;
import com.skillpilot.service.SystemConfigService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class Phase14SystemAuditWarningCleanupTest {

    @Autowired
    private SystemConfigService systemConfigService;

    @Test
    @DisplayName("Verify master dataset system health audit reports HEALTHY with zero warnings and zero errors")
    void testSystemHealthCleanAndHealthy() {
        SystemHealthResponse health = systemConfigService.getSystemHealth();

        assertThat(health).isNotNull();
        if (!health.getWarnings().isEmpty()) {
            System.out.println("REMAINING WARNINGS (" + health.getWarnings().size() + "):");
            health.getWarnings().forEach(w -> System.out.println(" - " + w));
        }
        assertThat(health.getStatus()).isEqualTo("HEALTHY");
        assertThat(health.getHealthScore()).isEqualTo(100);
        assertThat(health.getErrors()).isEmpty();
        assertThat(health.getWarnings()).isEmpty();
    }
}
