package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCareerExplanationResponse {
    private String careerTitle;
    private Integer matchScore;
    private String summary;
    private String explanation;
    private List<String> focusAreas;
    private String source; // 'gemini' or 'system-calculated'
}
