# SkillPilot — Complete REST API Flow Map

This document maps all REST endpoints across the SkillPilot platform, including HTTP method, URL path, Spring Boot controller, delegate service, repositories accessed, and underlying MySQL tables.

---

## 1. Authentication & Session Lifecycle (`/api/auth/**`)

| HTTP Method | Endpoint Path | Controller | Service | Repositories Touched | Primary Tables | Purpose |
|---|---|---|---|---|---|---|
| `POST` | `/api/auth/register` | `AuthController` | `AuthService` | `UserRepository` | `users` | Creates new student user account, hashes password via BCrypt, initializes profile. |
| `POST` | `/api/auth/login` | `AuthController` | `AuthService` | `UserRepository` | `users` | Authenticates email/password, issues HMAC-512 JWT token with role claims. |
| `POST` | `/api/auth/forgot-password` | `AuthController` | `AuthService`, `EmailService` | `UserRepository` | `users` | Generates reset token and dispatches HTML password recovery email. |
| `POST` | `/api/auth/reset-password` | `AuthController` | `AuthService` | `UserRepository` | `users` | Validates recovery token, hashes and updates new password in MySQL. |

---

## 2. User Intelligence & Profile Management (`/api/user/**`)

| HTTP Method | Endpoint Path | Controller | Service | Repositories Touched | Primary Tables | Purpose |
|---|---|---|---|---|---|---|
| `GET` | `/api/user/profile` | `UserProfileController` | `UserProfileService` | `UserRepository`, `UserSkillRepository` | `users`, `user_skills`, `skills` | Retrieves full 20-field profile, completeness %, and rated skills. |
| `PUT` | `/api/user/profile` | `UserProfileController` | `UserProfileService`, `CompletionCalculatorService` | `UserRepository` | `users` | Updates education, experience, employment, and target preference fields. |
| `GET` | `/api/user/skills` | `UserSkillController` | `UserSkillService` | `UserSkillRepository` | `user_skills`, `skills` | Retrieves all skill ratings ($0-5$) for the authenticated student. |
| `PUT` | `/api/user/skills` | `UserSkillController` | `UserSkillService` | `UserSkillRepository`, `SkillRepository` | `user_skills` | Upserts a specific skill proficiency level ($0 \dots 5$). |

---

## 3. Career Discovery & Scoring (`/api/careers/**`, `/api/user/career-matches/**`)

| HTTP Method | Endpoint Path | Controller | Service | Repositories Touched | Primary Tables | Purpose |
|---|---|---|---|---|---|---|
| `GET` | `/api/careers` | `CareerController` | `CareerService` | `CareerRepository`, `CareerSkillRequirementRepository` | `careers`, `career_skill_requirements`, `skills` | Returns list of all active careers with skill requirements. |
| `GET` | `/api/careers/{id}` | `CareerController` | `CareerService` | `CareerRepository`, `CareerSkillRequirementRepository` | `careers`, `career_skill_requirements`, `skills` | Retrieves single career details, prerequisites, and typical roles. |
| `GET` | `/api/user/career-matches` | `CareerDiscoveryController` | `CareerDiscoveryService`, `CareerScoringEngine` | `UserRepository`, `UserSkillRepository`, `CareerRepository`, `SystemConfigRepository`, `CareerMatchResultRepository` | `users`, `user_skills`, `careers`, `career_skill_requirements`, `career_match_results`, `system_configs` | Deterministically computes compatibility scores across all 36 careers (Algorithm v2.5). |
| `GET` | `/api/user/target-career` | `TargetCareerController` | `TargetCareerService` | `UserTargetCareerRepository`, `CareerRepository` | `user_target_careers`, `careers` | Retrieves currently selected target career for the student. |
| `POST` | `/api/user/target-career` | `TargetCareerController` | `TargetCareerService` | `UserTargetCareerRepository`, `CareerRepository` | `user_target_careers` | Selects target career, triggering questionnaire & skill gap invalidation. |

---

## 4. Dynamic Questionnaire Engine (`/api/questionnaire/**`)

| HTTP Method | Endpoint Path | Controller | Service | Repositories Touched | Primary Tables | Purpose |
|---|---|---|---|---|---|---|
| `GET` | `/api/questionnaire` | `QuestionnaireController` | `QuestionnaireService` | `QuestionRepository`, `QuestionOptionRepository`, `QuestionSkillMappingRepository`, `UserTargetCareerRepository` | `questions`, `question_options`, `question_skill_mappings`, `user_target_careers` | Fetches active questionnaire items (general + target-career specific). |
| `POST` | `/api/questionnaire/answers`| `QuestionnaireController` | `QuestionnaireService` | `UserQuestionAnswerRepository`, `QuestionOptionRepository` | `user_questionnaire_answers` | Submits answers for questions, persisting selected option IDs. |

---

## 5. Skill Gap Analysis (`/api/user/skill-gap/**`)

| HTTP Method | Endpoint Path | Controller | Service | Repositories Touched | Primary Tables | Purpose |
|---|---|---|---|---|---|---|
| `GET` | `/api/user/skill-gap` | `TargetCareerController` | `SkillGapService`, `SkillGapAnalysisEngine` | `UserRepository`, `UserSkillRepository`, `UserTargetCareerRepository`, `CareerSkillRequirementRepository` | `users`, `user_skills`, `user_target_careers`, `careers`, `career_skill_requirements`, `skills` | Computes multi-dimensional readiness (Skill %, Experience %, Education %, Overall %) for target career. |

---

## 6. Learning Roadmap & Milestone Progress (`/api/user/roadmaps/**`)

| HTTP Method | Endpoint Path | Controller | Service | Repositories Touched | Primary Tables | Purpose |
|---|---|---|---|---|---|---|
| `GET` | `/api/user/roadmaps/active` | `RoadmapController` | `RoadmapService` | `RoadmapRepository`, `RoadmapMilestoneRepository`, `UserTargetCareerRepository` | `roadmaps`, `roadmap_milestones`, `user_target_careers` | Fetches current learning roadmap with all milestone progress states. |
| `POST` | `/api/user/roadmaps/generate` | `RoadmapController` | `RoadmapService`, `RoadmapGenerationEngine` | `RoadmapRepository`, `RoadmapMilestoneRepository`, `UserTargetCareerRepository`, `CareerSkillRequirementRepository` | `roadmaps`, `roadmap_milestones`, `careers`, `career_skill_requirements` | Generates 3, 6, or 12-month milestone progression, preserving completed milestones. |
| `PUT` | `/api/user/roadmaps/{roadmapId}/milestones/{milestoneId}/progress` | `RoadmapController` | `RoadmapService` | `RoadmapRepository`, `RoadmapMilestoneRepository` | `roadmaps`, `roadmap_milestones` | Persists milestone status (`not_started`, `in_progress`, `completed`), progress %, notes, and timestamp. |

---

## 7. AI Advisory & Explanations (`/api/ai/**`)

| HTTP Method | Endpoint Path | Controller | Service | Repositories Touched | Primary Tables | Purpose |
|---|---|---|---|---|---|---|
| `POST` | `/api/ai/explain/career` | `AiController` | `GeminiExplanationService`, `FallbackExplanationService` | `CareerRepository` | `careers` | Generates personalized career match explanation and strategic advice. |
| `POST` | `/api/ai/explain/skill-gap` | `AiController` | `GeminiExplanationService`, `FallbackExplanationService` | `UserSkillRepository`, `CareerSkillRequirementRepository` | `user_skills`, `career_skill_requirements` | Generates actionable advice on closing skill gaps. |
| `POST` | `/api/ai/explain/roadmap` | `AiController` | `GeminiExplanationService`, `FallbackExplanationService` | `RoadmapMilestoneRepository` | `roadmap_milestones` | Generates phase-specific learning recommendations and study tips. |

---

## 8. Administrative Master Data & Diagnostics (`/api/admin/**`)

*Note: All `/api/admin/**` endpoints are guarded by `@PreAuthorize("hasRole('ADMIN')")`.*

| HTTP Method | Endpoint Path | Controller | Service | Repositories Touched | Primary Tables | Purpose |
|---|---|---|---|---|---|---|
| `GET` | `/api/admin/health` | `AdminSystemConfigController`| `SystemConfigService` | `CareerRepository`, `SkillRepository`, `CareerSkillRequirementRepository`, `QuestionOptionRepository` | `careers`, `skills`, `career_skill_requirements`, `question_options`, `question_skill_mappings` | Computes system health score ($0-100\%$) and configuration diagnostic warnings. |
| `GET` | `/api/admin/config` | `AdminSystemConfigController`| `SystemConfigService` | `SystemConfigRepository` | `system_configs` | Retrieves all algorithmic scoring weights and configuration keys. |
| `PUT` | `/api/admin/config` | `AdminSystemConfigController`| `SystemConfigService` | `SystemConfigRepository` | `system_configs` | Updates scoring weights (skill weight, question cap, min thresholds). |
| `POST` | `/api/admin/careers` | `AdminMasterDataController` | `CareerService` | `CareerRepository` | `careers` | Creates new industry career definition. |
| `PUT` | `/api/admin/careers/{id}` | `AdminMasterDataController` | `CareerService` | `CareerRepository` | `careers` | Updates career metadata, salary, growth rate, or demand level. |
| `DELETE`| `/api/admin/careers/{id}` | `AdminMasterDataController` | `CareerService` | `CareerRepository` | `careers` | Soft deletes or deactivates career. |
| `POST` | `/api/admin/careers/{id}/requirements` | `AdminMasterDataController` | `CareerService` | `CareerSkillRequirementRepository` | `career_skill_requirements` | Adds required skill with level and essential flag to career. |
| `DELETE`| `/api/admin/careers/{id}/requirements/{skillId}` | `AdminMasterDataController` | `CareerService` | `CareerSkillRequirementRepository` | `career_skill_requirements` | Removes skill requirement from career. |
| `POST` | `/api/admin/skills` | `AdminMasterDataController` | `SkillService` | `SkillRepository` | `skills` | Adds new skill to master catalog. |
| `PUT` | `/api/admin/skills/{id}` | `AdminMasterDataController` | `SkillService` | `SkillRepository` | `skills` | Updates skill name, category, or active status. |
| `POST` | `/api/admin/questions` | `AdminMasterDataController` | `QuestionnaireService` | `QuestionRepository` | `questions` | Creates new questionnaire item. |
| `POST` | `/api/admin/questions/{id}/options` | `AdminMasterDataController` | `QuestionnaireService` | `QuestionOptionRepository` | `question_options` | Adds selectable option to questionnaire item. |
| `POST` | `/api/admin/options/{id}/mappings` | `AdminMasterDataController` | `QuestionnaireService` | `QuestionSkillMappingRepository` | `question_skill_mappings` | Maps option to skill with impact weight. |
