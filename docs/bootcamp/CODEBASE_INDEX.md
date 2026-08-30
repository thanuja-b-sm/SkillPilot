# SkillPilot — Codebase Index & Bootcamp Learning Guide

This document provides a guided learning index for engineers and students studying the **SkillPilot** codebase. Each core module is classified by **Path**, **Purpose**, **Difficulty Level**, and its corresponding **Bootcamp Lesson Focus**.

---

## 1. Key Core Components Index

| File Path | Component Purpose | Difficulty | Bootcamp Lesson Focus |
|---|---|---|---|
| `frontend/src/context/AppContext.tsx` | Global frontend state machine managing auth tokens, profile intelligence, atomic career switching, and optimistic updates. | **Advanced** | **Lesson 4: Frontend State & Atomic Routing** |
| `backend/src/main/java/com/skillpilot/service/CareerScoringEngine.java` | Algorithm v2.5 deterministic scoring engine computing raw match scores ($0-100\%$) and normalized question bonuses. | **Advanced** | **Lesson 6: Deterministic Scoring Engines** |
| `backend/src/main/java/com/skillpilot/service/SkillGapAnalysisEngine.java` | Multi-dimensional readiness engine evaluating technical skills, experience alignment, education context, and gap severity. | **Advanced** | **Lesson 7: Skill Gap Analysis & Experience Buffers** |
| `backend/src/main/java/com/skillpilot/service/RoadmapGenerationEngine.java` | Duration-aware roadmap synthesis engine (3/6/12 months) assembling chronological milestone progressions. | **Advanced** | **Lesson 8: Dynamic Roadmap Generation** |
| `backend/src/main/java/com/skillpilot/service/AuthService.java` | Account creation, BCrypt validation, JWT issuance, and password recovery token handling. | **Medium** | **Lesson 3: Authentication & Security Hardening** |
| `backend/src/main/java/com/skillpilot/security/JwtAuthenticationFilter.java` | Spring Security filter extracting Bearer JWTs, validating signatures, and populating `SecurityUser` principals. | **Medium** | **Lesson 3: Authentication & Security Hardening** |
| `backend/src/main/java/com/skillpilot/service/UserProfileService.java` | Coordinates 20 User Intelligence fields, profile completeness meter calculation, and user skill updates. | **Medium** | **Lesson 5: User Intelligence & Profile Models** |
| `backend/src/main/java/com/skillpilot/service/RoadmapService.java` | Handles roadmap retrieval, milestone progress persistence, stale state detection, and regeneration safety. | **Medium** | **Lesson 8: Dynamic Roadmap Generation** |
| `backend/src/main/java/com/skillpilot/controller/AdminMasterDataController.java` | REST Controller managing CRUD operations for careers, skills, requirements, and questionnaire option mappings. | **Medium** | **Lesson 9: Administrative Master Data Management** |
| `backend/src/main/java/com/skillpilot/entity/User.java` | JPA entity mapping user credentials, roles, and 20 profile intelligence attributes. | **Easy** | **Lesson 2: Data Modeling & JPA Entities** |
| `backend/src/main/java/com/skillpilot/entity/Career.java` | JPA entity defining industry career tracks, salary data, growth metrics, and prerequisites. | **Easy** | **Lesson 2: Data Modeling & JPA Entities** |
| `backend/src/main/java/com/skillpilot/entity/CareerSkillRequirement.java` | Join entity linking careers to required skills with proficiency ratings and essential flags. | **Easy** | **Lesson 2: Data Modeling & JPA Entities** |
| `backend/src/main/java/com/skillpilot/entity/Question.java` | Dynamic questionnaire JPA entity supporting Single, Multiple, and Scale questions. | **Easy** | **Lesson 2: Data Modeling & JPA Entities** |
| `backend/src/main/java/com/skillpilot/service/ai/GeminiExplanationService.java` | Read-only AI layer generating natural language career insights with fallback protection. | **Medium** | **Lesson 10: AI Advisory Layer & Gemini Integration** |

---

## 2. Bootcamp Curriculum Roadmap

```
+-------------------------------------------------------------------------------+
|                        SKILLPILOT BOOTCAMP CURRICULUM                         |
+-------------------------------------------------------------------------------+
  [Module 1] System Architecture & Request Lifecycle
             - High-level client-server model
             - Request flow: Browser -> React -> Spring Boot -> MySQL

  [Module 2] Relational Data Modeling & Flyway Migrations
             - Entities: User, Career, Skill, CareerSkillRequirement, Roadmap
             - Foreign keys, cascading deletes, and historical snapshots

  [Module 3] Security & Role-Based Access Control (RBAC)
             - Stateless JWT (HMAC-SHA512), BCrypt password hashing
             - IDOR prevention, principal extraction, role isolation

  [Module 4] Frontend State Management with React Context
             - AppContext.tsx state engine, localStorage token persistence
             - Atomic career switching, route protection, error toasts

  [Module 5] User Intelligence & Profile Completeness
             - 20 persistent intelligence fields (education, experience, pace)
             - Weighted completeness scoring meter ($0-100\%$)

  [Module 6] Deterministic Intelligence Engines (Algorithm v2.5)
             - CareerScoringEngine: skill vectors, normalized question bonus
             - Mathematical monotonicity and zero-score floor reachability

  [Module 7] Multi-Dimensional Skill Gap Analysis
             - SkillGapAnalysisEngine: Skill, Experience, Education alignment
             - Gap classification: CRITICAL, IMPORTANT, MINOR, EXPERIENCE_SUPPORTED

  [Module 8] Dynamic Roadmap Generation & Milestone Tracking
             - RoadmapGenerationEngine: 3, 6, and 12 month strategies
             - Milestone status, completion percentage, notes persistence

  [Module 9] Admin Command Center & System Health Diagnostics
             - Real-time configuration health scoring ($0-100\%$)
             - Dynamic master data CRUD and requirement impact isolation

  [Module 10] AI Advisory Boundary & Gemini Integration
             - Strict decoupling between business logic and LLM outputs
             - Non-blocking FallbackExplanationService architecture
+-------------------------------------------------------------------------------+
```

---

## 3. Recommended Code Reading Sequence for New Engineers

For an engineer onboarding onto the SkillPilot platform, the recommended reading sequence is:

1. **Entity Layer & Migrations**:
   - `backend/src/main/resources/db/migration/V1__initial_schema.sql` $\to$ `V8__expand_user_profile_and_roadmap_tracking.sql`
   - `backend/src/main/java/com/skillpilot/entity/User.java`
   - `backend/src/main/java/com/skillpilot/entity/Career.java`
   - `backend/src/main/java/com/skillpilot/entity/CareerSkillRequirement.java`
   - `backend/src/main/java/com/skillpilot/entity/Roadmap.java` & `RoadmapMilestone.java`

2. **Security & Session Layer**:
   - `backend/src/main/java/com/skillpilot/security/JwtTokenProvider.java`
   - `backend/src/main/java/com/skillpilot/security/JwtAuthenticationFilter.java`
   - `backend/src/main/java/com/skillpilot/config/SecurityConfig.java`

3. **Core Intelligence Engines**:
   - `backend/src/main/java/com/skillpilot/service/CareerScoringEngine.java`
   - `backend/src/main/java/com/skillpilot/service/SkillGapAnalysisEngine.java`
   - `backend/src/main/java/com/skillpilot/service/RoadmapGenerationEngine.java`

4. **Service & Controller Orchestration**:
   - `backend/src/main/java/com/skillpilot/service/CareerDiscoveryService.java`
   - `backend/src/main/java/com/skillpilot/service/SkillGapService.java`
   - `backend/src/main/java/com/skillpilot/service/RoadmapService.java`
   - `backend/src/main/java/com/skillpilot/controller/RoadmapController.java`

5. **Frontend State & UI Orchestration**:
   - `frontend/src/types.ts`
   - `frontend/src/context/AppContext.tsx`
   - `frontend/src/pages/ProfilePage.tsx`
   - `frontend/src/pages/SkillGapAnalysisPage.tsx`
   - `frontend/src/pages/RoadmapPage.tsx`
   - `frontend/src/pages/AdminDashboardPage.tsx`
