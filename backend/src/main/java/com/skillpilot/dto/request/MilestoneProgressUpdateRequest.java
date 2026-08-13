package com.skillpilot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilestoneProgressUpdateRequest {

    private String status; // 'not_started', 'in_progress', 'completed'

    @Min(value = 0, message = "Completion percentage cannot be less than 0")
    @Max(value = 100, message = "Completion percentage cannot exceed 100")
    private Integer completionPercentage;

    private String notes;
}
