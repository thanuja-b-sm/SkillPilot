# SkillPilot — Product Improvement Plan

**Date:** August 13, 2026  
**Author:** SkillPilot Product & Systems Architect  
**Branch:** `feature/product-intelligence-improvements`  

---

## 1. Current Product Architecture & Student Journey

### Architecture Baseline
- **Authoritative Source of Truth:** MySQL 8.0 database (managed via Flyway schema migrations `V1`–`V5`).
- **Business Logic & Determinism:** Spring Boot backend services (`CareerScoringEngine`, `SkillGapAnalysisEngine`, `RoadmapGenerationEngine`).
- **Presentation Layer:** React 18 + TypeScript SPA with Vite.
- **AI Boundary:** Google Gemini is an optional explanation layer; zero participation in metrics calculation.

### Student Journey Flow
```
[ Landing / Registration ]
            │
            ▼
[ User Profile & Skill Setup ] ──► [ Target Career Selection ]
                                            │
                                            ▼
                               [ Career-Relevant Questionnaire ]
                                            │
                                            ▼
                                [ Career Match Results ]
                                            │
                                            ▼
                                 [ Skill Gap & Readiness ]
                                            │
                                            ▼
                                  [ Milestone Roadmap ]
```

---

## 2. Product & Intelligence Audit Findings

### Weaknesses Identified

1. **Admin Master-Data Configuration Diagnostics (P1):**
   - Administrators currently lack real-time visibility into database configuration gaps (e.g., careers missing essential skills, skills lacking questionnaire coverage, options missing skill mappings, or inactive skill requirements).
   - *Impact:* Incomplete backend data setup could silently lower questionnaire scoring or skill-gap accuracy without alerting administrators.

2. **Career-Specific Skill Highlighting & Filtering (P1):**
   - In `ProfilePage.tsx` and `SkillGapAnalysisPage.tsx`, skills need clearer visual categorization when evaluated against the selected target career (e.g., essential gaps vs non-essential gaps, current level vs required level).
   - *Impact:* Students need immediate visual feedback on which specific skills directly unlock their selected target career.

3. **Target Career State Synchronization (P1):**
   - When a student switches their target career, dependent state (career-specific questionnaire, target skill gaps, and active roadmap) must invalidate stale data and reload atomically from MySQL.
   - *Impact:* Guarantees students never view roadmap or gap data from a previously selected target career.

4. **Deterministic Engines Validation (P2):**
   - Verification that `CareerScoringEngine`, `SkillGapAnalysisEngine`, and `RoadmapGenerationEngine` handle edge cases (empty skill maps, 0-requirement careers, inactive career requirement snapshots) deterministically.

---

## 3. Recommended Improvements & Priority Matrix

| Priority | Feature / Improvement Area | Target Component / Service | Status | Expected Product & User Benefit |
|---|---|---|---|---|
| **P1** | **Admin Intelligence Diagnostics API & Dashboard** | `AdminConfigService.java`, `AdminDashboardPage.tsx` | **COMPLETED** | Provides real-time health checks & score for DB configuration (0-essential careers, unmapped questions, inactive skills). |
| **P1** | **Target Career Switching Atomicity & State Sync** | `AppContext.tsx`, `TargetCareerController.java` | **COMPLETED** | Atomically invalidates stale dependent state (questionnaire, skill gap, roadmap) with request sequence tracking to prevent race conditions. |
| **P1** | **Career-Specific Skill Highlighting in Student Profile** | `ProfilePage.tsx`, `SkillGapAnalysisPage.tsx` | **COMPLETED** | Displays essential requirement indicators, target level requirements, gap severity badges, and target met indicators. |
| **P2** | **Target Career & Diagnostics Integration Tests** | `TargetCareerSynchronizationTest.java`, `AdminDiagnosticsTest.java` | **COMPLETED** | Verifies target career isolation, roadmap switching, questionnaire reloading, and diagnostic checks across 140 tests. |

---

## 4. Implementation Status & Verification Summary

1. **Step 1:** Create `docs/product/PRODUCT_IMPROVEMENT_PLAN.md` *(Completed)*.
2. **Step 2:** Implement Admin Diagnostics backend health checks and score calculations (`getSystemHealth()`) *(Completed)*.
3. **Step 3:** Implement Admin Diagnostics UI panel & health score gauge in `AdminDashboardPage.tsx` *(Completed)*.
4. **Step 4:** Refactor and harden target career switching state synchronization in `AppContext.tsx` with request sequence tracking and immediate stale state clearing *(Completed)*.
5. **Step 5:** Enhance career-specific skill experience in `ProfilePage.tsx` and `SkillGapAnalysisPage.tsx` *(Completed)*.
6. **Step 6:** Add backend integration tests `TargetCareerSynchronizationTest.java` and `AdminDiagnosticsTest.java` *(Completed)*.
7. **Step 7:** Run full verification suite (`npx tsc --noEmit` PASS, `npm run build` PASS, `.\mvnw.cmd test` 140/140 PASS) *(Completed)*.
8. **Step 8:** Update `docs/IMPLEMENTATION_HISTORY.md` and push feature branch *(Completed)*.
