package com.skillpilot.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeminiResponseValidator {

    private final ObjectMapper objectMapper;

    public boolean isValidExplanationResponse(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            return false;
        }

        try {
            // Unwrap markdown code fence ```json ... ``` if returned by model
            String cleanJson = cleanJsonFence(jsonString);
            JsonNode root = objectMapper.readTree(cleanJson);

            // Must be object and contain at least summary or explanation
            if (!root.isObject()) {
                return false;
            }

            boolean hasSummary = root.has("summary") && !root.get("summary").asText().isBlank();
            boolean hasExplanation = root.has("explanation") && !root.get("explanation").asText().isBlank();

            if (!hasSummary && !hasExplanation) {
                return false;
            }

            // Text length upper sanity bound
            String summary = root.has("summary") ? root.get("summary").asText() : "";
            String explanation = root.has("explanation") ? root.get("explanation").asText() : "";

            if (summary.length() > 1500 || explanation.length() > 2500) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String cleanJsonFence(String text) {
        if (text == null) return "";
        String trimmed = text.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }
}
