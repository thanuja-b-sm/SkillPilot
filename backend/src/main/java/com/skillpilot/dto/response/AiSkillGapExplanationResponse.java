package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiSkillGapExplanationResponse {
    private String careerTitle;
    private Integer readinessScore;
    private String summary;
    private String explanation;
    private List<String> priorityGaps;
    private String source;
}
