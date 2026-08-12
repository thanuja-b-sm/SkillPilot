# Phase 7 — Questionnaire & Career Relevance Audit Report

**Date:** August 12, 2026  
**Auditor:** SkillPilot Assessment Architect

---

## 1. Relational Mappings & Scoring Path

```
[ Question ] ──1:N──► [ QuestionOption ] ──1:N──► [ QuestionSkillMapping ] ──N:1──► [ Skill ]
      │                        ▲                                                      │
      │                        │ selected_option_id                                   │ required_level
      ▼                        │                                                      ▼
[ UserQuestionAnswer ] ────────┘                                           [ CareerRequirement ]
```

---

## 2. Findings & Relevance Guarantees

1. **Relevance Filter:** Questionnaire option points only affect a career's score if the mapped skill is listed in that career's `CareerSkillRequirement` table. Unrelated skills do not inflate scores for irrelevant careers.
2. **Soft Deactivation Boundary:** Inactive questions (`isActive = false`) and options are filtered out during GET `/api/questionnaire`. Existing `UserQuestionAnswer` records persist historical evaluation consistency.
3. **Weight Validation:** Admin mutation endpoints validate option skill mapping weights ($0.1 \le \text{weight} \le 5.0$). Duplicate mappings are blocked via `uq_question_skill_mapping (question_option_id, skill_id)`.

---

## 3. Questionnaire Audit Rating: **EXCELLENT**
