# Security & Authentication Architecture

SkillPilot implements stateless JWT-based authentication and role-based access control (RBAC).

---

## 🔑 Authentication Flow

1. **User Login / Registration:**
   - POST `/api/auth/login` or POST `/api/auth/register`.
   - Credentials validated (BCrypt hashing for passwords).
   - Server issues a signed JWT token containing `sub` (userId), `email`, and `role` (`STUDENT` or `ADMIN`).
2. **Client Token Storage:**
   - Client stores token in `localStorage.setItem('skillpilot_token', token)`.
   - Token sent in `Authorization: Bearer <token>` header on subsequent requests.
3. **Session Restoration (`/api/auth/me`):**
   - On application startup or browser reload, client calls GET `/api/auth/me`.
   - Server returns `UserProfileResponse` containing `userRole` (`"student"` or `"admin"`).
   - Client resolves role and restores original browser URL route.

---

## 🛡️ Role-Based Access Control (RBAC)

- **Guest (`guest`):** Can view public careers, public skills, public questionnaire preview, and landing page.
- **Student (`student`):** Access to profile, skill levels, questionnaire submission, career matches, skill-gap analysis, target career selection, and milestone roadmaps.
- **Admin (`admin`):** Exclusive access to `/api/admin/**` endpoints for managing careers, career requirements, skills, questionnaires, options, skill mappings, system weights, and system health metrics.

---

## 🔒 Security Policies & Protections

- **Password Policy:** Minimum 8 characters, at least one digit and one uppercase letter. Hashed with `BCryptPasswordEncoder(12)`.
- **IDOR Protection:** User endpoints (`/api/user/**`, `/api/roadmaps/**`, `/api/careers/matches/**`) fetch `userId` strictly from `@AuthenticationPrincipal SecurityUser`. Users cannot inspect or mutate other users' private data.
- **Account Enumeration Defense:** Failed login returns generic error `"Invalid email or password"`.
- **Password Recovery Rate Limiting:** 6-digit verification code with expiration map and max 5 attempts lockout.
