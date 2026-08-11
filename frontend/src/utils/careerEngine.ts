import { Career, UserProfile, CareerMatchResult, SkillGapItem, GapSeverity, CareerRoadmap, RoadmapMilestone, QuestionItem } from '../types';

/**
 * Calculates a system-deterministic match score between user profile + questionnaire answers
 * and a given Career target profile.
 */
export function calculateCareerMatch(
  career: Career,
  userProfile: UserProfile,
  answers: Record<string, string | string[]>,
  questionnaireItems: QuestionItem[]
): CareerMatchResult {
  const userSkillMap = new Map(userProfile.skills.map(s => [s.skillId, s.level]));
  
  let totalRequiredWeight = 0;
  let earnedScore = 0;
  const keyStrengths: string[] = [];
  const keyGaps: string[] = [];

  career.requiredSkills.forEach(req => {
    const weight = req.isEssential ? 2.0 : 1.0;
    totalRequiredWeight += req.requiredLevel * weight;

    const userLevel = userSkillMap.get(req.skillId) || 0;
    
    // Earned proportional score
    const scoreForSkill = Math.min(userLevel, req.requiredLevel) * weight;
    earnedScore += scoreForSkill;

    if (userLevel >= req.requiredLevel) {
      keyStrengths.push(`${req.skillName} (Level ${userLevel}/${req.requiredLevel})`);
    } else {
      const gap = req.requiredLevel - userLevel;
      keyGaps.push(`${req.skillName} (Needs +${gap} level increase)`);
    }
  });

  // Direct skill match percentage
  const skillMatchRatio = totalRequiredWeight > 0 ? (earnedScore / totalRequiredWeight) : 0;

  // Questionnaire Interest Alignment Bonus
  let questionnaireBonus = 0;
  let totalQuestionsEvaluated = 0;

  questionnaireItems.forEach(q => {
    const selectedOptionIds = answers[q.id];
    if (!selectedOptionIds) return;

    totalQuestionsEvaluated++;
    const selectedList = Array.isArray(selectedOptionIds) ? selectedOptionIds : [selectedOptionIds];
    
    selectedList.forEach(optId => {
      const option = q.options.find(o => o.id === optId);
      if (option) {
        option.associatedSkills.forEach(assoc => {
          // Check if this associated skill belongs to the career
          const matchedReq = career.requiredSkills.find(r => r.skillId === assoc.skillId);
          if (matchedReq) {
            questionnaireBonus += (assoc.weight / 5) * 4; // up to 4% bonus per relevant alignment
          }
        });
      }
    });
  });

  // Calculate final score bounded between 45% and 98%
  let rawPercentage = Math.round((skillMatchRatio * 75) + Math.min(23, questionnaireBonus));
  if (rawPercentage < 45) rawPercentage = 45;
  if (rawPercentage > 98) rawPercentage = 98;

  let confidenceLevel: 'High' | 'Medium' | 'Moderate' = 'Moderate';
  if (rawPercentage >= 85) confidenceLevel = 'High';
  else if (rawPercentage >= 70) confidenceLevel = 'Medium';

  const fitReason = rawPercentage >= 80 
    ? `System calculated a ${rawPercentage}% match due to strong proficiency in ${keyStrengths.slice(0, 2).join(', ') || 'core skill requirements'}.`
    : `System calculated a ${rawPercentage}% match. Developing ${keyGaps.slice(0, 2).join(', ') || 'key skills'} will significantly improve alignment.`;

  return {
    career,
    matchScore: rawPercentage,
    keyStrengths,
    keyGaps,
    confidenceLevel,
    fitReason,
    systemCalculatedBadge: 'Deterministic Algorithm v2.4'
  };
}

/**
 * Computes granular skill gap items for a target career against user skills
 */
export function calculateSkillGaps(career: Career, userProfile: UserProfile): SkillGapItem[] {
  const userSkillMap = new Map(userProfile.skills.map(s => [s.skillId, s.level]));

  return career.requiredSkills.map(req => {
    const currentLevel = userSkillMap.get(req.skillId) || 0;
    const gapAmount = Math.max(0, req.requiredLevel - currentLevel);

    let severity: GapSeverity = 'low';
    if (gapAmount >= 3) severity = 'critical';
    else if (gapAmount === 2) severity = 'high';
    else if (gapAmount === 1) severity = 'medium';

    let recommendedAction = 'Maintain skill practice.';
    if (gapAmount > 0) {
      if (req.category === 'Technical') {
        recommendedAction = `Complete hands-on coding modules & build portfolio projects in ${req.skillName}.`;
      } else if (req.category === 'Tools & Frameworks') {
        recommendedAction = `Practice workflow integrations and environment setups for ${req.skillName}.`;
      } else if (req.category === 'Domain Knowledge') {
        recommendedAction = `Read case studies and complete industry certification modules for ${req.skillName}.`;
      } else {
        recommendedAction = `Engage in peer reviews, presentations, and group problem-solving for ${req.skillName}.`;
      }
    }

    return {
      skillId: req.skillId,
      skillName: req.skillName,
      category: req.category,
      currentLevel,
      requiredLevel: req.requiredLevel,
      gapAmount,
      severity,
      isEssential: req.isEssential,
      recommendedAction
    };
  });
}

/**
 * Generates structured milestone roadmap based on career requirements and identified skill gaps
 */
export function generateRoadmapForCareer(career: Career, gaps: SkillGapItem[]): CareerRoadmap {
  const criticalGaps = gaps.filter(g => g.severity === 'critical' || g.severity === 'high');
  const mediumGaps = gaps.filter(g => g.severity === 'medium' || g.severity === 'low');

  const milestones: RoadmapMilestone[] = [
    {
      id: 'm1',
      monthRange: 'Months 1 – 2',
      phaseTitle: 'Phase 1: Critical Skill Foundation',
      focusArea: criticalGaps.length > 0 ? criticalGaps.map(g => g.skillName).join(', ') : 'Core System Prerequisites',
      goals: criticalGaps.length > 0
        ? criticalGaps.map(g => `Address critical gap in ${g.skillName} (Target Level ${g.requiredLevel})`)
        : ['Review prerequisites', 'Setup development environment', 'Establish daily study routine'],
      expectedOutcome: 'Achieve foundational competence in high-priority career requirements.',
      recommendedCourses: [
        `Mastering ${criticalGaps[0]?.skillName || career.title} Basics`,
        'Industry Benchmark Foundations'
      ],
      status: 'in_progress'
    },
    {
      id: 'm2',
      monthRange: 'Months 3 – 4',
      phaseTitle: 'Phase 2: Applied Skills & Project Integration',
      focusArea: mediumGaps.length > 0 ? mediumGaps.map(g => g.skillName).join(', ') : 'Advanced Technical Application',
      goals: [
        'Build 2 practical portfolio projects applying required technologies',
        'Implement automated testing or verification workflows',
        'Receive code and architecture review from mentors'
      ],
      expectedOutcome: 'Demonstrable project repository showcasing applied domain skills.',
      recommendedCourses: [
        `Practical ${career.title} Applied Project Workshop`,
        'Enterprise Architecture Standards'
      ],
      status: 'not_started'
    },
    {
      id: 'm3',
      monthRange: 'Months 5 – 6',
      phaseTitle: 'Phase 3: Production Practice & Specialized Depth',
      focusArea: 'Industry Best Practices, Tooling & Workflow Optimization',
      goals: [
        'Optimize solution performance and documentation',
        'Deploy end-to-end applications or architectures to live staging',
        'Master CI/CD pipelines and team collaboration workflows'
      ],
      expectedOutcome: 'Production-ready codebases and verified system deployment.',
      recommendedCourses: [
        'Cloud & DevOps for Software Engineers',
        'Advanced Problem Solving & System Design'
      ],
      status: 'not_started'
    },
    {
      id: 'm4',
      monthRange: 'Months 7 – 8',
      phaseTitle: 'Phase 4: Professional Positioning & Portfolio Defense',
      focusArea: 'Interview Preparation, Technical Assessments & Career Launch',
      goals: [
        'Complete 10 mock technical interview sessions',
        'Refine GitHub portfolio and resume skill matrix',
        'Apply for target roles and present capstone project'
      ],
      expectedOutcome: 'Interview confidence and active candidate positioning for target career.',
      recommendedCourses: [
        `${career.title} Technical Interview Mastery`,
        'Executive Communication for Engineers'
      ],
      status: 'not_started'
    }
  ];

  const totalGapsCount = gaps.length;
  const closedGapsCount = gaps.filter(g => g.gapAmount === 0).length;
  const overallReadiness = Math.min(95, Math.max(50, Math.round((closedGapsCount / (totalGapsCount || 1)) * 100)));

  return {
    careerId: career.id,
    careerTitle: career.title,
    overallTimeline: '8 Months (Phased Roadmap)',
    overallReadiness,
    phases: milestones,
    aiExplanation: `System Calculated Summary: Milestone plan tailored for ${career.title}. Prioritizes ${criticalGaps.map(g => g.skillName).join(', ') || 'core competencies'} in early phases to maximize skill growth velocity.`
  };
}
