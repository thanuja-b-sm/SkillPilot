package com.skillpilot.service;

import com.skillpilot.dto.response.UserProfileResponse;
import com.skillpilot.dto.response.UserSkillResponse;
import com.skillpilot.entity.User;
import com.skillpilot.entity.UserSkill;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserProfileMapper {

    private final CompletionCalculatorService completionCalculatorService;

    public UserProfileMapper(CompletionCalculatorService completionCalculatorService) {
        this.completionCalculatorService = completionCalculatorService;
    }

    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        List<UserSkillResponse> skillResponses = new ArrayList<>();
        if (user.getSkills() != null) {
            skillResponses = user.getSkills().stream()
                    .map(this::toSkillResponse)
                    .collect(Collectors.toList());
        }

        int completion = completionCalculatorService.calculateCompletionPercentage(user);
        String roleValue = (user.getRole() != null) ? user.getRole().getValue() : "STUDENT";

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .title(user.getTitle() != null ? user.getTitle() : "Student Profile")
                .education(user.getEducation() != null ? user.getEducation() : "")
                .institutionName(user.getInstitutionName())
                .degreeLevel(user.getDegreeLevel())
                .majorFieldOfStudy(user.getMajorFieldOfStudy())
                .graduationYear(user.getGraduationYear())
                .educationStatus(user.getEducationStatus())
                .experienceYears(user.getExperienceYears() != null ? user.getExperienceYears() : 0)
                .employmentStatus(user.getEmploymentStatus())
                .currentJobTitle(user.getCurrentJobTitle())
                .currentIndustry(user.getCurrentIndustry())
                .relevantExperienceYears(user.getRelevantExperienceYears() != null ? user.getRelevantExperienceYears() : 0)
                .location(user.getLocation() != null ? user.getLocation() : "")
                .country(user.getCountry())
                .dateOfBirth(user.getDateOfBirth())
                .targetFocus(user.getTargetFocus() != null ? user.getTargetFocus() : "")
                .preferredWorkMode(user.getPreferredWorkMode())
                .preferredEmploymentType(user.getPreferredEmploymentType())
                .careerGoal(user.getCareerGoal())
                .weeklyHoursAvailable(user.getWeeklyHoursAvailable() != null ? user.getWeeklyHoursAvailable() : 10)
                .preferredLearningPace(user.getPreferredLearningPace() != null ? user.getPreferredLearningPace() : "Steady")
                .preferredRoadmapDuration(user.getPreferredRoadmapDuration() != null ? user.getPreferredRoadmapDuration() : 6)
                .bio(user.getBio() != null ? user.getBio() : "")
                .certifications(user.getCertifications())
                .portfolioUrl(user.getPortfolioUrl())
                .careerInterests(user.getCareerInterests())
                .completionPercentage(completion)
                .userRole(roleValue)
                .role(roleValue)
                .skills(skillResponses)
                .build();
    }

    public UserSkillResponse toSkillResponse(UserSkill userSkill) {
        if (userSkill == null) {
            return null;
        }

        return UserSkillResponse.builder()
                .skillId(userSkill.getSkill().getId())
                .name(userSkill.getSkill().getName())
                .category(userSkill.getSkill().getCategory())
                .level(userSkill.getLevel())
                .build();
    }
}
