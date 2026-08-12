# Master Implementation & Quality Plan

**Date:** August 12, 2026  
**Author:** SkillPilot Principal System Architect

---

## 🎯 Priority Matrix & Roadmap

### Priority 0 (P0) — Blocking / Security / Data Corruption
- **Status:** **0 Findings.** All high-risk vulnerabilities, token leaks, and SQL injection flaws resolved.

### Priority 1 (P1) — Session & Registration Lifecycle Hardening
1. **P1-1: DTO Role Serialization:** Add `userRole` to `UserProfileResponse.java` and map in `UserProfileMapper.java`. Preserves `/admin` route on browser refresh. *(Completed)*
2. **P1-2: Session Bootstrap Retry Machine:** Differentiate 401 Unauthorized from transient network errors. Implement 3-attempt bounded retry loop in `AppContext.tsx`. *(Completed)*
3. **P1-3: Registration Session Bootstrap:** Refactor `RegistrationPage.tsx` to call `loginWithAuthData(token, profile, role, 'register')`. *(Completed)*

### Priority 2 (P2) — Test Coverage & Deterministic Verification
1. **P2-1: Session Lifecycle Test Suite:** Create `SessionLifecycleAuditTest.java` verifying 401, 403, and role serialization semantics. *(Completed)*
2. **P2-2: Admin Requirement Impact Integration Test:** Create `AdminRequirementImpactTest.java` verifying that admin requirement updates affect future student skill gaps while preserving historical match snapshots in `CareerMatchResult`. *(Completed)*

### Priority 3 (P3) — Optional UX Polish & Gemini Badging
1. **P3-1: Gemini Fallback Indicator:** Optional badge indicating system-generated fallback summary when AI quota is exceeded. *(Deferred for future polish)*

---

## 📋 Verification Acceptance Criteria

- [x] **Frontend Typecheck:** `npx tsc --noEmit` returns 0 errors.
- [x] **Frontend Production Build:** `npm run build` succeeds cleanly.
- [x] **Backend Test Suite:** `.\mvnw.cmd test` passes 100% of 132 tests.
- [x] **Git & Documentation:** Repository clean, documented under `docs/`, committed on `feature/production-hardening`.
