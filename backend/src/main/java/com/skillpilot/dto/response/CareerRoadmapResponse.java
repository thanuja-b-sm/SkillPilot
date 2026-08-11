package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerRoadmapResponse {
    private String id;
    private String careerId;
    private String careerTitle;
    private String overallTimeline;
    private Integer overallReadiness;
    private String aiExplanation;
    private List<RoadmapMilestoneResponse> phases;
}
