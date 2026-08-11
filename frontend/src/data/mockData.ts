import { Career, QuestionItem, UserProfile, SystemConfig, CareerRoadmap } from '../types';

export const INITIAL_SKILLS = [
  { id: 'python', name: 'Python Programming', category: 'Technical' },
  { id: 'typescript', name: 'TypeScript / JavaScript', category: 'Technical' },
  { id: 'machine-learning', name: 'Machine Learning & AI', category: 'Technical' },
  { id: 'sql-db', name: 'SQL & Database Architecture', category: 'Technical' },
  { id: 'cloud-aws', name: 'Cloud Computing (AWS/GCP)', category: 'Technical' },
  { id: 'docker-k8s', name: 'Docker & Kubernetes', category: 'Tools & Frameworks' },
  { id: 'git', name: 'Git & CI/CD Pipelines', category: 'Tools & Frameworks' },
  { id: 'react', name: 'React / Frontend Architecture', category: 'Technical' },
  { id: 'data-analytics', name: 'Data Visualization & Analytics', category: 'Technical' },
  { id: 'cybersecurity', name: 'Network Security & Risk Mgmt', category: 'Domain Knowledge' },
  { id: 'product-mgmt', name: 'Product Strategy & Roadmap', category: 'Domain Knowledge' },
  { id: 'system-design', name: 'Distributed System Design', category: 'Technical' },
  { id: 'agile', name: 'Agile & Scrum Methodologies', category: 'Domain Knowledge' },
  { id: 'communication', name: 'Stakeholder Communication', category: 'Soft Skills' },
  { id: 'problem-solving', name: 'Critical Problem Solving', category: 'Soft Skills' },
  { id: 'ux-design', name: 'UX Research & Wireframing', category: 'Technical' },
  { id: 'deep-learning', name: 'Deep Learning & PyTorch', category: 'Technical' },
  { id: 'devops', name: 'Infrastructure as Code (Terraform)', category: 'Tools & Frameworks' },

  // Diverse Non-IT Fields
  { id: 'patient-care', name: 'Patient Care & Clinical Nursing', category: 'Domain Knowledge' },
  { id: 'medical-compliance', name: 'Healthcare Regulation & HIPAA', category: 'Domain Knowledge' },
  { id: 'financial-modeling', name: 'Financial Valuation & Modeling', category: 'Technical' },
  { id: 'corporate-finance', name: 'Corporate Finance & Audit', category: 'Domain Knowledge' },
  { id: 'clean-energy', name: 'Renewable Energy & Solar Engineering', category: 'Technical' },
  { id: 'cad-engineering', name: 'CAD Engineering Design', category: 'Tools & Frameworks' },
  { id: 'digital-marketing', name: 'Performance Marketing & SEO/SEM', category: 'Technical' },
  { id: 'brand-strategy', name: 'Brand Positioning & Growth', category: 'Soft Skills' },
  { id: 'figma-ui', name: 'Figma UI/UX & Design Systems', category: 'Tools & Frameworks' }
] as const;

export const INITIAL_CAREERS: Career[] = [
  {
    id: 'ai-software-engineer',
    title: 'AI & Machine Learning Engineer',
    category: 'Artificial Intelligence',
    description: 'Designs, builds, and deploys intelligent software systems, machine learning models, and GenAI pipeline services.',
    averageSalary: '$145,000 - $190,000 / yr',
    growthRate: '+32% (Very High Growth)',
    demandLevel: 'Very High',
    typicalRoles: ['AI Systems Engineer', 'MLOps Specialist', 'LLM Application Developer'],
    recommendedPrerequisites: ['Computer Science Fundamentals', 'Linear Algebra & Statistics'],
    requiredSkills: [
      { skillId: 'python', skillName: 'Python Programming', category: 'Technical', requiredLevel: 5, isEssential: true },
      { skillId: 'machine-learning', skillName: 'Machine Learning & AI', category: 'Technical', requiredLevel: 4, isEssential: true },
      { skillId: 'deep-learning', skillName: 'Deep Learning & PyTorch', category: 'Technical', requiredLevel: 4, isEssential: false },
      { skillId: 'cloud-aws', skillName: 'Cloud Computing (AWS/GCP)', category: 'Technical', requiredLevel: 3, isEssential: true },
      { skillId: 'docker-k8s', skillName: 'Docker & Kubernetes', category: 'Tools & Frameworks', requiredLevel: 3, isEssential: false },
      { skillId: 'problem-solving', skillName: 'Critical Problem Solving', category: 'Soft Skills', requiredLevel: 4, isEssential: true }
    ]
  },
  {
    id: 'cloud-architect',
    title: 'Cloud Solutions Architect',
    category: 'Cloud & Infrastructure',
    description: 'Architects scalable, reliable, and secure enterprise cloud infrastructure across AWS, Google Cloud, and Azure.',
    averageSalary: '$150,000 - $195,000 / yr',
    growthRate: '+24% (Strong Growth)',
    demandLevel: 'Very High',
    typicalRoles: ['Senior Cloud Architect', 'Enterprise Solutions Specialist', 'Infrastructure Lead'],
    recommendedPrerequisites: ['Networking Basics', 'Operating Systems Core'],
    requiredSkills: [
      { skillId: 'cloud-aws', skillName: 'Cloud Computing (AWS/GCP)', category: 'Technical', requiredLevel: 5, isEssential: true },
      { skillId: 'system-design', skillName: 'Distributed System Design', category: 'Technical', requiredLevel: 4, isEssential: true },
      { skillId: 'docker-k8s', skillName: 'Docker & Kubernetes', category: 'Tools & Frameworks', requiredLevel: 4, isEssential: true },
      { skillId: 'devops', skillName: 'Infrastructure as Code (Terraform)', category: 'Tools & Frameworks', requiredLevel: 4, isEssential: false },
      { skillId: 'cybersecurity', skillName: 'Network Security & Risk Mgmt', category: 'Domain Knowledge', requiredLevel: 3, isEssential: false },
      { skillId: 'communication', skillName: 'Stakeholder Communication', category: 'Soft Skills', requiredLevel: 4, isEssential: true }
    ]
  },
  {
    id: 'full-stack-developer',
    title: 'Senior Full-Stack Engineer',
    category: 'Software Engineering',
    description: 'Engineers full end-to-end web architectures, high-performance APIs, and responsive React/Node frontends.',
    averageSalary: '$120,000 - $165,000 / yr',
    growthRate: '+22% (Steady Demand)',
    demandLevel: 'High',
    typicalRoles: ['Full-Stack Developer', 'Frontend Lead', 'API Architect'],
    recommendedPrerequisites: ['HTML/CSS Standard', 'JavaScript ES6+'],
    requiredSkills: [
      { skillId: 'typescript', skillName: 'TypeScript / JavaScript', category: 'Technical', requiredLevel: 5, isEssential: true },
      { skillId: 'react', skillName: 'React / Frontend Architecture', category: 'Technical', requiredLevel: 4, isEssential: true },
      { skillId: 'sql-db', skillName: 'SQL & Database Architecture', category: 'Technical', requiredLevel: 4, isEssential: true },
      { skillId: 'git', skillName: 'Git & CI/CD Pipelines', category: 'Tools & Frameworks', requiredLevel: 3, isEssential: true },
      { skillId: 'problem-solving', skillName: 'Critical Problem Solving', category: 'Soft Skills', requiredLevel: 4, isEssential: true }
    ]
  },
  {
    id: 'data-scientist',
    title: 'Lead Data Scientist & Analytics Lead',
    category: 'Data & Analytics',
    description: 'Transforms raw corporate data into actionable predictive insights, statistical models, and executive dashboards.',
    averageSalary: '$130,000 - $175,000 / yr',
    growthRate: '+28% (High Growth)',
    demandLevel: 'Very High',
    typicalRoles: ['Quantitative Analyst', 'Data Scientist', 'BI Strategy Lead'],
    recommendedPrerequisites: ['Probability & Statistics', 'Calculus'],
    requiredSkills: [
      { skillId: 'python', skillName: 'Python Programming', category: 'Technical', requiredLevel: 4, isEssential: true },
      { skillId: 'data-analytics', skillName: 'Data Visualization & Analytics', category: 'Technical', requiredLevel: 5, isEssential: true },
      { skillId: 'sql-db', skillName: 'SQL & Database Architecture', category: 'Technical', requiredLevel: 4, isEssential: true },
      { skillId: 'machine-learning', skillName: 'Machine Learning & AI', category: 'Technical', requiredLevel: 3, isEssential: false },
      { skillId: 'communication', skillName: 'Stakeholder Communication', category: 'Soft Skills', requiredLevel: 4, isEssential: true }
    ]
  },
  {
    id: 'cybersecurity-analyst',
    title: 'Cybersecurity & Information Security Officer',
    category: 'Cybersecurity',
    description: 'Protects enterprise digital assets, conducts threat audits, configures zero-trust networks, and manages risk compliance.',
    averageSalary: '$125,000 - $170,000 / yr',
    growthRate: '+31% (Critical Demand)',
    demandLevel: 'Very High',
    typicalRoles: ['Security Analyst', 'Penetration Tester', 'SOC Engineer'],
    recommendedPrerequisites: ['Computer Networking', 'Linux Administration'],
    requiredSkills: [
      { skillId: 'cybersecurity', skillName: 'Network Security & Risk Mgmt', category: 'Domain Knowledge', requiredLevel: 5, isEssential: true },
      { skillId: 'cloud-aws', skillName: 'Cloud Computing (AWS/GCP)', category: 'Technical', requiredLevel: 3, isEssential: true },
      { skillId: 'python', skillName: 'Python Programming', category: 'Technical', requiredLevel: 3, isEssential: false },
      { skillId: 'problem-solving', skillName: 'Critical Problem Solving', category: 'Soft Skills', requiredLevel: 5, isEssential: true }
    ]
  },
  {
    id: 'product-manager',
    title: 'Technical Product Manager',
    category: 'Product & Management',
    description: 'Bridges engineering teams, business executives, and end-users to discover product market fit and execute feature roadmaps.',
    averageSalary: '$135,000 - $180,000 / yr',
    growthRate: '+19% (High Value)',
    demandLevel: 'High',
    typicalRoles: ['Product Manager', 'Group Product Manager', 'Product Owner'],
    recommendedPrerequisites: ['Business Fundamentals', 'Agile Basics'],
    requiredSkills: [
      { skillId: 'product-mgmt', skillName: 'Product Strategy & Roadmap', category: 'Domain Knowledge', requiredLevel: 5, isEssential: true },
      { skillId: 'agile', skillName: 'Agile & Scrum Methodologies', category: 'Domain Knowledge', requiredLevel: 4, isEssential: true },
      { skillId: 'communication', skillName: 'Stakeholder Communication', category: 'Soft Skills', requiredLevel: 5, isEssential: true },
      { skillId: 'ux-design', skillName: 'UX Research & Wireframing', category: 'Technical', requiredLevel: 3, isEssential: false },
      { skillId: 'data-analytics', skillName: 'Data Visualization & Analytics', category: 'Technical', requiredLevel: 3, isEssential: false }
    ]
  },

  // Diverse Non-IT Career Tracks
  {
    id: 'healthcare-clinical-manager',
    title: 'Clinical Operations Lead & Healthcare Director',
    category: 'Healthcare & Medicine',
    description: 'Manages hospital patient care workflows, clinical staff operations, medical safety protocols, and healthcare compliance.',
    averageSalary: '$115,000 - $160,000 / yr',
    growthRate: '+28% (Very High Growth)',
    demandLevel: 'Very High',
    typicalRoles: ['Clinical Operations Director', 'Nurse Manager', 'Healthcare Administrator'],
    recommendedPrerequisites: ['Health Sciences Degree', 'Clinical Practice License'],
    requiredSkills: [
      { skillId: 'patient-care', skillName: 'Patient Care & Clinical Nursing', category: 'Domain Knowledge', requiredLevel: 5, isEssential: true },
      { skillId: 'medical-compliance', skillName: 'Healthcare Regulation & HIPAA', category: 'Domain Knowledge', requiredLevel: 4, isEssential: true },
      { skillId: 'communication', skillName: 'Stakeholder Communication', category: 'Soft Skills', requiredLevel: 5, isEssential: true },
      { skillId: 'problem-solving', skillName: 'Critical Problem Solving', category: 'Soft Skills', requiredLevel: 4, isEssential: true }
    ]
  },
  {
    id: 'financial-investment-analyst',
    title: 'Senior Financial Analyst & Investment Strategist',
    category: 'Business & Finance',
    description: 'Evaluates corporate balance sheets, capital investment portfolios, market valuations, and risk mitigation strategies.',
    averageSalary: '$125,000 - $175,000 / yr',
    growthRate: '+21% (Strong Demand)',
    demandLevel: 'High',
    typicalRoles: ['Investment Analyst', 'Financial Controller', 'Corporate Finance Lead'],
    recommendedPrerequisites: ['Finance / Economics Degree', 'Financial Accounting Basics'],
    requiredSkills: [
      { skillId: 'financial-modeling', skillName: 'Financial Valuation & Modeling', category: 'Technical', requiredLevel: 5, isEssential: true },
      { skillId: 'corporate-finance', skillName: 'Corporate Finance & Audit', category: 'Domain Knowledge', requiredLevel: 4, isEssential: true },
      { skillId: 'data-analytics', skillName: 'Data Visualization & Analytics', category: 'Technical', requiredLevel: 4, isEssential: true },
      { skillId: 'communication', skillName: 'Stakeholder Communication', category: 'Soft Skills', requiredLevel: 4, isEssential: true }
    ]
  },
  {
    id: 'clean-tech-engineer',
    title: 'Renewable Energy & Clean Tech Systems Engineer',
    category: 'Engineering & Energy',
    description: 'Designs renewable solar/wind energy installations, microgrid power infrastructure, and energy efficiency solutions.',
    averageSalary: '$110,000 - $155,000 / yr',
    growthRate: '+35% (Exceptional Growth)',
    demandLevel: 'Very High',
    typicalRoles: ['Solar Systems Engineer', 'Energy Storage Specialist', 'Environmental Infrastructure Lead'],
    recommendedPrerequisites: ['Electrical / Mechanical Engineering Degree', 'Thermodynamics Core'],
    requiredSkills: [
      { skillId: 'clean-energy', skillName: 'Renewable Energy & Solar Engineering', category: 'Technical', requiredLevel: 5, isEssential: true },
      { skillId: 'cad-engineering', skillName: 'CAD Engineering Design', category: 'Tools & Frameworks', requiredLevel: 4, isEssential: true },
      { skillId: 'problem-solving', skillName: 'Critical Problem Solving', category: 'Soft Skills', requiredLevel: 5, isEssential: true }
    ]
  },
  {
    id: 'digital-growth-director',
    title: 'Digital Marketing & Growth Strategy Director',
    category: 'Marketing & Media',
    description: 'Spearheads multi-channel digital acquisition campaigns, brand positioning, customer retention, and marketing analytics.',
    averageSalary: '$118,000 - $165,000 / yr',
    growthRate: '+23% (High Growth)',
    demandLevel: 'High',
    typicalRoles: ['Growth Marketing Director', 'Head of Paid Acquisition', 'Brand Strategist'],
    recommendedPrerequisites: ['Marketing or Business Degree', 'Digital Media Basics'],
    requiredSkills: [
      { skillId: 'digital-marketing', skillName: 'Performance Marketing & SEO/SEM', category: 'Technical', requiredLevel: 5, isEssential: true },
      { skillId: 'brand-strategy', skillName: 'Brand Positioning & Growth', category: 'Soft Skills', requiredLevel: 4, isEssential: true },
      { skillId: 'data-analytics', skillName: 'Data Visualization & Analytics', category: 'Technical', requiredLevel: 4, isEssential: true },
      { skillId: 'communication', skillName: 'Stakeholder Communication', category: 'Soft Skills', requiredLevel: 5, isEssential: true }
    ]
  },
  {
    id: 'ui-ux-design-lead',
    title: 'Lead Product UI/UX Designer',
    category: 'Design & Creative',
    description: 'Crafts intuitive user experiences, interactive prototypes, design systems, and visual interfaces across multi-platform apps.',
    averageSalary: '$120,000 - $168,000 / yr',
    growthRate: '+26% (High Demand)',
    demandLevel: 'Very High',
    typicalRoles: ['Lead Product Designer', 'UX Researcher', 'Design System Architect'],
    recommendedPrerequisites: ['Visual Design Principles', 'User Research Fundamentals'],
    requiredSkills: [
      { skillId: 'figma-ui', skillName: 'Figma UI/UX & Design Systems', category: 'Tools & Frameworks', requiredLevel: 5, isEssential: true },
      { skillId: 'ux-design', skillName: 'UX Research & Wireframing', category: 'Technical', requiredLevel: 5, isEssential: true },
      { skillId: 'communication', skillName: 'Stakeholder Communication', category: 'Soft Skills', requiredLevel: 4, isEssential: true }
    ]
  }
];

export const INITIAL_QUESTIONNAIRE: QuestionItem[] = [
  {
    id: 'q1',
    section: 'Career Interests & Domain Focus',
    question: 'Which primary professional domain aligns best with your career ambitions?',
    description: 'Select the field where you find solving real-world challenges most engaging.',
    type: 'single',
    options: [
      {
        id: 'q1-ai',
        text: 'Artificial Intelligence, Machine Learning & Software Engineering',
        associatedSkills: [
          { skillId: 'python', weight: 4 },
          { skillId: 'machine-learning', weight: 5 },
          { skillId: 'deep-learning', weight: 4 }
        ]
      },
      {
        id: 'q1-healthcare',
        text: 'Healthcare, Clinical Patient Care & Health Systems Management',
        associatedSkills: [
          { skillId: 'patient-care', weight: 5 },
          { skillId: 'medical-compliance', weight: 4 },
          { skillId: 'communication', weight: 4 }
        ]
      },
      {
        id: 'q1-finance',
        text: 'Corporate Finance, Investment Strategy & Economic Valuation',
        associatedSkills: [
          { skillId: 'financial-modeling', weight: 5 },
          { skillId: 'corporate-finance', weight: 4 },
          { skillId: 'data-analytics', weight: 4 }
        ]
      },
      {
        id: 'q1-energy',
        text: 'Renewable Energy, Clean Tech & Infrastructure Engineering',
        associatedSkills: [
          { skillId: 'clean-energy', weight: 5 },
          { skillId: 'cad-engineering', weight: 4 },
          { skillId: 'problem-solving', weight: 4 }
        ]
      },
      {
        id: 'q1-marketing',
        text: 'Digital Growth Marketing, Media Strategy & Brand Building',
        associatedSkills: [
          { skillId: 'digital-marketing', weight: 5 },
          { skillId: 'brand-strategy', weight: 4 },
          { skillId: 'data-analytics', weight: 3 }
        ]
      },
      {
        id: 'q1-design',
        text: 'Product UI/UX Design, Creative Experience & Visual Systems',
        associatedSkills: [
          { skillId: 'figma-ui', weight: 5 },
          { skillId: 'ux-design', weight: 5 }
        ]
      }
    ]
  },
  {
    id: 'q2',
    section: 'Work Preference & Problem Solving Style',
    question: 'What type of daily problem-solving activities energize you the most?',
    description: 'Choose all statements that match your natural working habits.',
    type: 'multiple',
    options: [
      {
        id: 'q2-coding',
        text: 'Writing clean code, debugging complex algorithmic logic, building feature modules',
        associatedSkills: [
          { skillId: 'python', weight: 3 },
          { skillId: 'typescript', weight: 3 },
          { skillId: 'problem-solving', weight: 4 }
        ]
      },
      {
        id: 'q2-architecture',
        text: 'Designing high-level system components, server layouts, and infrastructure scalability',
        associatedSkills: [
          { skillId: 'system-design', weight: 5 },
          { skillId: 'cloud-aws', weight: 4 },
          { skillId: 'devops', weight: 4 }
        ]
      },
      {
        id: 'q2-people',
        text: 'Facilitating team discussions, presenting project roadmaps, and managing priorities',
        associatedSkills: [
          { skillId: 'communication', weight: 5 },
          { skillId: 'product-mgmt', weight: 4 },
          { skillId: 'agile', weight: 4 }
        ]
      },
      {
        id: 'q2-data',
        text: 'Analyzing datasets, extracting mathematical insights, creating visual charts',
        associatedSkills: [
          { skillId: 'data-analytics', weight: 5 },
          { skillId: 'sql-db', weight: 4 }
        ]
      }
    ]
  },
  {
    id: 'q3',
    section: 'Current Coding Experience & Fundamentals',
    question: 'How comfortable are you with programming languages (Python, JavaScript/TypeScript, SQL)?',
    description: 'Select your self-evaluated baseline level.',
    type: 'scale',
    options: [
      {
        id: 'q3-1',
        text: 'Beginner – Basic knowledge of syntax, simple loops, and elementary scripts',
        associatedSkills: [
          { skillId: 'python', weight: 1 },
          { skillId: 'typescript', weight: 1 }
        ]
      },
      {
        id: 'q3-2',
        text: 'Intermediate – Able to build functional web apps, write custom queries, and use standard libraries',
        associatedSkills: [
          { skillId: 'python', weight: 3 },
          { skillId: 'typescript', weight: 3 },
          { skillId: 'sql-db', weight: 3 }
        ]
      },
      {
        id: 'q3-3',
        text: 'Advanced – Proficient with async patterns, frameworks, DB optimization, and clean architectural design',
        associatedSkills: [
          { skillId: 'python', weight: 5 },
          { skillId: 'typescript', weight: 5 },
          { skillId: 'sql-db', weight: 4 }
        ]
      }
    ]
  },
  {
    id: 'q4',
    section: 'Learning Commitment & Timeline',
    question: 'How many dedicated hours per week can you allocate toward your career development roadmap?',
    description: 'This will help calibrate milestone pacing and study focus.',
    type: 'single',
    options: [
      {
        id: 'q4-light',
        text: '5 – 10 hours/week (Steady background pacing)',
        associatedSkills: []
      },
      {
        id: 'q4-med',
        text: '10 – 20 hours/week (Focused active acceleration)',
        associatedSkills: []
      },
      {
        id: 'q4-high',
        text: '20+ hours/week (Full-time intensive boot-camp focus)',
        associatedSkills: []
      }
    ]
  }
];

export const MOCK_USER_PROFILE: UserProfile = {
  id: 'usr-101',
  name: 'Alex Rivera',
  email: 'alex.rivera@university.edu',
  title: 'Computer Science Undergraduate & Aspiring AI Engineer',
  education: 'B.S. in Computer Science (Senior Year)',
  experienceYears: 1,
  location: 'San Francisco, CA',
  targetFocus: 'Artificial Intelligence & Machine Learning',
  bio: 'Passionate about leveraging modern software engineering principles to build intelligent systems and data pipelines.',
  completionPercentage: 85,
  skills: [
    { skillId: 'python', name: 'Python Programming', category: 'Technical', level: 3 },
    { skillId: 'typescript', name: 'TypeScript / JavaScript', category: 'Technical', level: 4 },
    { skillId: 'react', name: 'React / Frontend Architecture', category: 'Technical', level: 3 },
    { skillId: 'sql-db', name: 'SQL & Database Architecture', category: 'Technical', level: 2 },
    { skillId: 'machine-learning', name: 'Machine Learning & AI', category: 'Technical', level: 2 },
    { skillId: 'cloud-aws', name: 'Cloud Computing (AWS/GCP)', category: 'Technical', level: 1 },
    { skillId: 'git', name: 'Git & CI/CD Pipelines', category: 'Tools & Frameworks', level: 3 },
    { skillId: 'communication', name: 'Stakeholder Communication', category: 'Soft Skills', level: 4 },
    { skillId: 'problem-solving', name: 'Critical Problem Solving', category: 'Soft Skills', level: 4 }
  ]
};

export const SAMPLE_ROADMAPS: Record<string, CareerRoadmap> = {
  'ai-software-engineer': {
    careerId: 'ai-software-engineer',
    careerTitle: 'AI & Machine Learning Engineer',
    overallTimeline: '6 Months (Phased 4-Stage Plan)',
    overallReadiness: 72,
    aiExplanation: 'AI Analysis Note: The system identified high foundational affinity in Python and Problem Solving. Primary acceleration focus is bridging PyTorch deep learning modules and cloud model hosting.',
    phases: [
      {
        id: 'phase-1',
        monthRange: 'Months 1 – 2',
        phaseTitle: 'Advanced Python & Math Foundations',
        focusArea: 'Core Language Depth, NumPy & Linear Algebra',
        goals: [
          'Master Python object-oriented patterns and memory optimization',
          'Complete NumPy & Pandas data manipulation projects',
          'Review matrix calculus and gradient descent mathematics'
        ],
        expectedOutcome: 'Fluency in Python data structures and mathematical vector operations.',
        recommendedCourses: ['DeepLearning.AI Math for ML Specialization', 'Python Advanced Data Engineering'],
        status: 'completed'
      },
      {
        id: 'phase-2',
        monthRange: 'Months 3 – 4',
        phaseTitle: 'Machine Learning Algorithms & Scikit-Learn',
        focusArea: 'Supervised/Unsupervised Learning & Model Tuning',
        goals: [
          'Build end-to-end regression and classification pipelines',
          'Implement cross-validation, hyperparameter grid search, and metrics',
          'Deploy first ML REST API with FastAPI and Docker'
        ],
        expectedOutcome: 'Functional ML pipeline capable of serving predictions via clean API.',
        recommendedCourses: ['Hands-On Machine Learning with Scikit-Learn', 'Docker for ML Developers'],
        status: 'in_progress'
      },
      {
        id: 'phase-3',
        monthRange: 'Months 5 – 6',
        phaseTitle: 'Deep Learning & Neural Architectures',
        focusArea: 'PyTorch, Transformers & GenAI Fine-Tuning',
        goals: [
          'Construct CNNs and Transformers from scratch in PyTorch',
          'Experiment with HuggingFace model fine-tuning and LoRA techniques',
          'Implement Retrieval-Augmented Generation (RAG) with vector databases'
        ],
        expectedOutcome: 'Portfolio project featuring custom RAG pipeline and fine-tuned LLM.',
        recommendedCourses: ['Fast.ai Practical Deep Learning', 'HuggingFace Transformers Masterclass'],
        status: 'not_started'
      },
      {
        id: 'phase-4',
        monthRange: 'Months 7 – 8',
        phaseTitle: 'MLOps, Cloud Deployment & Production Hardening',
        focusArea: 'AWS Sagemaker, Monitoring & CI/CD',
        goals: [
          'Deploy ML models on Cloud infrastructure with automated scaling',
          'Set up MLflow model registry and drift monitoring alerts',
          'Conduct mock technical interviews and optimize GitHub portfolio'
        ],
        expectedOutcome: 'Job-ready portfolio and live cloud-hosted AI service.',
        recommendedCourses: ['Full Stack Deep Learning', 'AWS Certified Machine Learning Specialty'],
        status: 'not_started'
      }
    ]
  }
};

export const DEFAULT_SYSTEM_CONFIG: SystemConfig = {
  technicalWeight: 0.50,
  questionnaireWeight: 0.35,
  essentialSkillPenalty: 0.15,
  minimumMatchThreshold: 40
};
