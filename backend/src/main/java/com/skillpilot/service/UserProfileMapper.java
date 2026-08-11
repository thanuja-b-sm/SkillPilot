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

        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .title(user.getTitle() != null ? user.getTitle() : "Student Profile")
                .education(user.getEducation() != null ? user.getEducation() : "")
                .experienceYears(user.getExperienceYears() != null ? user.getExperienceYears() : 0)
                .location(user.getLocation() != null ? user.getLocation() : "")
                .targetFocus(user.getTargetFocus() != null ? user.getTargetFocus() : "")
                .bio(user.getBio() != null ? user.getBio() : "")
                .completionPercentage(completion)
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
