# Phase 5 — Database & Data Integrity Audit Report

**Date:** August 12, 2026  
**Auditor:** SkillPilot Lead Database Engineer

---

## 1. Relational Schema & Migration Strategy

SkillPilot uses **Flyway** schema migrations against **MySQL 8.0** (and H2 in-memory for unit testing).

- `V1__initial_schema.sql` — Establishes core relational entities, primary keys, foreign keys, and indexes.
- `V2__seed_master_data.sql` — Seeds initial active careers, skills, requirements, questions, and options.
- `V3__add_scoring_version.sql` — Adds `scoring_version` column to `career_match_results` for snapshot auditing.
- `V4__add_updated_at_career_match_results.sql` — Adds timestamp tracking column.
- `V5__seed_missing_career_requirements_and_non_it_questions.sql` — Seeds extended non-IT career requirements.

---

## 2. Entity Relationship & Integrity Map

```
                  ┌──────────────┐
                  │    User      │
                  └──────┬───────┘
                         │ 1:N
       ┌─────────────────┼─────────────────┬─────────────────┐
       ▼                 ▼                 ▼                 ▼
[ UserSkill ]   [ UserQuestionAns ] [ UserTargetCareer ] [ CareerMatchResult ]
       │                 │                 │                 │
       ▼ N:1             ▼ N:1             ▼ N:1             ▼ N:1
   [ Skill ]        [ Question ]       [ Career ] ◄──────────┘
       ▲                 ▲                 ▲
       │                 │                 │
       │ N:1             │ 1:N             │ 1:N
[ QSkillMapping ] ◄─ [ QuestionOption ]  [ CareerRequirement ]
```

---

## 3. Database Integrity & Snapshot Preservation Findings

1. **Foreign Key & Unique Constraints:** Verified. All join tables enforce composite unique constraints (`uq_user_skill`, `uq_user_question`, `uq_user_career_match`, `uq_career_skill`) preventing duplicate mapping entries.
2. **Cascades & Soft-Deletes:** Master entities (`Career`, `Skill`, `Question`) utilize `isActive` flags. Soft-deactivating master data preserves historical snapshot integrity in `CareerMatchResult` (`configSnapshot`, `requirementsSnapshot`) and existing `Roadmap` records.
3. **Orphan Record Prevention:** JPA entity definitions enforce `orphanRemoval = true` on `Roadmap.phases` and `@OnDelete(action = OnDeleteAction.CASCADE)` on child mapping entities.
4. **Transaction Safety:** Spring Service methods performing database writes are annotated with `@Transactional`. Partial writes are rolled back automatically on runtime exceptions.
