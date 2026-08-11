-- SkillPilot Initial Relational Schema Migration (MySQL 8.0)
-- V1__initial_schema.sql

-- 1. SYSTEM CONFIGURATION
CREATE TABLE system_configs (
    id VARCHAR(36) NOT NULL,
    technical_weight DECIMAL(4,3) NOT NULL DEFAULT 0.500,
    questionnaire_weight DECIMAL(4,3) NOT NULL DEFAULT 0.350,
    essential_skill_penalty DECIMAL(4,3) NOT NULL DEFAULT 0.150,
    minimum_match_threshold INT NOT NULL DEFAULT 40,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- 2. USERS TABLE
CREATE TABLE users (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'student',
    title VARCHAR(150) DEFAULT 'Student Profile',
    education VARCHAR(200) DEFAULT '',
    experience_years INT NOT NULL DEFAULT 0,
    location VARCHAR(100) DEFAULT '',
    target_focus VARCHAR(150) DEFAULT '',
    bio TEXT,
    completion_percentage INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- 3. SKILLS MASTER DICTIONARY
CREATE TABLE skills (
    id VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- 4. USER SKILLS SELF-ASSESSMENT
CREATE TABLE user_skills (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    skill_id VARCHAR(64) NOT NULL,
    level INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_skills_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_skill UNIQUE (user_id, skill_id)
);

-- 5. CAREER TRACKS
CREATE TABLE careers (
    id VARCHAR(64) NOT NULL,
    title VARCHAR(150) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    average_salary VARCHAR(100) NOT NULL,
    growth_rate VARCHAR(100) NOT NULL,
    demand_level VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- 5A. CAREER TYPICAL ROLES
CREATE TABLE career_typical_roles (
    career_id VARCHAR(64) NOT NULL,
    role_name VARCHAR(150) NOT NULL,
    PRIMARY KEY (career_id, role_name),
    CONSTRAINT fk_roles_career FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE
);

-- 5B. CAREER PREREQUISITES
CREATE TABLE career_prerequisites (
    career_id VARCHAR(64) NOT NULL,
    prerequisite VARCHAR(200) NOT NULL,
    PRIMARY KEY (career_id, prerequisite),
    CONSTRAINT fk_prereq_career FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE
);

-- 6. CAREER SKILL REQUIREMENTS
CREATE TABLE career_skill_requirements (
    id VARCHAR(36) NOT NULL,
    career_id VARCHAR(64) NOT NULL,
    skill_id VARCHAR(64) NOT NULL,
    required_level INT NOT NULL DEFAULT 1,
    is_essential BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_csr_career FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE,
    CONSTRAINT fk_csr_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_career_skill UNIQUE (career_id, skill_id)
);

-- 7. QUESTIONNAIRE ITEMS
CREATE TABLE questions (
    id VARCHAR(64) NOT NULL,
    section VARCHAR(150) NOT NULL,
    question TEXT NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- 8. QUESTION OPTIONS
CREATE TABLE question_options (
    id VARCHAR(64) NOT NULL,
    question_id VARCHAR(64) NOT NULL,
    option_text TEXT NOT NULL,
    display_order INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_options_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- 9. QUESTION OPTION SKILL MAPPINGS
CREATE TABLE question_skill_mappings (
    id VARCHAR(36) NOT NULL,
    option_id VARCHAR(64) NOT NULL,
    skill_id VARCHAR(64) NOT NULL,
    weight INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT fk_qsm_option FOREIGN KEY (option_id) REFERENCES question_options(id) ON DELETE CASCADE,
    CONSTRAINT fk_qsm_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    CONSTRAINT uq_option_skill UNIQUE (option_id, skill_id)
);

-- 10. USER QUESTIONNAIRE ANSWERS / RESPONSES
CREATE TABLE user_question_answers (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    question_id VARCHAR(64) NOT NULL,
    selected_option_ids JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_uqa_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_uqa_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_question UNIQUE (user_id, question_id)
);

-- 11. USER TARGET CAREER SELECTION
CREATE TABLE user_target_careers (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    career_id VARCHAR(64) NOT NULL,
    selected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_utc_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_utc_career FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE
);

-- 12. GENERATED CAREER MATCH RESULTS PER USER
CREATE TABLE career_match_results (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    career_id VARCHAR(64) NOT NULL,
    match_score INT NOT NULL,
    rank_position INT NOT NULL DEFAULT 1,
    confidence_level VARCHAR(20) NOT NULL,
    key_strengths JSON,
    key_gaps JSON,
    fit_reason TEXT,
    system_calculated_badge VARCHAR(100) DEFAULT 'Deterministic Algorithm v2.4',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_cmr_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmr_career FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_career_match UNIQUE (user_id, career_id)
);

-- 13. GENERATED ROADMAP PER USER AND TARGET CAREER
CREATE TABLE user_roadmaps (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    career_id VARCHAR(64) NOT NULL,
    overall_timeline VARCHAR(100) NOT NULL,
    overall_readiness INT NOT NULL DEFAULT 0,
    ai_explanation TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_career FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_career_roadmap UNIQUE (user_id, career_id)
);

-- 14. GENERATED ROADMAP MILESTONES PER USER ROADMAP
CREATE TABLE user_roadmap_milestones (
    id VARCHAR(36) NOT NULL,
    roadmap_id VARCHAR(36) NOT NULL,
    phase_order INT NOT NULL DEFAULT 1,
    month_range VARCHAR(50) NOT NULL,
    phase_title VARCHAR(150) NOT NULL,
    focus_area VARCHAR(200) NOT NULL,
    expected_outcome TEXT NOT NULL,
    goals JSON NOT NULL,
    recommended_courses JSON,
    status VARCHAR(20) NOT NULL DEFAULT 'not_started',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_urm_roadmap FOREIGN KEY (roadmap_id) REFERENCES user_roadmaps(id) ON DELETE CASCADE
);

-- 15. ROADMAP TEMPLATES
CREATE TABLE roadmap_templates (
    id VARCHAR(64) NOT NULL,
    career_id VARCHAR(64) NOT NULL UNIQUE,
    overall_timeline VARCHAR(100) NOT NULL,
    default_explanation TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_rt_career FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE CASCADE
);

-- 16. ROADMAP PHASE TEMPLATES
CREATE TABLE roadmap_phase_templates (
    id VARCHAR(64) NOT NULL,
    roadmap_template_id VARCHAR(64) NOT NULL,
    phase_order INT NOT NULL DEFAULT 1,
    month_range VARCHAR(50) NOT NULL,
    phase_title VARCHAR(150) NOT NULL,
    focus_area VARCHAR(200) NOT NULL,
    expected_outcome TEXT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_rpt_template FOREIGN KEY (roadmap_template_id) REFERENCES roadmap_templates(id) ON DELETE CASCADE
);

-- 16A. ROADMAP PHASE GOALS
CREATE TABLE roadmap_phase_goals (
    phase_id VARCHAR(64) NOT NULL,
    goal_text TEXT NOT NULL,
    goal_order INT NOT NULL DEFAULT 0,
    PRIMARY KEY (phase_id, goal_order),
    CONSTRAINT fk_rpg_phase FOREIGN KEY (phase_id) REFERENCES roadmap_phase_templates(id) ON DELETE CASCADE
);

-- 17. AI GENERATION LOG
CREATE TABLE ai_generation_logs (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36),
    career_id VARCHAR(64),
    prompt_text TEXT NOT NULL,
    response_text TEXT,
    status VARCHAR(20) NOT NULL,
    source VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_ai_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
