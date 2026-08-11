package com.skillpilot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillpilot.dto.response.CareerRoadmapResponse;
import com.skillpilot.dto.response.RoadmapMilestoneResponse;
import com.skillpilot.dto.response.SkillGapAnalysisResponse;
import com.skillpilot.dto.response.SkillGapItemResponse;
import com.skillpilot.entity.Career;
import com.skillpilot.entity.RoadmapPhaseTemplate;
import com.skillpilot.entity.RoadmapTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoadmapGenerationEngine {

    private final ObjectMapper objectMapper;

    public CareerRoadmapResponse generateRoadmap(
            Career career,
            SkillGapAnalysisResponse gapAnalysis,
            RoadmapTemplate template,
            int durationMonths) {

        int readiness = gapAnalysis.getReadinessScore();
        List<SkillGapItemResponse> missingSkills = new ArrayList<>(gapAnalysis.getMissingSkills());

        // Sort gaps deterministically: severity desc -> isEssential desc -> gapAmount desc -> skillId asc
        missingSkills.sort(getGapComparator());

        List<SkillGapItemResponse> criticalGaps = missingSkills.stream()
                .filter(g -> "critical".equalsIgnoreCase(g.getSeverity()) || "high".equalsIgnoreCase(g.getSeverity()))
                .collect(Collectors.toList());

        List<SkillGapItemResponse> mediumGaps = missingSkills.stream()
                .filter(g -> "medium".equalsIgnoreCase(g.getSeverity()) || "low".equalsIgnoreCase(g.getSeverity()))
                .collect(Collectors.toList());

        // Duration string format
        String overallTimeline = String.format("%d Months (Phased 4-Stage Plan)", durationMonths);

        // Calculate phase month ranges based on total durationMonths (distributed into 4 quarters)
        int q1End = Math.max(1, durationMonths / 4);
        int q2End = Math.max(q1End + 1, durationMonths / 2);
        int q3End = Math.max(q2End + 1, (3 * durationMonths) / 4);
        int q4End = durationMonths;

        String m1Range = String.format("Months 1 – %d", q1End);
        String m2Range = String.format("Months %d – %d", q1End + 1, q2End);
        String m3Range = String.format("Months %d – %d", q2End + 1, q3End);
        String m4Range = String.format("Months %d – %d", q3End + 1, q4End);

        List<RoadmapPhaseTemplate> phaseTemplates = template != null && template.getPhaseTemplates() != null
                ? new ArrayList<>(template.getPhaseTemplates())
                : Collections.emptyList();

        phaseTemplates.sort(Comparator.comparingInt(RoadmapPhaseTemplate::getPhaseOrder));

        RoadmapPhaseTemplate p1 = phaseTemplates.size() >= 1 ? phaseTemplates.get(0) : null;
        RoadmapPhaseTemplate p2 = phaseTemplates.size() >= 2 ? phaseTemplates.get(1) : null;
        RoadmapPhaseTemplate p3 = phaseTemplates.size() >= 3 ? phaseTemplates.get(2) : null;
        RoadmapPhaseTemplate p4 = phaseTemplates.size() >= 4 ? phaseTemplates.get(3) : null;

        // Phase 1 Focus Area & Goals
        String focusArea1 = !criticalGaps.isEmpty()
                ? criticalGaps.stream().map(SkillGapItemResponse::getSkillName).limit(3).collect(Collectors.joining(", "))
                : (p1 != null ? p1.getFocusArea() : "Core Prerequisites & System Architecture");

        List<String> goals1 = new ArrayList<>();
        if (!criticalGaps.isEmpty()) {
            for (SkillGapItemResponse g : criticalGaps.stream().limit(3).collect(Collectors.toList())) {
                goals1.add(String.format("Address critical gap in %s (Target Level %d)", g.getSkillName(), g.getRequiredLevel()));
            }
        } else if (p1 != null && p1.getGoals() != null && !p1.getGoals().isEmpty()) {
            goals1.addAll(p1.getGoals());
        } else {
            goals1.add("Review core technical prerequisites");
            goals1.add("Set up development workspace and testing harnesses");
            goals1.add("Establish daily study and implementation routine");
        }

        // Phase 2 Focus Area & Goals
        String focusArea2 = !mediumGaps.isEmpty()
                ? mediumGaps.stream().map(SkillGapItemResponse::getSkillName).limit(3).collect(Collectors.joining(", "))
                : (p2 != null ? p2.getFocusArea() : "Applied Technical & Workflow Practice");

        List<String> goals2 = new ArrayList<>();
        if (p2 != null && p2.getGoals() != null && !p2.getGoals().isEmpty()) {
            goals2.addAll(p2.getGoals());
        } else {
            goals2.add(String.format("Build 2 practical projects applying required %s technologies", career.getTitle()));
            goals2.add("Implement automated unit and integration verification workflows");
            goals2.add("Receive architecture review and refactor solution code");
        }

        // Phase 3 Focus Area & Goals
        String focusArea3 = p3 != null ? p3.getFocusArea() : "Production Hardening & System Optimization";
        List<String> goals3 = new ArrayList<>();
        if (p3 != null && p3.getGoals() != null && !p3.getGoals().isEmpty()) {
            goals3.addAll(p3.getGoals());
        } else {
            goals3.add("Optimize application latency, database queries, and memory footprints");
            goals3.add("Deploy end-to-end architectures to live cloud staging environment");
            goals3.add("Implement CI/CD deployment pipelines and automated monitoring");
        }

        // Phase 4 Focus Area & Goals
        String focusArea4 = p4 != null ? p4.getFocusArea() : "Portfolio Defense & Professional Positioning";
        List<String> goals4 = new ArrayList<>();
        if (p4 != null && p4.getGoals() != null && !p4.getGoals().isEmpty()) {
            goals4.addAll(p4.getGoals());
        } else {
            goals4.add("Complete technical assessment simulations and system design reviews");
            goals4.add("Finalize capstone GitHub repository and project documentation");
            goals4.add("Present portfolio artifacts to industry peer evaluation group");
        }

        List<RoadmapMilestoneResponse> milestones = List.of(
                RoadmapMilestoneResponse.builder()
                        .id("m1")
                        .monthRange(m1Range)
                        .phaseTitle(p1 != null ? p1.getPhaseTitle() : "Phase 1: Critical Skill Foundation")
                        .focusArea(focusArea1)
                        .goals(goals1)
                        .expectedOutcome(p1 != null ? p1.getExpectedOutcome() : "Achieve foundational competence in high-priority career requirements.")
                        .recommendedCourses(List.of(
                                String.format("Mastering %s Basics", !criticalGaps.isEmpty() ? criticalGaps.get(0).getSkillName() : career.getTitle()),
                                "Industry Benchmark Foundations"
                        ))
                        .status("in_progress")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m2")
                        .monthRange(m2Range)
                        .phaseTitle(p2 != null ? p2.getPhaseTitle() : "Phase 2: Applied Skills & Project Integration")
                        .focusArea(focusArea2)
                        .goals(goals2)
                        .expectedOutcome(p2 != null ? p2.getExpectedOutcome() : "Demonstrable project repository showcasing applied domain skills.")
                        .recommendedCourses(List.of(
                                String.format("Practical %s Applied Project Workshop", career.getTitle()),
                                "Enterprise Architecture Standards"
                        ))
                        .status("not_started")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m3")
                        .monthRange(m3Range)
                        .phaseTitle(p3 != null ? p3.getPhaseTitle() : "Phase 3: Production Practice & Specialized Depth")
                        .focusArea(focusArea3)
                        .goals(goals3)
                        .expectedOutcome(p3 != null ? p3.getExpectedOutcome() : "Production-ready codebases and verified system deployment.")
                        .recommendedCourses(List.of(
                                "Cloud & DevOps for Software Engineers",
                                "Advanced Problem Solving & System Design"
                        ))
                        .status("not_started")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m4")
                        .monthRange(m4Range)
                        .phaseTitle(p4 != null ? p4.getPhaseTitle() : "Phase 4: Professional Positioning & Portfolio Defense")
                        .focusArea(focusArea4)
                        .goals(goals4)
                        .expectedOutcome(p4 != null ? p4.getExpectedOutcome() : "Interview confidence and active candidate positioning for target career.")
                        .recommendedCourses(List.of(
                                String.format("%s Technical Interview Mastery", career.getTitle()),
                                "Executive Communication for Engineers"
                        ))
                        .status("not_started")
                        .build()
        );

        String aiExplanation;
        if (readiness >= 100 || missingSkills.isEmpty()) {
            aiExplanation = String.format("System Calculated Summary: Milestone plan tailored for %s. All core skill requirements met (100%% Readiness). Focus is advanced system design, production hardening, and portfolio defense.", career.getTitle());
        } else {
            String topSkills = missingSkills.stream().map(SkillGapItemResponse::getSkillName).limit(2).collect(Collectors.joining(", "));
            aiExplanation = String.format("System Calculated Summary: Milestone plan tailored for %s. Prioritizes %s in early phases to maximize skill growth velocity.", career.getTitle(), !topSkills.isEmpty() ? topSkills : "core competencies");
        }

        return CareerRoadmapResponse.builder()
                .careerId(career.getId())
                .careerTitle(career.getTitle())
                .overallTimeline(overallTimeline)
                .overallReadiness(readiness)
                .aiExplanation(aiExplanation)
                .phases(milestones)
                .build();
    }

    private Comparator<SkillGapItemResponse> getGapComparator() {
        return (a, b) -> {
            int sevCompare = Integer.compare(getSeverityRank(b.getSeverity()), getSeverityRank(a.getSeverity()));
            if (sevCompare != 0) return sevCompare;

            int essCompare = Boolean.compare(Boolean.TRUE.equals(b.getIsEssential()), Boolean.TRUE.equals(a.getIsEssential()));
            if (essCompare != 0) return essCompare;

            int gapCompare = Integer.compare(b.getGapAmount(), a.getGapAmount());
            if (gapCompare != 0) return gapCompare;

            return a.getSkillId().compareTo(b.getSkillId());
        };
    }

    private int getSeverityRank(String severity) {
        if ("critical".equalsIgnoreCase(severity)) return 4;
        if ("high".equalsIgnoreCase(severity)) return 3;
        if ("medium".equalsIgnoreCase(severity)) return 2;
        return 1;
    }
}
