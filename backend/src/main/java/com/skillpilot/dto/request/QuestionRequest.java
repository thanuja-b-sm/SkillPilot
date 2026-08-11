package com.skillpilot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    private String id;

    @NotBlank(message = "Section is required")
    private String section;

    @NotBlank(message = "Question text is required")
    private String question;

    private String description;

    @NotBlank(message = "Question type is required")
    private String type;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;

    private Boolean isActive;
}
