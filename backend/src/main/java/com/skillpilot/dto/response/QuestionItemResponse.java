package com.skillpilot.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionItemResponse {
    private String id;
    private String section;
    private String question;
    private String description;
    private String type;
    private List<QuestionOptionResponse> options;
}
