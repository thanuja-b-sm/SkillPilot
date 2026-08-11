package com.skillpilot.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfigResponse {
    private BigDecimal technicalWeight;
    private BigDecimal questionnaireWeight;
    private BigDecimal essentialSkillPenalty;
    private Integer minimumMatchThreshold;
}
