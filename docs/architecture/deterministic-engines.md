# Deterministic Calculation Engines

SkillPilot uses three backend deterministic engines to calculate career compatibility, skill gaps, and milestone roadmaps.

---

## 1. Career Scoring Engine (`CareerScoringEngine`)

### Calculation Logic:
1. **Skill Match Score (0–100):**
   - Evaluates user skill levels against career requirements (`CareerSkillRequirement`).
   - Essential skills weighted higher than non-essential skills.
2. **Questionnaire Match Score (0–100):**
   - Evaluates user assessment answers (`UserQuestionAnswer`) against option skill mappings (`QuestionSkillMapping`).
3. **Combined Match Score:**
   - Weighted combination based on configurable system parameters (`SystemConfig`: default 60% skill score + 40% questionnaire score).
4. **Deterministic Ranking:**
   - Ranked by `matchScore` descending. Ties broken deterministically by `careerId` ascending.

---

## 2. Skill Gap Analysis Engine (`SkillGapAnalysisEngine`)

### Calculation Logic:
1. **Requirement Comparison:**
   - Compares user's current level (0–5) against career required level (1–5) for target career.
2. **Readiness Score:**
   - Percentage of required skill levels satisfied across essential and non-essential requirements.
3. **Severity Rating:**
   - Categorizes gaps into `HIGH` (essential skill gap >= 2 levels), `MEDIUM` (gap = 1 level), `LOW` (minor gap), or `SATISFIED`.

---

## 3. Roadmap Generation Engine (`RoadmapGenerationEngine`)

### Calculation Logic:
1. **Duration Allocation:** 6-month or 12-month timeline.
2. **Phase Order & Prioritization:**
   - Phase 1: Foundational & Essential High-Severity Gaps.
   - Phase 2: Intermediate Technical Requirements.
   - Phase 3: Advanced Domain & Capstone Outcomes.
3. **Course & Goal Mapping:** Maps recommended courses and actionable goals deterministically to skill categories.
