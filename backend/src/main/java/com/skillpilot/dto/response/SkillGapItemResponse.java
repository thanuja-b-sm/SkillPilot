package com.skillpilot.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapItemResponse {

    private String skillId;
    private String skillName;
    private String category;
    private Integer currentLevel;
    private Integer requiredLevel;
    private Integer gapAmount;
    private String severity; // "critical", "high", "medium", "low"
    private String classification; // "CRITICAL", "IMPORTANT", "MINOR", "EXPERIENCE_SUPPORTED", "SATISFIED"
    private Boolean experienceSupported;
    private Boolean isEssential;
    private String recommendedAction;
}
