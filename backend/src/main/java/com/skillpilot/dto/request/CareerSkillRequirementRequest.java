package com.skillpilot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerSkillRequirementRequest {

    @NotBlank(message = "Skill ID is required")
    private String skillId;

    @Min(value = 1, message = "Required level must be at least 1")
    @Max(value = 5, message = "Required level cannot exceed 5")
    @Builder.Default
    private Integer requiredLevel = 3;

    @Builder.Default
    private Boolean isEssential = false;
}
