package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerMatchResultResponse {
    private CareerResponse career;
    private Integer matchScore;
    private List<String> keyStrengths;
    private List<String> keyGaps;
    private String confidenceLevel;
    private String fitReason;
    private String systemCalculatedBadge;
}
