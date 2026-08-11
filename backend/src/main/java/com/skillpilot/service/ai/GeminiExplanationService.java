package com.skillpilot.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.config.GeminiProperties;
import com.skillpilot.dto.request.AiEnhanceSummaryRequest;
import com.skillpilot.dto.response.*;
import com.skillpilot.entity.AIGenerationLog;
import com.skillpilot.entity.User;
import com.skillpilot.repository.AIGenerationLogRepository;
import com.skillpilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GeminiExplanationService {

    private static final Logger log = LoggerFactory.getLogger(GeminiExplanationService.class);

    private final GeminiProperties geminiProperties;
    private final GeminiPromptBuilder promptBuilder;
    private final GeminiResponseValidator responseValidator;
    private final FallbackExplanationService fallbackService;
    private final AIGenerationLogRepository aiGenerationLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AiEnhanceSummaryResponse enhanceSummary(String userId, AiEnhanceSummaryRequest request) {
        if (!isGeminiConfigured()) {
            return AiEnhanceSummaryResponse.builder()
                    .enhancedExplanation(String.format("System Calculated Summary: Milestone plan tailored for %s. Prioritizes foundational requirements in early phases.", request.getCareerTitle()))
                    .build();
        }

        String prompt = promptBuilder.buildCareerExplanationPrompt(
                request.getCareerTitle(),
                request.getCurrentMatchScore() != null ? request.getCurrentMatchScore() : 75,
                request.getKeyStrengths(),
                request.getKeyGaps(),
                request.getTargetRoleGoal()
        );

        String rawResponse = callGeminiApi(prompt);
        if (responseValidator.isValidExplanationResponse(rawResponse)) {
            try {
                String cleanJson = responseValidator.cleanJsonFence(rawResponse);
                JsonNode node = objectMapper.readTree(cleanJson);
                String summary = node.has("summary") ? node.get("summary").asText() : "";
                String explanation = node.has("explanation") ? node.get("explanation").asText() : "";
                String resultText = !summary.isBlank() ? summary + " " + explanation : explanation;

                saveLog(userId, null, prompt, resultText, "SUCCESS", "gemini");
                return AiEnhanceSummaryResponse.builder().enhancedExplanation(resultText.trim()).build();
            } catch (Exception e) {
                log.warn("Error parsing valid Gemini explanation JSON: {}", e.getMessage());
            }
        }

        saveLog(userId, null, prompt, "Fallback applied", "FALLBACK", "system-calculated");
        return AiEnhanceSummaryResponse.builder()
                .enhancedExplanation(String.format("System Calculated Summary: Milestone plan tailored for %s. Prioritizes foundational requirements in early phases.", request.getCareerTitle()))
                .build();
    }

    public AiCareerExplanationResponse explainCareerMatch(String userId, String careerTitle, Integer matchScore, List<String> keyStrengths, List<String> keyGaps, String targetRoleGoal) {
        if (!isGeminiConfigured()) {
            return fallbackService.getCareerFallback(careerTitle, matchScore, keyStrengths, keyGaps);
        }

        String prompt = promptBuilder.buildCareerExplanationPrompt(careerTitle, matchScore != null ? matchScore : 75, keyStrengths, keyGaps, targetRoleGoal);
        String rawResponse = callGeminiApi(prompt);

        if (responseValidator.isValidExplanationResponse(rawResponse)) {
            try {
                String cleanJson = responseValidator.cleanJsonFence(rawResponse);
                JsonNode node = objectMapper.readTree(cleanJson);
                List<String> focus = new ArrayList<>();
                if (node.has("focusAreas") && node.get("focusAreas").isArray()) {
                    node.get("focusAreas").forEach(n -> focus.add(n.asText()));
                }

                saveLog(userId, null, prompt, rawResponse, "SUCCESS", "gemini");
                return AiCareerExplanationResponse.builder()
                        .careerTitle(careerTitle)
                        .matchScore(matchScore != null ? matchScore : 75)
                        .summary(node.has("summary") ? node.get("summary").asText() : "")
                        .explanation(node.has("explanation") ? node.get("explanation").asText() : "")
                        .focusAreas(focus)
                        .source("gemini")
                        .build();
            } catch (Exception e) {
                log.warn("Gemini JSON parse failure: {}", e.getMessage());
            }
        }

        saveLog(userId, null, prompt, "Fallback applied", "FALLBACK", "system-calculated");
        return fallbackService.getCareerFallback(careerTitle, matchScore, keyStrengths, keyGaps);
    }

    public AiSkillGapExplanationResponse explainSkillGap(String userId, String careerTitle, Integer readinessScore, List<String> missingSkills) {
        if (!isGeminiConfigured()) {
            return fallbackService.getSkillGapFallback(careerTitle, readinessScore, missingSkills);
        }

        String prompt = promptBuilder.buildSkillGapPrompt(careerTitle, readinessScore != null ? readinessScore : 0, missingSkills);
        String rawResponse = callGeminiApi(prompt);

        if (responseValidator.isValidExplanationResponse(rawResponse)) {
            try {
                String cleanJson = responseValidator.cleanJsonFence(rawResponse);
                JsonNode node = objectMapper.readTree(cleanJson);
                List<String> gaps = new ArrayList<>();
                if (node.has("priorityGaps") && node.get("priorityGaps").isArray()) {
                    node.get("priorityGaps").forEach(n -> gaps.add(n.asText()));
                }

                saveLog(userId, null, prompt, rawResponse, "SUCCESS", "gemini");
                return AiSkillGapExplanationResponse.builder()
                        .careerTitle(careerTitle)
                        .readinessScore(readinessScore != null ? readinessScore : 0)
                        .summary(node.has("summary") ? node.get("summary").asText() : "")
                        .explanation(node.has("explanation") ? node.get("explanation").asText() : "")
                        .priorityGaps(gaps)
                        .source("gemini")
                        .build();
            } catch (Exception e) {
                log.warn("Gemini JSON parse failure: {}", e.getMessage());
            }
        }

        saveLog(userId, null, prompt, "Fallback applied", "FALLBACK", "system-calculated");
        return fallbackService.getSkillGapFallback(careerTitle, readinessScore, missingSkills);
    }

    public AiRoadmapExplanationResponse explainRoadmap(String userId, String careerTitle, String overallTimeline, Integer overallReadiness, List<String> phaseTitles) {
        if (!isGeminiConfigured()) {
            return fallbackService.getRoadmapFallback(careerTitle, overallTimeline, overallReadiness, phaseTitles);
        }

        String prompt = promptBuilder.buildRoadmapSummaryPrompt(careerTitle, overallTimeline, overallReadiness != null ? overallReadiness : 0, phaseTitles);
        String rawResponse = callGeminiApi(prompt);

        if (responseValidator.isValidExplanationResponse(rawResponse)) {
            try {
                String cleanJson = responseValidator.cleanJsonFence(rawResponse);
                JsonNode node = objectMapper.readTree(cleanJson);
                List<String> highlights = new ArrayList<>();
                if (node.has("stageHighlights") && node.get("stageHighlights").isArray()) {
                    node.get("stageHighlights").forEach(n -> highlights.add(n.asText()));
                }

                saveLog(userId, null, prompt, rawResponse, "SUCCESS", "gemini");
                return AiRoadmapExplanationResponse.builder()
                        .careerTitle(careerTitle)
                        .overallTimeline(overallTimeline)
                        .overallReadiness(overallReadiness != null ? overallReadiness : 0)
                        .summary(node.has("summary") ? node.get("summary").asText() : "")
                        .explanation(node.has("explanation") ? node.get("explanation").asText() : "")
                        .stageHighlights(highlights)
                        .source("gemini")
                        .build();
            } catch (Exception e) {
                log.warn("Gemini JSON parse failure: {}", e.getMessage());
            }
        }

        saveLog(userId, null, prompt, "Fallback applied", "FALLBACK", "system-calculated");
        return fallbackService.getRoadmapFallback(careerTitle, overallTimeline, overallReadiness, phaseTitles);
    }

    private String callGeminiApi(String prompt) {
        try {
            int timeout = geminiProperties.getTimeoutMs() > 0 ? geminiProperties.getTimeoutMs() : 5000;
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(timeout))
                    .build();

            String model = (geminiProperties.getModel() != null && !geminiProperties.getModel().isBlank())
                    ? geminiProperties.getModel()
                    : "gemini-1.5-flash";

            String uri = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                    model, geminiProperties.getApiKey());

            Map<String, Object> systemPart = Map.of("text", GeminiPromptBuilder.SYSTEM_INSTRUCTION);
            Map<String, Object> systemInstruction = Map.of("parts", List.of(systemPart));

            Map<String, Object> userPart = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(userPart));

            Map<String, Object> config = Map.of(
                    "temperature", geminiProperties.getTemperature(),
                    "maxOutputTokens", geminiProperties.getMaxOutputTokens(),
                    "responseMimeType", "application/json"
            );

            Map<String, Object> requestBodyMap = Map.of(
                    "systemInstruction", systemInstruction,
                    "contents", List.of(content),
                    "generationConfig", config
            );

            String requestBodyJson = objectMapper.writeValueAsString(requestBodyMap);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMillis(timeout))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.has("candidates") && root.get("candidates").isArray() && root.get("candidates").size() > 0) {
                    JsonNode cand = root.get("candidates").get(0);
                    if (cand.has("content") && cand.get("content").has("parts") && cand.get("content").get("parts").isArray()) {
                        JsonNode parts = cand.get("content").get("parts");
                        if (parts.size() > 0 && parts.get(0).has("text")) {
                            return parts.get(0).get("text").asText();
                        }
                    }
                }
            } else {
                log.warn("Gemini API call failed with status code {}, response: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.warn("Gemini API call exception: {}", e.getMessage(), e);
        }
        return null;
    }

    private boolean isGeminiConfigured() {
        return geminiProperties.isEnabled()
                && geminiProperties.getApiKey() != null
                && !geminiProperties.getApiKey().isBlank();
    }

    private void saveLog(String userId, String careerId, String prompt, String responseText, String status, String source) {
        try {
            User user = (userId != null) ? userRepository.findById(userId).orElse(null) : null;
            AIGenerationLog logEntity = AIGenerationLog.builder()
                    .id(UUID.randomUUID().toString())
                    .user(user)
                    .careerId(careerId)
                    .promptText(prompt != null ? prompt : "")
                    .responseText(responseText != null ? responseText : "")
                    .status(status)
                    .source(source)
                    .build();
            aiGenerationLogRepository.save(logEntity);
        } catch (Exception e) {
            log.warn("Could not save AI generation log: {}", e.getMessage());
        }
    }
}
