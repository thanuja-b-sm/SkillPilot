package com.skillpilot.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionnaireAnswerSubmission {

    @NotNull(message = "Answers payload is required")
    private Map<String, Object> answers; // e.g. {"q1": "q1-ai", "q2": ["q2-coding", "q2-architecture"]}
}
