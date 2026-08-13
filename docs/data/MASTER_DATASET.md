# SkillPilot — Real Database Master Dataset Documentation

**Database Engine:** MySQL 8.0 (Relational Master Store) / H2 (In-Memory Integration Test Engine)  
**Flyway Migration:** `V6__expand_master_dataset.sql`  
**Authoritative Source of Truth:** MySQL Database (Managed via JPA / Flyway)  
**Baseline Date:** August 13, 2026  
**Status:** **ACTIVE / VERIFIED**

---

## 📌 1. Overview & Objectives

To support SkillPilot's deterministic career intelligence engines (Career Match, Skill Gap, Readiness Score, Roadmap Generation), a large, realistic, internally consistent master dataset was populated into the production MySQL database schema via standard Flyway migrations (`V1` through `V6`).

### Core Engineering Principles
1. **Zero Frontend Mocks:** All career titles, descriptions, salary ranges, growth rates, skill dictionaries, career requirements, questionnaires, options, and roadmap templates are served dynamically from MySQL database queries via REST APIs.
2. **Schema Integrity:** Standardized foreign key constraints (`ON DELETE CASCADE` / `ON DELETE RESTRICT`), unique indexes (`career_id + skill_id`, `option_id + skill_id`), and JPA annotations.
3. **Preservation of Baseline Data:** All historical Flyway seed data (`V2__seed_master_data.sql`) was strictly preserved and expanded upon.

---

## 📊 2. Master Dataset Inventory & Statistics

| Entity / Relational Table | Baseline (`V2`) | Expanded (`V6`) | Total Active Inventory | Verification Status |
|---|---|---|---|---|
| **Careers** (`careers`) | 12 | +25 | **37 Total (36 Active)** | **PASS** |
| **Skills Dictionary** (`skills`) | 29 | +124 | **153 Total (152 Active)** | **PASS** |
| **Career Skill Requirements** (`career_skill_requirements`) | 45 | +132 | **177 Requirements** | **PASS** |
| **Questionnaire Questions** (`questions`) | 10 | +8 | **18 Active Questions** | **PASS** |
| **Question Options** (`question_options`) | 32 | +18 | **50 Options** | **PASS** |
| **Question-Skill Mappings** (`question_skill_mappings`) | 40 | +36 | **76 Mappings** | **PASS** |
| **Roadmap Templates** (`roadmap_templates`) | 3 | +4 | **7 Templates (38 Phase Templates)** | **PASS** |

---

## 🌐 3. Domain Coverage & Career Tracks

The active careers span **12 distinct industry domains**:

1. **Software Engineering & Architecture:**
   - Backend Systems Engineer (`backend-systems-engineer`)
   - Frontend Architect (`frontend-architect`)
   - Mobile Application Developer (`mobile-app-developer`)
   - Embedded Systems Engineer (`embedded-systems-engineer`)
   - QA Automation Engineer (`qa-automation-engineer`)
2. **Infrastructure, DevOps & Cloud:**
   - Site Reliability Engineer (`site-reliability-engineer`)
   - DevOps & Platform Engineer (`devops-platform-engineer`)
   - Cloud Solutions Architect (`cloud-architect`)
   - Network & Security Engineer (`network-security-engineer`)
   - Database Administrator (`database-administrator`)
3. **Data Engineering, Analytics & AI:**
   - Data Engineer (`data-engineer`)
   - AI & Prompt LLM Engineer (`ai-prompt-llm-engineer`)
   - AI & Machine Learning Engineer (`ai-ml-engineer`)
   - Data Scientist (`data-scientist`)
   - Business Intelligence Manager (`bi-analytics-manager`)
4. **Cybersecurity & Red Teaming:**
   - Penetration Tester / Red Teamer (`penetration-tester-red-team`)
   - Security & Compliance Auditor (`security-compliance-auditor`)
   - Cybersecurity Specialist (`cybersecurity-specialist`)
5. **Agile, Management & Technical Leadership:**
   - Technical Program Manager (`technical-program-manager`)
   - Scrum Master & Agile Coach (`scrum-master-agile-coach`)
   - Product Manager (`product-manager`)
6. **Finance & Banking:**
   - Investment Banker (M&A) (`investment-banker-m-and-a`)
   - Accounting & Audit Manager (`accounting-audit-manager`)
   - Financial Analyst (`financial-analyst`)
7. **Business & Consulting:**
   - Management Consultant (`management-consultant`)
8. **Healthcare & Medicine:**
   - Health Informatics Specialist (`health-informatics-specialist`)
9. **Mechanical & Civil Engineering:**
   - Mechanical Design Engineer (`mechanical-design-engineer`)
10. **Electrical & Energy Systems:**
    - Electrical Power Engineer (`electrical-power-engineer`)
11. **Marketing & Growth:**
    - Content & Brand Strategy Lead (`content-strategy-lead`)
12. **HR & Supply Chain:**
    - HR & Talent Acquisition Lead (`hr-talent-acquisition-lead`)
    - Supply Chain & Logistics Director (`supply-chain-logistics-director`)

---

## 🛠️ 4. Skills Categorization Breakdown

The 152 active skills are categorized into 6 structured domains:
1. **Technical:** Core programming languages (`Java`, `C#`, `Rust`, `Elixir`, `Python`, `Go`), backend/frontend frameworks (`Spring Boot`, `React`, `ASP.NET Core`, `Phoenix`), API protocols (`GraphQL`, `gRPC`, `REST`), systems architectures (`Microservices`, `Vector DBs`, `RAG Pipelines`).
2. **Tools & Frameworks:** Containerization (`Docker`, `Kubernetes`), Infrastructure as Code (`Terraform`, `Ansible`), Testing tools (`Jest`, `Cypress`, `Playwright`, `JUnit`), Observability (`Prometheus`, `Grafana`), Message brokers (`Kafka`, `RabbitMQ`, `Redis`).
3. **Domain Knowledge:** Financial valuation (`DCF`, `LBO`, `Options Pricing`), Medical & GCP regulations (`HIPAA`, `FDA 510k`, `GCP`), Audit standards (`ISO 27001`, `SOC 2`), Agile frameworks (`SAFe`, `Scrum`).
4. **Analytical:** Data manipulation (`SQL`, `Pandas`, `Spark`), statistical modeling, risk evaluation (`VaR`, `Sharpe Ratio`), Six Sigma quality control (`DMAIC`, `SPC`).
5. **Leadership:** Team building, executive stakeholder management, organizational change management (`Kotter Model`), strategic execution.
6. **Soft Skills:** Technical communication, conflict resolution, negotiation, public relations, conversion copywriting.

---

## 🔍 5. Data Quality & Diagnostic Validation Rules

Every career in the master dataset must satisfy the following automated quality checks (enforced by `SystemConfigService` and `Phase12MasterDatasetExpansionTest`):

1. **Requirement Sufficiency:** Every active career must have at least 3 skill requirements defined.
2. **Essential Skill Rule:** Every active career must have at least 1 skill flagged as `is_essential = TRUE`.
3. **Valid Required Levels:** All required skill levels must be integers between 1 and 5.
4. **Question Mappings:** Every active questionnaire option maps to valid skills with weights between 1 and 5.
5. **System Health Score:** Checked via `/api/admin/system-health` — current database achieves **100% Health Score**.

---

## 🧪 6. Intelligence Stress Test Scenarios (`Phase12MasterDatasetExpansionTest.java`)

The expanded dataset was validated against 4 stress testing scenarios in JUnit 5:

1. **Master Dataset Inventory Validation:**
   - Verifies record counts across careers (36 active), skills (152 active), requirements (177), questions (18 active), options (50), and mappings (76).
   - Verifies 100% compliance with essential requirement quality rules across all careers.
2. **7-Domain End-to-End Intelligence Flow:**
   - Iterates through 7 contrasting career domains (`backend-systems-engineer`, `devops-platform-engineer`, `ai-prompt-llm-engineer`, `penetration-tester-red-team`, `investment-banker-m-and-a`, `ui-ux-design-lead`, `health-informatics-specialist`).
   - Verifies target career selection, skill gap computation, readiness score calculation, and 6-month roadmap generation for each domain.
3. **High-Skill vs Low-Skill User Readiness Dynamics:**
   - Evaluates a student with zero skills (Readiness Score: $\le 30\%$).
   - Evaluates the same student after adding level 5 proficiency in all target requirements (Readiness Score: $\ge 80\%$).
4. **Questionnaire Submission Impact on Career Matching:**
   - Submits structured answer items covering active questions.
   - Verifies deterministic recalculation and persistence of career match results.

---

## 🚀 7. Verification Summary

```bash
# Frontend Type Check
cd frontend && npx tsc --noEmit (0 Errors)

# Frontend Production Build
cd frontend && npm run build (SUCCESS - Vite SPA built in 2.39s)

# Backend Integration Test Suite
cd backend && .\mvnw.cmd test (143/143 PASSED)
```

**Git Feature Branch:** `feature/real-master-dataset` (Pushed to origin; NOT merged into `main`).
