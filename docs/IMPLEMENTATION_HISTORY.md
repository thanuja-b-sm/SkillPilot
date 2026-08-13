# SkillPilot — Master Implementation History

**Repository:** `SkillPilot`  
**Current Branch:** `feature/production-hardening`  
**Baseline Date:** August 12, 2026  

---

## 📜 Program Execution & Milestone History

### Milestone 1: Core System Architecture & Authoritative DB Alignment
- **Problem:** Database state and frontend guest preview required explicit architectural boundary definition.
- **Root Cause:** Need to ensure MySQL remains the sole authoritative source of truth while React SPA operates as presentation layer.
- **Solution:** Enforced strict REST API model; guest preview isolated to landing page fallback; all authenticated matches and roadmaps fetch directly from MySQL database.
- **Files Changed:** `AppContext.tsx`, `CareerDiscoveryController.java`, `RoadmapController.java`.
- **Verification:** 132 backend JUnit integration tests passed cleanly.

### Milestone 2: Security & Role-Based Access Hardening
- **Problem:** Admin endpoints require robust server-side RBAC protection; user resources must be immune to IDOR.
- **Root Cause:** Reliance on client-provided IDs or roles can introduce authorization bypass vulnerabilities.
- **Solution:** Configured `SecurityConfig.java` with `@PreAuthorize("hasRole('ADMIN')")` on `/api/admin/**`. Enforced principal identity extraction via `@AuthenticationPrincipal SecurityUser` across user endpoints.
- **Files Changed:** `SecurityConfig.java`, `UserProfileController.java`, `UserSkillController.java`, `TargetCareerController.java`, `RoadmapController.java`.
- **Verification:** Security audit verified 100% compliance across RBAC, JWT signing (HS512), and IDOR prevention.

### Milestone 3: Session & API Lifecycle Hardening
- **Problem:** Browser refresh on `/admin` downgraded admin to student role; transient network delays destroyed `localStorage` JWT token.
- **Root Cause:** DTO `UserProfileResponse` omitted `userRole` property; session bootstrap purged tokens on initial fetch failure.
- **Solution:** Added `userRole` property to `UserProfileResponse.java` and `UserProfileMapper.java`. Hardened `AppContext.tsx` with a 3-attempt retry loop (`ensureMasterDataLoaded`) for startup delays, differentiating 401 Unauthorized from network errors.
- **Files Changed:** `UserProfileResponse.java`, `UserProfileMapper.java`, `AppContext.tsx`, `RegistrationPage.tsx`, `SessionLifecycleAuditTest.java`.
- **Verification:** `SessionLifecycleAuditTest.java` (5 tests) passed; manual reload on `/admin` verified.

### Milestone 4: Deterministic Engines & Gemini AI Boundary
- **Problem:** AI models must not alter business metrics, match scores, readiness percentages, or milestone ordering.
- **Root Cause:** External LLM non-determinism can corrupt authoritative scoring logic.
- **Solution:** Isolated `CareerScoringEngine`, `SkillGapAnalysisEngine`, and `RoadmapGenerationEngine` into 100% backend-authoritative components. Integrated Google Gemini as a read-only natural language explanation layer with non-blocking fallback (`FallbackExplanationService`).
- **Files Changed:** `CareerScoringEngine.java`, `SkillGapAnalysisEngine.java`, `RoadmapGenerationEngine.java`, `GeminiExplanationService.java`, `FallbackExplanationService.java`.
- **Verification:** 10 Gemini enhancement tests and 16 engine tests passed cleanly.

### Milestone 5: Admin Impact & Historical Snapshot Isolation
- **Problem:** Admin changes to career skill requirements or system config must not silently mutate past user match evaluations.
- **Root Cause:** Dynamic relational queries without snapshot serialization overwrite past evaluation history.
- **Solution:** Serialized `configSnapshot` and `requirementsSnapshot` as JSON text into `CareerMatchResult`. Created `AdminRequirementImpactTest.java` verifying future evaluations use updated requirements while past match snapshots remain immutable.
- **Files Changed:** `CareerMatchResult.java`, `CareerDiscoveryService.java`, `AdminRequirementImpactTest.java`.
- **Verification:** `AdminRequirementImpactTest.java` passed cleanly.

### Milestone 6: Product Intelligence & Admin Configuration Diagnostics
- **Problem:** Missing target-career invalidation upon selection; Admin lacked visibility into configuration health (careers without essential skills or questionnaire option mappings).
- **Root Cause:** AppContext target career selection did not invalidate stale roadmap state; SystemHealthResponse lacked health score metrics and detailed diagnostic warnings.
- **Solution:** Added `PRODUCT_IMPROVEMENT_PLAN.md` documenting architecture baseline, student journey, and priority matrix. Enhanced `SystemConfigService.getSystemHealth()` with `healthScore` percentage (0-100%) and diagnostics for career requirements, essential skill coverage, questionnaire option mappings, and question completeness. Updated `AdminDashboardPage.tsx` with a visual System Health Score Gauge and direct configuration repair navigation.
- **Files Changed:** `PRODUCT_IMPROVEMENT_PLAN.md`, `SystemHealthResponse.java`, `SystemConfigService.java`, `QuestionSkillMappingRepository.java`, `AdminDashboardPage.tsx`, `AdminDiagnosticsTest.java`, `types.ts`.
- **Verification:** `AdminDiagnosticsTest.java` passed cleanly; frontend `npx tsc --noEmit` (0 errors), `npm run build` (0 errors), and full backend `.\mvnw.cmd test` suite (134/134 passed).

### Milestone 7: Target Career Intelligence & Atomic State Synchronization
- **Problem:** Target career switching could leave stale questionnaire, skill gap, or roadmap data from previous career tracks; risk of race conditions on slow network calls.
- **Root Cause:** In-flight async requests were not guarded by sequence identifiers; dependent state was not cleared immediately upon target career selection.
- **Solution:** Hardened `AppContext.selectTargetCareer` with atomic state invalidation (`setBackendSkillGap(null)`, `setActiveRoadmap(null)`, `setQuestionnaire([])`) and sequence counter (`targetCareerSeqRef`) stale-request filtering. Enhanced `ProfilePage.tsx` with target career skill requirement badges (`Req: Lvl X`, `[ESSENTIAL]`, `Gap: -X`, `✓ Target Met`) and empty state banner. Added `TargetCareerSynchronizationTest.java` verifying state isolation, roadmap switching, and questionnaire reloading.
- **Files Changed:** `AppContext.tsx`, `ProfilePage.tsx`, `SkillGapAnalysisPage.tsx`, `QuestionnairePage.tsx`, `TargetCareerSynchronizationTest.java`, `PRODUCT_IMPROVEMENT_PLAN.md`, `IMPLEMENTATION_HISTORY.md`.
- **Browser Acceptance Verification (21 Scenarios):**
  - **Student Flow (Scenarios 1-13):** Verified student login, profile selection, career-specific skill requirement badges, questionnaire relevance, answer submission, readiness scoring, and roadmap generation for Career A ("AI & Machine Learning Engineer"). PASS.
  - **Career Switching & Persistence (Scenarios 14-21):** Verified atomic invalidation of Career A state upon switching to Career B ("Cloud Solutions Architect"), hard browser refresh retention, route/state consistency across Back/Forward navigation, session restoration across logout/re-login, rapid career switching race-condition resistance, and admin configuration updates. PASS.
- **Verification Summary:** `TargetCareerSynchronizationTest.java` passed cleanly; frontend `npx tsc --noEmit` (0 errors), `npm run build` (0 errors), and full backend `.\mvnw.cmd test` suite (139/139 passed).

---

## 📈 Final Pre-Merge Audit & Verification Summary

| Suite / Check | Result | Standard | Status |
|---|---|---|---|
| **Frontend Type Check (`npx tsc --noEmit`)** | **0 Errors** | Zero TypeScript compilation errors | **PASS** |
| **Frontend Production Build (`npm run build`)** | **SUCCESS** | Vite SPA + SSR bundle built in 3.04s | **PASS** |
| **Backend Test Suite (`.\mvnw.cmd test`)** | **139 / 139 Passed** | 100% JUnit 5 + Spring Boot integration test success | **PASS** |
| **Browser Acceptance Test Matrix (21 Scenarios)** | **21 / 21 Passed** | 100% Student, Career Switching, Race-Condition & Admin scenarios | **PASS** |
| **Tracked Secrets / `.env` Audit** | **0 Exposure** | Zero API keys, tokens, or credentials tracked | **PASS** |
| **Git Working Tree Status** | **CLEAN** | All synchronization fixes & tests logged | **PASS** |

