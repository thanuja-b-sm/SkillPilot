package com.skillpilot.dto.request;

import jakarta.validation.constraints.NotEmpty;
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
public class QuestionAnswerRequest {
    @NotEmpty(message = "Answers payload cannot be empty")
    private List<AnswerItem> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerItem {
        @NotNull(message = "Question ID is required")
        private String questionId;

        @NotNull(message = "Selected option IDs are required")
        private List<String> selectedOptionIds;
    }
}
