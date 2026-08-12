# Phase 1 — Architecture & Codebase Audit Report

**Date:** August 12, 2026  
**Auditor:** SkillPilot Lead System Auditor

---

## 1. Component & Data Flow Mapping

```
[ React SPA Pages ]
(Landing, Login, Register, Profile, Questionnaire, Results, TargetSelection, SkillGap, Roadmap, AdminDashboard)
       │
       ▼
[ AppContext (React Context) ]
(State management, session bootstrap machine, token watcher, route synchronization)
       │
       ▼  HTTP / REST JSON (Authorization: Bearer <JWT>)
[ Spring Boot Controller Layer ]
(AuthController, UserProfileController, UserSkillController, TargetCareerController,
 CareerDiscoveryController, QuestionnaireController, RoadmapController, AdminMasterDataController,
 AdminSystemConfigController, AiController)
       │
       ▼
[ Spring Boot Service Layer ]
(AuthService, UserProfileService, UserSkillService, TargetCareerService, CareerDiscoveryService,
 QuestionnaireService, SkillGapService, RoadmapService, SystemConfigService, GeminiExplanationService)
       │
       ▼
[ Spring Data JPA Repository Layer ]
(UserRepository, SkillRepository, CareerRepository, CareerSkillRequirementRepository,
 UserSkillRepository, UserQuestionAnswerRepository, UserTargetCareerRepository,
 CareerMatchResultRepository, RoadmapRepository, RoadmapMilestoneRepository,
 QuestionRepository, QuestionOptionRepository, QuestionSkillMappingRepository,
 SystemConfigRepository, AIGenerationLogRepository, PasswordResetTokenRepository)
       │
       ▼
[ MySQL 8.0 Database ]
(Flyway V1..V5 migrations, schema history, foreign keys, unique constraints, indices)
```

---

## 2. Architecture Audit Findings

### 2.1 Component & Service Mapping Summary
- **Frontend Architecture:** 10 core pages, 7 shared UI components (`Header`, `Footer`, `ToastContainer`, etc.), 1 central context (`AppContext`), and 1 utility engine (`careerEngine.ts` for guest fallback preview).
- **Backend Architecture:** 14 REST Controllers, 19 Services, 17 JPA Repositories, 5 Flyway DB Migrations.

### 2.2 Findings Matrix

| Finding ID | Domain | Description | Severity | Recommendation |
|---|---|---|---|---|
| **ARCH-001** | Frontend Fallback | `careerEngine.ts` contains guest preview scoring logic for unauthenticated landing visitors. | INFORMATIONAL | Maintain guest preview boundary; ensure logged-in state always renders `backendCareerMatches` from MySQL. |
| **ARCH-002** | Master Data Loading | `AppProvider` loads master data (`careers`, `skills`, `questionnaire`) on mount and retries if backend was starting up. | INFORMATIONAL | Verified functioning cleanly via `ensureMasterDataLoaded()` retry machine. |
| **ARCH-003** | Service Modularization | Master data operations in Admin are consolidated under `AdminMasterDataController.java`. | LOW | Structure is clean and efficient; maintain `@PreAuthorize("hasRole('ADMIN')")`. |

---

## 3. Dependency & Code Hygiene Review

- **Production Mock Data:** Confirmed NO mock data is used in production backend endpoints. All endpoints execute live SQL queries against H2 (in-memory for tests) and MySQL (in production).
- **Unused Dependencies:** Maven `pom.xml` and npm `package.json` reviewed. All dependencies (`spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `jjwt-api`, `flyway-core`, `lucide-react`) are active and necessary.
- **Dead Code:** Clean. Obsolete test controllers and legacy stub handlers have been pruned.
