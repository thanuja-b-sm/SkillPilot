# SkillPilot — AI-Powered Career Intelligence & Milestone Engine

SkillPilot is a full-stack career intelligence platform that matches students and professionals to the most suitable career paths based on their skill profiles, identifies personalized skill gaps, generates milestone-based roadmaps, and enhances insights using Gemini AI.

---

## Repository Structure

```
skillpilot/
├── frontend/       →  React 19 + Vite + TypeScript SPA with Express AI proxy
├── backend/        →  Spring Boot 3.2 REST API (Java 17)
├── docs/           →  Architecture, phase reports, and project documentation
├── README.md       →  This file
└── .gitignore
```

---

## Prerequisites

| Requirement | Version |
| :--- | :--- |
| Java Development Kit (JDK) | **17** (LTS) — required for Spring Boot |
| Apache Maven | **3.8.x+** |
| Node.js | **18.x or 20.x LTS** |
| npm | **9.x+** (bundled with Node.js) |
| MySQL Server | **8.0+** (running on localhost:3306) |

---

## Getting Started

### 1. Configure Environment Variables

**Frontend** — create `frontend/.env` from the example:
```bash
cp frontend/.env.example frontend/.env
# Edit frontend/.env and add your GEMINI_API_KEY
```

**Backend** — create `backend/.env` from the example (or set as system env vars):
```bash
cp backend/.env.example backend/.env
# Edit backend/.env and fill in DB_PASSWORD, JWT_SECRET, GEMINI_API_KEY
```

---

### 2. Start the Backend (Spring Boot — Port 8080)

**Windows (PowerShell):**
```powershell
cd backend
.\start-dev.ps1
```

**Linux / macOS:**
```bash
cd backend
set -a && source .env && set +a
./mvnw spring-boot:run
```

> `start-dev.ps1` automatically reads `backend/.env` and sets all required environment variables before starting Spring Boot.

> On first startup, Flyway automatically runs database migrations (V1–V4) to create all tables and seed master data into the `skillpilot` MySQL database.

To build and run a production JAR:
```bash
cd backend
.\mvnw clean package -DskipTests
java -jar target/skillpilot-backend-0.0.1-SNAPSHOT.jar
```

---

### 3. Start the Frontend (React + Express — Port 3000)

```bash
cd frontend
npm install
npm run dev
```

The frontend is served at **http://localhost:3000**.

> The Express server (`server.ts`) acts as:
> - A Vite development middleware host for the React SPA
> - A direct handler for AI enhancement endpoints (`/api/ai/enhance-summary`)
> - A reverse proxy forwarding all other `/api/*` calls to the Spring Boot backend on port 8080

---

## Architecture

```
Browser  →  http://localhost:3000
              │
              ▼
        Node.js / Express (server.ts)
              │
              ├── /api/ai/*  →  Gemini AI (direct)
              │
              └── /api/*     →  Spring Boot (http://localhost:8080)
                                      │
                                      └── MySQL (localhost:3306/skillpilot)
```

---

## Running Tests

**Backend unit & integration tests:**
```powershell
# Windows — env vars are loaded by the wrapper script
cd backend
.\mvnw test
```

**Frontend TypeScript check:**
```bash
cd frontend
npx tsc --noEmit
```

**Frontend production build:**
```bash
cd frontend
npm run build
```

---

## Documentation

All project documentation is in the [`docs/`](./docs/) directory:

- [`docs/phases/`](./docs/phases/) — Phase implementation reports (Phase 1–11)
- [`docs/project/`](./docs/project/) — System requirements and setup guide

---

## Security Notes

- **Never commit `.env` files** — they are git-ignored.
- The `backend/src/main/resources/application.yml` uses environment variables only; no secret values are hardcoded.
- The **Gemini API key** and **JWT secret** that were previously hardcoded as fallbacks have been removed. **You should rotate those credentials.**
- Frontend must NOT contain the backend JWT secret or database password.
