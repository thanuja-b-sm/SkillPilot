# SkillPilot Documentation Index

Welcome to the documentation repository for **SkillPilot** — AI-Powered Career Intelligence & Roadmap Platform.

This documentation serves as the authoritative technical record of SkillPilot's evolution, architecture, database-driven design, deterministic calculation engines, security posture, audit history, and testing status.

---

## 📁 Repository Structure

```
docs/
├── README.md                           # Master Documentation Index (this file)
├── architecture/                       # System Architecture & Component Design
│   ├── overview.md                     # High-level architecture & tech stack
│   ├── security-and-auth.md            # Authentication, JWT, RBAC & security posture
│   ├── deterministic-engines.md       # Career scoring, skill gap & roadmap generation
│   └── gemini-integration.md           # Decoupled AI explanation layer & fallback rules
├── audits/                             # Audit Reports & Verification Findings
│   ├── session-and-api-lifecycle-audit.md  # Session restoration & cold-start audit
│   └── end-to-end-application-audit.md     # E2E 20-dimension quality & stability audit
├── implementation/                     # Implementation Records & Fix Logs
│   ├── session-lifecycle-hardening.md  # Session restoration & route authority fixes
│   └── registration-bootstrap-hardening.md # Registration atomic session bootstrap
├── testing/                            # Test Coverage & Verification Matrix
│   └── test-suite-and-verification.md  # Automated tests (132 tests), build & verification
├── decisions/                          # Architecture Decision Records (ADRs)
│   ├── adr-001-mysql-authoritative-source.md
│   ├── adr-002-decoupled-gemini-explanation-layer.md
│   ├── adr-003-session-resilience-on-transient-errors.md
│   └── adr-004-historical-snapshot-preservation.md
├── phases/                             # Phase-by-Phase Historical Specifications
└── project/                            # System Requirements & Planning Specs
```

---

## 🚀 Key Architectural Principles

1. **Database as Authoritative Source of Truth:** MySQL (via Spring Data JPA) stores all master data, user profiles, career requirements, assessment answers, match results, and milestone roadmaps. Frontend never calculates or overrides authoritative scores.
2. **Deterministic Calculation Engines:** Career scoring (`CareerScoringEngine`), readiness computation (`SkillGapAnalysisEngine`), and milestone prioritization (`RoadmapGenerationEngine`) run strictly on the backend.
3. **Decoupled AI Explanation Layer:** Google Gemini provides context-aware explanations and summary enhancements. Gemini is strictly read-only and cannot alter business metrics, match scores, readiness percentages, or roadmap phases.
4. **Session Resilience & Route Authority:** Valid JWT sessions are preserved across temporary network or 5xx server errors. The browser URL remains authoritative across page reloads.

---

## 🔗 Quick Links

- [System Architecture Overview](architecture/overview.md)
- [Security & Authentication Architecture](architecture/security-and-auth.md)
- [Deterministic Engines Specification](architecture/deterministic-engines.md)
- [End-to-End Audit Report](audits/end-to-end-application-audit.md)
- [Test Suite & Verification Record](testing/test-suite-and-verification.md)
- [Architectural Decision Records (ADRs)](decisions/adr-001-mysql-authoritative-source.md)
