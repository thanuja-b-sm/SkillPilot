# SkillPilot — System Requirements & Execution Guide

This document details the software requirements, environment configurations, and exact terminal commands needed to set up, build, and run the **SkillPilot Academic Career Intelligence & Milestone Engine** application.

---

## 1. System Requirements & Prerequisites

To run both the Frontend (React + Vite + Express proxy) and Backend (Spring Boot + MySQL) microservices, your development or host system must have the following software installed:

| Component | Required Version | Purpose / Notes |
| :--- | :--- | :--- |
| **Java Development Kit (JDK)** | **JDK 17** (LTS minimum) | Required for Spring Boot 3.2.4 backend. (e.g., OpenJDK 17, Eclipse Temurin 17, Oracle JDK 17). |
| **Build Tool (Java)** | **Apache Maven 3.8.x+** | Dependency management and build tool for the Spring Boot application. |
| **Node.js Runtime** | **Node.js v18.x or v20.x+** | JavaScript runtime required for the Vite React frontend and Express proxy server (`server.ts`). |
| **Node Package Manager** | **npm v9.x+** | Comes bundled with Node.js. |
| **Database Management System** | **MySQL Server 8.0+** (or MariaDB 10.5+) | Relational database holding user accounts, career roadmaps, assessment logs, and master skills data. |
| **Operating System** | Windows 10/11, macOS, or Linux | Cross-platform compatibility. |

---

## 2. System Architecture & Port Mapping

SkillPilot operates on a two-tier application structure during development:

```
┌─────────────────────────────────────────┐
│              User Browser               │
│          http://localhost:3000          │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│     Node / Express Server (Port 3000)   │
│ ├─ Serves React Single Page App (Vite)  │
│ ├─ Handles AI Endpoints (/api/ai/*)     │
│ └─ Proxies REST APIs (/api/*) ──────────┼───────┐
└─────────────────────────────────────────┘       │ Proxy
                                                  ▼
                                ┌───────────────────────────────────┐
                                │   Spring Boot Backend (Port 8080) │
                                │   ├─ Spring Security & JWT Auth   │
                                │   ├─ Career Intelligence Engine   │
                                │   └─ JPA Hibernate ORM            │
                                └─────────────────┬─────────────────┘
                                                  │
                                                  ▼
                                ┌───────────────────────────────────┐
                                │     MySQL Database (Port 3306)    │
                                │     Database: `skillpilot`        │
                                │     (Flyway Auto Migrations)      │
                                └───────────────────────────────────┘
```

---

## 3. Environment & Configuration Setup

### A. Database Configuration (MySQL)
1. Ensure your MySQL service is running on `localhost:3306`.
2. Spring Boot will automatically attempt to create the `skillpilot` database if it does not exist.
3. Default credentials configured in `src/main/resources/application.yml`:
   - **Database URL**: `jdbc:mysql://localhost:3306/skillpilot`
   - **Username**: `root`
   - **Password**: `2005`
4. *Customizing credentials*: Set environment variables prior to running the Spring Boot app:
   ```bash
   # Windows PowerShell
   $env:DB_URL="jdbc:mysql://localhost:3306/skillpilot?createDatabaseIfNotExist=true&useSSL=false"
   $env:DB_USERNAME="your_username"
   $env:DB_PASSWORD="your_password"

   # Linux / macOS
   export DB_URL="jdbc:mysql://localhost:3306/skillpilot?createDatabaseIfNotExist=true&useSSL=false"
   export DB_USERNAME="your_username"
   export DB_PASSWORD="your_password"
   ```

### B. Environment Variables (`.env`)
Create a `.env` file in the project root directory (copied from `.env.example`):

```env
# Required for Gemini AI API calls (Roadmap Summary Enhancements)
GEMINI_API_KEY="YOUR_GEMINI_API_KEY_HERE"

# Optional: Host URL configuration
APP_URL="http://localhost:3000"
```

---

## 4. Commands & Code Execution Guide

### Step 1: Install Frontend & Proxy Server Dependencies

Run in the project root directory:

```bash
npm install
```

---

### Step 2: Run the Frontend & Express AI Proxy Server

To launch the dev server on **`http://localhost:3000`**:

```bash
npm run dev
```

#### Other Useful NPM Commands:
- **Type Check / Lint**:
  ```bash
  npm run lint
  ```
- **Build Production Bundle**:
  ```bash
  npm run build
  ```
- **Start Production Server**:
  ```bash
  npm run start
  ```

---

### Step 3: Run the Spring Boot Backend

Open a **separate terminal window** in the project root directory.

#### Option A: Run via Maven (Development)
```bash
mvn spring-boot:run
```

#### Option B: Build JAR and Run (Production / Testing)
```bash
# 1. Package the project into a runnable JAR file
mvn clean package -DskipTests

# 2. Execute the compiled JAR
java -jar target/skillpilot-backend-0.0.1-SNAPSHOT.jar
```

*Note: On startup, Flyway automatically runs database migration scripts (`V1__initial_schema.sql` through `V4__...`) to generate all tables and seed master data.*

---

## 5. Summary of Service URLs & Ports

| Service | Port | Endpoint URL | Description |
| :--- | :--- | :--- | :--- |
| **Frontend Web App & Proxy** | `3000` | `http://localhost:3000` | User Interface & Node.js Express server |
| **Health Check API** | `3000` | `http://localhost:3000/api/health` | Node server status check |
| **AI Enhancer API** | `3000` | `http://localhost:3000/api/ai/enhance-summary` | Gemini AI integration endpoint |
| **Spring Boot REST API** | `8080` | `http://localhost:8080/api` | Backend API (Auth, Career, Assessments) |
| **MySQL Database** | `3306` | `localhost:3306/skillpilot` | Relational storage |

---

## 6. Troubleshooting & Common Issues

1. **`[Proxy Warning] Spring Boot backend is offline on http://localhost:8080`**
   - **Cause**: The Node server is running, but Spring Boot backend has not been started yet.
   - **Fix**: Open another terminal and run `mvn spring-boot:run`.

2. **MySQL Connection Failed (`Access denied for user 'root'@'localhost'`)**
   - **Cause**: MySQL password in `application.yml` (`2005`) does not match your local MySQL root password.
   - **Fix**: Either set environment variables `$env:DB_PASSWORD="your_actual_password"` or update `src/main/resources/application.yml`.

3. **`java: unsupported class file version 61.0` or Java Version Mismatch**
   - **Cause**: System is using JDK 8 or 11 instead of JDK 17.
   - **Fix**: Check `java -version` and `mvn -version`. Ensure JAVA_HOME points to JDK 17+.

4. **Missing Gemini Features**
   - **Cause**: `GEMINI_API_KEY` missing in `.env`.
   - **Fix**: Add valid API key to `.env` file. (The application gracefully falls back to system-calculated reports if the key is missing).
