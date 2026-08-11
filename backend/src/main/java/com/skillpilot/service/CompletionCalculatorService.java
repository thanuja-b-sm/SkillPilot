package com.skillpilot.service;

import com.skillpilot.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CompletionCalculatorService {

    public int calculateCompletionPercentage(User user) {
        if (user == null) {
            return 0;
        }

        int score = 0;

        // 1. Profile completeness (Up to 40%)
        if (StringUtils.hasText(user.getName())) score += 5;
        if (StringUtils.hasText(user.getEmail())) score += 5;
        if (StringUtils.hasText(user.getTitle()) && !"Student Profile".equalsIgnoreCase(user.getTitle())) score += 5;
        if (StringUtils.hasText(user.getEducation())) score += 5;
        if (user.getExperienceYears() != null && user.getExperienceYears() > 0) score += 5;
        if (StringUtils.hasText(user.getLocation())) score += 5;
        if (StringUtils.hasText(user.getTargetFocus())) score += 5;
        if (StringUtils.hasText(user.getBio())) score += 5;

        // 2. User skills rated (Up to 30%)
        int ratedSkills = user.getSkills() != null ? user.getSkills().size() : 0;
        score += Math.min(30, ratedSkills * 6);

        // 3. Target Career Selection (15%)
        if (user.getTargetCareer() != null) {
            score += 15;
        }

        // 4. Discovery Questionnaire Answers (15%)
        if (user.getQuestionnaireAnswers() != null && !user.getQuestionnaireAnswers().isEmpty()) {
            score += 15;
        }

        return Math.min(100, Math.max(0, score));
    }
}
