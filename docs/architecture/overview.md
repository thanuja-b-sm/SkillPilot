# System Architecture Overview

SkillPilot is structured as a decoupled, multi-layer web application designed for career discovery, skill-gap analysis, and automated milestone roadmap generation.

---

## 🏗️ Technology Stack

| Layer | Technology | Role |
|---|---|---|
| **Frontend UI** | React 18 + TypeScript + Vite | Client SPA, state management (`AppContext`), responsive UI |
| **Backend REST API** | Spring Boot 3.2.4 + Java 17/20 | REST API controllers, business logic, security & deterministic calculation engines |
| **Persistence** | MySQL 8.0 + Spring Data JPA + Flyway | Authoritative relational store, Flyway schema migrations |
| **Security** | Spring Security + JJWT (0.12.5) + BCrypt | Stateless JWT authentication, RBAC authorization, password hashing |
| **AI Layer** | Google Gemini API (via JDK HttpClient) | Natural language explanation & summary enhancement |

---

## 📐 System Components & Data Flow

```
[ Browser React SPA ]
         │
         ▼  HTTP / REST (JWT Bearer)
[ Spring Boot Controller Layer ]
         │
         ├──► [ Spring Security & JWT Filter ]
         │
         ├──► [ Deterministic Engines ]
         │       ├── CareerScoringEngine
         │       ├── SkillGapAnalysisEngine
         │       └── RoadmapGenerationEngine
         │
         ├──► [ MySQL Relational Database ]
         │       (Users, Skills, Careers, Requirements, Answers, Matches, Roadmaps)
         │
         └──► [ Gemini Explanation Service ]
                 (Read-only text explanation enhancement)
```

---

## 🔒 System Boundaries & Guarantees

1. **Client Layer:** Renders state from backend APIs. Displays loading, error, empty, and data states transparently.
2. **Backend Deterministic Layer:** Executes exact mathematical scoring, ranking, skill-gap analysis, and milestone phase ordering.
3. **Database Layer:** Authoritative source of truth for all system metrics, user skills, questionnaire mappings, and historical snapshots.
4. **AI Layer:** Read-only explanation provider. Cannot alter scores, severity, readiness, or roadmap milestones.
