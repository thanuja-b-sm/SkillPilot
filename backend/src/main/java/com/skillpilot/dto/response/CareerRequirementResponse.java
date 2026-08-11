package com.skillpilot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerRequirementResponse {
    private String id;
    private String skillId;
    private String skillName;
    private String category;
    private Integer requiredLevel;
    private Boolean isEssential;
}
