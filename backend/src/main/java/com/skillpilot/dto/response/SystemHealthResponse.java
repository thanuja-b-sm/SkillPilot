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
public class SystemHealthResponse {
    private String status; // HEALTHY, WARNING, ERROR
    private int activeCareersCount;
    private int activeSkillsCount;
    private int totalRequirementsCount;
    private int totalQuestionnaireMappingsCount;
    private int totalQuestionsCount;
    private List<String> warnings;
    private List<String> errors;
}
