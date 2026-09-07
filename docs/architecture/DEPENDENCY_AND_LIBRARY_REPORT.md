# SkillPilot — Technical Dependency & Third-Party Library Report

**Document ID:** ARCH-DEP-001  
**Target Version:** v2.5-enterprise  
**Generated:** September 2026  
**Status:** Complete & Authoritative  

---

## Executive Summary

SkillPilot is engineered as a high-performance, deterministic career intelligence platform. The architecture deliberately prioritizes lightweight, purpose-built libraries over bloated external SDKs. 

- **Frontend:** React 19 SPA bundled with Vite 6, styled with Tailwind CSS 4, and utilizing Lucide vector icons for lightweight client footprint.
- **Backend:** Spring Boot 3.2 on Java 17, Spring Security with stateless JWT (JJWT 0.12.5), Flyway migration engine, and MySQL 8.0 connectivity.
- **AI Integration Boundary:** Unlike platforms that pull in heavy third-party GenAI SDKs on the backend, SkillPilot interacts with Google Gemini strictly via native Java 11+ `java.net.http.HttpClient` REST calls, keeping runtime dependencies minimal and avoiding classpath pollution.

---

## 1. Frontend Dependencies (`frontend/package.json`)

### 1.1 Direct Runtime Dependencies

| Library / Package | Declared Version | Classification | Primary Usage Location | Technical Rationale & Necessity |
| :--- | :--- | :--- | :--- | :--- |
| **`react`** | `^19.0.1` | Core Framework | Root UI runtime (`src/main.tsx`, `App.tsx`, all pages) | Provides the core declarative UI engine, component lifecycle, virtual DOM reconciliation, and modern hooks (`useState`, `useEffect`, `useContext`, `useCallback`, `useRef`). |
| **`react-dom`** | `^19.0.1` | DOM Renderer | `src/main.tsx` | Connects React virtual DOM trees directly to the browser DOM (`createRoot`). Essential for rendering the SPA into `index.html`. |
| **`lucide-react`** | `^0.546.0` | Iconography | Header, LoginPage, RegistrationPage, AdminDashboard, ProfilePage | Tree-shakeable SVG vector icons (e.g. `Compass`, `Lock`, `Mail`, `Eye`, `EyeOff`, `KeyRound`, `ShieldCheck`). Eliminates heavy icon font files and provides accessible, consistent UI iconography. |
| **`motion`** | `^12.23.24` | Animation Engine | Modals, notification toasts, transitions | High-performance hardware-accelerated animations (formerly Framer Motion) for smooth modal entrances, drawer animations, and feedback toasts. |
| **`express`** | `^4.21.2` | Production Web Server | `frontend/server.ts` | Minimalist Node.js web server used in containerized or standalone production deployments to serve the compiled `dist/` SPA bundle and reverse-proxy `/api` requests to Spring Boot. |
| **`dotenv`** | `^17.2.3` | Environment Config | `frontend/server.ts` | Loads environment variables (`PORT`, `API_TARGET`) from `.env` files into Node `process.env` during server runtime. |
| **`@google/genai`** | `^2.4.0` | Node Server AI Client | `frontend/server.ts` | Official Google GenAI SDK utilized by the Express server in `server.ts` to power the `POST /api/ai/enhance-summary` endpoint using `gemini-3.6-flash` (bundled into `dist/server.js` via `esbuild`). *(Note: When deployed directly against Spring Boot without `server.ts`, Spring Boot's `AiController` / `GeminiExplanationService` handles this endpoint natively via `java.net.http.HttpClient`).* |
| **`@tailwindcss/vite`** | `^4.1.14` | Styling Integration | `vite.config.ts`, `index.css` | First-party Vite plugin for Tailwind CSS 4 engine, compiling utility classes directly during the Vite pipeline. |
| **`@vitejs/plugin-react`**| `^5.0.4` | Vite Plugin | `vite.config.ts` | Enables Fast Refresh (HMR) and Babel/SWC JSX transformation for React components in Vite. |
| **`vite`** | `^6.2.3` | Bundler & Dev Server | Root tooling | Next-generation frontend build tool providing instantaneous ES module dev serving and Rollup-based production chunking. |

*(Note on Routing: SkillPilot intentionally does not declare `react-router-dom`. Navigation is managed cleanly via centralized state routing in `AppContext.tsx` (`activeTab`), preventing URL hash synchronization issues and minimizing client bundle overhead).*

### 1.2 Development & Build Dependencies

| Library / Package | Version | Purpose | Usage Location |
| :--- | :--- | :--- | :--- |
| **`typescript`** | `~5.8.2` | Static Type Checker | Entire frontend codebase | Enforces type safety across entities, API response DTOs, navigation states, and React component props (`tsconfig.json`). |
| **`tsx`** | `^4.21.0` | TypeScript Node Runner | `package.json` (`npm run dev`) | Executes TypeScript files directly without a manual compilation step, used for running `server.ts` in development. |
| **`esbuild`** | `^0.25.0` | Server Bundler | `package.json` (`npm run build`) | Rapidly bundles `server.ts` into a single standalone production file `dist/server.js`. |
| **`tailwindcss`** | `^4.1.14` | CSS Framework Engine | PostCSS / CSS build pipeline | Compiles atomic, modern utility classes and color tokens. |
| **`autoprefixer`** | `^10.4.21` | CSS Post-Processor | PostCSS | Adds vendor prefixes to CSS rules automatically for cross-browser compatibility. |
| **`@types/node`** | `^22.14.0` | TypeScript Definitions | Node environment | Type definitions for Node.js standard modules (`process`, `fs`, `path`). |
| **`@types/express`** | `^4.17.21` | TypeScript Definitions | `server.ts` | Type definitions for Express Request, Response, and Application. |

---

## 2. Backend Dependencies (`backend/pom.xml`)

### 2.1 Core Runtime Dependencies

| Group & Artifact ID | Scope | Primary Usage Location | Technical Rationale & Necessity |
| :--- | :--- | :--- | :--- |
| **`org.springframework.boot:spring-boot-starter-web`** | Compile (Default) | `controller/*`, `SkillPilotApplication` | Foundational Spring MVC framework providing embedded Apache Tomcat 10, Jackson JSON object mapper, HTTP request routing, REST controllers, and exception mapping. |
| **`org.springframework.boot:spring-boot-starter-data-jpa`** | Compile (Default) | `repository/*`, `entity/*` | Hibernate ORM integration, JPA entity management, automated connection pooling (HikariCP), Spring Data repositories, and `@Transactional` support. |
| **`org.springframework.boot:spring-boot-starter-security`** | Compile (Default) | `config/SecurityConfig`, `security/*` | Enterprise security framework managing the stateless security filter chain, BCrypt password hashing (`BCryptPasswordEncoder(12)`), CORS configurations, and role-based request authorization (`ROLE_STUDENT`, `ROLE_ADMIN`). |
| **`org.springframework.boot:spring-boot-starter-validation`** | Compile (Default) | `dto/request/*`, `controller/*` | Jakarta Validation engine enforcing domain constraints on incoming HTTP requests (`@Valid`, `@NotBlank`, `@Size`) before reaching service logic. |
| **`org.springframework.boot:spring-boot-starter-mail`** | Compile (Default) | `service/EmailService` | Spring JavaMail abstraction configuring `JavaMailSenderImpl` for SMTP email delivery via the Brevo relay host (`smtp-relay.brevo.com:587`). |
| **`com.mysql:mysql-connector-j`** | Runtime | Data source driver | The official JDBC Type 4 driver for MySQL 8.x database communications, connection protocol handling, and SQL execution. |
| **`org.flywaydb:flyway-core`** | Compile (Default) | `db/migration/*` | Automated, version-controlled database migrations (V1 through V11). Ensures repeatable schema initialization and consistency across environments. |
| **`org.flywaydb:flyway-mysql`** | Compile (Default) | Flyway engine | Dedicated MySQL dialect extension required by Flyway 9+ to support MySQL-specific DDL syntax and transactional schema updates. |
| **`io.jsonwebtoken:jjwt-api`** (0.12.5) | Compile (Default) | `security/JwtTokenProvider` | The API interface for JSON Web Tokens, defining secure token creation, header claims, and digital signature contracts. |
| **`io.jsonwebtoken:jjwt-impl`** (0.12.5) | Runtime | `security/JwtTokenProvider` | JJWT runtime implementation providing cryptographic algorithms (HMAC-SHA256) and token building. |
| **`io.jsonwebtoken:jjwt-jackson`** (0.12.5) | Runtime | `security/JwtTokenProvider` | Connects JJWT parsing and serialization directly with Spring's Jackson `ObjectMapper` for high-speed token claim deserialization. |
| **`org.projectlombok:lombok`** | Optional / Build | `entity/*`, `dto/*` | Eliminates boilerplate code at compile time via annotations (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`). |

### 2.2 Test Dependencies

| Group & Artifact ID | Scope | Purpose & Usage |
| :--- | :--- | :--- |
| **`org.springframework.boot:spring-boot-starter-test`** | Test | Core test framework bundling JUnit 5 (Jupiter), Mockito for mock object injection (`@MockBean`), AssertJ for fluent assertions, and Spring MockMvc for integration endpoint testing. |
| **`org.springframework.security:spring-security-test`** | Test | Security test utilities providing `@WithMockUser`, authentication test helpers, and security filter chain simulation in MockMvc. |
| **`com.h2database:h2`** | Test | In-memory relational database running in `MODE=MySQL` (`application-test.yml`). Enables lightning-fast, isolated test execution without requiring a live external MySQL database instance. |

---

## 3. Architectural Audit & Dependency Health Analysis

### 3.1 Unnecessary / Suspicious Dependency Audit
1. **Gemini SDK Absence in Backend:**  
   *Finding:* The backend does NOT include heavy external AI SDKs (e.g. LangChain4j, Spring AI, or Google Cloud Vertex AI client libraries).  
   *Verdict:* **Intentional & Optimal.** The platform communicates with the Gemini REST API using Java's standard `java.net.http.HttpClient` in `GeminiExplanationService.java`. This guarantees zero version conflicts, eliminates over 40MB of transitive JARs, and enforces the strict architectural separation where Gemini is only an optional text explanation layer.
2. **Icon Library Consolidation:**  
   *Finding:* Only `lucide-react` is installed. No redundant icon packages (`@heroicons/react`, `react-icons`, or FontAwesome) exist.  
   *Verdict:* **Optimal.** Clean single-source iconography.
3. **Database Drivers:**  
   *Finding:* Only `mysql-connector-j` (production) and `h2` (test scope) are present.  
   *Verdict:* **Clean.** Zero legacy drivers (PostgreSQL, SQLite) polluting the classpath.
4. **Duplicate Verification Tables:**  
   *Finding:* `password_reset_codes` and `email_verifications` exist as distinct domain models.  
   *Verdict:* **Clean Separation of Concerns.** While structurally similar, password recovery and new user registration verification have different security lifecycles, user statuses, and audit trails. Maintaining dedicated entities prevents accidental cross-invalidation of tokens.
