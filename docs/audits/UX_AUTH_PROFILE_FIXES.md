# SkillPilot — UX, Authentication & Profile Fixes Audit

**Audit ID:** AUD-2026-UX-AUTH  
**Status:** Verified & Passed  
**Branch:** `feature/ux-auth-polish-and-engine-documentation`  

---

## 1. Executive Summary

This audit validates targeted fixes across the SkillPilot user experience, account security, and profile intelligence:
1. **App Icon & Favicon:** Designed and integrated a high-resolution branded SVG favicon (`favicon.svg`) into `frontend/index.html`. Confirmed inclusion in Vite production build.
2. **Password Visibility Controls:** Implemented keyboard-accessible show/hide password toggle controls with `Eye` and `EyeOff` icons from `lucide-react` on `LoginPage.tsx` and `RegistrationPage.tsx`.
3. **New User Email Verification Flow:** Closed the critical authentication loophole where users could register without email verification. Implemented Flyway migration `V11`, JPA entity `EmailVerification`, Brevo SMTP email dispatch, rate-limiting, and verification endpoints.
4. **Date of Birth Input & Validation:** Replaced unstructured text input with HTML5 native accessible `<input type="date">`, bounded between 1900 and current date. Converted MySQL column from `VARCHAR(20)` to `DATE NULL` via Flyway `V11`. Added backend age boundary validation (13 to 120 years) and future date rejection.
5. **Focused Regression Sweep:** Validated zero regressions across authentication, password recovery, session restoration, admin dashboard, and roadmap tracking.

---

## 2. Issues Discovered & Remediation Details

### Bug 1: Missing Favicon in Browser Tab
- **Symptom:** Browser tabs displayed the default generic globe icon, and requests to `/favicon.ico` returned 404.
- **Root Cause:** `frontend/index.html` lacked `<link rel="icon">` metadata, and no brand favicon asset existed in `frontend/public/`.
- **Fix:** Created `frontend/public/favicon.svg` matching SkillPilot's dark slate container and sky-blue compass needle brand identity. Linked the asset in `index.html` with `<link rel="icon" type="image/svg+xml" href="/favicon.svg" />` and theme-color `#0f172a`. Verified `dist/favicon.svg` output on production build.

### Bug 2: Password Field Obfuscation Inflexibility
- **Symptom:** Users could not inspect typed passwords on the login and registration forms, causing frequent typos and failed login attempts.
- **Root Cause:** Both password inputs were hardcoded to `type="password"` without toggle controls.
- **Fix:** Added `showPassword` state to `LoginPage.tsx` and `RegistrationPage.tsx`. Replaced with accessible button controls using `Eye` and `EyeOff` icons from existing `lucide-react`. Set `aria-label`, `type="button"`, and right-padding `pr-10` to avoid text overlap.

### Bug 3: Unverified User Access Vulnerability
- **Symptom:** New accounts were automatically logged in with full JWT access immediately upon registration without validating email ownership.
- **Root Cause:** `AuthService.register()` immediately created an active JWT session without generating a verification token or persisting an email verification lifecycle.
- **Fix:** 
  - Added Flyway migration `V11__add_user_email_verification.sql` introducing `is_verified BOOLEAN NOT NULL DEFAULT TRUE` to `users` (preserving all existing accounts) and creating the `email_verifications` table.
  - Implemented `EmailVerification` entity and repository.
  - Updated `AuthService.register()` to create accounts with `isVerified = false`, generate a 6-digit cryptographically secure code (`SecureRandom`), and dispatch an email via Brevo SMTP using responsive HTML branding.
  - Added `POST /api/auth/verify-email` and `POST /api/auth/resend-verification`.
  - Blocked unverified logins in `AuthService.login()` with `BadRequestException("Account email is not verified. Please verify your email before logging in.")`.
  - Created an interactive "Verify Your Email" view in `RegistrationPage.tsx` with 60-second resend cooldown.

### Bug 4: Date of Birth Freeform String Vulnerability
- **Symptom:** Profile accepted invalid date strings, future dates (e.g. `2099-01-01`), and impossible birth dates without validation.
- **Root Cause:** `User.java` and MySQL column `date_of_birth` were `VARCHAR(20)` without validation in `UserProfileService.java`, and frontend used `<input type="text" placeholder="YYYY-MM-DD">`.
- **Fix:**
  - In Flyway `V11`, cleaned blank values and altered column `date_of_birth` to SQL `DATE NULL`.
  - In `User.java`, updated field to `java.time.LocalDate`.
  - In `UserProfileService.java`, added strict ISO-8601 parsing, rejected future dates (`dob.isAfter(now)`), and enforced reasonable age constraints (between 13 and 120 years).
  - In `ProfilePage.tsx`, updated to native `<input type="date">` with `max={today}` and `min="1900-01-01"`.

---

## 3. Regression Sweep Verification Matrix

| Area Tested | Test Scenario | Observed Result | Pass / Fail |
| :--- | :--- | :--- | :--- |
| **Registration** | Register with valid credentials | User created as `isVerified: false`; verification code generated in MySQL; email dispatched via Brevo; 201 response with `requiresVerification: true`. | **PASS** |
| **Login Security** | Attempt login with unverified account | HTTP 400 Bad Request: "Account email is not verified. Please verify your email before logging in." | **PASS** |
| **Email Verification** | Enter valid 6-digit code | HTTP 200 OK: User marked `isVerified = true`; JWT token generated; profile returned; immediate access granted. | **PASS** |
| **Invalid Code** | Enter wrong code | HTTP 400 Bad Request: "Invalid verification code." Attempt count incremented in DB. | **PASS** |
| **Lockout Protection** | Enter wrong code 5 times | HTTP 400 Bad Request: "Maximum verification attempts exceeded." Code locked out. | **PASS** |
| **Resend Code** | Trigger resend verification | Previous code invalidated in DB; new code generated and dispatched; 60s cooldown enforced in UI. | **PASS** |
| **Legacy User Login** | Pre-existing user login | Existing users (created prior to V11) default to `isVerified: true` and log in without verification prompts. | **PASS** |
| **Forgot Password** | Request password reset | Password reset flow operates independently using `password_reset_codes`; zero interference with email verification. | **PASS** |
| **Profile DOB** | Save valid DOB (`2001-08-20`) | Saved as SQL `DATE`; persisted across page refresh; returned in ISO-8601 format. | **PASS** |
| **Profile DOB Validation** | Save future DOB or age < 13 | HTTP 400 Bad Request rejected with descriptive error. | **PASS** |
| **Header Navigation** | Switch pages as guest and student | Protected tabs remain appropriately locked for guests and unlocked for students. | **PASS** |
