# Phase 6 — Deterministic Engine Audit Report

**Date:** August 12, 2026  
**Auditor:** SkillPilot Lead Scoring Architect

---

## 1. Calculation Logic & Determinism Verification

### 1.1 Career Scoring Engine (`CareerScoringEngine.java`)
- **Formula:** Combined score is computed deterministically:
  $$ \text{TotalScore} = \text{SkillMatchScore} \times \text{TechWeight} + \text{QuestionnaireScore} \times \text{QuestWeight} $$
- **System Config Injection:** Reads weights (`technicalWeight`, `questionnaireWeight`, `essentialSkillPenalty`, `minimumMatchThreshold`) from `SystemConfig`.
- **Tie-Breaking:** Results ordered by `matchScore` descending, then `careerId` ascending.
- **AI Boundary:** Zero AI participation. Completely mathematical.

### 1.2 Skill Gap Analysis Engine (`SkillGapAnalysisEngine.java`)
- **Readiness Score:** Weighted fulfillment across essential ($2.0\times$) and non-essential ($1.0\times$) requirements.
- **Severity Rating:**
  - Gap $\ge 3$: `CRITICAL`
  - Gap $= 2$: `HIGH`
  - Gap $= 1$: `MEDIUM`
  - Gap $= 0$: `LOW` / `SATISFIED`
- **Missing Skills List:** Formatted deterministically with categorized recommendation actions.

### 1.3 Roadmap Generation Engine (`RoadmapGenerationEngine.java`)
- **Timeline:** 6-Month or 12-Month duration evenly distributed across 4 quarter phases.
- **Prioritization Order:**
  1. Critical / Essential Gaps $\to$ Phase 1 (Foundations & High Severity)
  2. Medium Gaps $\to$ Phase 2 (Intermediate Technical Practice)
  3. Advanced Domain Gaps $\to$ Phase 3 (Specialization & Workflow Integration)
  4. Capstone Portfolio $\to$ Phase 4 (Portfolio & Interview Readiness)

---

## 2. Engine Audit Rating: **EXCELLENT (100% Deterministic)**
