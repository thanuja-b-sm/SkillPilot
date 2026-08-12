package com.skillpilot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSkillMappingRequest {

    @NotBlank(message = "Option ID is required")
    private String optionId;

    @NotBlank(message = "Skill ID is required")
    private String skillId;

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be at least 1")
    @Max(value = 5, message = "Weight cannot exceed 5")
    private Integer weight;
}
