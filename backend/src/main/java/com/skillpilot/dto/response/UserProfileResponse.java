package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private String title;

    private String education;
    private String institutionName;
    private String degreeLevel;
    private String majorFieldOfStudy;
    private Integer graduationYear;
    private String educationStatus;

    private Integer experienceYears;
    private String employmentStatus;
    private String currentJobTitle;
    private String currentIndustry;
    private Integer relevantExperienceYears;

    private String location;
    private String country;
    private String dateOfBirth;

    private String targetFocus;
    private String preferredWorkMode;
    private String preferredEmploymentType;
    private String careerGoal;

    private Integer weeklyHoursAvailable;
    private String preferredLearningPace;
    private Integer preferredRoadmapDuration;

    private String bio;
    private String certifications;
    private String portfolioUrl;
    private String careerInterests;

    private Integer completionPercentage;
    private String userRole;
    private String role;
    private List<UserSkillResponse> skills;
}
