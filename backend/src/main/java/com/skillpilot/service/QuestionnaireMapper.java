package com.skillpilot.service;

import com.skillpilot.dto.response.QuestionOptionResponse;
import com.skillpilot.dto.response.QuestionResponse;
import com.skillpilot.entity.Question;
import com.skillpilot.entity.QuestionOption;
import com.skillpilot.entity.QuestionSkillMapping;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionnaireMapper {

    public QuestionResponse toQuestionResponse(Question question) {
        if (question == null) {
            return null;
        }

        List<QuestionOptionResponse> optionDtos = (question.getOptions() != null)
                ? question.getOptions().stream()
                .sorted(Comparator.comparingInt(QuestionOption::getDisplayOrder))
                .map(this::toQuestionOptionResponse)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return QuestionResponse.builder()
                .id(question.getId())
                .section(question.getSection())
                .question(question.getQuestion())
                .description(question.getDescription())
                .type(question.getType() != null ? question.getType().name().toLowerCase() : "single")
                .displayOrder(question.getDisplayOrder())
                .isActive(question.getIsActive())
                .options(optionDtos)
                .build();
    }

    public QuestionOptionResponse toQuestionOptionResponse(QuestionOption option) {
        if (option == null) {
            return null;
        }

        List<QuestionOptionResponse.SkillMappingResponse> skillMappings = (option.getAssociatedSkills() != null)
                ? option.getAssociatedSkills().stream()
                .map(this::toSkillMappingResponse)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return QuestionOptionResponse.builder()
                .id(option.getId())
                .text(option.getOptionText())
                .displayOrder(option.getDisplayOrder())
                .associatedSkills(skillMappings)
                .build();
    }

    public QuestionOptionResponse.SkillMappingResponse toSkillMappingResponse(QuestionSkillMapping mapping) {
        if (mapping == null) {
            return null;
        }

        return QuestionOptionResponse.SkillMappingResponse.builder()
                .skillId(mapping.getSkill() != null ? mapping.getSkill().getId() : null)
                .weight(mapping.getWeight())
                .build();
    }
}
