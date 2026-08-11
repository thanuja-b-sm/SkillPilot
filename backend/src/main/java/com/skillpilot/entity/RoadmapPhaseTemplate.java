package com.skillpilot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roadmap_phase_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapPhaseTemplate {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roadmap_template_id", nullable = false)
    private RoadmapTemplate template;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "roadmap_phase_goals", joinColumns = @JoinColumn(name = "phase_id"))
    @Column(name = "goal_text", columnDefinition = "TEXT", nullable = false)
    @OrderColumn(name = "goal_order")
    @Builder.Default
    private List<String> goals = new ArrayList<>();
}
