package com.skillpilot.controller;

import com.skillpilot.dto.request.QuestionAnswerRequest;
import com.skillpilot.dto.response.QuestionResponse;
import com.skillpilot.dto.response.UserQuestionAnswerResponse;
import com.skillpilot.security.SecurityUser;
import com.skillpilot.service.QuestionnaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaire")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getActiveQuestionnaire() {
        return ResponseEntity.ok(questionnaireService.getActiveQuestionnaire());
    }

    @PostMapping("/answers")
    public ResponseEntity<List<UserQuestionAnswerResponse>> submitAnswers(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody QuestionAnswerRequest request) {
        return ResponseEntity.ok(questionnaireService.saveUserAnswers(securityUser.getId(), request));
    }

    @GetMapping("/answers")
    public ResponseEntity<List<UserQuestionAnswerResponse>> getUserAnswers(
            @AuthenticationPrincipal SecurityUser securityUser) {
        return ResponseEntity.ok(questionnaireService.getUserAnswers(securityUser.getId()));
    }
}
