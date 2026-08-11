package com.skillpilot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "careers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Career {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, unique = true, length = 150)
    private String title;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "average_salary", nullable = false, length = 100)
    private String averageSalary;

    @Column(name = "growth_rate", nullable = false, length = 100)
    private String growthRate;

    @Convert(converter = DemandLevelConverter.class)
    @Column(name = "demand_level", nullable = false, length = 20)
    private DemandLevel demandLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "career_typical_roles", joinColumns = @JoinColumn(name = "career_id"))
    @Column(name = "role_name", nullable = false, length = 150)
    @Builder.Default
    private List<String> typicalRoles = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "career_prerequisites", joinColumns = @JoinColumn(name = "career_id"))
    @Column(name = "prerequisite", nullable = false, length = 200)
    @Builder.Default
    private List<String> recommendedPrerequisites = new ArrayList<>();

    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<CareerSkillRequirement> requiredSkills = new ArrayList<>();

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
