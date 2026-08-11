package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapDiagnosticResponse {
    private String careerId;
    private Integer readinessRatio;
    private List<SkillGapItemResponse> skillGaps;
}
