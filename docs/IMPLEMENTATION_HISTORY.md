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

---

## 📈 Final Pre-Merge Audit & Verification Summary

| Suite / Check | Result | Standard | Status |
|---|---|---|---|
| **Frontend Type Check (`npx tsc --noEmit`)** | **0 Errors** | Zero TypeScript compilation errors | **PASS** |
| **Frontend Production Build (`npm run build`)** | **SUCCESS** | Vite SPA + SSR bundle built in 3.69s | **PASS** |
| **Backend Test Suite (`.\mvnw.cmd test`)** | **132 / 132 Passed** | 100% JUnit 5 + Spring Boot integration test success | **PASS** |
| **Tracked Secrets / `.env` Audit** | **0 Exposure** | Zero API keys, tokens, or credentials tracked | **PASS** |
| **Git Diff vs `main` (`git diff main...HEAD`)** | **Documentation Only** | 16 docs/ audit files changed since `main` | **PASS** |
| **Git Working Tree Status** | **CLEAN** | All audit reports and history logged in `docs/` | **PASS** |
| **Pre-Merge Recommendation** | **READY TO MERGE** | Fully verified production baseline | **READY** |
