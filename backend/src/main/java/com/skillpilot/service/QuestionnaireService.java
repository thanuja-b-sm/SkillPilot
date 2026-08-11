package com.skillpilot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.request.QuestionAnswerRequest;
import com.skillpilot.dto.request.QuestionOptionRequest;
import com.skillpilot.dto.request.QuestionRequest;
import com.skillpilot.dto.response.QuestionOptionResponse;
import com.skillpilot.dto.response.QuestionResponse;
import com.skillpilot.dto.response.UserQuestionAnswerResponse;
import com.skillpilot.entity.*;
import com.skillpilot.exception.BadRequestException;
import com.skillpilot.exception.ResourceNotFoundException;
import com.skillpilot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final UserQuestionAnswerRepository userQuestionAnswerRepository;
    private final UserRepository userRepository;
    private final QuestionnaireMapper questionnaireMapper;
    private final CompletionCalculatorService completionCalculatorService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<QuestionResponse> getActiveQuestionnaire() {
        return questionRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(questionnaireMapper::toQuestionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestionsAdmin() {
        return questionRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Question::getDisplayOrder))
                .map(questionnaireMapper::toQuestionResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserQuestionAnswerResponse> saveUserAnswers(String userId, QuestionAnswerRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new BadRequestException("Questionnaire answers submission cannot be empty");
        }

        List<UserQuestionAnswerResponse> responses = new ArrayList<>();

        for (QuestionAnswerRequest.AnswerItem item : request.getAnswers()) {
            Question question = questionRepository.findById(item.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question", "id", item.getQuestionId()));

            if (Boolean.FALSE.equals(question.getIsActive())) {
                throw new BadRequestException("Cannot submit answer to inactive question: " + item.getQuestionId());
            }

            if (item.getSelectedOptionIds() == null || item.getSelectedOptionIds().isEmpty()) {
                throw new BadRequestException("Selected options required for question: " + item.getQuestionId());
            }

            List<String> optionTexts = new ArrayList<>();

            for (String optionId : item.getSelectedOptionIds()) {
                QuestionOption option = questionOptionRepository.findById(optionId)
                        .orElseThrow(() -> new ResourceNotFoundException("QuestionOption", "id", optionId));

                if (!option.getQuestion().getId().equals(question.getId())) {
                    throw new BadRequestException("Option " + optionId + " does not belong to Question " + question.getId());
                }

                optionTexts.add(option.getOptionText());
            }

            String jsonOptionIds;
            try {
                jsonOptionIds = objectMapper.writeValueAsString(item.getSelectedOptionIds());
            } catch (Exception e) {
                jsonOptionIds = "[]";
            }

            // Save or update user response
            UserQuestionAnswer answer = userQuestionAnswerRepository.findByUserIdAndQuestionId(userId, question.getId())
                    .orElseGet(() -> UserQuestionAnswer.builder()
                            .id(UUID.randomUUID().toString())
                            .user(user)
                            .question(question)
                            .selectedOptionIds("[]")
                            .build());

            answer.setSelectedOptionIds(jsonOptionIds);
            UserQuestionAnswer saved = userQuestionAnswerRepository.save(answer);

            responses.add(UserQuestionAnswerResponse.builder()
                    .questionId(question.getId())
                    .questionText(question.getQuestion())
                    .selectedOptionIds(item.getSelectedOptionIds())
                    .selectedOptionTexts(optionTexts)
                    .updatedAt(saved.getUpdatedAt())
                    .build());
        }

        // Recalculate completion percentage
        user.setCompletionPercentage(completionCalculatorService.calculateCompletionPercentage(user));
        userRepository.save(user);

        return responses;
    }

    @Transactional(readOnly = true)
    public List<UserQuestionAnswerResponse> getUserAnswers(String userId) {
        List<UserQuestionAnswer> answers = userQuestionAnswerRepository.findByUserId(userId);
        List<UserQuestionAnswerResponse> result = new ArrayList<>();

        for (UserQuestionAnswer a : answers) {
            List<String> optIds;
            try {
                optIds = objectMapper.readValue(a.getSelectedOptionIds(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                optIds = Collections.emptyList();
            }

            List<String> optionTexts = optIds.stream()
                    .map(id -> questionOptionRepository.findById(id).map(QuestionOption::getOptionText).orElse(id))
                    .collect(Collectors.toList());

            result.add(UserQuestionAnswerResponse.builder()
                    .questionId(a.getQuestion().getId())
                    .questionText(a.getQuestion().getQuestion())
                    .selectedOptionIds(optIds)
                    .selectedOptionTexts(optionTexts)
                    .updatedAt(a.getUpdatedAt())
                    .build());
        }

        return result;
    }

    @Transactional
    public QuestionResponse createQuestion(QuestionRequest req) {
        String id = (req.getId() != null && !req.getId().isBlank()) ? req.getId() : UUID.randomUUID().toString();

        Question question = Question.builder()
                .id(id)
                .section(req.getSection())
                .question(req.getQuestion())
                .description(req.getDescription())
                .type(parseQuestionType(req.getType()))
                .displayOrder(req.getDisplayOrder())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .options(new ArrayList<>())
                .build();

        Question saved = questionRepository.save(question);
        return questionnaireMapper.toQuestionResponse(saved);
    }

    @Transactional
    public QuestionResponse updateQuestion(String id, QuestionRequest req) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        question.setSection(req.getSection());
        question.setQuestion(req.getQuestion());
        if (req.getDescription() != null) question.setDescription(req.getDescription());
        if (req.getType() != null) question.setType(parseQuestionType(req.getType()));
        if (req.getDisplayOrder() != null) question.setDisplayOrder(req.getDisplayOrder());
        if (req.getIsActive() != null) question.setIsActive(req.getIsActive());

        Question saved = questionRepository.save(question);
        return questionnaireMapper.toQuestionResponse(saved);
    }

    @Transactional
    public void deleteQuestion(String id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        // Soft deactivation to preserve user answer history
        question.setIsActive(false);
        questionRepository.save(question);
    }

    @Transactional
    public QuestionOptionResponse createOption(String questionId, QuestionOptionRequest req) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        String id = (req.getId() != null && !req.getId().isBlank()) ? req.getId() : UUID.randomUUID().toString();

        QuestionOption option = QuestionOption.builder()
                .id(id)
                .question(question)
                .optionText(req.getOptionText())
                .displayOrder(req.getDisplayOrder())
                .associatedSkills(new ArrayList<>())
                .build();

        QuestionOption saved = questionOptionRepository.save(option);
        return questionnaireMapper.toQuestionOptionResponse(saved);
    }

    @Transactional
    public QuestionOptionResponse updateOption(String id, QuestionOptionRequest req) {
        QuestionOption option = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionOption", "id", id));

        option.setOptionText(req.getOptionText());
        if (req.getDisplayOrder() != null) option.setDisplayOrder(req.getDisplayOrder());

        QuestionOption saved = questionOptionRepository.save(option);
        return questionnaireMapper.toQuestionOptionResponse(saved);
    }

    @Transactional
    public void deleteOption(String id) {
        QuestionOption option = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionOption", "id", id));
        questionOptionRepository.delete(option);
    }

    private QuestionType parseQuestionType(String typeStr) {
        if (typeStr == null) return QuestionType.SINGLE;
        switch (typeStr.toLowerCase()) {
            case "multiple":
                return QuestionType.MULTIPLE;
            case "scale":
                return QuestionType.SCALE;
            default:
                return QuestionType.SINGLE;
        }
    }
}
