# SkillPilot — Legacy Database Cleanup Plan

**Plan Date:** August 30, 2026  
**Execution Mechanism:** Flyway Migration `V10__cleanup_legacy_tables_and_add_saved_careers.sql`  
**Scope:** 48 Unmanaged / Legacy Duplicate / Inconsistent Tables (All 0 rows)

---

## 1. Safety Verification & Governance Rules

Before scheduling any table for removal, the following verification gates were satisfied:
1. **Zero Data Loss:** Every target table was confirmed to have **0 rows** in the production database.
2. **Zero Code References:** Ripgrep searches across `backend/src/` and `frontend/src/` confirmed no Java entity, repository, service, controller, DTO, or TypeScript component references these tables.
3. **No Migration History Conflicts:** None of the 48 target tables were created by official Flyway migrations `V1` through `V9`.
4. **Authoritative Replacement In Place:** Every legacy table has an active, tested, authoritative counterpart in the core schema.
5. **No Manual DDL:** All cleanup is executed strictly via versioned Flyway migration `V10` to ensure reproducibility across staging and test environments.

---

## 2. Table-by-Table Cleanup Register

| # | Table Name | Reason for Cleanup | Authoritative Replacement | Verified Code Refs | Current Rows | Migration Strategy | Risk |
|---|---|---|---|---|---|---|---|
| 1 | `activity_events` | Unmanaged speculative table | None / Logging framework | None | 0 | `DROP TABLE IF EXISTS` | None |
| 2 | `admin_notifications` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 3 | `admin_users` | Legacy duplicate RBAC | `users.role = 'ADMIN'` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 4 | `ai_bookmarks` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 5 | `ai_conversations` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 6 | `ai_messages` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 7 | `ai_prompt_templates` | Unmanaged speculative table | `GeminiExplanationService` constants | None | 0 | `DROP TABLE IF EXISTS` | None |
| 8 | `ai_settings` | Unmanaged speculative table | `application.yml` (`gemini.*`) | None | 0 | `DROP TABLE IF EXISTS` | None |
| 9 | `ai_usage_logs` | Legacy duplicate | `ai_generation_logs` (Flyway V1) | None | 0 | `DROP TABLE IF EXISTS` | None |
| 10 | `assessment_progress` | Unmanaged speculative table | `user_question_answers` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 11 | `audit_logs` | Unmanaged speculative table | Logback / SLF4J | None | 0 | `DROP TABLE IF EXISTS` | None |
| 12 | `bookmarked_resources` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 13 | `career_categories` | Legacy duplicate | `careers.category` column | None | 0 | `DROP TABLE IF EXISTS` | None |
| 14 | `career_goals` | Legacy duplicate | `user_target_careers` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 15 | `career_matching_rules` | Legacy duplicate | `system_configs` & `CareerScoringEngine` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 16 | `career_preferences` | Legacy duplicate | `users` profile intelligence fields | None | 0 | `DROP TABLE IF EXISTS` | None |
| 17 | `career_recommendations` | Legacy duplicate | `career_match_results` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 18 | `career_skills` | Legacy duplicate (BigInt ID) | `career_skill_requirements` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 19 | `education_records` | Legacy duplicate (BigInt user_id) | `users` profile intelligence fields | None | 0 | `DROP TABLE IF EXISTS` | None |
| 20 | `experience_records` | Legacy duplicate (BigInt user_id) | `users` profile intelligence fields | None | 0 | `DROP TABLE IF EXISTS` | None |
| 21 | `favorite_careers` | Incompatible legacy table | Replaced by clean `saved_careers` (V10) | None | 0 | `DROP TABLE IF EXISTS` | None |
| 22 | `feature_flags` | Unmanaged speculative table | `application.yml` (`gemini.enabled`, etc.) | None | 0 | `DROP TABLE IF EXISTS` | None |
| 23 | `interview_sessions` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 24 | `learning_effort_reference` | Unmanaged speculative table | `RoadmapGenerationEngine` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 25 | `learning_resources` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 26 | `learning_streaks` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 27 | `milestone_completion` | Legacy duplicate (BigInt ID) | `user_roadmap_milestones.status` & `completed_at` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 28 | `permissions` | Legacy duplicate RBAC | `users.role` enum & Spring Security | None | 0 | `DROP TABLE IF EXISTS` | None |
| 29 | `progress_checkpoints` | Legacy duplicate (BigInt ID) | `user_roadmap_milestones.goals` (JSON) | None | 0 | `DROP TABLE IF EXISTS` | None |
| 30 | `question_categories` | Legacy duplicate | `questions.section` column | None | 0 | `DROP TABLE IF EXISTS` | None |
| 31 | `readiness_snapshots` | Legacy duplicate | Dynamic `SkillGapAnalysisEngine` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 32 | `recommendation_history` | Legacy duplicate | `career_match_results` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 33 | `resume_analysis_results` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 34 | `resume_uploads` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 35 | `roadmap_history` | Legacy duplicate | `user_roadmaps.updated_at` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 36 | `roadmap_milestones` | Legacy duplicate (BigInt ID) | `user_roadmap_milestones` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 37 | `roadmaps` | Legacy duplicate (BigInt ID) | `user_roadmaps` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 38 | `role_permissions` | Legacy duplicate RBAC | `users.role` enum | None | 0 | `DROP TABLE IF EXISTS` | None |
| 39 | `roles` | Legacy duplicate RBAC | `users.role` enum (`STUDENT`, `ADMIN`) | None | 0 | `DROP TABLE IF EXISTS` | None |
| 40 | `saved_careers` (legacy) | Incompatible BigInt schema | Rebuilt as `saved_careers` (UUID FKs) | None | 0 | `DROP TABLE IF EXISTS` | None |
| 41 | `skill_gap_history` | Legacy duplicate | Dynamic `SkillGapAnalysisEngine` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 42 | `skill_gap_reports` | Legacy duplicate | Dynamic `SkillGapAnalysisEngine` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 43 | `system_metrics` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |
| 44 | `user_answers` | Legacy duplicate (BigInt user_id) | `user_question_answers` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 45 | `user_profiles` | Legacy duplicate (BigInt user_id) | `users` profile intelligence fields | None | 0 | `DROP TABLE IF EXISTS` | None |
| 46 | `user_roles` | Legacy duplicate RBAC | `users.role` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 47 | `user_skill_ratings` | Legacy duplicate (BigInt user_id) | `user_skills` | None | 0 | `DROP TABLE IF EXISTS` | None |
| 48 | `weekly_learning_goals` | Unmanaged speculative table | None | None | 0 | `DROP TABLE IF EXISTS` | None |

---

## 3. Migration Execution Plan (`V10`)

Flyway migration `V10__cleanup_legacy_tables_and_add_saved_careers.sql` will perform the following atomic operations:

1. Temporarily disable foreign key constraints (`SET FOREIGN_KEY_CHECKS = 0;`).
2. Drop all 48 unmanaged/legacy empty tables.
3. Re-enable foreign key constraints (`SET FOREIGN_KEY_CHECKS = 1;`).
4. Create the new, authoritative `saved_careers` table with UUID user identification and relational cascade links.

```sql
CREATE TABLE saved_careers (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    career_id VARCHAR(64) NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_saved_career_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_career_career FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE,
    CONSTRAINT uq_saved_career UNIQUE (user_id, career_id),
    INDEX idx_saved_careers_user_id (user_id),
    INDEX idx_saved_careers_career_id (career_id)
);
```
