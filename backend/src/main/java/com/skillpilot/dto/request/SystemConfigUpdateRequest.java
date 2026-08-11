package com.skillpilot.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfigUpdateRequest {

    @DecimalMin(value = "0.05", message = "Technical weight must be at least 0.05")
    @DecimalMax(value = "0.85", message = "Technical weight cannot exceed 0.85")
    private BigDecimal technicalWeight;

    @DecimalMin(value = "0.05", message = "Questionnaire weight must be at least 0.05")
    @DecimalMax(value = "0.85", message = "Questionnaire weight cannot exceed 0.85")
    private BigDecimal questionnaireWeight;

    @DecimalMin(value = "0.05", message = "Essential skill penalty must be at least 0.05")
    @DecimalMax(value = "0.50", message = "Essential skill penalty cannot exceed 0.50")
    private BigDecimal essentialSkillPenalty;

    @Min(value = 10, message = "Minimum match threshold must be at least 10")
    @Max(value = 90, message = "Minimum match threshold cannot exceed 90")
    private Integer minimumMatchThreshold;
}
