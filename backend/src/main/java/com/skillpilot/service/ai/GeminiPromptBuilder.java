package com.skillpilot.service.ai;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiPromptBuilder {

    public static final String SYSTEM_INSTRUCTION =
            "You are the explanation layer of SkillPilot. " +
            "All numerical values, rankings, skill gaps, readiness scores, priorities, and roadmap ordering supplied in the input are authoritative. " +
            "Do not recalculate, modify, reinterpret, or contradict them. " +
            "Provide concise, clear, and professional explanations for the student learner. " +
            "Do not invent courses, certifications, companies, salaries, or job offers.";

    public String buildCareerExplanationPrompt(String careerTitle, int matchScore, List<String> keyStrengths, List<String> keyGaps, String targetRoleGoal) {
        return String.format(
                "System Inputs:\n" +
                "- Career Role: %s\n" +
                "- Authoritative Match Score: %d%%\n" +
                "- Key Strengths: %s\n" +
                "- Key Skill Gaps: %s\n" +
                "- Student Target Goal: %s\n\n" +
                "Task: Return a valid JSON object with keys:\n" +
                "{\n" +
                "  \"summary\": \"Brief 1-sentence summary of why this career aligns with the score of %d%%\",\n" +
                "  \"explanation\": \"2-3 sentence clear explanation highlighting how strengths support alignment and how gaps can be addressed.\",\n" +
                "  \"focusAreas\": [\"Top focus area 1\", \"Top focus area 2\"]\n" +
                "}",
                careerTitle, matchScore,
                keyStrengths != null ? String.join(", ", keyStrengths) : "None",
                keyGaps != null ? String.join(", ", keyGaps) : "None",
                targetRoleGoal != null ? targetRoleGoal : "Career Advancement",
                matchScore
        );
    }

    public String buildSkillGapPrompt(String careerTitle, int readinessScore, List<String> missingSkills) {
        return String.format(
                "System Inputs:\n" +
                "- Target Career: %s\n" +
                "- Authoritative Readiness Score: %d%%\n" +
                "- Priority Skill Gaps: %s\n\n" +
                "Task: Return a valid JSON object with keys:\n" +
                "{\n" +
                "  \"summary\": \"1-sentence explanation of the %d%% readiness assessment.\",\n" +
                "  \"explanation\": \"2-3 sentence overview explaining skill development steps for the priority gaps.\",\n" +
                "  \"priorityGaps\": [\"Priority gap 1\", \"Priority gap 2\"]\n" +
                "}",
                careerTitle, readinessScore,
                missingSkills != null ? String.join(", ", missingSkills) : "None",
                readinessScore
        );
    }

    public String buildRoadmapSummaryPrompt(String careerTitle, String overallTimeline, int readinessScore, List<String> phaseTitles) {
        return String.format(
                "System Inputs:\n" +
                "- Target Career: %s\n" +
                "- Overall Timeline: %s\n" +
                "- Baseline Readiness: %d%%\n" +
                "- Milestone Sequences: %s\n\n" +
                "Task: Return a valid JSON object with keys:\n" +
                "{\n" +
                "  \"summary\": \"1-sentence summary of the roadmap plan.\",\n" +
                "  \"explanation\": \"2-3 sentence explanation summarizing how the phased stages progressively build career competency.\",\n" +
                "  \"stageHighlights\": [\"Stage highlight 1\", \"Stage highlight 2\"]\n" +
                "}",
                careerTitle, overallTimeline, readinessScore,
                phaseTitles != null ? String.join(", ", phaseTitles) : "None"
        );
    }
}
