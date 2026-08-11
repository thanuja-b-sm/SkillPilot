package com.skillpilot.service;

import com.skillpilot.dto.response.CareerRequirementResponse;
import com.skillpilot.dto.response.CareerResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.CareerSkillRequirement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CareerMapper {

    public CareerResponse toCareerResponse(Career career) {
        if (career == null) {
            return null;
        }

        List<CareerRequirementResponse> reqs = (career.getRequiredSkills() != null)
                ? career.getRequiredSkills().stream()
                .map(this::toCareerRequirementResponse)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return CareerResponse.builder()
                .id(career.getId())
                .title(career.getTitle())
                .category(career.getCategory())
                .description(career.getDescription())
                .averageSalary(career.getAverageSalary())
                .growthRate(career.getGrowthRate())
                .demandLevel(career.getDemandLevel() != null ? career.getDemandLevel().getValue() : null)
                .isActive(career.getIsActive())
                .typicalRoles(career.getTypicalRoles() != null ? new ArrayList<>(career.getTypicalRoles()) : Collections.emptyList())
                .recommendedPrerequisites(career.getRecommendedPrerequisites() != null ? new ArrayList<>(career.getRecommendedPrerequisites()) : Collections.emptyList())
                .requiredSkills(reqs)
                .build();
    }

    public CareerRequirementResponse toCareerRequirementResponse(CareerSkillRequirement req) {
        if (req == null) {
            return null;
        }

        return CareerRequirementResponse.builder()
                .id(req.getId())
                .skillId(req.getSkill() != null ? req.getSkill().getId() : null)
                .skillName(req.getSkill() != null ? req.getSkill().getName() : null)
                .category(req.getSkill() != null ? req.getSkill().getCategory() : null)
                .requiredLevel(req.getRequiredLevel())
                .isEssential(req.getIsEssential())
                .build();
    }
}
