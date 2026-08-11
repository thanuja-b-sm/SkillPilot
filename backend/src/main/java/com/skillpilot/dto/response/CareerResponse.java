package com.skillpilot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerResponse {
    private String id;
    private String title;
    private String category;
    private String description;
    private String averageSalary;
    private String growthRate;
    private String demandLevel;
    private Boolean isActive;
    private List<String> typicalRoles;
    private List<String> recommendedPrerequisites;
    private List<CareerRequirementResponse> requiredSkills;
}
