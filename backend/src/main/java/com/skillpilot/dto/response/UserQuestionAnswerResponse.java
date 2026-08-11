package com.skillpilot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestionAnswerResponse {
    private String questionId;
    private String questionText;
    private List<String> selectedOptionIds;
    private List<String> selectedOptionTexts;
    private LocalDateTime updatedAt;
}
