package com.skillpilot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerMatchResponse {

    private CareerResponse career;
    private Integer matchScore;
    private List<String> keyStrengths;
    private List<String> keyGaps;
    private String confidenceLevel;
    private String fitReason;
    private String systemCalculatedBadge;
}
