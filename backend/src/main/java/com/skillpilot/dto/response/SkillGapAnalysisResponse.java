package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapAnalysisResponse {

    private CareerResponse career;
    private Integer readinessScore; // Retained for backward compatibility
    private Integer skillReadiness;
    private Integer experienceAlignment;
    private Integer educationAlignment;
    private Integer overallReadiness;

    private List<SkillGapItemResponse> skills;
    private List<String> strengths;
    private List<SkillGapItemResponse> missingSkills;
    private Integer totalRequiredSkills;
    private Integer completedSkills;
}
