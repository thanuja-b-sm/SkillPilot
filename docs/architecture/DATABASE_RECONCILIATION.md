# SkillPilot — Database ↔ Backend ↔ Frontend Reconciliation Matrix

**Audit Date:** August 30, 2026  
**Environment:** MySQL 8.0 (`skillpilot` database)  
**Total Tables In Database:** 70  
**Flyway-Managed Active Tables:** 21  
**Unmanaged / Legacy Duplicate Tables:** 48  
**Flyway Migration Catalog:** 1 (`flyway_schema_history`)  

---

## 1. Master Table Inventory & Reconciliation Table

| # | Table | Rows | Classification | Backend Entity | Repository | Service | Controller/API | Frontend Usage | Replacement / Dependency | Action |
|---|---|---|---|---|---|---|---|---|---|---|
| 1 | `careers` | 42 | **A. ACTIVE CORE** | `Career` | `CareerRepository` | `CareerDiscoveryService`, `RoadmapService` | `/api/careers/**`, `/api/admin/careers/**` | `CareerDiscoveryPage`, `CareerResultsPage`, `ProfilePage`, `AdminDashboardPage` | Authoritative career catalogue | **KEEP (Core)** |
| 2 | `skills` | 158 | **A. ACTIVE CORE** | `Skill` | `SkillRepository` | `UserProfileService`, `SkillGapService` | `/api/skills/**`, `/api/admin/skills/**` | `ProfilePage`, `SkillGapAnalysisPage`, `AdminDashboardPage` | Authoritative skills dictionary | **KEEP (Core)** |
| 3 | `career_skill_requirements` | 178 | **A. ACTIVE CORE** | `CareerSkillRequirement` | `CareerSkillRequirementRepository` | `CareerScoringEngine`, `SkillGapAnalysisEngine` | `/api/admin/careers/{id}/skills` | `ProfilePage`, `SkillGapAnalysisPage`, `AdminDashboardPage` | Career skill matrix mapping | **KEEP (Core)** |
| 4 | `career_prerequisites` | 44 | **A. ACTIVE CORE** | ElementCollection (`Career`) | N/A (JPA mapping) | `CareerService`, `CareerMapper` | `/api/careers/**` | `CareerResultsPage`, `ProfilePage` | Career prerequisite list | **KEEP (Core)** |
| 5 | `career_typical_roles` | 62 | **A. ACTIVE CORE** | ElementCollection (`Career`) | N/A (JPA mapping) | `CareerService`, `CareerMapper` | `/api/careers/**` | `CareerResultsPage` | Typical industry roles list | **KEEP (Core)** |
| 6 | `questions` | 18 | **A. ACTIVE CORE** | `Question` | `QuestionRepository` | `QuestionnaireService` | `/api/questions/**`, `/api/admin/questions/**` | `QuestionnairePage`, `AdminDashboardPage` | Questionnaire master items | **KEEP (Core)** |
| 7 | `question_options` | 49 | **A. ACTIVE CORE** | `QuestionOption` | `QuestionOptionRepository` | `QuestionnaireService` | `/api/questions/**`, `/api/admin/questions/**` | `QuestionnairePage`, `AdminDashboardPage` | Options per question | **KEEP (Core)** |
| 8 | `question_skill_mappings` | 124 | **A. ACTIVE CORE** | `QuestionSkillMapping` | `QuestionSkillMappingRepository` | `QuestionnaireService`, `CareerScoringEngine` | `/api/admin/questions/**` | `AdminDashboardPage` | Option skill weight mapping | **KEEP (Core)** |
| 9 | `roadmap_templates` | 5 | **A. ACTIVE CORE** | `RoadmapTemplate` | `RoadmapTemplateRepository` | `RoadmapGenerationEngine` | Internal engine fallback | Engine fallback template | **KEEP (Core)** |
| 10 | `roadmap_phase_templates` | 10 | **A. ACTIVE CORE** | `RoadmapPhaseTemplate` | `RoadmapPhaseTemplateRepository` | `RoadmapGenerationEngine` | Internal engine fallback | Engine fallback phase | **KEEP (Core)** |
| 11 | `roadmap_phase_goals` | 24 | **A. ACTIVE CORE** | ElementCollection (`RoadmapPhaseTemplate`) | N/A (JPA mapping) | `RoadmapGenerationEngine` | Internal engine fallback | Engine fallback goals | **KEEP (Core)** |
| 12 | `system_configs` | 1 | **A. ACTIVE CORE** | `SystemConfig` | `SystemConfigRepository` | `SystemConfigService` | `/api/admin/system/config` | `AdminDashboardPage` | Scoring algorithm weights | **KEEP (Core)** |
| 13 | `users` | 9 | **A. ACTIVE CORE** | `User` | `UserRepository` | `AuthService`, `UserProfileService` | `/api/auth/**`, `/api/user/profile` | `LoginPage`, `RegistrationPage`, `ProfilePage`, `Header` | Core user identity & 20 profile intelligence fields | **KEEP (Core)** |
| 14 | `user_skills` | 7 | **A. ACTIVE CORE** | `UserSkill` | `UserSkillRepository` | `UserProfileService`, `UserSkillService` | `/api/user/skills/**` | `ProfilePage`, `SkillGapAnalysisPage` | Persisted student skill proficiencies (Lvl 0-5) | **KEEP (Core)** |
| 15 | `user_question_answers` | 5 | **A. ACTIVE CORE** | `UserQuestionAnswer` | `UserQuestionAnswerRepository` | `QuestionnaireService` | `/api/user/questionnaire/answers` | `QuestionnairePage`, `CareerResultsPage` | Persisted student responses | **KEEP (Core)** |
| 16 | `user_target_careers` | 4 | **A. ACTIVE CORE** | `UserTargetCareer` | `UserTargetCareerRepository` | `TargetCareerService`, `RoadmapService` | `/api/user/target-career` | `TargetCareerSelectionPage`, `ProfilePage`, `RoadmapPage`, `SkillGapAnalysisPage` | Selected target track | **KEEP (Core)** |
| 17 | `career_match_results` | 96 | **A. ACTIVE CORE** | `CareerMatchResult` | `CareerMatchResultRepository` | `CareerDiscoveryService` | `/api/careers/matches` | `CareerResultsPage` | Immutable historical match snapshots | **KEEP (Core)** |
| 18 | `user_roadmaps` | 5 | **A. ACTIVE CORE** | `Roadmap` | `RoadmapRepository` | `RoadmapService` | `/api/user/roadmaps/**` | `RoadmapPage` | Generated user career roadmaps | **KEEP (Core)** |
| 19 | `user_roadmap_milestones` | 20 | **A. ACTIVE CORE** | `RoadmapMilestone` | `RoadmapMilestoneRepository` | `RoadmapService` | `/api/user/roadmaps/**/progress` | `RoadmapPage` | Milestone progress, sliders, notes & status | **KEEP (Core)** |
| 20 | `password_reset_codes` | 7 | **A. ACTIVE CORE** | `PasswordResetCode` | `PasswordResetCodeRepository` | `AuthService`, `EmailService` | `/api/auth/forgot-password`, `/api/auth/reset-password` | `LoginPage` (Forgot Password Modal) | 6-digit OTP codes, 15m expiry, attempt limit | **KEEP (Core)** |
| 21 | `ai_generation_logs` | 0 | **B. ACTIVE BUT EMPTY** | `AIGenerationLog` | `AIGenerationLogRepository` | `GeminiExplanationService` | Internal audit | Background Gemini explanation logger | **KEEP (Audit)** |
| 22 | `flyway_schema_history` | 9 | **SYSTEM TABLE** | N/A | N/A | N/A | N/A | Flyway metadata | Migration version control | **KEEP (System)** |
| 23 | `activity_events` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 24 | `admin_notifications` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 25 | `admin_users` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `users.role = 'admin'` | **CLEANUP (V10)** |
| 26 | `ai_bookmarks` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 27 | `ai_conversations` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 28 | `ai_messages` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 29 | `ai_prompt_templates` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 30 | `ai_settings` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 31 | `ai_usage_logs` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `ai_generation_logs` | **CLEANUP (V10)** |
| 32 | `assessment_progress` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 33 | `audit_logs` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 34 | `bookmarked_resources` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 35 | `career_categories` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Handled via `careers.category` column | **CLEANUP (V10)** |
| 36 | `career_goals` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `user_target_careers` | **CLEANUP (V10)** |
| 37 | `career_matching_rules` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `system_configs` & `CareerScoringEngine` | **CLEANUP (V10)** |
| 38 | `career_preferences` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Handled via `users` target_focus/intelligence fields | **CLEANUP (V10)** |
| 39 | `career_recommendations` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `career_match_results` | **CLEANUP (V10)** |
| 40 | `career_skills` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `career_skill_requirements` | **CLEANUP (V10)** |
| 41 | `education_records` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Handled via `users.education` & `education_level` | **CLEANUP (V10)** |
| 42 | `experience_records` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Handled via `users.experience_years` & intelligence fields | **CLEANUP (V10)** |
| 43 | `favorite_careers` | 0 | **F. BROKEN / INCONSISTENT** | None | None | None | None | None | Incompatible `bigint user_id`; replaced by Flyway V10 `saved_careers` | **CLEANUP (V10)** |
| 44 | `feature_flags` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 45 | `interview_sessions` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 46 | `learning_effort_reference` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 47 | `learning_resources` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 48 | `learning_streaks` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 49 | `milestone_completion` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `user_roadmap_milestones` progress columns | **CLEANUP (V10)** |
| 50 | `permissions` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `users.role` enum & Spring Security RBAC | **CLEANUP (V10)** |
| 51 | `progress_checkpoints` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Handled via `user_roadmap_milestones.goals` (JSON) | **CLEANUP (V10)** |
| 52 | `question_categories` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Handled via `questions.section` column | **CLEANUP (V10)** |
| 53 | `readiness_snapshots` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by dynamic `SkillGapAnalysisEngine` & `user_roadmaps` | **CLEANUP (V10)** |
| 54 | `recommendation_history` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `career_match_results` | **CLEANUP (V10)** |
| 55 | `resume_analysis_results` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 56 | `resume_uploads` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 57 | `roadmap_history` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `user_roadmaps.updated_at` | **CLEANUP (V10)** |
| 58 | `roadmap_milestones` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `user_roadmap_milestones` | **CLEANUP (V10)** |
| 59 | `roadmaps` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `user_roadmaps` | **CLEANUP (V10)** |
| 60 | `role_permissions` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `UserRole` enum (`STUDENT`, `ADMIN`) | **CLEANUP (V10)** |
| 61 | `roles` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `UserRole` enum (`STUDENT`, `ADMIN`) | **CLEANUP (V10)** |
| 62 | `saved_careers` (legacy) | 0 | **F. BROKEN / INCONSISTENT** | None | None | None | None | None | BigInt PK/FK schema broken; replaced by Flyway V10 `saved_careers` | **REBUILD (V10)** |
| 63 | `skill_gap_history` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by dynamic `SkillGapAnalysisEngine` | **CLEANUP (V10)** |
| 64 | `skill_gap_reports` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by dynamic `SkillGapAnalysisEngine` | **CLEANUP (V10)** |
| 65 | `system_metrics` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |
| 66 | `user_answers` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `user_question_answers` | **CLEANUP (V10)** |
| 67 | `user_profiles` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by direct `users` profile columns | **CLEANUP (V10)** |
| 68 | `user_roles` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `users.role` | **CLEANUP (V10)** |
| 69 | `user_skill_ratings` | 0 | **D. LEGACY / DUPLICATE** | None | None | None | None | None | Replaced by `user_skills` | **CLEANUP (V10)** |
| 70 | `weekly_learning_goals` | 0 | **E. UNUSED / ORPHAN** | None | None | None | None | None | Speculative unmanaged table | **CLEANUP (V10)** |

---

## 2. Priority Investigation: Duplicate Data Models

### A. Career / Skill Requirements
- **Legacy Duplicate:** `career_skills` (`id bigint auto_increment`, `career_id varchar(64)`, `skill_id varchar(64)`).
- **Authoritative Model:** `career_skill_requirements` (`id varchar(36)`, `career_id varchar(64)`, `skill_id varchar(64)`, `required_level int`, `is_essential boolean`).
- **Verdict:** `career_skill_requirements` holds 178 active relational mappings populated via Flyway V1, V6, V7. `career_skills` is completely unreferenced and empty.

### B. Questionnaire Answers
- **Legacy Duplicate:** `user_answers` (`user_id bigint`, `question_id varchar(64)`, `selected_options json`).
- **Authoritative Model:** `user_question_answers` (`user_id varchar(36)`, `question_id varchar(64)`, `selected_option_ids json`).
- **Verdict:** `user_question_answers` is backed by `UserQuestionAnswer` entity and Flyway V1. `user_answers` is unreferenced and empty.

### C. User Profiles & Biographical Attributes
- **Legacy Duplicate:** `user_profiles` (`id bigint auto_increment`, `user_id bigint`), `education_records`, `experience_records`, `career_preferences`.
- **Authoritative Model:** `users` table holds direct intelligence fields (`title`, `education`, `experience_years`, `location`, `target_focus`, `bio`, `completion_percentage`, `education_level`, `major`, `graduation_year`, `career_stage`, `target_role_level`, `weekly_availability_hours`, `learning_pace`, `preferred_learning_style`, `risk_tolerance`, `industry_preference`, `work_environment_preference`, `relocation_preference`).
- **Verdict:** The direct `users` profile design eliminates 1-to-1 join overhead and ensures atomic user profile updates.

### D. Role-Based Access Control (RBAC)
- **Legacy Duplicate:** `roles`, `permissions`, `role_permissions`, `user_roles`, `admin_users` (complex multi-table RBAC with `bigint` IDs).
- **Authoritative Model:** `users.role` enum (`'student'`, `'admin'`) mapped to `UserRole` in `User.java` and enforced by Spring Security via `@PreAuthorize("hasRole('ADMIN')")` in `SecurityConfig.java`.
- **Verdict:** SkillPilot’s role hierarchy is clean and deterministic. The legacy role tables are unreferenced and empty.

### E. Roadmaps & Milestones
- **Legacy Duplicate:** `roadmaps` (`user_id bigint`), `roadmap_milestones`, `roadmap_history`, `milestone_completion`, `progress_checkpoints`, `weekly_learning_goals`.
- **Authoritative Model:** `user_roadmaps` (`id varchar(36)`, `user_id varchar(36)`, `career_id varchar(64)`) and `user_roadmap_milestones` (`id varchar(36)`, `roadmap_id varchar(36)`, `status`, `completion_percentage`, `notes`, `completed_at`, `target_skill_id`, `current_level`, `required_level`, `gap_severity`).
- **Verdict:** `user_roadmaps` and `user_roadmap_milestones` are the active models with 5 active user roadmaps and 20 milestones.

---

## 3. Database Inconsistency & Foreign Key Audit

1. **User ID Incompatibility (`varchar(36)` vs `bigint`):**
   - Core tables use UUID strings (`VARCHAR(36)`): `users.id`, `user_skills.user_id`, `user_target_careers.user_id`, `career_match_results.user_id`, `user_roadmaps.user_id`, `password_reset_codes.user_id`.
   - The 48 unmanaged tables use `BIGINT` for user ID and have no foreign key constraint to `users(id)`, making them structurally incompatible with the authentication system.
2. **Missing Cascade Deletes in Legacy Tables:**
   - Unmanaged tables lack cascade deletes or reference non-existent profile records.
3. **Structured Milestone Field Migration (Flyway V8):**
   - Historical milestones created prior to Flyway V8 have `NULL` in `target_skill_id`, `current_level`, `required_level`, `gap_severity` while preserving structured information in `goals` (JSON). Newly generated roadmaps automatically populate all structured columns.

---

## 4. Validated Feature Implementation: Saved Careers (`saved_careers`)

To provide immediate student value while resolving the broken legacy `saved_careers` / `favorite_careers` schema, Flyway migration `V10` replaces the broken tables with a clean, fully-persisted **Saved Careers** model:

- **Database Table:** `saved_careers` (`id VARCHAR(36) PK`, `user_id VARCHAR(36) FK users(id)`, `career_id VARCHAR(64) FK careers(id)`, `notes TEXT`, `created_at TIMESTAMP`).
- **Backend Architecture:**
  - Entity: `SavedCareer`
  - Repository: `SavedCareerRepository`
  - Service: `SavedCareerService`
  - Controller: `SavedCareerController` (`GET /api/user/saved-careers`, `POST /api/user/saved-careers/{careerId}`, `DELETE /api/user/saved-careers/{careerId}`)
- **Frontend Architecture:**
  - `AppContext.tsx`: `savedCareers`, `savedCareerIds` set, `toggleSaveCareer()`
  - `CareerResultsPage.tsx`: Bookmark heart toggle on all career match cards.
  - `ProfilePage.tsx`: "Saved Careers" interactive drawer/section with quick "Set as Target Career" and "Remove" actions.
