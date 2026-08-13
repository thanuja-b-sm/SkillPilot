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

        int readiness = gapAnalysis.getOverallReadiness() != null ? gapAnalysis.getOverallReadiness() : gapAnalysis.getReadinessScore();
        List<SkillGapItemResponse> missingSkills = new ArrayList<>(gapAnalysis.getMissingSkills());

        // Sort gaps deterministically: severity desc -> isEssential desc -> gapAmount desc -> skillId asc
        missingSkills.sort(getGapComparator());

        List<SkillGapItemResponse> criticalGaps = missingSkills.stream()
                .filter(g -> "critical".equalsIgnoreCase(g.getSeverity()) || "high".equalsIgnoreCase(g.getSeverity()))
                .collect(Collectors.toList());

        List<SkillGapItemResponse> mediumGaps = missingSkills.stream()
                .filter(g -> "medium".equalsIgnoreCase(g.getSeverity()) || "low".equalsIgnoreCase(g.getSeverity()))
                .collect(Collectors.toList());

        String overallTimeline = String.format("%d Months (%s Strategy)", durationMonths,
                durationMonths == 3 ? "Intensive Quick-Win" : (durationMonths == 12 ? "Comprehensive Mastery" : "Standard Acceleration"));

        List<RoadmapPhaseTemplate> phaseTemplates = template != null && template.getPhaseTemplates() != null
                ? new ArrayList<>(template.getPhaseTemplates())
                : Collections.emptyList();
        phaseTemplates.sort(Comparator.comparingInt(RoadmapPhaseTemplate::getPhaseOrder));

        List<RoadmapMilestoneResponse> milestones;
        if (durationMonths == 3) {
            milestones = generate3MonthStrategy(career, missingSkills, criticalGaps, mediumGaps, phaseTemplates);
        } else if (durationMonths == 12) {
            milestones = generate12MonthStrategy(career, missingSkills, criticalGaps, mediumGaps, phaseTemplates);
        } else {
            milestones = generate6MonthStrategy(career, missingSkills, criticalGaps, mediumGaps, phaseTemplates);
        }

        String aiExplanation;
        if (readiness >= 100 || missingSkills.isEmpty()) {
            aiExplanation = String.format("System Calculated Summary: Milestone plan tailored for %s across %d months. All core requirements met (100%% Readiness). Focus is advanced system design, production hardening, and portfolio defense.", career.getTitle(), durationMonths);
        } else {
            String topSkills = missingSkills.stream().map(SkillGapItemResponse::getSkillName).limit(2).collect(Collectors.joining(", "));
            aiExplanation = String.format("System Calculated Summary: %d-month milestone plan for %s. Prioritizes %s in early phases to maximize skill growth velocity.", durationMonths, career.getTitle(), !topSkills.isEmpty() ? topSkills : "core competencies");
        }

        return CareerRoadmapResponse.builder()
                .careerId(career.getId())
                .careerTitle(career.getTitle())
                .overallTimeline(overallTimeline)
                .durationMonths(durationMonths)
                .overallReadiness(readiness)
                .completedMilestonesCount(0)
                .totalMilestonesCount(milestones.size())
                .isStale(false)
                .status("active")
                .aiExplanation(aiExplanation)
                .phases(milestones)
                .build();
    }

    private List<RoadmapMilestoneResponse> generate3MonthStrategy(
            Career career, List<SkillGapItemResponse> missingSkills,
            List<SkillGapItemResponse> criticalGaps, List<SkillGapItemResponse> mediumGaps,
            List<RoadmapPhaseTemplate> phaseTemplates) {

        SkillGapItemResponse primaryGap = !missingSkills.isEmpty() ? missingSkills.get(0) : null;
        SkillGapItemResponse secondaryGap = missingSkills.size() > 1 ? missingSkills.get(1) : null;

        return List.of(
                RoadmapMilestoneResponse.builder()
                        .id("m1")
                        .phaseOrder(1)
                        .monthRange("Month 1")
                        .phaseTitle("Phase 1: Urgent Prerequisites & Critical Gap Quick Wins")
                        .focusArea(primaryGap != null ? primaryGap.getSkillName() : "Core Foundations")
                        .goals(List.of(
                                primaryGap != null ? String.format("Bridge %s gap (Level %d -> %d)", primaryGap.getSkillName(), primaryGap.getCurrentLevel(), primaryGap.getRequiredLevel()) : "Master foundational career concepts",
                                "Set up development tools and testing workflow"
                        ))
                        .expectedOutcome("Achieve initial proficiency in high-priority required skills.")
                        .recommendedCourses(List.of(String.format("Accelerated %s Crash Course", primaryGap != null ? primaryGap.getSkillName() : career.getTitle())))
                        .status("in_progress")
                        .completionPercentage(0)
                        .targetSkillId(primaryGap != null ? primaryGap.getSkillId() : null)
                        .currentLevel(primaryGap != null ? primaryGap.getCurrentLevel() : null)
                        .requiredLevel(primaryGap != null ? primaryGap.getRequiredLevel() : null)
                        .gapSeverity(primaryGap != null ? primaryGap.getSeverity() : "CRITICAL")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m2")
                        .phaseOrder(2)
                        .monthRange("Month 2")
                        .phaseTitle("Phase 2: Core Skill Development & Hands-on Implementation")
                        .focusArea(secondaryGap != null ? secondaryGap.getSkillName() : "Applied Practice")
                        .goals(List.of(
                                secondaryGap != null ? String.format("Build applied project incorporating %s", secondaryGap.getSkillName()) : "Complete hands-on domain exercise",
                                "Implement unit tests and error handling"
                        ))
                        .expectedOutcome("Working codebase demonstrating applied skill integration.")
                        .recommendedCourses(List.of(String.format("%s Practical Project Workshop", career.getTitle())))
                        .status("not_started")
                        .completionPercentage(0)
                        .targetSkillId(secondaryGap != null ? secondaryGap.getSkillId() : null)
                        .currentLevel(secondaryGap != null ? secondaryGap.getCurrentLevel() : null)
                        .requiredLevel(secondaryGap != null ? secondaryGap.getRequiredLevel() : null)
                        .gapSeverity(secondaryGap != null ? secondaryGap.getSeverity() : "HIGH")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m3")
                        .phaseOrder(3)
                        .monthRange("Month 3")
                        .phaseTitle("Phase 3: Portfolio Showcase & Professional Readiness")
                        .focusArea("Portfolio Capstone & Positioning")
                        .goals(List.of(
                                "Publish capstone project repository with complete documentation",
                                "Prepare technical interview question walkthroughs"
                        ))
                        .expectedOutcome("Complete portfolio ready for employer review.")
                        .recommendedCourses(List.of(String.format("%s Technical Interview Preparation", career.getTitle())))
                        .status("not_started")
                        .completionPercentage(0)
                        .gapSeverity("MINOR")
                        .build()
        );
    }

    private List<RoadmapMilestoneResponse> generate6MonthStrategy(
            Career career, List<SkillGapItemResponse> missingSkills,
            List<SkillGapItemResponse> criticalGaps, List<SkillGapItemResponse> mediumGaps,
            List<RoadmapPhaseTemplate> phaseTemplates) {

        SkillGapItemResponse g1 = missingSkills.size() > 0 ? missingSkills.get(0) : null;
        SkillGapItemResponse g2 = missingSkills.size() > 1 ? missingSkills.get(1) : null;
        SkillGapItemResponse g3 = missingSkills.size() > 2 ? missingSkills.get(2) : null;

        return List.of(
                RoadmapMilestoneResponse.builder()
                        .id("m1")
                        .phaseOrder(1)
                        .monthRange("Months 1 – 2")
                        .phaseTitle("Phase 1: Critical Skill Foundation")
                        .focusArea(g1 != null ? g1.getSkillName() : "Foundational Prerequisites")
                        .goals(List.of(
                                g1 != null ? String.format("Bridge %s gap (Level %d -> %d)", g1.getSkillName(), g1.getCurrentLevel(), g1.getRequiredLevel()) : "Review core technical prerequisites",
                                "Establish daily study and implementation routine"
                        ))
                        .expectedOutcome("Foundational competence in core career requirements.")
                        .recommendedCourses(List.of(String.format("Mastering %s Foundations", g1 != null ? g1.getSkillName() : career.getTitle())))
                        .status("in_progress")
                        .completionPercentage(0)
                        .targetSkillId(g1 != null ? g1.getSkillId() : null)
                        .currentLevel(g1 != null ? g1.getCurrentLevel() : null)
                        .requiredLevel(g1 != null ? g1.getRequiredLevel() : null)
                        .gapSeverity(g1 != null ? g1.getSeverity() : "CRITICAL")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m2")
                        .phaseOrder(2)
                        .monthRange("Month 3")
                        .phaseTitle("Phase 2: Applied Skills & Project Integration")
                        .focusArea(g2 != null ? g2.getSkillName() : "Applied Projects")
                        .goals(List.of(
                                g2 != null ? String.format("Develop real-world application featuring %s", g2.getSkillName()) : "Build multi-component project",
                                "Receive architecture review and refactor solution code"
                        ))
                        .expectedOutcome("Demonstrable project repository showcasing applied domain skills.")
                        .recommendedCourses(List.of(String.format("Practical %s Applied Project", career.getTitle())))
                        .status("not_started")
                        .completionPercentage(0)
                        .targetSkillId(g2 != null ? g2.getSkillId() : null)
                        .currentLevel(g2 != null ? g2.getCurrentLevel() : null)
                        .requiredLevel(g2 != null ? g2.getRequiredLevel() : null)
                        .gapSeverity(g2 != null ? g2.getSeverity() : "HIGH")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m3")
                        .phaseOrder(3)
                        .monthRange("Months 4 – 5")
                        .phaseTitle("Phase 3: Production Practice & Specialized Depth")
                        .focusArea(g3 != null ? g3.getSkillName() : "Production Engineering")
                        .goals(List.of(
                                g3 != null ? String.format("Advance %s to target level %d", g3.getSkillName(), g3.getRequiredLevel()) : "Optimize application latency and database performance",
                                "Implement CI/CD deployment pipelines and automated monitoring"
                        ))
                        .expectedOutcome("Production-ready codebase and deployment verification.")
                        .recommendedCourses(List.of("Advanced System Optimization & Cloud Operations"))
                        .status("not_started")
                        .completionPercentage(0)
                        .targetSkillId(g3 != null ? g3.getSkillId() : null)
                        .currentLevel(g3 != null ? g3.getCurrentLevel() : null)
                        .requiredLevel(g3 != null ? g3.getRequiredLevel() : null)
                        .gapSeverity(g3 != null ? g3.getSeverity() : "MEDIUM")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m4")
                        .phaseOrder(4)
                        .monthRange("Month 6")
                        .phaseTitle("Phase 4: Professional Positioning & Portfolio Defense")
                        .focusArea("Interview & Portfolio Defense")
                        .goals(List.of(
                                "Complete technical assessment simulations and system design reviews",
                                "Present portfolio artifacts to industry peer evaluation group"
                        ))
                        .expectedOutcome("Interview confidence and active candidate positioning.")
                        .recommendedCourses(List.of(String.format("%s Technical Interview Mastery", career.getTitle())))
                        .status("not_started")
                        .completionPercentage(0)
                        .gapSeverity("LOW")
                        .build()
        );
    }

    private List<RoadmapMilestoneResponse> generate12MonthStrategy(
            Career career, List<SkillGapItemResponse> missingSkills,
            List<SkillGapItemResponse> criticalGaps, List<SkillGapItemResponse> mediumGaps,
            List<RoadmapPhaseTemplate> phaseTemplates) {

        SkillGapItemResponse g1 = missingSkills.size() > 0 ? missingSkills.get(0) : null;
        SkillGapItemResponse g2 = missingSkills.size() > 1 ? missingSkills.get(1) : null;

        return List.of(
                RoadmapMilestoneResponse.builder()
                        .id("m1")
                        .phaseOrder(1)
                        .monthRange("Months 1 – 3")
                        .phaseTitle("Phase 1: Foundational Prerequisites & Core Theory")
                        .focusArea(g1 != null ? g1.getSkillName() : "Core Prerequisites")
                        .goals(List.of("Master foundational theory and core architecture concepts", "Complete structured coding and problem-solving exercises"))
                        .expectedOutcome("Solid conceptual understanding of domain requirements.")
                        .recommendedCourses(List.of(String.format("%s Comprehensive Foundations", career.getTitle())))
                        .status("in_progress")
                        .completionPercentage(0)
                        .targetSkillId(g1 != null ? g1.getSkillId() : null)
                        .currentLevel(g1 != null ? g1.getCurrentLevel() : null)
                        .requiredLevel(g1 != null ? g1.getRequiredLevel() : null)
                        .gapSeverity("CRITICAL")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m2")
                        .phaseOrder(2)
                        .monthRange("Months 4 – 6")
                        .phaseTitle("Phase 2: Core Applied Engineering & Tooling")
                        .focusArea(g2 != null ? g2.getSkillName() : "Applied Engineering")
                        .goals(List.of("Build 3 end-to-end projects implementing core domain workflows", "Integrate automated testing and continuous integration"))
                        .expectedOutcome("Applied project portfolio showcasing technical depth.")
                        .recommendedCourses(List.of("Enterprise Software Architecture & Workflow Design"))
                        .status("not_started")
                        .completionPercentage(0)
                        .targetSkillId(g2 != null ? g2.getSkillId() : null)
                        .currentLevel(g2 != null ? g2.getCurrentLevel() : null)
                        .requiredLevel(g2 != null ? g2.getRequiredLevel() : null)
                        .gapSeverity("HIGH")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m3")
                        .phaseOrder(3)
                        .monthRange("Months 7 – 9")
                        .phaseTitle("Phase 3: Advanced Specialization & System Architecture")
                        .focusArea("Advanced Architecture")
                        .goals(List.of("Implement distributed caching, async messaging, and scaling", "Conduct load testing and performance profiling"))
                        .expectedOutcome("High-performance system deployment.")
                        .recommendedCourses(List.of("Distributed Systems & Cloud-Native Engineering"))
                        .status("not_started")
                        .completionPercentage(0)
                        .gapSeverity("MEDIUM")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m4")
                        .phaseOrder(4)
                        .monthRange("Months 10 – 11")
                        .phaseTitle("Phase 4: Capstone Project & Cloud Deployment")
                        .focusArea("Production Capstone")
                        .goals(List.of("Build capstone production system with CI/CD pipeline", "Document system design and operational runbooks"))
                        .expectedOutcome("Full production capstone project live on cloud.")
                        .recommendedCourses(List.of("Production Engineering & DevOps Masterclass"))
                        .status("not_started")
                        .completionPercentage(0)
                        .gapSeverity("LOW")
                        .build(),

                RoadmapMilestoneResponse.builder()
                        .id("m5")
                        .phaseOrder(5)
                        .monthRange("Month 12")
                        .phaseTitle("Phase 5: Technical Interview & Executive Positioning")
                        .focusArea("Career Transition")
                        .goals(List.of("Execute mock technical interview assessments", "Finalize resume, LinkedIn, and GitHub portfolio defense"))
                        .expectedOutcome("Candidate readiness for top-tier career opportunities.")
                        .recommendedCourses(List.of(String.format("%s Senior Technical Interview Preparation", career.getTitle())))
                        .status("not_started")
                        .completionPercentage(0)
                        .gapSeverity("LOW")
                        .build()
        );
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
