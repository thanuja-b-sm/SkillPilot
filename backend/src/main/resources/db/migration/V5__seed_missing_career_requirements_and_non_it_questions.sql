-- SkillPilot Migration V5: Seed Missing Career Skill Requirements, Non-IT Questionnaire Items, and Historical Context Columns
-- V5__seed_missing_career_requirements_and_non_it_questions.sql

-- 1. Schema Additions for Historical Preservation
ALTER TABLE career_match_results ADD COLUMN config_snapshot TEXT AFTER fit_reason;
ALTER TABLE career_match_results ADD COLUMN requirements_snapshot TEXT AFTER config_snapshot;
ALTER TABLE user_roadmaps ADD COLUMN generation_context TEXT AFTER ai_explanation;
UPDATE system_configs SET minimum_match_threshold = 45;

-- 2. Seed Missing Career Skill Requirements for 5 Active Careers
INSERT INTO career_skill_requirements (id, career_id, skill_id, required_level, is_essential) VALUES
('csr-32', 'healthcare-clinical-manager', 'patient-care', 5, TRUE),
('csr-33', 'healthcare-clinical-manager', 'medical-compliance', 4, TRUE),
('csr-34', 'healthcare-clinical-manager', 'communication', 5, TRUE),
('csr-35', 'healthcare-clinical-manager', 'problem-solving', 4, TRUE),

('csr-36', 'financial-investment-analyst', 'financial-modeling', 5, TRUE),
('csr-37', 'financial-investment-analyst', 'corporate-finance', 4, TRUE),
('csr-38', 'financial-investment-analyst', 'data-analytics', 4, TRUE),
('csr-39', 'financial-investment-analyst', 'communication', 4, TRUE),

('csr-40', 'clean-tech-engineer', 'clean-energy', 5, TRUE),
('csr-41', 'clean-tech-engineer', 'cad-engineering', 4, TRUE),
('csr-42', 'clean-tech-engineer', 'problem-solving', 5, TRUE),

('csr-43', 'digital-growth-director', 'digital-marketing', 5, TRUE),
('csr-44', 'digital-growth-director', 'brand-strategy', 4, TRUE),
('csr-45', 'digital-growth-director', 'data-analytics', 4, TRUE),
('csr-46', 'digital-growth-director', 'communication', 5, TRUE),

('csr-47', 'ui-ux-design-lead', 'figma-ui', 5, TRUE),
('csr-48', 'ui-ux-design-lead', 'ux-design', 5, TRUE),
('csr-49', 'ui-ux-design-lead', 'communication', 4, TRUE);

-- 3. Non-IT Questionnaire Options for Question q1
INSERT INTO question_options (id, question_id, option_text, display_order) VALUES
('q1-healthcare', 'q1', 'Healthcare, Clinical Patient Care & Health Systems Management', 6),
('q1-finance', 'q1', 'Corporate Finance, Investment Strategy & Economic Valuation', 7),
('q1-energy', 'q1', 'Renewable Energy, Clean Tech & Infrastructure Engineering', 8),
('q1-marketing', 'q1', 'Digital Growth Marketing, Media Strategy & Brand Building', 9),
('q1-design', 'q1', 'Product UI/UX Design, Creative Experience & Visual Systems', 10);

-- 4. Question Option Skill Mappings for Non-IT Options
INSERT INTO question_skill_mappings (id, option_id, skill_id, weight) VALUES
('qsm-35', 'q1-healthcare', 'patient-care', 5),
('qsm-36', 'q1-healthcare', 'medical-compliance', 4),
('qsm-37', 'q1-healthcare', 'communication', 4),

('qsm-38', 'q1-finance', 'financial-modeling', 5),
('qsm-39', 'q1-finance', 'corporate-finance', 4),
('qsm-40', 'q1-finance', 'data-analytics', 4),

('qsm-41', 'q1-energy', 'clean-energy', 5),
('qsm-42', 'q1-energy', 'cad-engineering', 4),
('qsm-43', 'q1-energy', 'problem-solving', 4),

('qsm-44', 'q1-marketing', 'digital-marketing', 5),
('qsm-45', 'q1-marketing', 'brand-strategy', 4),
('qsm-46', 'q1-marketing', 'data-analytics', 3),

('qsm-47', 'q1-design', 'figma-ui', 5),
('qsm-48', 'q1-design', 'ux-design', 5);
