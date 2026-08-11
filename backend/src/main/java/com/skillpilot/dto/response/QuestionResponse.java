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
public class QuestionResponse {
    private String id;
    private String section;
    private String question;
    private String description;
    private String type;
    private Integer displayOrder;
    private Boolean isActive;
    private List<QuestionOptionResponse> options;
}
