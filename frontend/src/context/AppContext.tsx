import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { 
  PageId, 
  UserRole, 
  UserProfile, 
  Career, 
  QuestionItem, 
  CareerMatchResult, 
  SkillGapItem, 
  CareerRoadmap,
  SystemConfig,
  SkillMeta
} from '../types';
import { 
  INITIAL_CAREERS, 
  INITIAL_QUESTIONNAIRE, 
  INITIAL_SKILLS, 
  DEFAULT_SYSTEM_CONFIG
} from '../data/mockData';
import { calculateCareerMatch, calculateSkillGaps, generateRoadmapForCareer } from '../utils/careerEngine';

interface ToastAlert {
  id: string;
  type: 'success' | 'info' | 'warning' | 'error';
  message: string;
}

// Empty profile shell for unauthenticated state
const EMPTY_USER_PROFILE: UserProfile = {
  id: '',
  name: 'Guest',
  email: '',
  title: '',
  education: '',
  experienceYears: 0,
  location: '',
  targetFocus: '',
  bio: '',
  skills: [],
  completionPercentage: 0
};

interface AppContextType {
  userRole: UserRole;
  setUserRole: (role: UserRole) => void;
  activePage: PageId;
  navigateTo: (page: PageId, options?: { replace?: boolean }) => void;
  isLoadingAuth: boolean;
  
  // Auth
  token: string | null;
  setToken: (t: string | null) => void;
  loginWithAuthData: (authToken: string, profile: UserProfile, roleStr?: string) => void;
  
  // User Data
  userProfile: UserProfile;
  setUserProfile: React.Dispatch<React.SetStateAction<UserProfile>>;
  updateUserSkill: (skillId: string, level: number) => void;
  
  // Questionnaire & Calculations
  questionnaire: QuestionItem[];
  questionnaireAnswers: Record<string, string | string[]>;
  saveQuestionAnswer: (questionId: string, answer: string | string[]) => void;
  resetQuestionnaire: () => void;
  
  // Careers Data & Selection
  careers: Career[];
  selectedTargetCareer: Career | null;
  selectTargetCareer: (careerId: string) => void;
  
  // Match & Analysis State
  careerMatches: CareerMatchResult[];
  isLoadingMatches: boolean;
  recalculateCareerMatches: () => void;
  skillGaps: SkillGapItem[];
  backendSkillGap?: any;
  isLoadingSkillGap: boolean;
  activeRoadmap: CareerRoadmap | null;
  isLoadingRoadmap: boolean;
  generateRoadmap: (durationMonths: number) => Promise<void>;
  
  // AI State
  aiEnhancing: boolean;
  enhanceRoadmapSummaryWithAI: () => Promise<void>;
  
  // System Config & Admin CRUD
  skillsList: SkillMeta[];
  systemConfig: SystemConfig;
  updateSystemConfig: (config: Partial<SystemConfig>) => void;
  
  // Admin Operations
  addCareer: (career: Career) => void;
  updateCareer: (career: Career) => void;
  deleteCareer: (careerId: string) => void;
  activateCareer: (careerId: string) => Promise<void>;
  addSkill: (skill: { name: string; category: string; description?: string }) => Promise<void>;
  updateSkill: (id: string, skill: { name: string; category: string; description?: string }) => Promise<void>;
  deleteSkill: (id: string) => Promise<void>;
  activateSkill: (skillId: string) => Promise<void>;
  addCareerRequirement: (careerId: string, req: { skillId: string; requiredLevel: number; isEssential?: boolean }) => Promise<void>;
  deleteCareerRequirement: (reqId: string) => Promise<void>;
  addQuestionSkillMapping: (req: { optionId: string; skillId: string; weight: number }) => Promise<void>;
  deleteQuestionSkillMapping: (mappingId: string) => Promise<void>;
  addQuestionItem: (item: QuestionItem) => void;
  updateQuestionItem: (item: QuestionItem) => Promise<void>;
  deleteQuestionItem: (itemId: string) => void;
  addQuestionOption: (questionId: string, optionText: string, displayOrder?: number) => Promise<void>;
  updateQuestionOption: (optionId: string, optionText: string, displayOrder?: number) => Promise<void>;
  deleteQuestionOption: (optionId: string) => Promise<void>;
  refreshCareers: () => Promise<void>;
  refreshSkills: () => Promise<void>;
  refreshQuestionnaire: () => Promise<void>;
  
  // Toasts
  toasts: ToastAlert[];
  showToast: (message: string, type?: ToastAlert['type']) => void;
  removeToast: (id: string) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

const getPathForPage = (page: PageId): string => {
  if (page === 'landing') return '/';
  return `/${page}`;
};

const getPageFromPath = (path: string): PageId => {
  const clean = path.trim().toLowerCase().replace(/\/$/, '');
  if (clean === '' || clean === '/landing' || clean === '/') return 'landing';
  if (clean === '/register') return 'register';
  if (clean === '/login') return 'login';
  if (clean === '/profile') return 'profile';
  if (clean === '/questionnaire') return 'questionnaire';
  if (clean === '/results') return 'results';
  if (clean === '/target-selection') return 'target-selection';
  if (clean === '/skill-gap') return 'skill-gap';
  if (clean === '/roadmap') return 'roadmap';
  if (clean === '/admin') return 'admin';
  return 'landing';
};

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [userRole, setUserRoleState] = useState<UserRole>('guest');
  const [activePage, setActivePageState] = useState<PageId>(() => getPageFromPath(window.location.pathname));
  const [token, setTokenState] = useState<string | null>(() => localStorage.getItem('skillpilot_token'));
  const [isLoadingAuth, setIsLoadingAuth] = useState<boolean>(() => Boolean(localStorage.getItem('skillpilot_token')));
  
  const [userProfile, setUserProfile] = useState<UserProfile>(EMPTY_USER_PROFILE);
  const [careers, setCareers] = useState<Career[]>([]);
  const [questionnaire, setQuestionnaire] = useState<QuestionItem[]>([]);
  const [skillsList, setSkillsList] = useState<SkillMeta[]>([]);
  const [systemConfig, setSystemConfig] = useState<SystemConfig>(DEFAULT_SYSTEM_CONFIG);
  
  const [questionnaireAnswers, setQuestionnaireAnswers] = useState<Record<string, string | string[]>>({});
  
  const [selectedTargetCareerId, setSelectedTargetCareerId] = useState<string>('');
  const [toasts, setToasts] = useState<ToastAlert[]>([]);
  const [aiEnhancing, setAiEnhancing] = useState<boolean>(false);
  const [isLoadingMatches, setIsLoadingMatches] = useState(false);
  const [isLoadingSkillGap, setIsLoadingSkillGap] = useState(false);
  const [isLoadingRoadmap, setIsLoadingRoadmap] = useState(false);

  // Helper for Toast Alerts (Suppresses all toasts except logout notifications per user preference)
  const showToast = (message: string, type: ToastAlert['type'] = 'info') => {
    if (!message.toLowerCase().includes('logged out') && !message.toLowerCase().includes('logout')) {
      return;
    }
    const id = Date.now().toString();
    setToasts(prev => [...prev, { id, type, message }]);
    setTimeout(() => {
      removeToast(id);
    }, 4000);
  };

  const removeToast = (id: string) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  };

  const [backendCareerMatches, setBackendCareerMatches] = useState<CareerMatchResult[] | null>(null);
  const [backendSkillGap, setBackendSkillGap] = useState<any | null>(null);

  // Expose token setter so login pages can update context
  const setToken = (t: string | null) => {
    setTokenState(t);
    if (t) {
      localStorage.setItem('skillpilot_token', t);
    } else {
      localStorage.removeItem('skillpilot_token');
    }
  };

  const fetchBackendCareerMatches = useCallback((authToken: string) => {
    setIsLoadingMatches(true);
    fetch('/api/careers/matches', {
      headers: { 'Authorization': `Bearer ${authToken}` }
    })
    .then(r => r.ok ? r.json() : null)
    .then(data => {
      if (data && Array.isArray(data) && data.length > 0) {
        setBackendCareerMatches(data);
      }
    })
    .catch(err => console.warn('Failed to fetch backend career matches:', err))
    .finally(() => setIsLoadingMatches(false));
  }, []);

  const fetchBackendSkillGap = useCallback((authToken: string) => {
    setIsLoadingSkillGap(true);
    fetch('/api/user/target-career/skill-gap', {
      headers: { 'Authorization': `Bearer ${authToken}` }
    })
    .then(r => r.ok ? r.json() : null)
    .then(data => {
      if (data) {
        setBackendSkillGap(data);
      }
    })
    .catch(err => console.warn('Failed to fetch backend skill gap:', err))
    .finally(() => setIsLoadingSkillGap(false));
  }, []);

  // Fetch the most recent roadmap for this user (no new generation)
  const fetchExistingRoadmap = useCallback(async (authToken: string) => {
    try {
      const res = await fetch('/api/user/roadmaps', {
        headers: { 'Authorization': `Bearer ${authToken}` }
      });
      if (res.ok) {
        const data = await res.json();
        setActiveRoadmap(data);
        return true;
      }
      return false;
    } catch {
      return false;
    }
  }, []);

  // Explicit generate roadmap (user-triggered or first time)
  const generateRoadmap = useCallback(async (durationMonths: number = 6) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (!activeTok) return;
    setIsLoadingRoadmap(true);
    try {
      const res = await fetch('/api/user/roadmaps/generate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${activeTok}`
        },
        body: JSON.stringify({ durationMonths })
      });
      if (res.ok) {
        const data = await res.json();
        setActiveRoadmap(data);
        showToast(`${durationMonths}-month roadmap generated successfully!`, 'success');
      } else {
        showToast('Failed to generate roadmap. Please try again.', 'error');
      }
    } catch (err) {
      console.warn('Failed to generate roadmap:', err);
      showToast('Unable to reach roadmap generation service.', 'error');
    } finally {
      setIsLoadingRoadmap(false);
    }
  }, [token]);

  const navigateTo = useCallback((page: PageId, options?: { replace?: boolean }) => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
    const targetPath = getPathForPage(page);
    if (options?.replace) {
      window.history.replaceState({ page }, '', targetPath);
    } else if (window.location.pathname !== targetPath) {
      window.history.pushState({ page }, '', targetPath);
    }
    setActivePageState(page);
  }, []);

  // Listen for browser Back/Forward popstate events
  useEffect(() => {
    const handlePopState = (event: PopStateEvent) => {
      const pageFromState = event.state?.page as PageId | undefined;
      const pageFromUrl = getPageFromPath(window.location.pathname);
      const targetPage = pageFromState || pageFromUrl;
      setActivePageState(targetPage);
    };

    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, []);

  // Ensure current route matches initial URL on mount
  useEffect(() => {
    const currentPath = window.location.pathname;
    const currentPage = getPageFromPath(currentPath);
    window.history.replaceState({ page: currentPage }, '', currentPath);
  }, []);

  // Master data fetcher with retry capability for cold start / startup race
  const fetchMasterData = useCallback(async () => {
    let hasLoadedAny = false;
    try {
      const [cRes, sRes, qRes] = await Promise.all([
        fetch('/api/careers').catch(() => null),
        fetch('/api/skills').catch(() => null),
        fetch('/api/questionnaire').catch(() => null)
      ]);
      if (cRes && cRes.ok) {
        const data = await cRes.json().catch(() => null);
        if (Array.isArray(data) && data.length > 0) {
          setCareers(data);
          hasLoadedAny = true;
        }
      }
      if (sRes && sRes.ok) {
        const data = await sRes.json().catch(() => null);
        if (Array.isArray(data) && data.length > 0) {
          setSkillsList(data);
          hasLoadedAny = true;
        }
      }
      if (qRes && qRes.ok) {
        const data = await qRes.json().catch(() => null);
        if (Array.isArray(data) && data.length > 0) {
          setQuestionnaire(data);
          hasLoadedAny = true;
        }
      }
    } catch (err) {
      console.warn('Backend master data fetch error:', err);
    }
    return hasLoadedAny;
  }, []);

  const ensureMasterDataLoaded = useCallback(async (maxRetries = 3, delayMs = 600) => {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      const success = await fetchMasterData();
      if (success) break;
      if (attempt < maxRetries) {
        await new Promise(res => setTimeout(res, delayMs));
      }
    }
  }, [fetchMasterData]);

  // Fetch authenticated user data (matches, gaps, roadmaps, answers, target career)
  const fetchAuthenticatedUserData = useCallback(async (authToken: string) => {
    if (!authToken) return;
    try {
      const ansRes = await fetch('/api/questionnaire/answers', {
        headers: { 'Authorization': `Bearer ${authToken}` }
      }).catch(() => null);

      if (ansRes && ansRes.ok) {
        const userAnswers = await ansRes.json().catch(() => null);
        if (userAnswers && Array.isArray(userAnswers) && userAnswers.length > 0) {
          const answersMap: Record<string, string | string[]> = {};
          userAnswers.forEach((ans: any) => {
            const optIds = ans.selectedOptionIds || [];
            answersMap[ans.questionId] = optIds.length === 1 ? optIds[0] : optIds;
          });
          setQuestionnaireAnswers(answersMap);
        }
      }

      const tcRes = await fetch('/api/user/target-career', {
        headers: { 'Authorization': `Bearer ${authToken}` }
      }).catch(() => null);

      if (tcRes && tcRes.ok) {
        const tc = await tcRes.json().catch(() => null);
        if (tc && tc.careerId) {
          setSelectedTargetCareerId(tc.careerId);
        }
      }

      fetchBackendCareerMatches(authToken);
      fetchBackendSkillGap(authToken);
      fetchExistingRoadmap(authToken);
    } catch (err) {
      console.warn('Failed to fetch authenticated user data:', err);
    }
  }, [fetchBackendCareerMatches, fetchBackendSkillGap, fetchExistingRoadmap]);

  const isInitializingRef = React.useRef(false);

  // Initialize session from localStorage token on startup
  const initializeSession = useCallback(async (savedToken: string) => {
    if (isInitializingRef.current) return;
    isInitializingRef.current = true;
    setIsLoadingAuth(true);

    let response: Response | null = null;
    let fetchError: any = null;

    // Bounded retry loop for transient backend availability
    for (let attempt = 1; attempt <= 3; attempt++) {
      try {
        response = await fetch('/api/auth/me', {
          headers: { 'Authorization': `Bearer ${savedToken}` }
        });
        fetchError = null;
        break;
      } catch (err) {
        fetchError = err;
        if (attempt < 3) {
          await new Promise(res => setTimeout(res, 600));
        }
      }
    }

    if (fetchError || !response) {
      console.warn('Backend unavailable during session restoration. Retaining stored session token.');
      setIsLoadingAuth(false);
      isInitializingRef.current = false;
      ensureMasterDataLoaded();
      return;
    }

    if (response.status === 401) {
      localStorage.removeItem('skillpilot_token');
      setTokenState(null);
      setUserRoleState('guest');
      const initialPage = getPageFromPath(window.location.pathname);
      if (['admin', 'profile', 'skill-gap', 'roadmap', 'target-selection'].includes(initialPage)) {
        navigateTo('login', { replace: true });
      }
      showToast('Session expired. Please sign in again.', 'warning');
      setIsLoadingAuth(false);
      isInitializingRef.current = false;
      ensureMasterDataLoaded();
      return;
    }

    if (response.status === 403) {
      console.warn('403 Forbidden on /api/auth/me');
      setIsLoadingAuth(false);
      isInitializingRef.current = false;
      ensureMasterDataLoaded();
      return;
    }

    if (response.ok) {
      const profile = await response.json().catch(() => null);
      if (profile && profile.id) {
        setUserProfile(profile);

        const roleStr = (profile.userRole || profile.role || '').toLowerCase();
        const isAdmin = roleStr === 'admin' || profile.roles?.includes('ADMIN');
        const role: UserRole = isAdmin ? 'admin' : 'student';

        setUserRoleState(role);
        setTokenState(savedToken);

        const initialPage = getPageFromPath(window.location.pathname);

        if (role === 'admin') {
          if (initialPage === 'landing' || initialPage === 'login' || initialPage === 'register') {
            navigateTo('admin', { replace: true });
          } else {
            navigateTo(initialPage, { replace: true });
          }
        } else {
          if (initialPage === 'admin') {
            showToast('Access denied. Administrator privileges required.', 'error');
            navigateTo('results', { replace: true });
          } else if (initialPage === 'login' || initialPage === 'register' || initialPage === 'landing') {
            navigateTo('results', { replace: true });
          } else {
            navigateTo(initialPage, { replace: true });
          }
        }

        // Migrate guest questionnaire answers if present
        const guestAnswersRaw = localStorage.getItem('skillpilot_guest_answers');
        if (guestAnswersRaw) {
          try {
            const guestAns = JSON.parse(guestAnswersRaw);
            const payload = Object.entries(guestAns).map(([qId, ans]) => ({
              questionId: qId,
              selectedOptionIds: Array.isArray(ans) ? ans : [ans]
            }));

            if (payload.length > 0) {
              await fetch('/api/questionnaire/answers', {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json',
                  'Authorization': `Bearer ${savedToken}`
                },
                body: JSON.stringify({ answers: payload })
              });
            }
            localStorage.removeItem('skillpilot_guest_answers');
            showToast('Migrated guest self-assessment progress to your account!', 'success');
          } catch (e) {
            console.warn('Failed migrating guest answers:', e);
          }
        }

        fetchAuthenticatedUserData(savedToken);
      }
    }

    setIsLoadingAuth(false);
    isInitializingRef.current = false;
    ensureMasterDataLoaded();
  }, [ensureMasterDataLoaded, fetchAuthenticatedUserData, navigateTo]);

  // Clean login helper method
  const loginWithAuthData = useCallback((authToken: string, profile: UserProfile, roleStr?: string, targetPage?: PageId) => {
    setToken(authToken);
    if (profile) {
      setUserProfile(profile);
    }
    const rStr = (roleStr || profile?.userRole || profile?.role || '').toLowerCase();
    const isAdmin = rStr === 'admin' || profile?.roles?.includes('ADMIN');
    const role: UserRole = isAdmin ? 'admin' : 'student';
    setUserRoleState(role);

    const destination = targetPage || (role === 'admin' ? 'admin' : 'results');
    navigateTo(destination);
    ensureMasterDataLoaded();
    fetchAuthenticatedUserData(authToken);
  }, [setToken, navigateTo, ensureMasterDataLoaded, fetchAuthenticatedUserData]);

  // Fetch career-specific skills and questionnaire when target career changes
  useEffect(() => {
    if (selectedTargetCareerId) {
      fetch(`/api/careers/${selectedTargetCareerId}/skills`)
        .then(res => res.ok ? res.json() : null)
        .then(data => {
          if (data && Array.isArray(data) && data.length > 0) {
            setSkillsList(data.map((r: any) => ({
              id: r.skillId,
              name: r.skillName,
              category: r.category,
              requiredLevel: r.requiredLevel,
              isEssential: r.isEssential
            })));
          }
        })
        .catch(err => console.warn('Failed to fetch career specific skills:', err));

      fetch(`/api/questionnaire/career/${selectedTargetCareerId}`)
        .then(res => res.ok ? res.json() : null)
        .then(data => {
          if (data && Array.isArray(data) && data.length > 0) {
            setQuestionnaire(data);
          }
        })
        .catch(err => console.warn('Failed to fetch career specific questionnaire:', err));
    } else {
      fetch('/api/skills')
        .then(res => res.ok ? res.json() : null)
        .then(data => { if (data && data.length > 0) setSkillsList(data); })
        .catch(err => console.warn('Backend skills fetch fallback', err));

      fetch('/api/questionnaire')
        .then(res => res.ok ? res.json() : null)
        .then(data => { if (data && data.length > 0) setQuestionnaire(data); })
        .catch(err => console.warn('Backend questionnaire fetch fallback', err));
    }
  }, [selectedTargetCareerId]);

  // Synchronize master data & authenticated session on startup
  useEffect(() => {
    ensureMasterDataLoaded();

    const savedToken = localStorage.getItem('skillpilot_token');
    if (savedToken) {
      initializeSession(savedToken);
    } else {
      setIsLoadingAuth(false);
    }
  }, [ensureMasterDataLoaded, initializeSession]);

  // Role switching & logout logic
  const setUserRole = (role: UserRole) => {
    setUserRoleState(role);
    if (role === 'admin') {
      navigateTo('admin');
      showToast('Switched to Administrator Workspace', 'info');
    } else if (role === 'student') {
      if (activePage === 'admin' || activePage === 'landing') {
        navigateTo('results');
      }
      showToast('Logged in as Student Profile', 'success');
    } else {
      // Full logout — clear all cached state
      setToken(null);
      setUserProfile(EMPTY_USER_PROFILE);
      setBackendCareerMatches(null);
      setBackendSkillGap(null);
      setActiveRoadmap(null);
      setQuestionnaireAnswers({});
      setSelectedTargetCareerId('');
      navigateTo('landing');
      showToast('Logged out of session', 'info');
    }
  };

  // Persist User Skill update to Spring Boot backend
  const updateUserSkill = async (skillId: string, level: number) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    
    // Optimistic local update
    setUserProfile(prev => {
      const existingIdx = prev.skills.findIndex(s => s.skillId === skillId);
      let updatedSkills = [...prev.skills];
      if (existingIdx >= 0) {
        updatedSkills[existingIdx] = { ...updatedSkills[existingIdx], level };
      } else {
        const skillMeta = skillsList.find(s => s.id === skillId);
        updatedSkills.push({
          skillId,
          name: skillMeta ? skillMeta.name : skillId,
          category: skillMeta ? skillMeta.category : 'Technical',
          level
        });
      }
      return { ...prev, skills: updatedSkills };
    });

    if (activeTok) {
      try {
        const res = await fetch('/api/user/skills', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${activeTok}`
          },
          body: JSON.stringify({ skillId, level })
        });
        if (res.ok) {
          // Refresh full profile to get updated completionPercentage
          const profRes = await fetch('/api/user/profile', {
            headers: { 'Authorization': `Bearer ${activeTok}` }
          });
          if (profRes.ok) {
            const updatedProf = await profRes.json();
            setUserProfile(updatedProf);
          }
          fetchBackendCareerMatches(activeTok);
          fetchBackendSkillGap(activeTok);
        }
      } catch (err) {
        console.error('Error syncing skill rating to backend:', err);
      }
    }
    showToast('Updated skill self-assessment rating', 'success');
  };

  // Save questionnaire answer
  const saveQuestionAnswer = async (questionId: string, answer: string | string[]) => {
    const updatedAnswers = { ...questionnaireAnswers, [questionId]: answer };
    setQuestionnaireAnswers(updatedAnswers);

    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const optionIds = Array.isArray(answer) ? answer : [answer];
        const res = await fetch('/api/questionnaire/answers', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${activeTok}`
          },
          body: JSON.stringify({
            answers: [
              {
                questionId,
                selectedOptionIds: optionIds
              }
            ]
          })
        });

        if (res.ok) {
          // Refresh profile to reflect updated readiness completion
          const profRes = await fetch('/api/user/profile', {
            headers: { 'Authorization': `Bearer ${activeTok}` }
          });
          if (profRes.ok) {
            const updatedProf = await profRes.json();
            setUserProfile(updatedProf);
          }
          fetchBackendCareerMatches(activeTok);
        }
      } catch (err) {
        console.error('Error persisting questionnaire answer:', err);
      }
    } else {
      localStorage.setItem('skillpilot_guest_answers', JSON.stringify(updatedAnswers));
    }
  };

  const resetQuestionnaire = () => {
    setQuestionnaireAnswers({});
    showToast('Questionnaire reset. Please answer the questions again.', 'info');
  };

  // Explicit recalculate career matches
  const recalculateCareerMatches = () => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      fetchBackendCareerMatches(activeTok);
      showToast('Recalculating career matches from backend…', 'info');
    }
  };

  // Compute Career Matches (authoritative backend matches when logged in, local preview for guests)
  const careerMatches: CareerMatchResult[] = (backendCareerMatches && backendCareerMatches.length > 0)
    ? backendCareerMatches
    : careers.map(career => 
        calculateCareerMatch(career, userProfile, questionnaireAnswers, questionnaire)
      ).sort((a, b) => b.matchScore - a.matchScore);

  // Selected Target Career object
  const selectedTargetCareer = (selectedTargetCareerId
    ? careers.find(c => c.id === selectedTargetCareerId) || null
    : null) || (careers.length > 0 && userRole !== 'guest' ? null : null);

  // Skill Gaps: derived from backend when authenticated, local calc for guest preview
  const skillGaps: SkillGapItem[] = (() => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok && backendSkillGap && backendSkillGap.skills) {
      // Map backend SkillGapItemResponse to frontend SkillGapItem
      return (backendSkillGap.skills as any[]).map((s: any): SkillGapItem => ({
        skillId: s.skillId,
        skillName: s.skillName,
        category: s.category,
        currentLevel: s.currentLevel ?? 0,
        requiredLevel: s.requiredLevel ?? 0,
        gapAmount: s.gapAmount ?? 0,
        severity: s.severity as any,
        isEssential: s.isEssential ?? false,
        recommendedAction: s.recommendedAction ?? ''
      }));
    }
    // Guest fallback: local calculation
    return selectedTargetCareer
      ? calculateSkillGaps(selectedTargetCareer, userProfile)
      : [];
  })();

  const selectTargetCareer = (careerId: string) => {
    setSelectedTargetCareerId(careerId);
    const target = careers.find(c => c.id === careerId);
    if (target) {
      showToast(`Selected "${target.title}" as Target Career!`, 'success');
    }

    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      // Load career-specific questionnaire from backend
      fetch(`/api/questionnaire/career/${careerId}`, {
        headers: { 'Authorization': `Bearer ${activeTok}` }
      })
      .then(r => r.ok ? r.json() : null)
      .then(qData => {
        if (qData && Array.isArray(qData) && qData.length > 0) {
          setQuestionnaire(qData);
        }
      })
      .catch(err => console.warn('Failed to fetch career-specific questionnaire:', err));

      fetch('/api/user/target-career', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${activeTok}`
        },
        body: JSON.stringify({ careerId })
      })
      .then(async () => {
        fetchBackendSkillGap(activeTok);
        // Try to load existing roadmap for new target; don't auto-generate
        const hadExisting = await fetchExistingRoadmap(activeTok);
        if (!hadExisting) {
          setActiveRoadmap(null); // Clear stale roadmap
        }
      })
      .catch(err => console.warn('Failed to persist target career:', err));
    }
  };

  // Roadmap state
  const [activeRoadmap, setActiveRoadmap] = useState<CareerRoadmap | null>(null);

  // For guest view: use local roadmap generation
  useEffect(() => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (!activeTok && selectedTargetCareer) {
      const defaultRoadmap = generateRoadmapForCareer(selectedTargetCareer, skillGaps);
      setActiveRoadmap(defaultRoadmap);
    }
  }, [selectedTargetCareerId, token]);

  // AI Narrative Enhancer Endpoint Call
  const enhanceRoadmapSummaryWithAI = async () => {
    if (!selectedTargetCareer || !activeRoadmap) return;
    const activeTok = token || localStorage.getItem('skillpilot_token');
    setAiEnhancing(true);
    showToast('Sending context to AI wording enhancer...', 'info');

    const topMatch = careerMatches.find(m => m.career.id === selectedTargetCareer.id);

    try {
      const headers: HeadersInit = { 'Content-Type': 'application/json' };
      if (activeTok) headers['Authorization'] = `Bearer ${activeTok}`;

      const res = await fetch('/api/ai/enhance-summary', {
        method: 'POST',
        headers,
        body: JSON.stringify({
          careerTitle: selectedTargetCareer.title,
          currentMatchScore: topMatch ? topMatch.matchScore : 85,
          keyStrengths: topMatch ? topMatch.keyStrengths : [],
          keyGaps: topMatch ? topMatch.keyGaps : [],
          targetRoleGoal: userProfile.targetFocus
        })
      });

      const data = await res.json();
      if (data && data.enhancedExplanation) {
        setActiveRoadmap(prev => prev ? {
          ...prev,
          aiExplanation: `AI-Polished Narrative: ${data.enhancedExplanation}`
        } : null);
        showToast('Roadmap explanation summary polished by AI!', 'success');
      }
    } catch (err) {
      console.error(err);
      showToast('AI enhancement complete with system fallback.', 'info');
    } finally {
      setAiEnhancing(false);
    }
  };

  // Admin CRUD Functions
  const addCareer = async (newCareer: Career) => {
    if (token) {
      try {
        const res = await fetch('/api/admin/careers', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            title: newCareer.title,
            category: newCareer.category,
            description: newCareer.description,
            averageSalary: newCareer.averageSalary,
            growthRate: newCareer.growthRate,
            demandLevel: newCareer.demandLevel,
            typicalRoles: newCareer.typicalRoles,
            recommendedPrerequisites: newCareer.recommendedPrerequisites
          })
        });
        if (res.ok) {
          const saved = await res.json();
          setCareers(prev => [saved, ...prev]);
          showToast(`Added new career: ${saved.title}`, 'success');
          return;
        }
      } catch (e) {
        console.error(e);
      }
    }
    setCareers(prev => [newCareer, ...prev]);
    showToast(`Added new career: ${newCareer.title}`, 'success');
  };

  const updateCareer = async (updated: Career) => {
    if (token) {
      try {
        const res = await fetch(`/api/admin/careers/${updated.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            title: updated.title,
            category: updated.category,
            description: updated.description,
            averageSalary: updated.averageSalary,
            growthRate: updated.growthRate,
            demandLevel: updated.demandLevel,
            typicalRoles: updated.typicalRoles,
            recommendedPrerequisites: updated.recommendedPrerequisites
          })
        });
        if (res.ok) {
          const saved = await res.json();
          setCareers(prev => prev.map(c => c.id === saved.id ? saved : c));
          showToast(`Updated career details for ${saved.title}`, 'success');
          return;
        }
      } catch (e) {
        console.error(e);
      }
    }
    setCareers(prev => prev.map(c => c.id === updated.id ? updated : c));
    showToast(`Updated career details for ${updated.title}`, 'success');
  };

  const deleteCareer = async (careerId: string) => {
    if (token) {
      try {
        await fetch(`/api/admin/careers/${careerId}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${token}` }
        });
      } catch (e) {
        console.error(e);
      }
    }
    setCareers(prev => prev.filter(c => c.id !== careerId));
    showToast(`Deactivated career record`, 'info');
  };

  const addQuestionItem = async (item: QuestionItem) => {
    if (token) {
      try {
        const res = await fetch('/api/admin/questionnaire', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            section: item.section || 'General',
            question: item.question,
            description: item.description,
            type: item.type || 'single',
            displayOrder: (item as any).displayOrder || 1
          })
        });
        if (res.ok) {
          const saved = await res.json();
          setQuestionnaire(prev => [...prev, saved]);
          showToast(`Added questionnaire item`, 'success');
          return;
        }
      } catch (e) {
        console.error(e);
      }
    }
    setQuestionnaire(prev => [...prev, item]);
    showToast(`Added questionnaire item`, 'success');
  };

  const deleteQuestionItem = async (itemId: string) => {
    if (token) {
      try {
        await fetch(`/api/admin/questionnaire/${itemId}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${token}` }
        });
      } catch (e) {
        console.error(e);
      }
    }
    setQuestionnaire(prev => prev.filter(q => q.id !== itemId));
    showToast(`Removed questionnaire item`, 'info');
  };

  const updateSystemConfig = async (newCfg: Partial<SystemConfig>) => {
    if (token) {
      try {
        const res = await fetch('/api/admin/config', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            technicalWeight: newCfg.technicalWeight,
            questionnaireWeight: newCfg.questionnaireWeight,
            essentialSkillPenalty: newCfg.essentialSkillPenalty,
            minimumMatchThreshold: newCfg.minimumMatchThreshold
          })
        });
        if (res.ok) {
          const saved = await res.json();
          setSystemConfig(prev => ({ ...prev, ...saved }));
          showToast('Updated system calculation weights in MySQL', 'success');
          return;
        }
      } catch (e) {
        console.error(e);
      }
    }
    setSystemConfig(prev => ({ ...prev, ...newCfg }));
    showToast('Updated system calculation weights', 'success');
  };

  const activateCareer = async (careerId: string) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch(`/api/admin/careers/${careerId}/activate`, {
          method: 'PUT',
          headers: { 'Authorization': `Bearer ${activeTok}` }
        });
        if (res.ok) {
          const updated = await res.json();
          setCareers(prev => prev.map(c => c.id === updated.id ? updated : c));
          showToast(`Reactivated career: ${updated.title}`, 'success');
        }
      } catch (e) {
        console.error('Error activating career:', e);
      }
    }
  };

  const activateSkill = async (skillId: string) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch(`/api/admin/skills/${skillId}/activate`, {
          method: 'PUT',
          headers: { 'Authorization': `Bearer ${activeTok}` }
        });
        if (res.ok) {
          const updated = await res.json();
          setSkillsList(prev => prev.map(s => s.id === updated.id ? updated : s));
          showToast(`Reactivated skill: ${updated.name}`, 'success');
        }
      } catch (e) {
        console.error('Error activating skill:', e);
      }
    }
  };

  const addCareerRequirement = async (careerId: string, req: { skillId: string; requiredLevel: number; isEssential?: boolean }) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch(`/api/admin/careers/${careerId}/requirements`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${activeTok}`
          },
          body: JSON.stringify(req)
        });
        if (res.ok) {
          const updatedCareer = await res.json();
          setCareers(prev => prev.map(c => c.id === updatedCareer.id ? updatedCareer : c));
          showToast(`Updated career skill requirements`, 'success');
        }
      } catch (e) {
        console.error('Error adding requirement:', e);
      }
    }
  };

  const deleteCareerRequirement = async (reqId: string) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        await fetch(`/api/admin/career-requirements/${reqId}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${activeTok}` }
        });
        showToast('Removed requirement', 'info');
      } catch (e) {
        console.error('Error deleting requirement:', e);
      }
    }
  };

  const addQuestionSkillMapping = async (req: { optionId: string; skillId: string; weight: number }) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch('/api/admin/question-skill-mappings', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${activeTok}`
          },
          body: JSON.stringify(req)
        });
        if (res.ok) {
          showToast('Added question skill mapping', 'success');
          refreshQuestionnaire();
        }
      } catch (e) {
        console.error('Error adding question skill mapping:', e);
      }
    }
  };

  const deleteQuestionSkillMapping = async (mappingId: string) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        await fetch(`/api/admin/question-skill-mappings/${mappingId}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${activeTok}` }
        });
        showToast('Removed question skill mapping', 'info');
        refreshQuestionnaire();
      } catch (e) {
        console.error('Error deleting question skill mapping:', e);
      }
    }
  };

  const refreshCareers = async () => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    const endpoint = activeTok ? '/api/admin/careers' : '/api/careers';
    const headers = activeTok ? { 'Authorization': `Bearer ${activeTok}` } : {};
    try {
      const res = await fetch(endpoint, { headers });
      if (res.ok) setCareers(await res.json());
    } catch (e) {
      console.error('Error refreshing careers:', e);
    }
  };

  const refreshSkills = async () => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    const endpoint = activeTok ? '/api/admin/skills' : '/api/skills';
    const headers = activeTok ? { 'Authorization': `Bearer ${activeTok}` } : {};
    try {
      const res = await fetch(endpoint, { headers });
      if (res.ok) setSkillsList(await res.json());
    } catch (e) {
      console.error('Error refreshing skills:', e);
    }
  };

  const refreshQuestionnaire = async () => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    const endpoint = activeTok ? '/api/admin/questionnaire' : '/api/questionnaire';
    const headers = activeTok ? { 'Authorization': `Bearer ${activeTok}` } : {};
    try {
      const res = await fetch(endpoint, { headers });
      if (res.ok) setQuestionnaire(await res.json());
    } catch (e) {
      console.error('Error refreshing questionnaire:', e);
    }
  };

  const addSkill = async (skill: { name: string; category: string; description?: string }) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch('/api/admin/skills', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${activeTok}` },
          body: JSON.stringify(skill)
        });
        if (res.ok) {
          const saved = await res.json();
          setSkillsList(prev => [...prev, saved]);
          showToast(`Added skill: ${saved.name}`, 'success');
          return;
        }
      } catch (e) {
        console.error('Error adding skill:', e);
      }
    }
  };

  const updateSkill = async (id: string, skill: { name: string; category: string; description?: string }) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch(`/api/admin/skills/${id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${activeTok}` },
          body: JSON.stringify(skill)
        });
        if (res.ok) {
          const saved = await res.json();
          setSkillsList(prev => prev.map(s => s.id === id ? saved : s));
          showToast(`Updated skill: ${saved.name}`, 'success');
          return;
        }
      } catch (e) {
        console.error('Error updating skill:', e);
      }
    }
  };

  const deleteSkill = async (id: string) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        await fetch(`/api/admin/skills/${id}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${activeTok}` }
        });
        setSkillsList(prev => prev.filter(s => s.id !== id));
        showToast('Deactivated skill', 'info');
      } catch (e) {
        console.error('Error deleting skill:', e);
      }
    }
  };

  const updateQuestionItem = async (item: QuestionItem) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch(`/api/admin/questionnaire/${item.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${activeTok}` },
          body: JSON.stringify({
            section: item.section,
            question: item.question,
            description: item.description,
            type: item.type,
            displayOrder: (item as any).displayOrder || 1
          })
        });
        if (res.ok) {
          showToast('Updated question item', 'success');
          refreshQuestionnaire();
        }
      } catch (e) {
        console.error('Error updating question:', e);
      }
    }
  };

  const addQuestionOption = async (questionId: string, optionText: string, displayOrder: number = 1) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch(`/api/admin/questions/${questionId}/options`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${activeTok}` },
          body: JSON.stringify({ optionText, displayOrder })
        });
        if (res.ok) {
          showToast('Added answer option', 'success');
          refreshQuestionnaire();
        }
      } catch (e) {
        console.error('Error adding option:', e);
      }
    }
  };

  const updateQuestionOption = async (optionId: string, optionText: string, displayOrder: number = 1) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        const res = await fetch(`/api/admin/question-options/${optionId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${activeTok}` },
          body: JSON.stringify({ optionText, displayOrder })
        });
        if (res.ok) {
          showToast('Updated answer option', 'success');
          refreshQuestionnaire();
        }
      } catch (e) {
        console.error('Error updating option:', e);
      }
    }
  };

  const deleteQuestionOption = async (optionId: string) => {
    const activeTok = token || localStorage.getItem('skillpilot_token');
    if (activeTok) {
      try {
        await fetch(`/api/admin/question-options/${optionId}`, {
          method: 'DELETE',
          headers: { 'Authorization': `Bearer ${activeTok}` }
        });
        showToast('Removed answer option', 'info');
        refreshQuestionnaire();
      } catch (e) {
        console.error('Error deleting option:', e);
      }
    }
  };

  return (
    <AppContext.Provider value={{
      userRole,
      setUserRole,
      activePage,
      navigateTo,
      isLoadingAuth,
      token,
      setToken,
      loginWithAuthData,
      userProfile,
      setUserProfile,
      updateUserSkill,
      questionnaire,
      questionnaireAnswers,
      saveQuestionAnswer,
      resetQuestionnaire,
      careers,
      selectedTargetCareer,
      selectTargetCareer,
      careerMatches,
      isLoadingMatches,
      recalculateCareerMatches,
      skillGaps,
      backendSkillGap,
      isLoadingSkillGap,
      activeRoadmap,
      isLoadingRoadmap,
      generateRoadmap,
      aiEnhancing,
      enhanceRoadmapSummaryWithAI,
      skillsList,
      systemConfig,
      updateSystemConfig,
      addCareer,
      updateCareer,
      deleteCareer,
      activateCareer,
      addSkill,
      updateSkill,
      deleteSkill,
      activateSkill,
      addCareerRequirement,
      deleteCareerRequirement,
      addQuestionSkillMapping,
      deleteQuestionSkillMapping,
      addQuestionItem,
      updateQuestionItem,
      deleteQuestionItem,
      addQuestionOption,
      updateQuestionOption,
      deleteQuestionOption,
      refreshCareers,
      refreshSkills,
      refreshQuestionnaire,
      toasts,
      showToast,
      removeToast
    }}>
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};
