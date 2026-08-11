package com.skillpilot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapGenerateRequest {

    @Min(value = 6, message = "Roadmap duration must be at least 6 months")
    @Max(value = 12, message = "Roadmap duration cannot exceed 12 months")
    @Builder.Default
    private Integer durationMonths = 6;
}
