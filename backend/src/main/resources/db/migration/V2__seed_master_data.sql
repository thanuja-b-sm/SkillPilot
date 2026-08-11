-- SkillPilot Master Dataset Seeding Migration (MySQL 8.0)
-- V2__seed_master_data.sql

-- 1. SYSTEM CONFIG
INSERT INTO system_configs (id, technical_weight, questionnaire_weight, essential_skill_penalty, minimum_match_threshold, is_active)
VALUES ('sys-cfg-1', 0.500, 0.350, 0.150, 40, TRUE);

-- 2. MASTER SKILLS
INSERT INTO skills (id, name, category, description, is_active) VALUES
('python', 'Python Programming', 'Technical', 'Core Python programming, OOP, and scientific computing stack.', TRUE),
('typescript', 'TypeScript / JavaScript', 'Technical', 'Modern JS/TS syntax, async patterns, and browser/Node execution.', TRUE),
('machine-learning', 'Machine Learning & AI', 'Technical', 'Supervised/unsupervised algorithms, scikit-learn, and model tuning.', TRUE),
('sql-db', 'SQL & Database Architecture', 'Technical', 'Relational database schema design, queries, indexing, and normalization.', TRUE),
('cloud-aws', 'Cloud Computing (AWS/GCP)', 'Technical', 'Enterprise cloud services, IAM, EC2/S3, and serverless compute.', TRUE),
('docker-k8s', 'Docker & Kubernetes', 'Tools & Frameworks', 'Containerization, microservice orchestration, and deployment specs.', TRUE),
('git', 'Git & CI/CD Pipelines', 'Tools & Frameworks', 'Version control branching workflows, GitHub Actions, and deployment automation.', TRUE),
('react', 'React / Frontend Architecture', 'Technical', 'Component architecture, state management, hooks, and single-page apps.', TRUE),
('data-analytics', 'Data Visualization & Analytics', 'Technical', 'Pandas, data cleaning, statistical plotting, and executive reporting.', TRUE),
('cybersecurity', 'Network Security & Risk Mgmt', 'Domain Knowledge', 'Threat modeling, zero-trust network policy, and compliance auditing.', TRUE),
('product-mgmt', 'Product Strategy & Roadmap', 'Domain Knowledge', 'Market discovery, feature prioritization, and product lifecycle management.', TRUE),
('system-design', 'Distributed System Design', 'Technical', 'High-availability architectures, caching strategies, and load balancing.', TRUE),
('agile', 'Agile & Scrum Methodologies', 'Domain Knowledge', 'Sprint planning, backlog refinement, and team velocity metrics.', TRUE),
('communication', 'Stakeholder Communication', 'Soft Skills', 'Technical writing, executive presentations, and cross-functional alignment.', TRUE),
('problem-solving', 'Critical Problem Solving', 'Soft Skills', 'Algorithmic logic decomposition, debugging, and root-cause analysis.', TRUE),
('ux-design', 'UX Research & Wireframing', 'Technical', 'User journey mapping, low-fidelity wireframing, and usability testing.', TRUE),
('deep-learning', 'Deep Learning & PyTorch', 'Technical', 'Neural network architectures, PyTorch tensors, CNNs, and Transformers.', TRUE),
('devops', 'Infrastructure as Code (Terraform)', 'Tools & Frameworks', 'Automated cloud provisioning with Terraform and declarative manifests.', TRUE),

-- Diverse Non-IT Skills
('patient-care', 'Patient Care & Clinical Nursing', 'Domain Knowledge', 'Clinical nursing protocols, bedside care, triage, and patient monitoring.', TRUE),
('medical-compliance', 'Healthcare Regulation & HIPAA', 'Domain Knowledge', 'Patient privacy regulations, HIPAA compliance, and medical audit standards.', TRUE),
('financial-modeling', 'Financial Valuation & Modeling', 'Technical', 'DCF valuation, LBO models, financial statements, and capital forecasting.', TRUE),
('corporate-finance', 'Corporate Finance & Audit', 'Domain Knowledge', 'Balance sheet audit, working capital management, and corporate tax compliance.', TRUE),
('clean-energy', 'Renewable Energy & Solar Engineering', 'Technical', 'Solar PV arrays, wind turbine microgrids, and clean power grid storage.', TRUE),
('cad-engineering', 'CAD Engineering Design', 'Tools & Frameworks', 'AutoCAD 3D modeling, structural engineering blueprints, and drafting.', TRUE),
('digital-marketing', 'Performance Marketing & SEO/SEM', 'Technical', 'Paid search acquisition, Google Ads, SEO technical audits, and CAC optimization.', TRUE),
('brand-strategy', 'Brand Positioning & Growth', 'Soft Skills', 'Strategic messaging, brand identity, customer personas, and retention.', TRUE),
('figma-ui', 'Figma UI/UX & Design Systems', 'Tools & Frameworks', 'Component design tokens, auto-layout, interactive prototypes, and design systems.', TRUE);

-- 3. CAREERS
INSERT INTO careers (id, title, category, description, average_salary, growth_rate, demand_level, is_active) VALUES
('ai-software-engineer', 'AI & Machine Learning Engineer', 'Artificial Intelligence', 'Designs, builds, and deploys intelligent software systems, machine learning models, and GenAI pipeline services.', '$145,000 - $190,000 / yr', '+32% (Very High Growth)', 'Very High', TRUE),
('cloud-architect', 'Cloud Solutions Architect', 'Cloud & Infrastructure', 'Architects scalable, reliable, and secure enterprise cloud infrastructure across AWS, Google Cloud, and Azure.', '$150,000 - $195,000 / yr', '+24% (Strong Growth)', 'Very High', TRUE),
('full-stack-developer', 'Senior Full-Stack Engineer', 'Software Engineering', 'Engineers full end-to-end web architectures, high-performance APIs, and responsive React/Node frontends.', '$120,000 - $165,000 / yr', '+22% (Steady Demand)', 'High', TRUE),
('data-scientist', 'Lead Data Scientist & Analytics Lead', 'Data & Analytics', 'Transforms raw corporate data into actionable predictive insights, statistical models, and executive dashboards.', '$130,000 - $175,000 / yr', '+28% (High Growth)', 'Very High', TRUE),
('cybersecurity-analyst', 'Cybersecurity & Information Security Officer', 'Cybersecurity', 'Protects enterprise digital assets, conducts threat audits, configures zero-trust networks, and manages risk compliance.', '$125,000 - $170,000 / yr', '+31% (Critical Demand)', 'Very High', TRUE),
('product-manager', 'Technical Product Manager', 'Product & Management', 'Bridges engineering teams, business executives, and end-users to discover product market fit and execute feature roadmaps.', '$135,000 - $180,000 / yr', '+19% (High Value)', 'High', TRUE),

-- Non-IT Career Tracks
('healthcare-clinical-manager', 'Clinical Operations Lead & Healthcare Director', 'Healthcare & Medicine', 'Manages hospital patient care workflows, clinical staff operations, medical safety protocols, and healthcare compliance.', '$115,000 - $160,000 / yr', '+28% (Very High Growth)', 'Very High', TRUE),
('financial-investment-analyst', 'Senior Financial Analyst & Investment Strategist', 'Business & Finance', 'Evaluates corporate balance sheets, capital investment portfolios, market valuations, and risk mitigation strategies.', '$125,000 - $175,000 / yr', '+21% (Strong Demand)', 'High', TRUE),
('clean-tech-engineer', 'Renewable Energy & Clean Tech Systems Engineer', 'Engineering & Energy', 'Designs renewable solar/wind energy installations, microgrid power infrastructure, and energy efficiency solutions.', '$110,000 - $155,000 / yr', '+35% (Exceptional Growth)', 'Very High', TRUE),
('digital-growth-director', 'Digital Marketing & Growth Strategy Director', 'Marketing & Media', 'Spearheads multi-channel digital acquisition campaigns, brand positioning, customer retention, and marketing analytics.', '$118,000 - $165,000 / yr', '+23% (High Growth)', 'High', TRUE),
('ui-ux-design-lead', 'Lead Product UI/UX Designer', 'Design & Creative', 'Crafts intuitive user experiences, interactive prototypes, design systems, and visual interfaces across multi-platform apps.', '$120,000 - $168,000 / yr', '+26% (High Demand)', 'Very High', TRUE);

-- 3A. CAREER TYPICAL ROLES
INSERT INTO career_typical_roles (career_id, role_name) VALUES
('ai-software-engineer', 'AI Systems Engineer'),
('ai-software-engineer', 'MLOps Specialist'),
('ai-software-engineer', 'LLM Application Developer'),
('cloud-architect', 'Senior Cloud Architect'),
('cloud-architect', 'Enterprise Solutions Specialist'),
('cloud-architect', 'Infrastructure Lead'),
('full-stack-developer', 'Full-Stack Developer'),
('full-stack-developer', 'Frontend Lead'),
('full-stack-developer', 'API Architect'),
('data-scientist', 'Quantitative Analyst'),
('data-scientist', 'Data Scientist'),
('data-scientist', 'BI Strategy Lead'),
('cybersecurity-analyst', 'Security Analyst'),
('cybersecurity-analyst', 'Penetration Tester'),
('cybersecurity-analyst', 'SOC Engineer'),
('product-manager', 'Product Manager'),
('product-manager', 'Group Product Manager'),
('product-manager', 'Product Owner'),
('healthcare-clinical-manager', 'Clinical Operations Director'),
('healthcare-clinical-manager', 'Nurse Manager'),
('healthcare-clinical-manager', 'Healthcare Administrator'),
('financial-investment-analyst', 'Investment Analyst'),
('financial-investment-analyst', 'Financial Controller'),
('financial-investment-analyst', 'Corporate Finance Lead'),
('clean-tech-engineer', 'Solar Systems Engineer'),
('clean-tech-engineer', 'Energy Storage Specialist'),
('clean-tech-engineer', 'Environmental Infrastructure Lead'),
('digital-growth-director', 'Growth Marketing Director'),
('digital-growth-director', 'Head of Paid Acquisition'),
('digital-growth-director', 'Brand Strategist'),
('ui-ux-design-lead', 'Lead Product Designer'),
('ui-ux-design-lead', 'UX Researcher'),
('ui-ux-design-lead', 'Design System Architect');

-- 3B. CAREER PREREQUISITES
INSERT INTO career_prerequisites (career_id, prerequisite) VALUES
('ai-software-engineer', 'Computer Science Fundamentals'),
('ai-software-engineer', 'Linear Algebra & Statistics'),
('cloud-architect', 'Networking Basics'),
('cloud-architect', 'Operating Systems Core'),
('full-stack-developer', 'HTML/CSS Standard'),
('full-stack-developer', 'JavaScript ES6+'),
('data-scientist', 'Probability & Statistics'),
('data-scientist', 'Calculus'),
('cybersecurity-analyst', 'Computer Networking'),
('cybersecurity-analyst', 'Linux Administration'),
('product-manager', 'Business Fundamentals'),
('product-manager', 'Agile Basics'),
('healthcare-clinical-manager', 'Health Sciences Degree'),
('healthcare-clinical-manager', 'Clinical Practice License'),
('financial-investment-analyst', 'Finance / Economics Degree'),
('financial-investment-analyst', 'Financial Accounting Basics'),
('clean-tech-engineer', 'Electrical / Mechanical Engineering Degree'),
('clean-tech-engineer', 'Thermodynamics Core'),
('digital-growth-director', 'Marketing or Business Degree'),
('digital-growth-director', 'Digital Media Basics'),
('ui-ux-design-lead', 'Visual Design Principles'),
('ui-ux-design-lead', 'User Research Fundamentals');

-- 3C. CAREER SKILL REQUIREMENTS
INSERT INTO career_skill_requirements (id, career_id, skill_id, required_level, is_essential) VALUES
('csr-1', 'ai-software-engineer', 'python', 5, TRUE),
('csr-2', 'ai-software-engineer', 'machine-learning', 4, TRUE),
('csr-3', 'ai-software-engineer', 'deep-learning', 4, FALSE),
('csr-4', 'ai-software-engineer', 'cloud-aws', 3, TRUE),
('csr-5', 'ai-software-engineer', 'docker-k8s', 3, FALSE),
('csr-6', 'ai-software-engineer', 'problem-solving', 4, TRUE),

('csr-7', 'cloud-architect', 'cloud-aws', 5, TRUE),
('csr-8', 'cloud-architect', 'system-design', 4, TRUE),
('csr-9', 'cloud-architect', 'docker-k8s', 4, TRUE),
('csr-10', 'cloud-architect', 'devops', 4, FALSE),
('csr-11', 'cloud-architect', 'cybersecurity', 3, FALSE),
('csr-12', 'cloud-architect', 'communication', 4, TRUE),

('csr-13', 'full-stack-developer', 'typescript', 5, TRUE),
('csr-14', 'full-stack-developer', 'react', 4, TRUE),
('csr-15', 'full-stack-developer', 'sql-db', 4, TRUE),
('csr-16', 'full-stack-developer', 'git', 3, TRUE),
('csr-17', 'full-stack-developer', 'problem-solving', 4, TRUE),

('csr-18', 'data-scientist', 'python', 4, TRUE),
('csr-19', 'data-scientist', 'data-analytics', 5, TRUE),
('csr-20', 'data-scientist', 'sql-db', 4, TRUE),
('csr-21', 'data-scientist', 'machine-learning', 3, FALSE),
('csr-22', 'data-scientist', 'communication', 4, TRUE),

('csr-23', 'cybersecurity-analyst', 'cybersecurity', 5, TRUE),
('csr-24', 'cybersecurity-analyst', 'cloud-aws', 3, TRUE),
('csr-25', 'cybersecurity-analyst', 'python', 3, FALSE),
('csr-26', 'cybersecurity-analyst', 'problem-solving', 5, TRUE),

('csr-27', 'product-manager', 'product-mgmt', 5, TRUE),
('csr-28', 'product-manager', 'agile', 4, TRUE),
('csr-29', 'product-manager', 'communication', 5, TRUE),
('csr-30', 'product-manager', 'ux-design', 3, FALSE),
('csr-31', 'product-manager', 'data-analytics', 3, FALSE);

-- 4. QUESTIONNAIRE SURVEY ITEMS
INSERT INTO questions (id, section, question, description, type, display_order, is_active) VALUES
('q1', 'Career Interests & Domain Focus', 'Which primary domain in technology aligns best with your professional curiosity?', 'Select the field where you find solving real-world problems most engaging.', 'single', 1, TRUE),
('q2', 'Work Preference & Problem Solving Style', 'What type of daily problem-solving activities energize you the most?', 'Choose all statements that match your natural working habits.', 'multiple', 2, TRUE),
('q3', 'Current Coding Experience & Fundamentals', 'How comfortable are you with programming languages (Python, JavaScript/TypeScript, SQL)?', 'Select your self-evaluated baseline level.', 'scale', 3, TRUE),
('q4', 'Learning Commitment & Timeline', 'How many dedicated hours per week can you allocate toward your career development roadmap?', 'This will help calibrate milestone pacing and study focus.', 'single', 4, TRUE);

-- 4A. QUESTION OPTIONS
INSERT INTO question_options (id, question_id, option_text, display_order) VALUES
('q1-ai', 'q1', 'Artificial Intelligence, Machine Learning & Intelligent Automation', 1),
('q1-cloud', 'q1', 'Cloud Infrastructure, Enterprise Systems & System Architecture', 2),
('q1-web', 'q1', 'Full-Stack Web Development, Modern APIs & User Interfaces', 3),
('q1-data', 'q1', 'Data Analytics, Business Intelligence & Statistical Discovery', 4),
('q1-security', 'q1', 'Cybersecurity, Information Assurance & Risk Compliance', 5),

('q2-coding', 'q2', 'Writing clean code, debugging complex algorithmic logic, building feature modules', 1),
('q2-architecture', 'q2', 'Designing high-level system components, server layouts, and infrastructure scalability', 2),
('q2-people', 'q2', 'Facilitating team discussions, presenting project roadmaps, and managing priorities', 3),
('q2-data', 'q2', 'Analyzing datasets, extracting mathematical insights, creating visual charts', 4),

('q3-1', 'q3', 'Beginner – Basic knowledge of syntax, simple loops, and elementary scripts', 1),
('q3-2', 'q3', 'Intermediate – Able to build functional web apps, write custom queries, and use standard libraries', 2),
('q3-3', 'q3', 'Advanced – Proficient with async patterns, frameworks, DB optimization, and clean architectural design', 3),

('q4-light', 'q4', '5 – 10 hours/week (Steady background pacing)', 1),
('q4-med', 'q4', '10 – 20 hours/week (Focused active acceleration)', 2),
('q4-high', 'q4', '20+ hours/week (Full-time intensive boot-camp focus)', 3);

-- 4B. QUESTION OPTION SKILL MAPPINGS
INSERT INTO question_skill_mappings (id, option_id, skill_id, weight) VALUES
('qsm-1', 'q1-ai', 'python', 4),
('qsm-2', 'q1-ai', 'machine-learning', 5),
('qsm-3', 'q1-ai', 'deep-learning', 4),
('qsm-4', 'q1-cloud', 'cloud-aws', 5),
('qsm-5', 'q1-cloud', 'system-design', 4),
('qsm-6', 'q1-cloud', 'docker-k8s', 4),
('qsm-7', 'q1-web', 'typescript', 5),
('qsm-8', 'q1-web', 'react', 5),
('qsm-9', 'q1-web', 'sql-db', 4),
('qsm-10', 'q1-data', 'data-analytics', 5),
('qsm-11', 'q1-data', 'sql-db', 4),
('qsm-12', 'q1-data', 'python', 3),
('qsm-13', 'q1-security', 'cybersecurity', 5),
('qsm-14', 'q1-security', 'cloud-aws', 3),
('qsm-15', 'q1-security', 'problem-solving', 4),

('qsm-16', 'q2-coding', 'python', 3),
('qsm-17', 'q2-coding', 'typescript', 3),
('qsm-18', 'q2-coding', 'problem-solving', 4),
('qsm-19', 'q2-architecture', 'system-design', 5),
('qsm-20', 'q2-architecture', 'cloud-aws', 4),
('qsm-21', 'q2-architecture', 'devops', 4),
('qsm-22', 'q2-people', 'communication', 5),
('qsm-23', 'q2-people', 'product-mgmt', 4),
('qsm-24', 'q2-people', 'agile', 4),
('qsm-25', 'q2-data', 'data-analytics', 5),
('qsm-26', 'q2-data', 'sql-db', 4),

('qsm-27', 'q3-1', 'python', 1),
('qsm-28', 'q3-1', 'typescript', 1),
('qsm-29', 'q3-2', 'python', 3),
('qsm-30', 'q3-2', 'typescript', 3),
('qsm-31', 'q3-2', 'sql-db', 3),
('qsm-32', 'q3-3', 'python', 5),
('qsm-33', 'q3-3', 'typescript', 5),
('qsm-34', 'q3-3', 'sql-db', 4);

-- 5. ROADMAP TEMPLATES
INSERT INTO roadmap_templates (id, career_id, overall_timeline, default_explanation) VALUES
('ai-software-engineer', 'ai-software-engineer', '6 Months (Phased 4-Stage Plan)', 'AI Analysis Note: The system identified high foundational affinity in Python and Problem Solving. Primary acceleration focus is bridging PyTorch deep learning modules and cloud model hosting.');

INSERT INTO roadmap_phase_templates (id, roadmap_template_id, phase_order, month_range, phase_title, focus_area, expected_outcome) VALUES
('m1', 'ai-software-engineer', 1, 'Months 1 – 2', 'Advanced Python & Math Foundations', 'Core Language Depth, NumPy & Linear Algebra', 'Fluency in Python data structures and mathematical vector operations.'),
('m2', 'ai-software-engineer', 2, 'Months 3 – 4', 'Machine Learning Algorithms & Scikit-Learn', 'Supervised/Unsupervised Learning & Model Tuning', 'Functional ML pipeline capable of serving predictions via clean API.'),
('m3', 'ai-software-engineer', 3, 'Months 5 – 6', 'Deep Learning & Neural Architectures', 'PyTorch, Transformers & GenAI Fine-Tuning', 'Portfolio project featuring custom RAG pipeline and fine-tuned LLM.'),
('m4', 'ai-software-engineer', 4, 'Months 7 – 8', 'MLOps, Cloud Deployment & Production Hardening', 'AWS Sagemaker, Monitoring & CI/CD', 'Job-ready portfolio and live cloud-hosted AI service.');

INSERT INTO roadmap_phase_goals (phase_id, goal_text, goal_order) VALUES
('m1', 'Master Python object-oriented patterns and memory optimization', 1),
('m1', 'Complete NumPy & Pandas data manipulation projects', 2),
('m1', 'Review matrix calculus and gradient descent mathematics', 3),
('m2', 'Build end-to-end regression and classification pipelines', 1),
('m2', 'Implement cross-validation, hyperparameter grid search, and metrics', 2),
('m2', 'Deploy first ML REST API with FastAPI and Docker', 3),
('m3', 'Construct CNNs and Transformers from scratch in PyTorch', 1),
('m3', 'Experiment with HuggingFace model fine-tuning and LoRA techniques', 2),
('m3', 'Implement Retrieval-Augmented Generation (RAG) with vector databases', 3),
('m4', 'Deploy ML models on Cloud infrastructure with automated scaling', 1),
('m4', 'Set up MLflow model registry and drift monitoring alerts', 2),
('m4', 'Conduct mock technical interviews and optimize GitHub portfolio', 3);
