# Gemini AI Integration & Fallback Rules

SkillPilot integrates Google Gemini API strictly as an **explanation and readability enhancement layer**.

---

## 🛡️ AI Boundary & Constraints

1. **Read-Only Context:** Gemini receives only sanitized, non-sensitive prompt context (career titles, match scores, readiness percentages, milestone names).
2. **No Data Leakage:** Gemini never receives passwords, hashes, JWT tokens, API keys, or personal identifiable information.
3. **No Calculation Authority:** Gemini **CANNOT** calculate or alter match scores, readiness percentages, gap severity, phase ordering, or admin configurations.
4. **Deterministic Fallback:** If Gemini API is unconfigured, disabled, times out (>15s), or fails due to network/quota issues, `FallbackExplanationService` returns system-calculated summaries seamlessly without throwing errors.

---

## 📡 AI Service Endpoints

- POST `/api/ai/enhance-summary` -> Enhances milestone roadmap summary.
- POST `/api/ai/explain-career` -> Generates career compatibility explanation.
- POST `/api/ai/explain-skill-gap` -> Generates skill-gap analysis explanation.
- POST `/api/ai/explain-roadmap` -> Generates custom roadmap explanation.
