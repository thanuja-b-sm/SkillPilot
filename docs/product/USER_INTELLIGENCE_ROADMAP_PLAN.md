# User Intelligence, Skill Gap & Roadmap Overhaul Architecture

## 1. Overview
This document specifies the architectural enhancements implemented in `feature/user-intelligence-roadmap-overhaul` for SkillPilot:
1. **User Profile Intelligence Expansion**: Extended schema and DTOs to capture comprehensive education, career, professional, preference, and learning commitment details.
2. **Flyway Migration V8**: Schema evolution adding profile columns to `users` and milestone tracking columns to `user_roadmap_milestones`.
3. **Profile Completeness Calculator**: Weighted multi-factor indicator ($0-100\%$) prioritizing high-value career intelligence fields.
4. **Experience-Aware Skill-Gap Engine**: Multi-dimensional readiness scoring (Skill Readiness, Experience Alignment, Education Alignment, Overall Readiness) and experience-buffered gap classifications.
5. **Duration-Aware Roadmap Engine**: Dedicated phase planning strategies for 3-month (Rapid Intensive), 6-month (Standard Acceleration), and 12-month (Comprehensive Mastery) durations.
6. **Milestone Tracking Persistence**: Server-side status, completion percentage, notes, and completion timestamp persistence in MySQL.
7. **Regeneration Safety & Traceability**: Milestone progress preservation across roadmap regenerations and explicit skill gap linkage tags ($L_{curr} \to L_{req}$).
8. **Stale Roadmap Detection**: Automatic timestamp comparison warning when user skills or profile change after roadmap creation.

---

## 2. Database Schema Changes (`V8__expand_user_profile_and_roadmap_tracking.sql`)

### `users` Table Extensions
- `country` (VARCHAR 100)
- `date_of_birth` (VARCHAR 20)
- `institution_name` (VARCHAR 150)
- `degree_level` (VARCHAR 100)
- `major_field_of_study` (VARCHAR 150)
- `graduation_year` (INT)
- `education_status` (VARCHAR 50)
- `employment_status` (VARCHAR 100)
- `current_job_title` (VARCHAR 150)
- `current_industry` (VARCHAR 100)
- `relevant_experience_years` (INT DEFAULT 0)
- `certifications` (TEXT)
- `portfolio_url` (VARCHAR 255)
- `career_interests` (TEXT)
- `preferred_work_mode` (VARCHAR 50)
- `preferred_employment_type` (VARCHAR 50)
- `career_goal` (VARCHAR 255)
- `weekly_hours_available` (INT DEFAULT 10)
- `preferred_learning_pace` (VARCHAR 50 DEFAULT 'Steady')
- `preferred_roadmap_duration` (INT DEFAULT 6)

### `user_roadmap_milestones` Table Extensions
- `completion_percentage` (INT NOT NULL DEFAULT 0)
- `target_skill_id` (VARCHAR 50)
- `current_level` (INT)
- `required_level` (INT)
- `gap_severity` (VARCHAR 30)
- `notes` (TEXT)
- `completed_at` (DATETIME)

---

## 3. Profile Completeness Formula (`CompletionCalculatorService.java`)
Calculated deterministically based on key intelligence fields ($0-100\%$):
- **Target Career Selection** ($20\%$)
- **User Skills Rated** ($\ge 3$ skills) ($20\%$)
- **Education Details** (degreeLevel, institutionName, majorFieldOfStudy) ($15\%$)
- **Experience Details** (employmentStatus, experienceYears, relevantExperienceYears) ($15\%$)
- **Personal Details** (name, location, country) ($15\%$)
- **Learning Preferences** (weeklyHoursAvailable, preferredWorkMode, careerGoal) ($15\%$)

---

## 4. Multi-Dimensional Readiness & Skill Gap Model

### Readiness Dimensions
- **Skill Readiness**: Weighted fulfillment of user skill levels against career requirements.
- **Experience Alignment**: Seniority alignment based on `relevantExperienceYears` ($\ge 5 \to 100\%$, $3-4 \to 85\%$, $1-2 \to 65\%$, $0 \to 40\%$).
- **Education Alignment**: Major domain relevance to target career category ($90\%$ match, $60\%$ baseline).
- **Overall Readiness**: $(0.60 \times SkillReadiness) + (0.25 \times ExperienceAlignment) + (0.15 \times EducationAlignment)$.

### Gap Classifications
- `CRITICAL`: Gap $\ge 3$
- `IMPORTANT`: Gap $= 2$
- `MINOR`: Gap $= 1$ (without experience buffer)
- `EXPERIENCE_SUPPORTED`: Gap $= 1$ with $\ge 3$ years relevant experience
- `SATISFIED`: Gap $= 0$

---

## 5. Roadmap Duration Models & Persistence

| Duration | Strategy | Phase Count | Focus |
|---|---|---|---|
| **3 Months** | Rapid Intensive Quick-Win | 3 | Urgent prerequisites, applied quick wins, capstone portfolio |
| **6 Months** | Standard Acceleration | 4 | Critical foundations, core projects, production depth, portfolio defense |
| **12 Months** | Comprehensive Mastery | 5 | Foundational theory, core applied, advanced specialization, production capstone, executive positioning |

### API Endpoints
- `POST /api/user/roadmaps/generate`: Accepts optional `durationMonths` (3, 6, 12).
- `GET /api/user/roadmaps`: Returns user's active roadmap with `isStale` flag and completion metrics.
- `PUT /api/user/roadmaps/{roadmapId}/milestones/{milestoneId}/progress`: Persists status, completion percentage ($0-100\%$), and notes.

---

## 6. Security & User Isolation
All roadmap operations resolve the authenticated user from Spring Security `SecurityUser`. Supplying cross-user IDs returns HTTP 403 Forbidden.
