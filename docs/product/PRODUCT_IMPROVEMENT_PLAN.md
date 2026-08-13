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

| Priority | Feature / Improvement Area | Target Component / Service | Expected Product & User Benefit |
|---|---|---|---|
| **P1** | **Admin Intelligence Diagnostics API & Dashboard** | `AdminDiagnosticsController.java`, `AdminDiagnosticsService.java`, `AdminDashboard.tsx` | Provides real-time health checks for DB configuration (0-essential careers, unmapped questions, inactive skill references). |
| **P1** | **Target Career Switching Atomicity & State Sync** | `AppContext.tsx`, `TargetCareerController.java` | Atomically refreshes career-specific questionnaire, skill gaps, and roadmap upon target career selection, eliminating stale state. |
| **P1** | **Career-Specific Skill Highlighting in Student Profile** | `ProfilePage.tsx`, `SkillGapAnalysisPage.tsx` | Displays essential requirement indicators, gap severity badges, and level comparisons tailored to the target career. |
| **P2** | **Diagnostics Unit & Integration Tests** | `AdminDiagnosticsTest.java` | Verifies automated detection of configuration defects and relationship integrity. |

---

## 4. Proposed Implementation Order

1. **Step 1:** Create `docs/product/PRODUCT_IMPROVEMENT_PLAN.md` *(Completed)*.
2. **Step 2:** Implement Admin Diagnostics backend service, DTOs, and REST controller (`GET /api/admin/diagnostics`).
3. **Step 3:** Implement Admin Diagnostics UI panel in `AdminDashboard.tsx`.
4. **Step 4:** Refactor and harden target career switching state synchronization in `AppContext.tsx`.
5. **Step 5:** Enhance career-specific skill experience in `ProfilePage.tsx` and `SkillGapAnalysisPage.tsx`.
6. **Step 6:** Add backend integration tests for Admin Diagnostics and target career switching.
7. **Step 7:** Run full verification suite (`npx tsc --noEmit`, `npm run build`, `.\mvnw.cmd test`).
8. **Step 8:** Update `docs/IMPLEMENTATION_HISTORY.md` and push feature branch.
