package com.skillpilot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "career_match_results", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_career_match", columnNames = {"user_id", "career_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerMatchResult {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @Column(name = "match_score", nullable = false)
    private Integer matchScore;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @Column(name = "confidence_level", nullable = false, length = 20)
    private String confidenceLevel;

    @Column(name = "fit_reason", columnDefinition = "TEXT", nullable = false)
    private String fitReason;

    @Column(name = "system_calculated_badge", nullable = false, length = 100)
    private String systemCalculatedBadge;

    @Column(name = "key_strengths", columnDefinition = "JSON")
    private String keyStrengthsJson;

    @Column(name = "key_gaps", columnDefinition = "JSON")
    private String keyGapsJson;

    @Column(name = "scoring_version", nullable = false, length = 20)
    @Builder.Default
    private String scoringVersion = "v2.4";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
