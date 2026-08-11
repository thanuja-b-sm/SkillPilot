package com.skillpilot.controller;

import com.skillpilot.dto.request.CareerRequest;
import com.skillpilot.dto.request.QuestionOptionRequest;
import com.skillpilot.dto.request.QuestionRequest;
import com.skillpilot.dto.request.SkillRequest;
import com.skillpilot.dto.response.CareerResponse;
import com.skillpilot.dto.response.QuestionOptionResponse;
import com.skillpilot.dto.response.QuestionResponse;
import com.skillpilot.entity.Skill;
import com.skillpilot.service.CareerService;
import com.skillpilot.service.QuestionnaireService;
import com.skillpilot.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMasterDataController {

    private final CareerService careerService;
    private final SkillService skillService;
    private final QuestionnaireService questionnaireService;

    // --- CAREERS ---
    @GetMapping("/careers")
    public ResponseEntity<List<CareerResponse>> getAllCareers() {
        return ResponseEntity.ok(careerService.getAllCareersAdmin());
    }

    @PostMapping("/careers")
    public ResponseEntity<CareerResponse> createCareer(@Valid @RequestBody CareerRequest req) {
        return new ResponseEntity<>(careerService.createCareer(req), HttpStatus.CREATED);
    }

    @PutMapping("/careers/{id}")
    public ResponseEntity<CareerResponse> updateCareer(@PathVariable String id, @Valid @RequestBody CareerRequest req) {
        return ResponseEntity.ok(careerService.updateCareer(id, req));
    }

    @DeleteMapping("/careers/{id}")
    public ResponseEntity<Void> deleteCareer(@PathVariable String id) {
        careerService.deleteCareer(id);
        return ResponseEntity.noContent().build();
    }

    // --- SKILLS ---
    @GetMapping("/skills")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkillsAdmin());
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody SkillRequest req) {
        return new ResponseEntity<>(skillService.createSkill(req), HttpStatus.CREATED);
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable String id, @Valid @RequestBody SkillRequest req) {
        return ResponseEntity.ok(skillService.updateSkill(id, req));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable String id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    // --- QUESTIONNAIRE ---
    @GetMapping("/questionnaire")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok(questionnaireService.getAllQuestionsAdmin());
    }

    @PostMapping("/questionnaire")
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest req) {
        return new ResponseEntity<>(questionnaireService.createQuestion(req), HttpStatus.CREATED);
    }

    @PutMapping("/questionnaire/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable String id, @Valid @RequestBody QuestionRequest req) {
        return ResponseEntity.ok(questionnaireService.updateQuestion(id, req));
    }

    @DeleteMapping("/questionnaire/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String id) {
        questionnaireService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    // --- OPTIONS ---
    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<QuestionOptionResponse> createOption(
            @PathVariable String questionId,
            @Valid @RequestBody QuestionOptionRequest req) {
        return new ResponseEntity<>(questionnaireService.createOption(questionId, req), HttpStatus.CREATED);
    }

    @PutMapping("/question-options/{id}")
    public ResponseEntity<QuestionOptionResponse> updateOption(
            @PathVariable String id,
            @Valid @RequestBody QuestionOptionRequest req) {
        return ResponseEntity.ok(questionnaireService.updateOption(id, req));
    }

    @DeleteMapping("/question-options/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable String id) {
        questionnaireService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }

    // --- CAREER REQUIREMENTS ---
    @PostMapping("/careers/{careerId}/requirements")
    public ResponseEntity<CareerResponse> addCareerRequirement(
            @PathVariable String careerId,
            @Valid @RequestBody com.skillpilot.dto.request.CareerSkillRequirementRequest req) {
        return ResponseEntity.ok(careerService.addRequirement(careerId, req));
    }

    @PutMapping("/career-requirements/{id}")
    public ResponseEntity<CareerResponse> updateCareerRequirement(
            @PathVariable String id,
            @Valid @RequestBody com.skillpilot.dto.request.CareerSkillRequirementRequest req) {
        return ResponseEntity.ok(careerService.updateRequirement(id, req));
    }

    @DeleteMapping("/career-requirements/{id}")
    public ResponseEntity<Void> deleteCareerRequirement(@PathVariable String id) {
        careerService.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }
}
