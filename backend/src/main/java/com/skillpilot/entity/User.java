package com.skillpilot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.STUDENT;

    @Column(length = 150)
    @Builder.Default
    private String title = "Student Profile";

    @Column(length = 200)
    @Builder.Default
    private String education = "";

    @Column(name = "institution_name", length = 150)
    private String institutionName;

    @Column(name = "degree_level", length = 100)
    private String degreeLevel;

    @Column(name = "major_field_of_study", length = 150)
    private String majorFieldOfStudy;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "education_status", length = 50)
    private String educationStatus;

    @Column(name = "experience_years", nullable = false)
    @Builder.Default
    private Integer experienceYears = 0;

    @Column(name = "employment_status", length = 100)
    private String employmentStatus;

    @Column(name = "current_job_title", length = 150)
    private String currentJobTitle;

    @Column(name = "current_industry", length = 100)
    private String currentIndustry;

    @Column(name = "relevant_experience_years")
    @Builder.Default
    private Integer relevantExperienceYears = 0;

    @Column(length = 100)
    @Builder.Default
    private String location = "";

    @Column(length = 100)
    private String country;

    @Column(name = "date_of_birth", length = 20)
    private String dateOfBirth;

    @Column(name = "target_focus", length = 150)
    @Builder.Default
    private String targetFocus = "";

    @Column(name = "preferred_work_mode", length = 50)
    private String preferredWorkMode;

    @Column(name = "preferred_employment_type", length = 50)
    private String preferredEmploymentType;

    @Column(name = "career_goal", length = 255)
    private String careerGoal;

    @Column(name = "weekly_hours_available")
    @Builder.Default
    private Integer weeklyHoursAvailable = 10;

    @Column(name = "preferred_learning_pace", length = 50)
    @Builder.Default
    private String preferredLearningPace = "Steady";

    @Column(name = "preferred_roadmap_duration")
    @Builder.Default
    private Integer preferredRoadmapDuration = 6;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(name = "portfolio_url", length = 255)
    private String portfolioUrl;

    @Column(name = "career_interests", columnDefinition = "TEXT")
    private String careerInterests;

    @Column(name = "completion_percentage", nullable = false)
    @Builder.Default
    private Integer completionPercentage = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserQuestionAnswer> questionnaireAnswers = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserTargetCareer targetCareer;
}
