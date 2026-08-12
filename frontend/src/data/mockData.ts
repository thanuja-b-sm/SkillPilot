import { Career, QuestionItem, SystemConfig, SkillMeta } from '../types';

/**
 * SkillPilot Master Data Initial Default Shells
 * All authoritative business data is loaded directly from Spring Boot REST API + MySQL database.
 */

export const INITIAL_SKILLS: SkillMeta[] = [];

export const INITIAL_CAREERS: Career[] = [];

export const INITIAL_QUESTIONNAIRE: QuestionItem[] = [];

export const DEFAULT_SYSTEM_CONFIG: SystemConfig = {
  technicalWeight: 0.50,
  questionnaireWeight: 0.35,
  essentialSkillPenalty: 0.15,
  minimumMatchThreshold: 40
};
