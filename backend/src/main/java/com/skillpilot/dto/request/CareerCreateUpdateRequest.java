package com.skillpilot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerCreateUpdateRequest {

    private String id;

    @NotBlank(message = "Career title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    private String averageSalary;
    private String growthRate;
    private String demandLevel;
    private List<String> typicalRoles;
    private List<String> recommendedPrerequisites;
}
