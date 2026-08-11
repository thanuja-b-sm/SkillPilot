package com.skillpilot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCreateUpdateRequest {

    private String id;

    @NotBlank(message = "Section is required")
    private String section;

    @NotBlank(message = "Question text is required")
    private String question;

    private String description;

    @NotBlank(message = "Question type is required (single, multiple, scale)")
    private String type;

    private Integer displayOrder;
}
