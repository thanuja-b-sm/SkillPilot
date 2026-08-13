export type PageId = 
  | 'landing' 
  | 'register' 
  | 'login' 
  | 'profile' 
  | 'questionnaire' 
  | 'results' 
  | 'target-selection' 
  | 'skill-gap' 
  | 'roadmap' 
  | 'admin';

export type UserRole = 'guest' | 'student' | 'admin';

export type GapSeverity = 'critical' | 'high' | 'medium' | 'low';

export interface SkillMeta {
  id: string;
  name: string;
  category: 'Technical' | 'Domain Knowledge' | 'Tools & Frameworks' | 'Soft Skills' | string;
  description?: string;
  isActive?: boolean;
}

export interface UserSkill {
  skillId: string;
  name: string;
  category: 'Technical' | 'Domain Knowledge' | 'Tools & Frameworks' | 'Soft Skills';
  level: number; // 0 to 5
}

export interface UserProfile {
  id: string;
  name: string;
  email: string;
  title?: string;
  education?: string;
  institutionName?: string;
  degreeLevel?: string;
  majorFieldOfStudy?: string;
  graduationYear?: number;
  educationStatus?: string;
  experienceYears?: number;
  employmentStatus?: string;
  currentJobTitle?: string;
  currentIndustry?: string;
  relevantExperienceYears?: number;
  location?: string;
  country?: string;
  dateOfBirth?: string;
  targetFocus?: string;
  preferredWorkMode?: string;
  preferredEmploymentType?: string;
  careerGoal?: string;
  weeklyHoursAvailable?: number;
  preferredLearningPace?: string;
  preferredRoadmapDuration?: number;
  bio?: string;
  certifications?: string;
  portfolioUrl?: string;
  careerInterests?: string;
  skills: UserSkill[];
  completionPercentage: number;
  userRole?: string;
  role?: string;
  roles?: string[];
}

export interface QuestionOption {
  id: string;
  text: string;
  associatedSkills: { skillId: string; weight: number }[];
  weightMultiplier?: number;
}

export interface QuestionItem {
  id: string;
  section: string;
  question: string;
  description?: string;
  type: 'single' | 'multiple' | 'scale';
  options: QuestionOption[];
}

export interface SkillRequirement {
  skillId: string;
  skillName: string;
  category: 'Technical' | 'Domain Knowledge' | 'Tools & Frameworks' | 'Soft Skills';
  requiredLevel: number; // 1 to 5
  isEssential: boolean;
}

export interface Career {
  id: string;
  title: string;
  category: string;
  description: string;
  averageSalary: string;
  growthRate: string;
  demandLevel: 'High' | 'Very High' | 'Moderate';
  requiredSkills: SkillRequirement[];
  recommendedPrerequisites: string[];
  typicalRoles: string[];
}

export interface CareerMatchResult {
  career: Career;
  matchScore: number; // 0 - 100
  readinessScore?: number;
  isRecommended?: boolean;
  keyStrengths: string[];
  keyGaps: string[];
  confidenceLevel: 'High' | 'Medium' | 'Moderate' | 'Low' | string;
  fitReason: string;
  systemCalculatedBadge: string;
}

export interface SkillGapItem {
  skillId: string;
  skillName: string;
  category: string;
  currentLevel: number;
  requiredLevel: number;
  gapAmount: number;
  severity: GapSeverity;
  classification?: string;
  experienceSupported?: boolean;
  isEssential: boolean;
  recommendedAction: string;
}

export interface SkillGapAnalysisResponse {
  career: Career;
  readinessScore: number;
  skillReadiness?: number;
  experienceAlignment?: number;
  educationAlignment?: number;
  overallReadiness?: number;
  skills: SkillGapItem[];
  strengths: string[];
  missingSkills: SkillGapItem[];
  totalRequiredSkills: number;
  completedSkills: number;
}

export interface RoadmapMilestone {
  id: string;
  phaseOrder?: number;
  monthRange: string; // e.g. "Months 1–2"
  phaseTitle: string; // e.g. "Foundational Mastery"
  focusArea: string;
  goals: string[];
  expectedOutcome: string;
  recommendedCourses: string[];
  status: 'not_started' | 'in_progress' | 'completed' | string;
  completionPercentage?: number;
  targetSkillId?: string;
  currentLevel?: number;
  requiredLevel?: number;
  gapSeverity?: string;
  notes?: string;
  completedAt?: string;
}

export interface CareerRoadmap {
  id?: string;
  careerId: string;
  careerTitle: string;
  overallTimeline: string;
  durationMonths?: number;
  overallReadiness: number;
  completedMilestonesCount?: number;
  totalMilestonesCount?: number;
  isStale?: boolean;
  status?: string;
  phases: RoadmapMilestone[];
  aiExplanation?: string;
}

export interface SystemConfig {
  technicalWeight: number;
  questionnaireWeight: number;
  essentialSkillPenalty: number;
  minimumMatchThreshold: number;
}

export interface SystemHealthResponse {
  status: 'HEALTHY' | 'WARNING' | 'ERROR';
  healthScore: number;
  activeCareersCount: number;
  activeSkillsCount: number;
  totalRequirementsCount: number;
  totalQuestionnaireMappingsCount: number;
  totalQuestionsCount: number;
  warnings: string[];
  errors: string[];
}
