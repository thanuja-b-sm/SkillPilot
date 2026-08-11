package com.skillpilot.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequirementResponse {
    private String skillId;
    private String skillName;
    private String category;
    private Integer requiredLevel;
    private Boolean isEssential;
}
