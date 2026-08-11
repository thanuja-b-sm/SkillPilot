package com.skillpilot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roadmap_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapTemplate {

    @Id
    @Column(length = 64)
    private String id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "career_id", nullable = false, unique = true)
    private Career career;

    @Column(name = "overall_timeline", nullable = false, length = 100)
    private String overallTimeline;

    @Column(name = "default_explanation", columnDefinition = "TEXT", nullable = false)
    private String defaultExplanation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("phaseOrder ASC")
    @Builder.Default
    private List<RoadmapPhaseTemplate> phaseTemplates = new ArrayList<>();
}
