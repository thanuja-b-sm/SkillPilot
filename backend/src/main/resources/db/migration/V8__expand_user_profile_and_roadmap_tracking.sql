-- SkillPilot Migration V8: Expand User Profile Intelligence and Roadmap Milestone Tracking
-- V8__expand_user_profile_and_roadmap_tracking.sql

-- 1. ADD PROFILE INTELLIGENCE COLUMNS TO USERS TABLE
ALTER TABLE users ADD COLUMN country VARCHAR(100) AFTER location;
ALTER TABLE users ADD COLUMN date_of_birth VARCHAR(20) AFTER country;

ALTER TABLE users ADD COLUMN institution_name VARCHAR(150) AFTER education;
ALTER TABLE users ADD COLUMN degree_level VARCHAR(100) AFTER institution_name;
ALTER TABLE users ADD COLUMN major_field_of_study VARCHAR(150) AFTER degree_level;
ALTER TABLE users ADD COLUMN graduation_year INT AFTER major_field_of_study;
ALTER TABLE users ADD COLUMN education_status VARCHAR(50) AFTER graduation_year;

ALTER TABLE users ADD COLUMN employment_status VARCHAR(100) AFTER experience_years;
ALTER TABLE users ADD COLUMN current_job_title VARCHAR(150) AFTER employment_status;
ALTER TABLE users ADD COLUMN current_industry VARCHAR(100) AFTER current_job_title;
ALTER TABLE users ADD COLUMN relevant_experience_years INT DEFAULT 0 AFTER current_industry;

ALTER TABLE users ADD COLUMN certifications TEXT AFTER bio;
ALTER TABLE users ADD COLUMN portfolio_url VARCHAR(255) AFTER certifications;
ALTER TABLE users ADD COLUMN career_interests TEXT AFTER portfolio_url;

ALTER TABLE users ADD COLUMN preferred_work_mode VARCHAR(50) AFTER target_focus;
ALTER TABLE users ADD COLUMN preferred_employment_type VARCHAR(50) AFTER preferred_work_mode;
ALTER TABLE users ADD COLUMN career_goal VARCHAR(255) AFTER preferred_employment_type;

ALTER TABLE users ADD COLUMN weekly_hours_available INT DEFAULT 10 AFTER career_goal;
ALTER TABLE users ADD COLUMN preferred_learning_pace VARCHAR(50) DEFAULT 'Steady' AFTER weekly_hours_available;
ALTER TABLE users ADD COLUMN preferred_roadmap_duration INT DEFAULT 6 AFTER preferred_learning_pace;

-- 2. ADD MILESTONE TRACKING COLUMNS TO USER_ROADMAP_MILESTONES TABLE
ALTER TABLE user_roadmap_milestones ADD COLUMN completion_percentage INT NOT NULL DEFAULT 0 AFTER status;
ALTER TABLE user_roadmap_milestones ADD COLUMN target_skill_id VARCHAR(50) AFTER completion_percentage;
ALTER TABLE user_roadmap_milestones ADD COLUMN current_level INT AFTER target_skill_id;
ALTER TABLE user_roadmap_milestones ADD COLUMN required_level INT AFTER current_level;
ALTER TABLE user_roadmap_milestones ADD COLUMN gap_severity VARCHAR(30) AFTER required_level;
ALTER TABLE user_roadmap_milestones ADD COLUMN notes TEXT AFTER gap_severity;
ALTER TABLE user_roadmap_milestones ADD COLUMN completed_at DATETIME AFTER notes;
