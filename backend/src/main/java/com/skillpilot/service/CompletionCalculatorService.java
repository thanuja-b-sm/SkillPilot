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

        // 1. Target Career Selection (20%)
        if (user.getTargetCareer() != null) {
            score += 20;
        }

        // 2. User Skills Rated (20%)
        int ratedSkills = user.getSkills() != null ? user.getSkills().size() : 0;
        score += Math.min(20, ratedSkills * 5);

        // 3. Education Details (15%)
        if (StringUtils.hasText(user.getEducation()) || StringUtils.hasText(user.getDegreeLevel())) score += 5;
        if (StringUtils.hasText(user.getInstitutionName())) score += 5;
        if (StringUtils.hasText(user.getMajorFieldOfStudy())) score += 5;

        // 4. Experience Details (15%)
        if (StringUtils.hasText(user.getEmploymentStatus())) score += 5;
        if (user.getExperienceYears() != null && user.getExperienceYears() > 0) score += 5;
        if (user.getRelevantExperienceYears() != null && user.getRelevantExperienceYears() > 0) score += 5;

        // 5. Personal & Contact Details (15%)
        if (StringUtils.hasText(user.getName())) score += 5;
        if (StringUtils.hasText(user.getLocation())) score += 5;
        if (StringUtils.hasText(user.getCountry())) score += 5;

        // 6. Learning & Work Preferences (15%)
        if (user.getWeeklyHoursAvailable() != null && user.getWeeklyHoursAvailable() > 0) score += 5;
        if (StringUtils.hasText(user.getPreferredWorkMode())) score += 5;
        if (StringUtils.hasText(user.getCareerGoal()) || StringUtils.hasText(user.getTargetFocus())) score += 5;

        return Math.min(100, Math.max(0, score));
    }
}
