package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapMilestoneResponse {
    private String id;
    private String monthRange;
    private String phaseTitle;
    private String focusArea;
    private List<String> goals;
    private String expectedOutcome;
    private List<String> recommendedCourses;
    private String status; // 'not_started', 'in_progress', 'completed'
}
