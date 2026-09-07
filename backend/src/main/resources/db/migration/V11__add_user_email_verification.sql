-- SkillPilot Migration V11: Add User Email Verification and Date of Birth Schema Refinement
-- V11__add_user_email_verification.sql

-- 1. Add email verification flag to users (defaults to TRUE so all existing accounts remain active)
ALTER TABLE users ADD COLUMN is_verified BOOLEAN NOT NULL DEFAULT TRUE;

-- 2. Safely convert date_of_birth from VARCHAR to DATE
UPDATE users SET date_of_birth = NULL WHERE date_of_birth IS NOT NULL AND date_of_birth = '';
ALTER TABLE users MODIFY COLUMN date_of_birth DATE NULL;

-- 3. Create persistent email verification codes table
CREATE TABLE email_verifications (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    email VARCHAR(150) NOT NULL,
    verification_code VARCHAR(10) NOT NULL,
    expires_at DATETIME NOT NULL,
    attempts_count INT NOT NULL DEFAULT 0,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_ev_user_id ON email_verifications (user_id);
CREATE INDEX idx_ev_email ON email_verifications (email);
CREATE INDEX idx_ev_expires_at ON email_verifications (expires_at);
