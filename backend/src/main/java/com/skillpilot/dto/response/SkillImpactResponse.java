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
public class SkillImpactResponse {
    private String skillId;
    private String name;
    private String category;
    private boolean isActive;
    private int careerCount;
    private int careerRequirementCount;
    private int questionnaireMappingCount;
    private List<CareerSummary> affectedCareers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareerSummary {
        private String id;
        private String title;
        private boolean isActive;
    }
}
