package com.skillpilot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerImpactResponse {
    private String careerId;
    private String title;
    private String category;
    @JsonProperty("isActive")
    private boolean isActive;
    private int requiredSkillCount;
    private int essentialSkillCount;
    private int questionnaireCount;
    private int optionMappingCount;
    private int activeMatchResultCount;
    private int activeRoadmapCount;
    @JsonProperty("isConfigurationComplete")
    private boolean isConfigurationComplete;
    private List<String> requiredSkillNames;
}
