-- SkillPilot Legacy Tables Cleanup & Saved Careers Migration
-- V10__cleanup_legacy_tables_and_add_saved_careers.sql

SET FOREIGN_KEY_CHECKS = 0;

-- 1. DROP UNMANAGED / LEGACY DUPLICATE / INCONSISTENT TABLES (48 Empty Tables)
DROP TABLE IF EXISTS activity_events;
DROP TABLE IF EXISTS admin_notifications;
DROP TABLE IF EXISTS admin_users;
DROP TABLE IF EXISTS ai_bookmarks;
DROP TABLE IF EXISTS ai_conversations;
DROP TABLE IF EXISTS ai_messages;
DROP TABLE IF EXISTS ai_prompt_templates;
DROP TABLE IF EXISTS ai_settings;
DROP TABLE IF EXISTS ai_usage_logs;
DROP TABLE IF EXISTS assessment_progress;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS bookmarked_resources;
DROP TABLE IF EXISTS career_categories;
DROP TABLE IF EXISTS career_goals;
DROP TABLE IF EXISTS career_matching_rules;
DROP TABLE IF EXISTS career_preferences;
DROP TABLE IF EXISTS career_recommendations;
DROP TABLE IF EXISTS career_skills;
DROP TABLE IF EXISTS education_records;
DROP TABLE IF EXISTS experience_records;
DROP TABLE IF EXISTS favorite_careers;
DROP TABLE IF EXISTS feature_flags;
DROP TABLE IF EXISTS interview_sessions;
DROP TABLE IF EXISTS learning_effort_reference;
DROP TABLE IF EXISTS learning_resources;
DROP TABLE IF EXISTS learning_streaks;
DROP TABLE IF EXISTS milestone_completion;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS progress_checkpoints;
DROP TABLE IF EXISTS question_categories;
DROP TABLE IF EXISTS readiness_snapshots;
DROP TABLE IF EXISTS recommendation_history;
DROP TABLE IF EXISTS resume_analysis_results;
DROP TABLE IF EXISTS resume_uploads;
DROP TABLE IF EXISTS roadmap_history;
DROP TABLE IF EXISTS roadmap_milestones;
DROP TABLE IF EXISTS roadmaps;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS saved_careers;
DROP TABLE IF EXISTS skill_gap_history;
DROP TABLE IF EXISTS skill_gap_reports;
DROP TABLE IF EXISTS system_metrics;
DROP TABLE IF EXISTS user_answers;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_skill_ratings;
DROP TABLE IF EXISTS weekly_learning_goals;

SET FOREIGN_KEY_CHECKS = 1;

-- 2. CREATE AUTHORITATIVE SAVED CAREERS TABLE (UUID Links & Cascade Deletion)
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
