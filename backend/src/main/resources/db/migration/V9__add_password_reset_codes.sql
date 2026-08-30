-- SkillPilot Migration V9: Add Password Reset Verification Code Persistence
-- V9__add_password_reset_codes.sql

CREATE TABLE password_reset_codes (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NULL,
    email VARCHAR(150) NOT NULL,
    reset_code VARCHAR(10) NOT NULL,
    expires_at DATETIME NOT NULL,
    attempts_count INT NOT NULL DEFAULT 0,
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_prc_user_id (user_id),
    INDEX idx_prc_email (email),
    INDEX idx_prc_expires_at (expires_at)
);

