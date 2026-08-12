# Phase 2 — Security Audit Report

**Date:** August 12, 2026  
**Auditor:** SkillPilot Lead Security Auditor

---

## 1. Security Architecture Evaluation

### 1.1 Authentication & JWT Token Safety
- **JWT Signing:** HMAC SHA-512 (`SignatureAlgorithm.HS512`) using a 512-bit secret key configured via `app.jwt.secret`.
- **Token Validation:** `JwtAuthenticationFilter` intercepts requests and validates signatures via `JwtTokenProvider`. Expired, malformed, or tampered tokens result in immediate HTTP 401 Unauthorized.
- **Stateless Session Policy:** Configured via `SessionCreationPolicy.STATELESS`. Server stores no session state; authentication relies exclusively on JWT validation.

### 1.2 Authorization & Role-Based Access Control (RBAC)
- **Server-Side Enforcement:** Route-level authorization is enforced in `SecurityConfig.java` and method-level authorization via `@PreAuthorize("hasRole('ADMIN')")`.
- **Admin Isolation:** All `/api/admin/**` routes require `ROLE_ADMIN`. Non-admin or unauthenticated access results in HTTP 403 Forbidden or HTTP 401 Unauthorized.
- **IDOR Protection:** User-specific endpoints (`/api/user/profile`, `/api/user/skills`, `/api/user/target-career`, `/api/careers/matches`, `/api/roadmaps/user`) retrieve `userId` directly from Spring Security principal (`SecurityUser.getId()`). Request bodies cannot override principal identity.

### 1.3 Password & Secret Security
- **Hashing:** `BCryptPasswordEncoder(12)`.
- **DTO Scrubbing:** Passwords and hashes are excluded from `UserProfileResponse`, `AuthResponse`, and log output.
- **Password Reset Security:** 6-digit cryptographically random verification code stored in `PasswordResetToken` entity with an expiration window and attempt threshold (max 5 invalid attempts triggers lockout). Account enumeration is prevented via generic failure messages.
- **Secret Protection:** `.env` files are listed in `.gitignore`. Secrets (`app.jwt.secret`, `gemini.api-key`, database credentials) are injected via system environment variables. Frontend bundle contains zero backend credentials.

### 1.4 Gemini AI Security & Boundary
- **Sanitized Prompts:** `GeminiPromptBuilder` formats context containing only career titles, scores, and readiness percentages. Passwords, hashes, JWT tokens, and API keys are strictly excluded.
- **Read-Only Scope:** Gemini responses are parsed exclusively for natural language summary text. Gemini cannot mutate database entities, match scores, readiness percentages, or admin configurations.

---

## 2. Security Rating Summary

| Security Category | Rating | Compliance Status |
|---|---|---|
| **JWT Signing & Validation** | **EXCELLENT** | Fully Compliant |
| **Server-Side Authorization (RBAC)** | **EXCELLENT** | Fully Compliant |
| **IDOR Protection** | **EXCELLENT** | Fully Compliant |
| **Password Hashing (BCrypt 12)** | **EXCELLENT** | Fully Compliant |
| **Password Reset Security** | **HIGH** | Fully Compliant |
| **Secret & Key Isolation** | **EXCELLENT** | Fully Compliant |
| **Gemini AI Boundary** | **EXCELLENT** | Fully Compliant |
