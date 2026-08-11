package com.skillpilot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiEnhanceSummaryRequest {

    @NotBlank(message = "Career title is required")
    private String careerTitle;

    private Integer currentMatchScore;
    private List<String> keyStrengths;
    private List<String> keyGaps;
    private String targetRoleGoal;
}
