package com.skillpilot.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

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
}
