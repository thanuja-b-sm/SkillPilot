package com.skillpilot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerRequest {
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;
    private String averageSalary;
    private String growthRate;
    private String demandLevel;
    private Boolean isActive;
    private List<String> typicalRoles;
    private List<String> recommendedPrerequisites;
}
