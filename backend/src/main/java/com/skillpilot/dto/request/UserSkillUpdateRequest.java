package com.skillpilot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSkillUpdateRequest {

    @NotBlank(message = "Skill ID is required")
    private String skillId;

    @NotNull(message = "Skill level is required")
    @Min(value = 0, message = "Skill level cannot be less than 0")
    @Max(value = 5, message = "Skill level cannot be greater than 5")
    private Integer level;
}
