package com.skillpilot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetCareerSelectionRequest {

    @NotBlank(message = "Career ID is required")
    private String careerId;
}
