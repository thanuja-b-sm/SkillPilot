-- SkillPilot Master Dataset Expansion Migration (MySQL 8.0 / H2)
-- V6__expand_master_dataset.sql

-- ============================================================================
-- 1. EXPANDED SKILLS DICTIONARY (~140 NEW SKILLS across multiple domains)
-- ============================================================================
INSERT INTO skills (id, name, category, description, is_active) VALUES
-- Software Engineering & Architecture
('java-spring', 'Java & Spring Boot Core', 'Technical', 'Enterprise Java 17+, Spring Boot microservices, Dependency Injection, and JPA.', TRUE),
('csharp-dotnet', 'C# & .NET Enterprise', 'Technical', 'C# language, ASP.NET Core Web APIs, Entity Framework, and Azure integrations.', TRUE),
('go-lang', 'Golang Microservices', 'Technical', 'Concurrent systems programming, goroutines, gRPC, and cloud native microservices.', TRUE),
('cplusplus', 'C++ System Systems Programming', 'Technical', 'Low-level memory management, RAII, STL, multi-threading, and embedded systems.', TRUE),
('graphql-api', 'GraphQL & REST API Design', 'Technical', 'GraphQL schema design, Apollo Server/Client, RESTful contracts, and OpenAPI.', TRUE),
('microservices-arch', 'Microservices & Event-Driven Systems', 'Technical', 'Decomposed domain architectures, event-driven messaging, and saga patterns.', TRUE),
('kafka-rabbitmq', 'Apache Kafka & Message Queues', 'Tools & Frameworks', 'Event streaming with Kafka, topic partitions, consumers, and RabbitMQ queues.', TRUE),
('redis-caching', 'Redis & Distributed Caching', 'Tools & Frameworks', 'In-memory key-value caching, Pub/Sub, Redis Sentinel, and session stores.', TRUE),
('react-native', 'React Native Cross-Platform', 'Technical', 'Cross-platform iOS/Android development using React Native and Expo.', TRUE),
('flutter-dart', 'Flutter & Dart Development', 'Technical', 'Declarative UI design with Flutter, state management (Bloc/Provider), and Dart.', TRUE),
('ios-swift', 'iOS Development (Swift & SwiftUI)', 'Technical', 'Native iOS engineering with Swift 5, SwiftUI layouts, and Xcode toolchains.', TRUE),
('android-kotlin', 'Android Development (Kotlin)', 'Technical', 'Native Android application development using Kotlin, Jetpack Compose, and Coroutines.', TRUE),
('testing-jest-cypress', 'Unit & E2E Testing (Jest/Cypress)', 'Tools & Frameworks', 'Automated frontend testing with Jest, React Testing Library, and Cypress E2E.', TRUE),
('backend-testing-junit', 'Backend Testing (JUnit & Mockito)', 'Tools & Frameworks', 'Unit testing Java/Spring with JUnit 5, Mockito, and SpringBootTest MockMvc.', TRUE),

-- Cloud, Infrastructure & Security
('azure-cloud', 'Microsoft Azure Infrastructure', 'Technical', 'Azure App Services, AKS, Azure Functions, Blob Storage, and Virtual Networks.', TRUE),
('gcp-cloud', 'Google Cloud Platform (GCP)', 'Technical', 'GCP Compute Engine, GKE, BigQuery, Cloud Run, and IAM security policies.', TRUE),
('terraform-iac', 'Terraform & Infrastructure as Code', 'Tools & Frameworks', 'Declarative infrastructure provisioning with HCL, Terraform state, and modules.', TRUE),
('ansible-automation', 'Ansible IT Automation', 'Tools & Frameworks', 'Configuration management, agentless playbook automation, and server provisioning.', TRUE),
('linux-sysadmin', 'Linux Administration & Shell Scripting', 'Technical', 'Kernel fundamentals, Bash scripting, systemd, process management, and SSH.', TRUE),
('ci-cd-github-actions', 'GitHub Actions & CI/CD Pipelines', 'Tools & Frameworks', 'Continuous integration workflows, automated deployment runners, and secrets management.', TRUE),
('prometheus-grafana', 'Prometheus & Grafana Observability', 'Tools & Frameworks', 'Metrics collection, alerting rules, Prometheus queries, and Grafana dashboards.', TRUE),
('vault-secret-mgmt', 'HashiCorp Vault & Secrets Security', 'Tools & Frameworks', 'Secret storage, dynamic credentials, encryption at rest, and identity management.', TRUE),
('penetration-testing', 'Ethical Hacking & Pen Testing', 'Technical', 'Vulnerability assessment, Metasploit, OWASP Top 10 exploits, and network audits.', TRUE),
('siem-soc-monitoring', 'SIEM & SOC Security Monitoring', 'Tools & Frameworks', 'Security Log Analysis, Splunk, Elastic Security, and Incident Response Playbooks.', TRUE),
('compliance-iso-soc2', 'SOC 2 & ISO 27001 Compliance', 'Domain Knowledge', 'Security governance, audit evidence collection, SOC2 controls, and ISO standards.', TRUE),

-- Data, AI & Analytics
('python-pandas-numpy', 'Data Analysis with Pandas & NumPy', 'Technical', 'Data wrangling, matrix manipulation, series aggregation, and feature engineering.', TRUE),
('sql-advanced', 'Advanced SQL & Query Optimization', 'Technical', 'Window functions, CTEs, query plan execution indexing, and stored procedures.', TRUE),
('bigquery-snowflake', 'Snowflake & BigQuery Data Warehousing', 'Tools & Frameworks', 'Enterprise data warehousing, dimensional modeling, and cloud SQL analytics.', TRUE),
('apache-spark', 'Apache Spark & Big Data Processing', 'Technical', 'Distributed PySpark dataframes, resilient datasets, and ETL streaming pipelines.', TRUE),
('mlops-mlflow', 'MLOps & Model Deployment (MLflow)', 'Tools & Frameworks', 'Model tracking with MLflow, model registry, Containerized serving, and drift detection.', TRUE),
('nlp-spacy-huggingface', 'NLP & LLM Engineering (HuggingFace)', 'Technical', 'Natural language processing, Transformer embeddings, LangChain, and fine-tuning.', TRUE),
('computer-vision', 'Computer Vision & OpenCV', 'Technical', 'Image processing, object detection (YOLO), OpenCV, and convolutional neural networks.', TRUE),
('powerbi-tableau', 'Tableau & Power BI Dashboards', 'Tools & Frameworks', 'Interactive executive reporting, DAX measures, and visual analytics dashboards.', TRUE),
('data-governance', 'Data Governance & Data Quality', 'Domain Knowledge', 'Data lineage tracking, schema validation, data catalogs, and metadata management.', TRUE),

-- Product, Project & Business Management
('scrum-agile-framework', 'Scrum & Kanban Frameworks', 'Domain Knowledge', 'Sprint orchestration, burndown velocity, backlog grooming, and Agile ceremonies.', TRUE),
('jira-confluence', 'Jira & Confluence Management', 'Tools & Frameworks', 'Workflow automation, issue tracking, project boards, and team documentation.', TRUE),
('technical-roadmap-planning', 'Technical Roadmap & Discovery', 'Domain Knowledge', 'Feature sizing, milestone planning, dependency mapping, and release orchestration.', TRUE),
('market-competitor-analysis', 'Market & Competitor Analysis', 'Analytical', 'TAM/SAM sizing, competitive benchmarking, feature positioning, and user personas.', TRUE),
('okr-kpi-tracking', 'OKR & KPI Performance Tracking', 'Domain Knowledge', 'Defining measurable key results, executive metric reporting, and quarterly OKRs.', TRUE),
('risk-management', 'Project Risk & Mitigation Planning', 'Analytical', 'Risk registers, impact analysis, contingency strategies, and scope control.', TRUE),
('vendor-management', 'Vendor & Partner Relations', 'Soft Skills', 'Contract negotiations, SLA management, procurement, and vendor evaluation.', TRUE),

-- Design, UI/UX & Creative
('figma-prototyping', 'Figma Prototyping & Design Systems', 'Tools & Frameworks', 'Component libraries, design tokens, auto-layout, and high-fidelity interaction design.', TRUE),
('user-research-interviews', 'User Research & Usability Testing', 'Domain Knowledge', 'Qualitative user interviews, usability testing sessions, persona mapping, and heuristic audits.', TRUE),
('information-architecture', 'Information Architecture & Card Sorting', 'Domain Knowledge', 'Site map hierarchy, navigation taxonomy, content structuring, and user flow mapping.', TRUE),
('adobe-creative-suite', 'Adobe Creative Cloud (Photoshop/Illustrator)', 'Tools & Frameworks', 'Vector graphic illustration, image retouching, brand asset creation, and layout design.', TRUE),
('motion-design-aftereffects', 'Motion Graphics & After Effects', 'Tools & Frameworks', 'UI micro-interactions, Lottie animations, video editing, and motion branding.', TRUE),
('accessibility-wcag', 'Accessibility Standards (WCAG 2.1)', 'Domain Knowledge', 'Screen reader compatibility, color contrast ratios, keyboard navigation, and ARIA tags.', TRUE),

-- Finance, Accounting & Business Analytics
('financial-statement-analysis', 'Financial Statement Analysis', 'Analytical', 'Balance sheet audit, cash flow statement analysis, P&L reporting, and ratio calculation.', TRUE),
('corporate-valuation-dcf', 'DCF Valuation & Financial Modeling', 'Analytical', 'Discounted cash flow modeling, LBO evaluation, WACC estimation, and scenario analysis.', TRUE),
('investment-portfolio-mgmt', 'Portfolio & Asset Management', 'Domain Knowledge', 'Asset allocation, Sharpe ratio evaluation, risk-adjusted returns, and diversification.', TRUE),
('corporate-taxation-audit', 'Corporate Tax & Compliance Audit', 'Domain Knowledge', 'GAAP/IFRS compliance, tax strategy, internal controls auditing, and financial risk.', TRUE),
('mergers-acquisitions', 'M&A Due Diligence & Structuring', 'Analytical', 'Synergy valuation, target company screening, contract deal structuring, and integration.', TRUE),
('excel-financial-modeling', 'Advanced Excel & Financial Macros', 'Tools & Frameworks', 'VBA automation, complex financial formulas, sensitivity tables, and Monte Carlo simulation.', TRUE),

-- Healthcare, Biology & Life Sciences
('clinical-triage-patient-care', 'Clinical Triage & Patient Assessment', 'Domain Knowledge', 'Vital sign monitoring, emergency triage, clinical diagnosis support, and bedside care.', TRUE),
('electronic-health-records', 'EHR & Health IT Systems (Epic/Cerner)', 'Tools & Frameworks', 'Electronic health record management, HL7/FHIR protocols, and clinical documentation.', TRUE),
('pharmaceutical-pharmacology', 'Pharmacology & Drug Discovery', 'Domain Knowledge', 'Drug target identification, clinical trial design, pharmacokinetics, and FDA approvals.', TRUE),
('biostatistical-analysis', 'Biostatistics & Clinical Trial Analytics', 'Analytical', 'Survival analysis, clinical trial hypothesis testing, R statistical programming, and SAS.', TRUE),
('healthcare-quality-assurance', 'Healthcare Quality & Patient Safety', 'Domain Knowledge', 'Clinical risk management, infection control protocols, and JCAHO accreditation.', TRUE),

-- Engineering, Construction & Clean Tech
('autocad-structural-design', 'AutoCAD & Structural Blueprint Drafting', 'Tools & Frameworks', '2D/3D CAD drafting, structural load calculations, blueprint reading, and building specs.', TRUE),
('revit-bim-modeling', 'Revit BIM & Architectural Modeling', 'Tools & Frameworks', 'Building Information Modeling (BIM), 3D architectural coordination, and MEP design.', TRUE),
('electrical-circuit-design', 'Electrical Circuit & Power Systems', 'Technical', 'PCB design, power distribution grids, low-voltage circuits, and microcontrollers.', TRUE),
('thermodynamics-fluid-mechanics', 'Thermodynamics & Fluid Dynamics', 'Technical', 'Thermal energy transfer, CFD simulation, HVAC calculations, and fluid mechanics.', TRUE),
('solar-pv-engineering', 'Solar PV & Wind Grid System Engineering', 'Technical', 'Photovoltaic array sizing, wind turbine microgrids, battery energy storage, and inverter specs.', TRUE),
('environmental-impact-audit', 'Environmental Impact Assessment', 'Domain Knowledge', 'NEPA compliance, carbon footprint auditing, environmental risk mitigation, and sustainability.', TRUE),

-- Marketing, Growth & Communications
('seo-technical-audit', 'Technical SEO & Content Strategy', 'Technical', 'Site crawlability, schema markup, keyword research, backlink acquisition, and domain authority.', TRUE),
('paid-acquisition-google-meta', 'Paid Acquisition (Google Ads & Meta Ads)', 'Tools & Frameworks', 'Campaign setup, CPC bidding strategies, conversion tracking, and retargeting workflows.', TRUE),
('marketing-analytics-ga4', 'Google Analytics 4 & Attribution', 'Tools & Frameworks', 'GA4 event tracking, funnel analysis, multi-touch attribution, and customer lifetime value.', TRUE),
('content-copywriting-brand', 'Persuasive Copywriting & Content Marketing', 'Soft Skills', 'Headline optimization, brand storytelling, whitepaper drafting, and email copy.', TRUE),
('social-media-growth', 'Social Media Strategy & Community Growth', 'Soft Skills', 'Viral content creation, community engagement, brand channel management, and influencer strategy.', TRUE),
('conversion-rate-opt-cro', 'Conversion Rate Optimization (CRO)', 'Analytical', 'A/B testing with Optimizely, landing page UX audit, heatmaps, and funnel conversion optimization.', TRUE),

-- Human Resources, Talent & Education
('talent-acquisition-sourcing', 'Talent Acquisition & Technical Sourcing', 'Soft Skills', 'Candidate sourcing, structured behavioral interviews, offer negotiation, and ATS tracking.', TRUE),
('hr-compensation-benefits', 'Compensation & Benefits Structuring', 'Domain Knowledge', 'Salary benchmarking, equity planning, health insurance benefit design, and HR compliance.', TRUE),
('employee-relations-culture', 'Employee Relations & Culture Management', 'Soft Skills', 'Conflict resolution, performance improvement plans, retention strategy, and DE&I initiatives.', TRUE),
('instructional-design-addie', 'Instructional Design (ADDIE Model)', 'Domain Knowledge', 'Curriculum mapping, learning outcome specification, ADDIE framework, and e-learning design.', TRUE),
('lms-platform-admin', 'LMS Administration (Canvas/Moodle)', 'Tools & Frameworks', 'Course publishing, quiz configuration, SCORM packaging, and learning analytics.', TRUE),

-- Operations, Logistics & Supply Chain
('supply-chain-optimization', 'Supply Chain & Demand Forecasting', 'Analytical', 'Inventory optimization, demand planning, safety stock calculation, and vendor logistics.', TRUE),
('lean-six-sigma', 'Lean Six Sigma & Process Improvement', 'Analytical', 'DMAIC framework, root cause analysis, waste reduction, and process flow mapping.', TRUE),
('logistics-fleet-management', 'Logistics & Distribution Management', 'Domain Knowledge', 'Freight route optimization, warehouse management systems (WMS), and customs clearance.', TRUE),
('warehouse-mgmt-wms', 'Warehouse Management Systems (WMS)', 'Tools & Frameworks', 'WMS software architecture, slotting optimization, RFID tracking, and inventory control.', TRUE),
('logistics-customs-compliance', 'Customs & Trade Compliance', 'Domain Knowledge', 'Import/export regulations, tariff classification, HS codes, and international trade compliance.', TRUE),
('procurement-contract-mgmt', 'Strategic Procurement & Sourcing', 'Analytical', 'Supplier evaluation, RFP execution, contract negotiation, and spend analytics.', TRUE),
('inventory-safety-stock', 'Inventory Control & Safety Stock Analysis', 'Analytical', 'Reorder point calculation, ABC inventory classification, and holding cost minimization.', TRUE),
('six-sigma-black-belt', 'Six Sigma Black Belt Process Quality', 'Domain Knowledge', 'Statistical process control (SPC), capability analysis (Cp/Cpk), and DOE experiments.', TRUE),

-- Additional Advanced Tech, AI & Cloud Skills
('vector-db-pinecone', 'Vector Databases (Pinecone/Weaviate)', 'Tools & Frameworks', 'High-dimensional vector indexing, similarity search, HNSW index tuning, and RAG storage.', TRUE),
('rag-architecture', 'Advanced RAG Pipeline Design', 'Technical', 'Document chunking strategies, hybrid keyword-vector search, re-ranking, and context synthesis.', TRUE),
('langchain-agentic', 'LangChain & Agentic Frameworks', 'Technical', 'Autonomous agent loops, tool-calling agents, LangGraph workflows, and multi-agent systems.', TRUE),
('ml-feature-store', 'ML Feature Store (Feast/Hopsworks)', 'Tools & Frameworks', 'Online/offline feature serving, feature versioning, point-in-time joins, and feature stores.', TRUE),
('spark-streaming', 'Structured Spark Streaming', 'Technical', 'Real-time micro-batch processing, watermarking, stateful streaming joins, and Delta Lake.', TRUE),
('tableau-dax', 'DAX & Complex PowerBI Expressions', 'Technical', 'DAX measures, calculated columns, evaluation contexts, and time-intelligence functions.', TRUE),
('data-mesh-architecture', 'Data Mesh & Domain Data Products', 'Technical', 'Decentralized data governance, data product specification, and domain-driven data mesh.', TRUE),
('rust-systems-lang', 'Rust Systems Programming', 'Technical', 'Borrow checker semantics, zero-cost abstractions, unsafe blocks, and memory safety without GC.', TRUE),
('csharp-dotnet-core', 'ASP.NET Core Web APIs', 'Technical', 'RESTful API controllers, Dependency Injection container, middleware pipelines, and EF Core.', TRUE),
('aspnet-microservices', 'Enterprise .NET Microservices', 'Technical', 'gRPC service communication, MassTransit message bus, and OpenTelemetry instrumentation.', TRUE),
('elixir-phoenix', 'Elixir & Phoenix Framework', 'Technical', 'BEAM virtual machine concurrency, OTP GenServer actors, Phoenix LiveView, and fault tolerance.', TRUE),
('solidity-smart-contracts', 'Solidity & Smart Contract Audit', 'Technical', 'EVM execution, ERC-20/721 tokens, reentrancy defense, and Hardhat development.', TRUE),
('webassembly-wasm', 'WebAssembly (WASM) Web Engineering', 'Technical', 'Compiling Rust/C++ to WASM, shared array buffers, and high-performance browser modules.', TRUE),
('playwright-e2e-testing', 'Playwright E2E Automation', 'Tools & Frameworks', 'Browser automation, visual regression testing, network interception, and parallel test suites.', TRUE),
('storybook-design-tokens', 'Storybook & Design System Tokens', 'Tools & Frameworks', 'UI component documentation, accessibility addons, design tokens, and atomic components.', TRUE),

-- Additional Healthcare, Finance & Marketing Skills
('clinical-pharmacology', 'Clinical Pharmacology & Therapeutics', 'Domain Knowledge', 'Drug mechanism of action, pharmacodynamics, dosage calculations, and adverse reactions.', TRUE),
('medical-triage-emergency', 'Emergency Medical Triage', 'Domain Knowledge', 'Emergency severity index (ESI), trauma evaluation, rapid stabilization, and clinical triage.', TRUE),
('clinical-trial-management', 'Clinical Trial Operations (GCP)', 'Domain Knowledge', 'Good Clinical Practice (GCP), IRB approvals, clinical monitoring, and protocol compliance.', TRUE),
('medical-device-compliance', 'Medical Device Regulation (FDA 510k)', 'Domain Knowledge', 'FDA 510(k) clearance, ISO 13485 quality management, and medical device risk analysis.', TRUE),
('telehealth-systems', 'Telehealth & Remote Monitoring IT', 'Technical', 'Remote patient monitoring (RPM), HIPAA video streaming, and EHR integrations.', TRUE),
('investment-valuation-dcf', 'Advanced Valuation & LBO Modeling', 'Analytical', 'Leveraged buyout (LBO) modeling, debt waterfall scheduling, and IRR calculations.', TRUE),
('fixed-income-bond-analytics', 'Fixed Income & Bond Analytics', 'Analytical', 'Yield curve modeling, duration/convexity metrics, bond pricing, and credit spreads.', TRUE),
('derivatives-pricing', 'Options & Financial Derivatives Pricing', 'Analytical', 'Black-Scholes pricing model, Greeks calculation, volatility surfaces, and hedging strategies.', TRUE),
('risk-adjusted-return-sharpe', 'Portfolio Risk & Sharpe Ratio Analysis', 'Analytical', 'Value at Risk (VaR), Conditional VaR, Sharpe ratio optimization, and portfolio risk.', TRUE),
('forex-hedging', 'Foreign Exchange (FX) Risk Hedging', 'Analytical', 'Currency forward contracts, FX swaps, translation exposure management, and hedging.', TRUE),
('growth-funnel-attribution', 'Growth Funnel & Multi-Touch Attribution', 'Analytical', 'Customer acquisition cost (CAC), LTV/CAC ratio, attribution modeling, and funnel audit.', TRUE),
('copywriting-landing-pages', 'Conversion Copywriting & Landing Pages', 'Soft Skills', 'High-converting headline copy, sales messaging, benefit positioning, and CTA optimization.', TRUE),
('email-marketing-automation', 'Email Lifecycle Automation (Klaviyo/HubSpot)', 'Tools & Frameworks', 'Behavioral email triggers, audience segmentation, DMARC/DKIM compliance, and retention.', TRUE),
('brand-positioning-pr', 'Public Relations & Strategic Brand Positioning', 'Soft Skills', 'Media relations, press release drafting, crisis communication, and brand authority.', TRUE),
('influencer-marketing', 'Influencer & Affiliate Partnerships', 'Soft Skills', 'Creator outreach, contract negotiations, affiliate tracking, and campaign ROI analytics.', TRUE),
('finite-element-analysis-fea', 'Finite Element Analysis (FEA)', 'Technical', 'ANSYS structural stress simulation, mesh generation, thermal stress, and boundary conditions.', TRUE),
('scada-industrial-control', 'SCADA & Industrial Control Systems', 'Technical', 'PLC ladder logic programming, HMI interfaces, Modbus protocols, and industrial automation.', TRUE),
('hvac-building-automation', 'HVAC & Smart Building Automation', 'Technical', 'Building energy management (BMS), BACnet protocol, psychrometrics, and HVAC design.', TRUE),
('robotics-ros2', 'Robotics Operating System (ROS 2)', 'Technical', 'ROS 2 nodes, publisher/subscriber topics, kinematics, sensor fusion, and navigation.', TRUE),
('agile-coaching-safe', 'Scaled Agile Framework (SAFe)', 'Domain Knowledge', 'Program Increment (PI) planning, agile release trains (ART), and enterprise agility.', TRUE),
('change-management', 'Organizational Change Management', 'Leadership', 'Kotter 8-step model, stakeholder impact analysis, communication plans, and adoption.', TRUE),

-- Soft Skills & Core Analytical Attributes
('leadership-team-building', 'Team Leadership & Mentorship', 'Leadership', 'Guiding engineering teams, mentoring junior staff, fostering accountability, and delegation.', TRUE),
('strategic-thinking', 'Strategic Vision & Execution', 'Leadership', 'Long-term goals positioning, business alignment, trade-off analysis, and strategic execution.', TRUE),
('critical-thinking-logic', 'Structured Logic & Critical Reasoning', 'Analytical', 'First-principles reasoning, hypothesis testing, data-driven decision making, and bias avoidance.', TRUE),
('cross-functional-collaboration', 'Cross-Functional Team Collaboration', 'Soft Skills', 'Bridging engineering, business, design, and legal stakeholders toward shared objectives.', TRUE),
('negotiation-persuasion', 'Executive Negotiation & Persuasion', 'Soft Skills', 'Win-win contract negotiation, executive stakeholder management, and deal closing.', TRUE),
('adaptability-resilience', 'Adaptability & Agile Problem Solving', 'Soft Skills', 'Thriving under ambiguity, pivoting on changing requirements, and continuous learning.', TRUE);


-- ============================================================================
-- 2. EXPANDED CAREERS DATASET (25 NEW ACTIVE CAREERS -> Total 36 Active Careers)
-- ============================================================================
INSERT INTO careers (id, title, category, description, average_salary, growth_rate, demand_level, is_active) VALUES
-- Software Engineering & Architecture
('backend-systems-engineer', 'Senior Backend & Systems Engineer', 'Software Engineering', 'Architects resilient server-side microservices, transactional databases, and high-concurrency APIs.', '$135,000 - $180,000 / yr', '+25% (High Growth)', 'Very High', TRUE),
('frontend-architect', 'Lead Frontend Systems Architect', 'Software Engineering', 'Directs modern web frontend architectures, design system implementations, performance optimization, and Web Vitals.', '$130,000 - $175,000 / yr', '+21% (Strong Demand)', 'High', TRUE),
('mobile-app-developer', 'Senior Mobile Engineer (iOS & Android)', 'Software Engineering', 'Engineers native and cross-platform mobile applications with offline sync, fluid UI, and app store deployment.', '$125,000 - $170,000 / yr', '+23% (High Demand)', 'High', TRUE),
('embedded-systems-engineer', 'Embedded Systems & IoT Engineer', 'Software Engineering', 'Develops real-time firmware, micro-controller code, C/C++ drivers, and hardware-software interfaces.', '$120,000 - $165,000 / yr', '+19% (Steady Demand)', 'High', TRUE),
('qa-automation-engineer', 'Lead QA Automation & Test Engineer', 'Software Engineering', 'Builds comprehensive automated testing frameworks, CI/CD integration suites, and reliability audits.', '$110,000 - $150,000 / yr', '+18% (Steady Demand)', 'Medium', TRUE),
('site-reliability-engineer', 'Site Reliability Engineer (SRE)', 'Software Engineering', 'Ensures high availability, fault tolerance, incident response automation, and SLA/SLO performance targets.', '$140,000 - $185,000 / yr', '+29% (Very High Growth)', 'Very High', TRUE),

-- Cloud & Infrastructure
('devops-platform-engineer', 'DevOps & Platform Automation Engineer', 'Cloud & Infrastructure', 'Standardizes Infrastructure-as-Code, Kubernetes developer platforms, and continuous delivery pipelines.', '$135,000 - $180,000 / yr', '+27% (Very High Growth)', 'Very High', TRUE),
('network-security-engineer', 'Network & Security Infrastructure Architect', 'Cloud & Infrastructure', 'Configures enterprise firewalls, VPN tunnels, zero-trust network policy, and hybrid-cloud connectivity.', '$130,000 - $172,000 / yr', '+22% (High Demand)', 'High', TRUE),
('database-administrator', 'Database Infrastructure Specialist & DBA', 'Cloud & Infrastructure', 'Manages high-availability database clusters, replication, query tuning, and disaster recovery strategies.', '$120,000 - $160,000 / yr', '+17% (Steady Demand)', 'Medium', TRUE),

-- Data & AI
('data-engineer', 'Senior Data Engineer & Pipeline Architect', 'Data & Analytics', 'Architects large-scale data ingestion pipelines, ETL transformations, data lakes, and data warehouses.', '$135,000 - $180,000 / yr', '+30% (Very High Growth)', 'Very High', TRUE),
('ai-prompt-llm-engineer', 'Generative AI & LLM Application Engineer', 'Artificial Intelligence', 'Engineers LLM agents, Retrieval-Augmented Generation (RAG) pipelines, prompt templates, and vector databases.', '$140,000 - $190,000 / yr', '+40% (Exceptional Growth)', 'Very High', TRUE),
('bi-analytics-manager', 'Business Intelligence & Analytics Manager', 'Data & Analytics', 'Translates complex operational data into executive dashboards, business insights, and growth metrics.', '$120,000 - $165,000 / yr', '+20% (High Demand)', 'High', TRUE),

-- Cybersecurity & Compliance
('penetration-tester-red-team', 'Ethical Hacker & Red Team Security Specialist', 'Cybersecurity', 'Simulates advanced cyber attacks, identifies zero-day vulnerabilities, and conducts penetration testing.', '$130,000 - $178,000 / yr', '+28% (High Demand)', 'Very High', TRUE),
('security-compliance-auditor', 'Information Security & Compliance Auditor', 'Cybersecurity', 'Audits enterprise compliance against SOC 2, ISO 27001, HIPAA, and GDPR standards.', '$115,000 - $155,000 / yr', '+21% (Strong Demand)', 'High', TRUE),

-- Product & Project Management
('scrum-master-agile-coach', 'Agile Program Coach & Lead Scrum Master', 'Product & Management', 'Facilitates agile team ceremonies, removes organizational blockers, and optimizes development velocity.', '$115,000 - $155,000 / yr', '+18% (Steady Demand)', 'Medium', TRUE),
('technical-program-manager', 'Technical Program Manager (TPM)', 'Product & Management', 'Drives complex cross-engineering initiatives, risk mitigation, schedule delivery, and executive communication.', '$140,000 - $185,000 / yr', '+22% (High Demand)', 'High', TRUE),

-- Business, Finance & Accounting
('investment-banker-m-and-a', 'Investment Banking & M&A Specialist', 'Business & Finance', 'Structures corporate mergers and acquisitions, capital raises, valuation analyses, and financial modeling.', '$150,000 - $220,000 / yr', '+19% (High Value)', 'High', TRUE),
('management-consultant', 'Strategic Management Consultant', 'Business & Finance', 'Advises C-suite executives on corporate strategy, operational reorganization, market expansion, and profitability.', '$140,000 - $195,000 / yr', '+21% (High Demand)', 'High', TRUE),
('accounting-audit-manager', 'Corporate Audit & Accounting Manager', 'Business & Finance', 'Oversees financial accounting compliance, internal controls, tax strategy, and regulatory filings.', '$115,000 - $158,000 / yr', '+16% (Steady Demand)', 'Medium', TRUE),

-- Healthcare & Bio-Informatics
('health-informatics-specialist', 'Health Informatics Systems Lead', 'Healthcare & Medicine', 'Integrates clinical healthcare data systems, EHR databases, medical analytics, and patient privacy compliance.', '$110,000 - $150,000 / yr', '+25% (High Growth)', 'High', TRUE),

-- Engineering & Clean Energy
('mechanical-design-engineer', 'Mechanical Design & CAD Engineer', 'Engineering & Energy', 'Engineers mechanical components, structural assemblies, CAD models, and thermal systems.', '$110,000 - $152,000 / yr', '+17% (Steady Demand)', 'Medium', TRUE),
('electrical-power-engineer', 'Electrical Power & Hardware Engineer', 'Engineering & Energy', 'Designs high-voltage electrical circuits, power grids, PCB hardware, and control systems.', '$115,000 - $160,000 / yr', '+20% (High Demand)', 'High', TRUE),

-- Marketing & Media
('content-strategy-lead', 'Content Strategy & Marketing Director', 'Marketing & Media', 'Leads multi-platform brand content, editorial strategy, whitepapers, and customer acquisition copy.', '$105,000 - $148,000 / yr', '+18% (Steady Demand)', 'Medium', TRUE),

-- HR & Education
('hr-talent-acquisition-lead', 'Global Talent Acquisition Lead', 'Human Resources', 'Spearheads technical recruitment, candidate evaluation, compensation planning, and employer branding.', '$105,000 - $145,000 / yr', '+19% (High Demand)', 'Medium', TRUE),

-- Operations & Supply Chain
('supply-chain-logistics-director', 'Supply Chain & Operations Director', 'Operations & Logistics', 'Directs global supply chain logistics, demand forecasting, inventory optimization, and vendor networks.', '$130,000 - $180,000 / yr', '+24% (High Growth)', 'High', TRUE);


-- Typical Roles & Prerequisites for new careers
INSERT INTO career_typical_roles (career_id, role_name) VALUES
('backend-systems-engineer', 'Backend Developer'), ('backend-systems-engineer', 'API Architect'), ('backend-systems-engineer', 'Distributed Systems Lead'),
('frontend-architect', 'Lead Frontend Engineer'), ('frontend-architect', 'UI Architect'), ('frontend-architect', 'Web Performance Engineer'),
('mobile-app-developer', 'iOS Engineer'), ('mobile-app-developer', 'Android Engineer'), ('mobile-app-developer', 'Flutter Specialist'),
('site-reliability-engineer', 'SRE Lead'), ('site-reliability-engineer', 'Infrastructure Reliability Engineer'),
('devops-platform-engineer', 'Platform Engineer'), ('devops-platform-engineer', 'DevOps Lead'),
('data-engineer', 'Big Data Engineer'), ('data-engineer', 'ETL Pipeline Architect'),
('ai-prompt-llm-engineer', 'Generative AI Developer'), ('ai-prompt-llm-engineer', 'RAG Systems Specialist'),
('penetration-tester-red-team', 'Red Team Operator'), ('penetration-tester-red-team', 'Ethical Hacker'),
('investment-banker-m-and-a', 'M&A Associate'), ('investment-banker-m-and-a', 'Investment Banking Vice President'),
('supply-chain-logistics-director', 'Logistics Director'), ('supply-chain-logistics-director', 'Supply Chain Manager');

INSERT INTO career_prerequisites (career_id, prerequisite) VALUES
('backend-systems-engineer', 'Object-Oriented Programming & Relational Databases'),
('frontend-architect', 'Advanced JavaScript/TypeScript & Web Performance Standards'),
('mobile-app-developer', 'Mobile Application Design & REST/GraphQL API Consumption'),
('site-reliability-engineer', 'Linux Administration, Networking & Systems Automation'),
('devops-platform-engineer', 'Docker Containerization & Infrastructure as Code'),
('data-engineer', 'SQL Proficiency & Distributed Data Processing Basics'),
('ai-prompt-llm-engineer', 'Python Programming & Natural Language Processing Fundamentals'),
('penetration-tester-red-team', 'Networking Protocols, Operating System Internals & Security Fundamentals'),
('investment-banker-m-and-a', 'Corporate Accounting & Financial Modeling Fundamentals'),
('supply-chain-logistics-director', 'Operations Management & Process Optimization');


-- ============================================================================
-- 3. CAREER SKILL REQUIREMENTS FOR ALL CAREERS (8-14 Requirements per career)
-- ============================================================================

-- helper UUID values in deterministic patterns or UUID()
INSERT INTO career_skill_requirements (id, career_id, skill_id, required_level, is_essential) VALUES
-- 1. backend-systems-engineer
('csr-bse-1', 'backend-systems-engineer', 'java-spring', 5, TRUE),
('csr-bse-2', 'backend-systems-engineer', 'sql-db', 4, TRUE),
('csr-bse-3', 'backend-systems-engineer', 'system-design', 4, TRUE),
('csr-bse-4', 'backend-systems-engineer', 'microservices-arch', 4, TRUE),
('csr-bse-5', 'backend-systems-engineer', 'redis-caching', 3, FALSE),
('csr-bse-6', 'backend-systems-engineer', 'kafka-rabbitmq', 3, FALSE),
('csr-bse-7', 'backend-systems-engineer', 'docker-k8s', 3, TRUE),
('csr-bse-8', 'backend-systems-engineer', 'backend-testing-junit', 4, FALSE),
('csr-bse-9', 'backend-systems-engineer', 'problem-solving', 4, TRUE),

-- 2. frontend-architect
('csr-fa-1', 'frontend-architect', 'typescript', 5, TRUE),
('csr-fa-2', 'frontend-architect', 'react', 5, TRUE),
('csr-fa-3', 'frontend-architect', 'graphql-api', 4, FALSE),
('csr-fa-4', 'frontend-architect', 'figma-ui', 3, FALSE),
('csr-fa-5', 'frontend-architect', 'accessibility-wcag', 4, TRUE),
('csr-fa-6', 'frontend-architect', 'testing-jest-cypress', 4, TRUE),
('csr-fa-7', 'frontend-architect', 'conversion-rate-opt-cro', 3, FALSE),
('csr-fa-8', 'frontend-architect', 'problem-solving', 4, TRUE),

-- 3. mobile-app-developer
('csr-mad-1', 'mobile-app-developer', 'flutter-dart', 4, TRUE),
('csr-mad-2', 'mobile-app-developer', 'ios-swift', 4, TRUE),
('csr-mad-3', 'mobile-app-developer', 'android-kotlin', 4, FALSE),
('csr-mad-4', 'mobile-app-developer', 'react-native', 3, FALSE),
('csr-mad-5', 'mobile-app-developer', 'graphql-api', 3, FALSE),
('csr-mad-6', 'mobile-app-developer', 'git', 4, TRUE),
('csr-mad-7', 'mobile-app-developer', 'problem-solving', 4, TRUE),

-- 4. site-reliability-engineer
('csr-sre-1', 'site-reliability-engineer', 'linux-sysadmin', 5, TRUE),
('csr-sre-2', 'site-reliability-engineer', 'prometheus-grafana', 4, TRUE),
('csr-sre-3', 'site-reliability-engineer', 'docker-k8s', 5, TRUE),
('csr-sre-4', 'site-reliability-engineer', 'terraform-iac', 4, TRUE),
('csr-sre-5', 'site-reliability-engineer', 'python', 4, FALSE),
('csr-sre-6', 'site-reliability-engineer', 'go-lang', 3, FALSE),
('csr-sre-7', 'site-reliability-engineer', 'ci-cd-github-actions', 4, TRUE),

-- 5. devops-platform-engineer
('csr-dpe-1', 'devops-platform-engineer', 'terraform-iac', 5, TRUE),
('csr-dpe-2', 'devops-platform-engineer', 'docker-k8s', 5, TRUE),
('csr-dpe-3', 'devops-platform-engineer', 'ansible-automation', 4, FALSE),
('csr-dpe-4', 'devops-platform-engineer', 'cloud-aws', 4, TRUE),
('csr-dpe-5', 'devops-platform-engineer', 'ci-cd-github-actions', 5, TRUE),
('csr-dpe-6', 'devops-platform-engineer', 'vault-secret-mgmt', 3, FALSE),

-- 6. data-engineer
('csr-de-1', 'data-engineer', 'sql-advanced', 5, TRUE),
('csr-de-2', 'data-engineer', 'python-pandas-numpy', 4, TRUE),
('csr-de-3', 'data-engineer', 'apache-spark', 4, TRUE),
('csr-de-4', 'data-engineer', 'bigquery-snowflake', 4, TRUE),
('csr-de-5', 'data-engineer', 'kafka-rabbitmq', 3, FALSE),
('csr-de-6', 'data-engineer', 'data-governance', 3, FALSE),
('csr-de-7', 'data-engineer', 'cloud-aws', 4, TRUE),

-- 7. ai-prompt-llm-engineer
('csr-aple-1', 'ai-prompt-llm-engineer', 'python', 5, TRUE),
('csr-aple-2', 'ai-prompt-llm-engineer', 'nlp-spacy-huggingface', 5, TRUE),
('csr-aple-3', 'ai-prompt-llm-engineer', 'deep-learning', 4, TRUE),
('csr-aple-4', 'ai-prompt-llm-engineer', 'mlops-mlflow', 3, FALSE),
('csr-aple-5', 'ai-prompt-llm-engineer', 'redis-caching', 3, FALSE),
('csr-aple-6', 'ai-prompt-llm-engineer', 'graphql-api', 3, FALSE),

-- 8. penetration-tester-red-team
('csr-ptr-1', 'penetration-tester-red-team', 'penetration-testing', 5, TRUE),
('csr-ptr-2', 'penetration-tester-red-team', 'cybersecurity', 5, TRUE),
('csr-ptr-3', 'penetration-tester-red-team', 'linux-sysadmin', 4, TRUE),
('csr-ptr-4', 'penetration-tester-red-team', 'python', 3, FALSE),
('csr-ptr-5', 'penetration-tester-red-team', 'compliance-iso-soc2', 3, FALSE),

-- 9. bi-analytics-manager
('csr-bam-1', 'bi-analytics-manager', 'powerbi-tableau', 5, TRUE),
('csr-bam-2', 'bi-analytics-manager', 'sql-advanced', 4, TRUE),
('csr-bam-3', 'bi-analytics-manager', 'data-analytics', 4, TRUE),
('csr-bam-4', 'bi-analytics-manager', 'okr-kpi-tracking', 4, TRUE),
('csr-bam-5', 'bi-analytics-manager', 'communication', 4, FALSE),

-- 10. technical-program-manager
('csr-tpm-1', 'technical-program-manager', 'technical-roadmap-planning', 5, TRUE),
('csr-tpm-2', 'technical-program-manager', 'risk-management', 4, TRUE),
('csr-tpm-3', 'technical-program-manager', 'scrum-agile-framework', 4, TRUE),
('csr-tpm-4', 'technical-program-manager', 'jira-confluence', 4, FALSE),
('csr-tpm-5', 'technical-program-manager', 'communication', 5, TRUE),

-- 11. investment-banker-m-and-a
('csr-ib-1', 'investment-banker-m-and-a', 'corporate-valuation-dcf', 5, TRUE),
('csr-ib-2', 'investment-banker-m-and-a', 'financial-statement-analysis', 5, TRUE),
('csr-ib-3', 'investment-banker-m-and-a', 'mergers-acquisitions', 4, TRUE),
('csr-ib-4', 'investment-banker-m-and-a', 'excel-financial-modeling', 5, TRUE),
('csr-ib-5', 'investment-banker-m-and-a', 'negotiation-persuasion', 4, FALSE),

-- 12. management-consultant
('csr-mc-1', 'management-consultant', 'strategic-thinking', 5, TRUE),
('csr-mc-2', 'management-consultant', 'critical-thinking-logic', 5, TRUE),
('csr-mc-3', 'management-consultant', 'market-competitor-analysis', 4, TRUE),
('csr-mc-4', 'management-consultant', 'communication', 5, TRUE),
('csr-mc-5', 'management-consultant', 'financial-statement-analysis', 3, FALSE),

-- 13. health-informatics-specialist
('csr-his-1', 'health-informatics-specialist', 'electronic-health-records', 5, TRUE),
('csr-his-2', 'health-informatics-specialist', 'medical-compliance', 5, TRUE),
('csr-his-3', 'health-informatics-specialist', 'sql-db', 4, TRUE),
('csr-his-4', 'health-informatics-specialist', 'data-governance', 4, FALSE),

-- 14. mechanical-design-engineer
('csr-mde-1', 'mechanical-design-engineer', 'autocad-structural-design', 5, TRUE),
('csr-mde-2', 'mechanical-design-engineer', 'thermodynamics-fluid-mechanics', 4, TRUE),
('csr-mde-3', 'mechanical-design-engineer', 'cad-engineering', 5, TRUE),
('csr-mde-4', 'mechanical-design-engineer', 'problem-solving', 4, TRUE),

-- 15. electrical-power-engineer
('csr-epe-1', 'electrical-power-engineer', 'electrical-circuit-design', 5, TRUE),
('csr-epe-2', 'electrical-power-engineer', 'solar-pv-engineering', 4, FALSE),
('csr-epe-3', 'electrical-power-engineer', 'autocad-structural-design', 3, FALSE),
('csr-epe-4', 'electrical-power-engineer', 'problem-solving', 4, TRUE),

-- 16. content-strategy-lead
('csr-csl-1', 'content-strategy-lead', 'content-copywriting-brand', 5, TRUE),
('csr-csl-2', 'content-strategy-lead', 'seo-technical-audit', 4, TRUE),
('csr-csl-3', 'content-strategy-lead', 'social-media-growth', 4, FALSE),
('csr-csl-4', 'content-strategy-lead', 'brand-strategy', 4, TRUE),

-- 17. hr-talent-acquisition-lead
('csr-hr-1', 'hr-talent-acquisition-lead', 'talent-acquisition-sourcing', 5, TRUE),
('csr-hr-2', 'hr-talent-acquisition-lead', 'employee-relations-culture', 4, TRUE),
('csr-hr-3', 'hr-talent-acquisition-lead', 'hr-compensation-benefits', 4, FALSE),
('csr-hr-4', 'hr-talent-acquisition-lead', 'communication', 5, TRUE),

-- 18. supply-chain-logistics-director
('csr-sc-1', 'supply-chain-logistics-director', 'supply-chain-optimization', 5, TRUE),
('csr-sc-2', 'supply-chain-logistics-director', 'logistics-fleet-management', 4, TRUE),
('csr-sc-3', 'supply-chain-logistics-director', 'lean-six-sigma', 4, TRUE),
('csr-sc-4', 'supply-chain-logistics-director', 'vendor-management', 4, FALSE),
('csr-sc-5', 'supply-chain-logistics-director', 'risk-management', 4, FALSE),

-- 19. embedded-systems-engineer
('csr-ese-1', 'embedded-systems-engineer', 'cplusplus', 5, TRUE),
('csr-ese-2', 'embedded-systems-engineer', 'electrical-circuit-design', 4, TRUE),
('csr-ese-3', 'embedded-systems-engineer', 'linux-sysadmin', 4, TRUE),
('csr-ese-4', 'embedded-systems-engineer', 'robotics-ros2', 3, FALSE),

-- 20. qa-automation-engineer
('csr-qae-1', 'qa-automation-engineer', 'testing-jest-cypress', 5, TRUE),
('csr-qae-2', 'qa-automation-engineer', 'playwright-e2e-testing', 5, TRUE),
('csr-qae-3', 'qa-automation-engineer', 'backend-testing-junit', 4, TRUE),
('csr-qae-4', 'qa-automation-engineer', 'ci-cd-github-actions', 3, FALSE),

-- 21. network-security-engineer
('csr-nse-1', 'network-security-engineer', 'cybersecurity', 5, TRUE),
('csr-nse-2', 'network-security-engineer', 'linux-sysadmin', 4, TRUE),
('csr-nse-3', 'network-security-engineer', 'penetration-testing', 4, TRUE),
('csr-nse-4', 'network-security-engineer', 'siem-soc-monitoring', 3, FALSE),

-- 22. database-administrator
('csr-dba-1', 'database-administrator', 'sql-db', 5, TRUE),
('csr-dba-2', 'database-administrator', 'sql-advanced', 5, TRUE),
('csr-dba-3', 'database-administrator', 'linux-sysadmin', 4, TRUE),
('csr-dba-4', 'database-administrator', 'redis-caching', 3, FALSE),

-- 23. security-compliance-auditor
('csr-sca-1', 'security-compliance-auditor', 'compliance-iso-soc2', 5, TRUE),
('csr-sca-2', 'security-compliance-auditor', 'cybersecurity', 4, TRUE),
('csr-sca-3', 'security-compliance-auditor', 'data-governance', 4, TRUE),
('csr-sca-4', 'security-compliance-auditor', 'risk-management', 4, FALSE),

-- 24. scrum-master-agile-coach
('csr-sm-1', 'scrum-master-agile-coach', 'scrum-agile-framework', 5, TRUE),
('csr-sm-2', 'scrum-master-agile-coach', 'agile-coaching-safe', 4, TRUE),
('csr-sm-3', 'scrum-master-agile-coach', 'jira-confluence', 4, TRUE),
('csr-sm-4', 'scrum-master-agile-coach', 'leadership-team-building', 4, FALSE),

-- 25. accounting-audit-manager
('csr-aam-1', 'accounting-audit-manager', 'corporate-taxation-audit', 5, TRUE),
('csr-aam-2', 'accounting-audit-manager', 'financial-statement-analysis', 5, TRUE),
('csr-aam-3', 'accounting-audit-manager', 'excel-financial-modeling', 4, TRUE),
('csr-aam-4', 'accounting-audit-manager', 'risk-management', 4, FALSE);


-- ============================================================================
-- 4. QUESTIONNAIRE QUESTIONS & OPTIONS (~40 NEW QUESTIONS)
-- ============================================================================
INSERT INTO questions (id, section, question, description, type, display_order, is_active) VALUES
-- Technical & Systems Engineering Section
('q-se-1', 'Software Architecture', 'When designing a microservice API expected to handle 50,000 requests/sec, which architectural pattern do you prioritize?', 'Evaluates backend systems engineering and caching principles.', 'SINGLE_CHOICE', 11, TRUE),
('q-se-2', 'Data Persistence & Storage', 'How do you handle relational database scaling when query read volume quadruples?', 'Measures database tuning and indexing capability.', 'SINGLE_CHOICE', 12, TRUE),
('q-se-3', 'Frontend Performance', 'What is your primary technique for optimizing Web Vitals and large SPA bundle sizes?', 'Assesses modern frontend architecture skills.', 'SINGLE_CHOICE', 13, TRUE),
('q-se-4', 'Mobile Engineering', 'How do you manage offline data sync in mobile applications?', 'Evaluates mobile engineering practices.', 'SINGLE_CHOICE', 14, TRUE),
('q-se-5', 'Continuous Integration', 'Which CI/CD pipeline strategy best guarantees zero-downtime deployment?', 'Assesses DevOps and automation practices.', 'SINGLE_CHOICE', 15, TRUE),

-- Cloud & Cybersecurity Section
('q-cc-1', 'Cloud Infrastructure', 'What approach do you take when provisioning dynamic cloud environments across AWS and Azure?', 'Measures Infrastructure-as-Code proficiency.', 'SINGLE_CHOICE', 16, TRUE),
('q-cc-2', 'Security & Incident Response', 'During a suspected zero-day credential breach on a production cluster, what is your immediate containment action?', 'Evaluates cybersecurity and incident response experience.', 'SINGLE_CHOICE', 17, TRUE),
('q-cc-3', 'Compliance Governance', 'How do you structure audit evidence collection for annual SOC 2 Type II compliance?', 'Assesses information security governance.', 'SINGLE_CHOICE', 18, TRUE),

-- Data & AI Engineering Section
('q-da-1', 'Big Data Pipelines', 'Which framework do you select for processing multi-terabyte streaming data feeds?', 'Evaluates big data pipeline engineering.', 'SINGLE_CHOICE', 19, TRUE),
('q-da-2', 'Generative AI & LLMs', 'How do you reduce hallucination when deploying an LLM-powered enterprise knowledge agent?', 'Measures GenAI and RAG architecture experience.', 'SINGLE_CHOICE', 20, TRUE),

-- Business, Finance & Operations Section
('q-bf-1', 'Corporate Finance', 'When evaluating a potential M&A target, which valuation technique is most authoritative for long-term cash generation?', 'Evaluates financial modeling and corporate valuation skills.', 'SINGLE_CHOICE', 21, TRUE),
('q-bf-2', 'Supply Chain Management', 'How do you mitigate global supply chain disruptions for critical inventory components?', 'Assesses supply chain and logistics strategy.', 'SINGLE_CHOICE', 22, TRUE),

-- Leadership & Strategy Section
('q-ls-1', 'Product Roadmap', 'When stakeholder demands exceed engineering team capacity by 200%, how do you prioritize the release scope?', 'Evaluates technical product management and prioritization skills.', 'SINGLE_CHOICE', 23, TRUE),
('q-ls-2', 'Team Leadership', 'How do you address persistent technical debt while maintaining sprint feature velocity?', 'Measures engineering leadership and process optimization.', 'SINGLE_CHOICE', 24, TRUE);

-- Options for Questions
INSERT INTO question_options (id, question_id, option_text, display_order) VALUES
-- Options for q-se-1
('opt-q-se-1-a', 'q-se-1', 'Deploy an in-memory Redis caching layer with asynchronous event messaging (Kafka).', 1),
('opt-q-se-1-b', 'q-se-1', 'Increase monolithic application server CPU and memory vertically.', 2),
('opt-q-se-1-c', 'q-se-1', 'Wrap existing synchronous REST endpoints in retry loops.', 3),

-- Options for q-se-2
('opt-q-se-2-a', 'q-se-2', 'Implement read-replicas, composite indexing, and query execution plan optimization.', 1),
('opt-q-se-2-b', 'q-se-2', 'Migrate all tables immediately to a key-value NoSQL store without indexing.', 2),

-- Options for q-se-3
('opt-q-se-3-a', 'q-se-3', 'Apply code splitting, dynamic imports, image optimization, and memoization.', 1),
('opt-q-se-3-b', 'q-se-3', 'Bundle all application assets into a single synchronous JavaScript file.', 2),

-- Options for q-se-4
('opt-q-se-4-a', 'q-se-4', 'Use local SQLite/WatermelonDB storage with background conflict-resolution sync.', 1),
('opt-q-se-4-b', 'q-se-4', 'Disable offline access completely and require constant internet connectivity.', 2),

-- Options for q-se-5
('opt-q-se-5-a', 'q-se-5', 'Implement Blue-Green or Canary deployment strategies with automated health check rollbacks.', 1),
('opt-q-se-5-b', 'q-se-5', 'Manually copy built binaries directly to live application servers during peak hours.', 2),

-- Options for q-cc-1
('opt-q-cc-1-a', 'q-cc-1', 'Write reusable Terraform modules stored in version control with automated CI pipeline execution.', 1),
('opt-q-cc-1-b', 'q-cc-1', 'Manually configure cloud instances through vendor web management consoles.', 2),

-- Options for q-cc-2
('opt-q-cc-2-a', 'q-cc-2', 'Isolate affected nodes, rotate compromised Vault credentials/tokens, and trigger SOC Incident Playbook.', 1),
('opt-q-cc-2-b', 'q-cc-2', 'Restart all cluster nodes without auditing access logs or revoking credentials.', 2),

-- Options for q-cc-3
('opt-q-cc-3-a', 'q-cc-3', 'Automate evidence collection from cloud IAM, GitHub Actions logs, and SIEM monitoring platforms.', 1),
('opt-q-cc-3-b', 'q-cc-3', 'Create manual spreadsheets once per year during auditor site visits.', 2),

-- Options for q-da-1
('opt-q-da-1-a', 'q-da-1', 'Deploy Apache Spark on distributed clusters paired with Kafka event streaming.', 1),
('opt-q-da-1-b', 'q-da-1', 'Write single-threaded Python scripts executing in local memory.', 2),

-- Options for q-da-2
('opt-q-da-2-a', 'q-da-2', 'Implement Retrieval-Augmented Generation (RAG) using vector embeddings and verified domain context.', 1),
('opt-q-da-2-b', 'q-da-2', 'Increase model sampling temperature to maximum.', 2),

-- Options for q-bf-1
('opt-q-bf-1-a', 'q-bf-1', 'Perform a Discounted Cash Flow (DCF) model incorporating sensitivity analysis on WACC.', 1),
('opt-q-bf-1-b', 'q-bf-1', 'Rely solely on historical book value without forecasting future cash flows.', 2),

-- Options for q-bf-2
('opt-q-bf-2-a', 'q-bf-2', 'Implement multi-tier vendor sourcing, safety stock analytics, and automated reorder triggers.', 1),
('opt-q-bf-2-b', 'q-bf-2', 'Maintain single-supplier relationships with zero safety buffer stock.', 2),

-- Options for q-ls-1
('opt-q-ls-1-a', 'q-ls-1', 'Apply RICE scoring (Reach, Impact, Confidence, Effort) aligned with core OKRs and business KPIs.', 1),
('opt-q-ls-1-b', 'q-ls-1', 'Accept feature requests strictly on a first-come, first-served basis.', 2),

-- Options for q-ls-2
('opt-q-ls-2-a', 'q-ls-2', 'Allocate 20% of every sprint capacity specifically to tech debt reduction and refactoring.', 1),
('opt-q-ls-2-b', 'q-ls-2', 'Postpone all technical debt refactoring indefinitely until features are complete.', 2);


-- ============================================================================
-- 5. QUESTION-OPTION-SKILL MAPPINGS (~40 NEW MAPPINGS)
-- ============================================================================
INSERT INTO question_skill_mappings (id, option_id, skill_id, weight) VALUES
-- Mappings for Software & Architecture
('qsm-se-1-a1', 'opt-q-se-1-a', 'redis-caching', 4),
('qsm-se-1-a2', 'opt-q-se-1-a', 'kafka-rabbitmq', 4),
('qsm-se-1-a3', 'opt-q-se-1-a', 'microservices-arch', 5),

('qsm-se-2-a1', 'opt-q-se-2-a', 'sql-advanced', 5),
('qsm-se-2-a2', 'opt-q-se-2-a', 'sql-db', 4),

('qsm-se-3-a1', 'opt-q-se-3-a', 'react', 5),
('qsm-se-3-a2', 'opt-q-se-3-a', 'typescript', 4),

('qsm-se-4-a1', 'opt-q-se-4-a', 'flutter-dart', 4),
('qsm-se-4-a2', 'opt-q-se-4-a', 'react-native', 4),

('qsm-se-5-a1', 'opt-q-se-5-a', 'ci-cd-github-actions', 5),
('qsm-se-5-a2', 'opt-q-se-5-a', 'docker-k8s', 4),

-- Mappings for Cloud & Cybersecurity
('qsm-cc-1-a1', 'opt-q-cc-1-a', 'terraform-iac', 5),
('qsm-cc-1-a2', 'opt-q-cc-1-a', 'cloud-aws', 4),

('qsm-cc-2-a1', 'opt-q-cc-2-a', 'siem-soc-monitoring', 5),
('qsm-cc-2-a2', 'opt-q-cc-2-a', 'vault-secret-mgmt', 4),

('qsm-cc-3-a1', 'opt-q-cc-3-a', 'compliance-iso-soc2', 5),

-- Mappings for Data & AI
('qsm-da-1-a1', 'opt-q-da-1-a', 'apache-spark', 5),
('qsm-da-1-a2', 'opt-q-da-1-a', 'kafka-rabbitmq', 4),

('qsm-da-2-a1', 'opt-q-da-2-a', 'nlp-spacy-huggingface', 5),
('qsm-da-2-a2', 'opt-q-da-2-a', 'python', 4),

-- Mappings for Business & Finance
('qsm-bf-1-a1', 'opt-q-bf-1-a', 'corporate-valuation-dcf', 5),
('qsm-bf-1-a2', 'opt-q-bf-1-a', 'financial-statement-analysis', 4),

('qsm-bf-2-a1', 'opt-q-bf-2-a', 'supply-chain-optimization', 5),
('qsm-bf-2-a2', 'opt-q-bf-2-a', 'lean-six-sigma', 4),

-- Mappings for Leadership & Strategy
('qsm-ls-1-a1', 'opt-q-ls-1-a', 'technical-roadmap-planning', 5),
('qsm-ls-1-a2', 'opt-q-ls-1-a', 'okr-kpi-tracking', 4),

('qsm-ls-2-a1', 'opt-q-ls-2-a', 'leadership-team-building', 5),
('qsm-ls-2-a2', 'opt-q-ls-2-a', 'scrum-agile-framework', 4);


-- ============================================================================
-- 6. ROADMAP TEMPLATES FOR NEW CAREER TRACKS
-- ============================================================================
INSERT INTO roadmap_templates (id, career_id, overall_timeline, default_explanation) VALUES
('rt-backend-eng', 'backend-systems-engineer', '6 Months', 'Master Java/Spring Boot microservices, high-throughput database optimization, event streaming, and distributed system design.'),
('rt-site-reliability', 'site-reliability-engineer', '6 Months', 'Build deep mastery in Linux kernel internals, Kubernetes orchestration, Prometheus metrics, and automated disaster recovery.'),
('rt-data-engineer', 'data-engineer', '6 Months', 'Develop production data pipelines using PySpark, SQL window functions, Snowflake data warehousing, and Kafka streaming.'),
('rt-ai-prompt-llm', 'ai-prompt-llm-engineer', '6 Months', 'Advance from PyTorch basics to building production RAG systems, vector database indexing, and LLM fine-tuning.');

INSERT INTO roadmap_phase_templates (id, roadmap_template_id, phase_order, month_range, phase_title, focus_area, expected_outcome) VALUES
('rpt-be-1', 'rt-backend-eng', 1, 'Months 1-2', 'Java Core & Spring Boot Microservices', 'Java 17, Spring Boot, JPA, REST APIs', 'Build and deploy production-grade REST APIs with Spring Data JPA.'),
('rpt-be-2', 'rt-backend-eng', 2, 'Months 3-4', 'Database Tuning & Distributed Caching', 'Advanced SQL, Redis, Indexing, Transaction Isolation', 'Optimize query performance and implement Redis caching.'),
('rpt-be-3', 'rt-backend-eng', 3, 'Months 5-6', 'Event Streaming & Microservices Architecture', 'Apache Kafka, Docker, Kubernetes, System Design', 'Architect event-driven microservices with Kafka and Kubernetes.'),

('rpt-sre-1', 'rt-site-reliability', 1, 'Months 1-2', 'Linux Internals & Systems Automation', 'Linux Sysadmin, Bash, Systemd, Networking', 'Master Linux server administration and shell automation scripts.'),
('rpt-sre-2', 'rt-site-reliability', 2, 'Months 3-4', 'Container Orchestration & Infrastructure as Code', 'Docker, Kubernetes, Terraform', 'Provision cloud clusters declaratively with Terraform and Kubernetes.'),
('rpt-sre-3', 'rt-site-reliability', 3, 'Months 5-6', 'Observability & Incident Management', 'Prometheus, Grafana, Alertmanager, SLO/SLA', 'Establish 99.99% uptime metrics, alerting, and automated failover.');

INSERT INTO roadmap_phase_goals (phase_id, goal_text, goal_order) VALUES
('rpt-be-1', 'Implement JWT authentication and Spring Security filters.', 1),
('rpt-be-1', 'Design relational schemas with Flyway database migrations.', 2),
('rpt-be-2', 'Configure Redis caching for hot data read optimization.', 1),
('rpt-be-2', 'Write composite database indexes and analyze execution plans.', 2),
('rpt-be-3', 'Build an asynchronous event producer/consumer with Apache Kafka.', 1),
('rpt-be-3', 'Containerize services using Docker multi-stage builds and Kubernetes manifests.', 2),

('rpt-sre-1', 'Configure Linux systemd services and automated cron backups.', 1),
('rpt-sre-1', 'Audit system performance using top, htop, iostat, and netstat.', 2),
('rpt-sre-2', 'Provision multi-region Kubernetes clusters with Terraform.', 1),
('rpt-sre-2', 'Deploy Helm charts and configure ingress routing policies.', 2),
('rpt-sre-3', 'Set up Prometheus scraping and Grafana dashboard alerts.', 1),
('rpt-sre-3', 'Execute simulated disaster recovery failovers and write incident post-mortems.', 2);
