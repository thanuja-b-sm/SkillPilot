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
public class QuestionOptionRequest {
    private String id;

    @NotBlank(message = "Question ID is required")
    private String questionId;

    @NotBlank(message = "Option text is required")
    private String optionText;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}
