package com.skillpilot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedCareerResponse {
    private String id;
    private String careerId;
    private String title;
    private String category;
    private String description;
    private String averageSalary;
    private String growthRate;
    private String demandLevel;
    private String notes;
    private LocalDateTime savedAt;
}
