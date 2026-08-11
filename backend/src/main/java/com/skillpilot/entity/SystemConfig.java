package com.skillpilot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "technical_weight", nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal technicalWeight = new BigDecimal("0.500");

    @Column(name = "questionnaire_weight", nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal questionnaireWeight = new BigDecimal("0.350");

    @Column(name = "essential_skill_penalty", nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal essentialSkillPenalty = new BigDecimal("0.150");

    @Column(name = "minimum_match_threshold", nullable = false)
    @Builder.Default
    private Integer minimumMatchThreshold = 45;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
