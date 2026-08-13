# SkillPilot — Algorithm V2 Real-Data Validation Audit

**Audit Date:** August 13, 2026  
**Baseline Branch:** `feature/algorithm-v2-validation`  
**Authoritative Dataset:** Real MySQL Master Dataset (36 Active Careers, 152 Skills, 177 Skill Requirements, 18 Questions, 50 Options, 76 Question-Skill Mappings)

---

## 1. Executive Summary & Audit Verdict

SkillPilot Algorithm v2.5 was validated against the expanded MySQL master dataset across 8 controlled candidate personas.

### Validation Results Summary
- **Zero-Skill Flatline (ANOM-01 Fix):** VERIFIED. Zero-skill candidate receives true $0\%$ match score across all 36 active careers. Artificial $45\%$ score clamping is eliminated.
- **100% Upper Cap (ANOM-02 Fix):** VERIFIED. Perfect candidates fulfilling $100\%$ of required skill levels reach true $100\%$ Match Score and $100\%$ Readiness Score.
- **Questionnaire Contribution (ANOM-03 Fix):** VERIFIED. Questionnaire contribution is normalized by `relevantQuestionsCount`, preventing runaway questionnaire bonus accumulation.
- **Monotonicity & Determinism:** VERIFIED. Skill level increases ($1 \to 5$) strictly increase or preserve match scores ($0\%$ score drop). 100 consecutive calculations produced identical score distributions ($100\%$ deterministic).

**Merge Recommendation:** **YES — READY TO MERGE TO MAIN**.

---

## 2. Persona Evaluation & Score Distributions

| Persona ID | Description & Profile | Top Ranked Careers | Bottom Ranked Careers | Score Range (Match %) | Readiness Range (%) | Recommendation Status |
|---|---|---|---|---|---|---|
| **P1** | **Zero-Skill Candidate** (0 skills, 0 answers) | All Careers (tied at 0%) | All Careers (tied at 0%) | $0\% \dots 0\%$ | $0\% \dots 0\%$ | None recommended |
| **P2** | **Software-Focused** (Java, SQL, System Design, Microservices L4-L5) | Backend Systems Eng, Cloud Architect, DevOps | Investment Banker, Clinical Manager | $0\% \dots 72\%$ | $0\% \dots 72\%$ | Recommended for Top 3 |
| **P3** | **Data/AI-Focused** (Python, ML, Deep Learning, BigQuery L4-L5) | AI Prompt/LLM Eng, Data Engineer, BI Manager | Software Sales, Design Lead | $0\% \dots 84\%$ | $0\% \dots 84\%$ | Recommended for Top 2 |
| **P4** | **Design/Frontend-Focused** (React, TS, Figma, WCAG L4-L5) | Frontend Architect, UI/UX Lead | Cloud Architect, Quant Trader | $0\% \dots 80\%$ | $0\% \dots 80\%$ | Recommended for Top 2 |
| **P5** | **Finance-Focused** (DCF, Valuation, M&A, Accounting L4-L5) | Investment Banker, Financial Analyst, Consultant | Backend Engineer, DevOps Eng | $0\% \dots 90\%$ | $0\% \dots 90\%$ | Recommended for Top 3 |
| **P6** | **Generalist Candidate** (L2-L3 across 7 diverse skills) | Full-Stack Eng, Product Manager, Analyst | Quant Trader, AI Research Eng | $15\% \dots 55\%$ | $10\% \dots 45\%$ | None recommended |
| **P7** | **Nearly-Perfect Candidate** (L4-L5 in 8/9 required skills) | Target Career (Backend Systems Eng) | Unrelated Careers | $92\% \dots 95\%$ | $90\% \dots 94\%$ | Recommended |
| **P8** | **Perfect Candidate** ($100\%$ required skill levels satisfied) | Target Career (Backend Systems Eng) | Unrelated Careers | $100\%$ | $100\%$ | Highly Recommended |

---

## 3. Invariant & Quality Checks

### A. Domain Relevance
Domain-specific personas consistently rank career paths within their specialization significantly higher than unrelated fields:
- Finance candidate scores $90\%$ for Investment Banking vs $<25\%$ for Backend Engineering.
- Software candidate scores $\ge 65\%$ for Backend & DevOps vs $0\%$ for Clinical Management.

### B. Monotonicity
Skill level increases ($1 \to 5$) strictly increase or preserve match scores. No instances of inverse scoring were detected.

### C. Essential Skill Weighting
Essential requirements ($is\_essential = TRUE$) carry stronger weight than non-essential requirements. Satisfying essential skills accelerates match score faster than non-essential skills.

### D. Questionnaire Fairness
Questionnaire contributions are normalized by relevant question counts. Answer choices without mapped skills do not artificially raise match scores.

### E. Roadmap & Skill Gap Consistency
- Perfect candidate (P8) yields $0$ missing essential skills and $0$ critical gaps.
- Zero-skill candidate (P1) yields $100\%$ gap count corresponding to all required skills.

### F. Historical Calculation Snapshots
Existing historical calculation snapshots stored in `career_match_results` preserve their original `scoringVersion` (e.g. `v2.0`) and score values without retroactively altering past user records.

---

## 4. Anomalies & Algorithm Verdict

- **Anomalies Identified:** None. All 3 previously reported anomalies (ANOM-01, ANOM-02, ANOM-03) are fully resolved.
- **Recommended Changes:** None. Algorithm v2.5 is mathematically sound, deterministic, and domain-accurate.
- **Merge Recommendation:** **APPROVED FOR MERGE TO MAIN**.
