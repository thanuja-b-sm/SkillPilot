package com.skillpilot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_roadmap_milestones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapMilestone {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @Column(name = "phase_order", nullable = false)
    @Builder.Default
    private Integer phaseOrder = 1;

    @Column(name = "month_range", nullable = false, length = 50)
    private String monthRange;

    @Column(name = "phase_title", nullable = false, length = 150)
    private String phaseTitle;

    @Column(name = "focus_area", nullable = false, length = 200)
    private String focusArea;

    @Column(name = "expected_outcome", columnDefinition = "TEXT", nullable = false)
    private String expectedOutcome;

    @Column(columnDefinition = "JSON", nullable = false)
    private String goals; // JSON string array of goals e.g. ["Goal 1", "Goal 2"]

    @Column(name = "recommended_courses", columnDefinition = "JSON")
    private String recommendedCourses; // JSON string array of study topic tags

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "not_started"; // 'completed', 'in_progress', 'not_started'

    @Column(name = "completion_percentage", nullable = false)
    @Builder.Default
    private Integer completionPercentage = 0;

    @Column(name = "target_skill_id", length = 50)
    private String targetSkillId;

    @Column(name = "current_level")
    private Integer currentLevel;

    @Column(name = "required_level")
    private Integer requiredLevel;

    @Column(name = "gap_severity", length = 30)
    private String gapSeverity;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
