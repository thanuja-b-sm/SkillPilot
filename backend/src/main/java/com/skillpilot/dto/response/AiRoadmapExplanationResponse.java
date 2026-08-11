package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRoadmapExplanationResponse {
    private String careerTitle;
    private String overallTimeline;
    private Integer overallReadiness;
    private String summary;
    private String explanation;
    private List<String> stageHighlights;
    private String source;
}
