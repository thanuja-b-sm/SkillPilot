# ADR-002: Decoupled Gemini AI Explanation Layer

## Status
Accepted

## Context
SkillPilot incorporates Google Gemini AI to provide natural language explanations for career matches, skill gaps, and roadmaps.

## Decision
Google Gemini is strictly constrained to an **explanation and readability layer**.

Gemini receives read-only context (career titles, scores, gap titles) and generates summary text. Gemini **CANNOT** alter business metrics, match scores, readiness percentages, gap severity, or roadmap milestone phase order.

If Gemini API is unconfigured or unavailable, a deterministic `FallbackExplanationService` delivers system-calculated text without throwing runtime errors.

## Consequences
- **Positive:** Protects application determinism and prevents AI hallucinations from altering user career scoring.
- **Positive:** System functions 100% reliably even when AI quotas are exceeded or API key is absent.
- **Negative:** AI explanations are summaries rather than dynamic score generators.
