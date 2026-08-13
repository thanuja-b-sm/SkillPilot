import React, { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { Career, QuestionItem, SkillMeta, QuestionOption } from '../types';
import { 
  ShieldCheck, 
  Briefcase, 
  Sliders, 
  HelpCircle, 
  Plus, 
  Trash2, 
  Edit, 
  Search, 
  Save, 
  X, 
  CheckCircle2, 
  Layers, 
  BarChart, 
  FileText,
  UserCheck,
  Loader2,
  Users,
  Map,
  AlertCircle,
  ChevronRight,
  ChevronDown,
  Zap,
  TrendingUp,
  DollarSign,
  Activity,
  Info,
  ExternalLink,
  Filter
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const { 
    userRole,
    careers, 
    skillsList, 
    questionnaire, 
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
    showToast,
    setUserRole,
    navigateTo,
    token
  } = useApp();

  const [activeTab, setActiveTab] = useState<'overview' | 'careers' | 'skills' | 'questionnaire' | 'weights'>('careers');
  const [searchTerm, setSearchTerm] = useState('');
  const [skillSearchTerm, setSkillSearchTerm] = useState('');
  const [questionSearchTerm, setQuestionSearchTerm] = useState('');
  const [activeFilter, setActiveFilter] = useState<'all' | 'active' | 'inactive'>('all');
  
  const [adminStats, setAdminStats] = useState<Record<string, any> | null>(null);
  const [healthData, setHealthData] = useState<Record<string, any> | null>(null);
  const [isLoadingStats, setIsLoadingStats] = useState(false);
  const [weightsForm, setWeightsForm] = useState({ ...systemConfig });

  // --- CAREER DETAIL & CONFIGURATION WORKSPACE STATE ---
  const [selectedCareerDetail, setSelectedCareerDetail] = useState<Career | null>(null);
  const [careerImpactData, setCareerImpactData] = useState<any | null>(null);
  const [careerQuestionnaireData, setCareerQuestionnaireData] = useState<QuestionItem[] | null>(null);
  const [isLoadingCareerImpact, setIsLoadingCareerImpact] = useState(false);
  const [isAddReqModalOpen, setIsAddReqModalOpen] = useState(false);
  const [reqFormSkillId, setReqFormSkillId] = useState('');
  const [reqFormLevel, setReqFormLevel] = useState<number>(3);
  const [reqFormIsEssential, setReqFormIsEssential] = useState(true);

  // --- SKILL IMPACT WORKSPACE STATE ---
  const [selectedSkillDetail, setSelectedSkillDetail] = useState<SkillMeta | null>(null);
  const [skillImpactData, setSkillImpactData] = useState<any | null>(null);
  const [isLoadingSkillImpact, setIsLoadingSkillImpact] = useState(false);
  const [isSkillModalOpen, setIsSkillModalOpen] = useState(false);
  const [editingSkill, setEditingSkill] = useState<SkillMeta | null>(null);
  const [skillFormData, setSkillFormData] = useState({ name: '', category: 'Technical', description: '' });

  // --- QUESTIONNAIRE WORKSPACE STATE ---
  const [expandedQuestionId, setExpandedQuestionId] = useState<string | null>(null);
  const [isQuestionModalOpen, setIsQuestionModalOpen] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState<QuestionItem | null>(null);
  const [questionFormData, setQuestionFormData] = useState({ section: 'General', question: '', description: '', type: 'single' as 'single' | 'multiple' | 'scale', displayOrder: 1 });
  const [isOptionModalOpen, setIsOptionModalOpen] = useState(false);
  const [targetQuestionForOption, setTargetQuestionForOption] = useState<string | null>(null);
  const [editingOption, setEditingOption] = useState<QuestionOption | null>(null);
  const [optionFormData, setOptionFormData] = useState({ optionText: '', displayOrder: 1 });

  // --- MAPPING MODAL STATE ---
  const [isMappingModalOpen, setIsMappingModalOpen] = useState(false);
  const [targetOptionIdForMapping, setTargetOptionIdForMapping] = useState<string | null>(null);
  const [mappingSkillId, setMappingSkillId] = useState('');
  const [mappingWeight, setMappingWeight] = useState<number>(3);

  // --- CAREER CREATE/EDIT MODAL STATE ---
  const [isCareerModalOpen, setIsCareerModalOpen] = useState(false);
  const [editingCareer, setEditingCareer] = useState<Career | null>(null);
  const [careerFormData, setCareerFormData] = useState<Partial<Career>>({
    title: '',
    category: 'Software Engineering',
    description: '',
    averageSalary: '$120,000 - $160,000 / yr',
    growthRate: '+20%',
    demandLevel: 'High',
    typicalRoles: ['Software Engineer'],
    recommendedPrerequisites: ['Computer Science Fundamentals'],
    requiredSkills: []
  });

  // Synchronize admin master data on dashboard mount
  useEffect(() => {
    refreshCareers();
    refreshSkills();
    refreshQuestionnaire();
  }, []);

  // System Stats & Health fetch effect
  useEffect(() => {
    if (activeTab === 'overview' || activeTab === 'weights') {
      const tok = token || localStorage.getItem('skillpilot_token');
      if (!tok) return;
      setIsLoadingStats(true);
      Promise.all([
        fetch('/api/admin/stats', { headers: { 'Authorization': `Bearer ${tok}` } }).then(r => r.ok ? r.json() : null).catch(() => null),
        fetch('/api/admin/health-check', { headers: { 'Authorization': `Bearer ${tok}` } }).then(r => r.ok ? r.json() : null).catch(() => null)
      ])
      .then(([stats, health]) => {
        if (stats) setAdminStats(stats);
        if (health) setHealthData(health);
      })
      .catch(err => console.warn('Failed to fetch admin stats:', err))
      .finally(() => setIsLoadingStats(false));
    }
  }, [activeTab, token]);

  // Load Career Impact & Relevant Questions when detail view opens
  const handleOpenCareerDetail = async (career: Career) => {
    setSelectedCareerDetail(career);
    const tok = token || localStorage.getItem('skillpilot_token');
    if (!tok) return;
    setIsLoadingCareerImpact(true);
    try {
      const [impactRes, questRes] = await Promise.all([
        fetch(`/api/admin/careers/${career.id}/impact`, { headers: { 'Authorization': `Bearer ${tok}` } }),
        fetch(`/api/questionnaire/career/${career.id}`, { headers: { 'Authorization': `Bearer ${tok}` } })
      ]);
      if (impactRes.ok) {
        setCareerImpactData(await impactRes.json());
      }
      if (questRes.ok) {
        setCareerQuestionnaireData(await questRes.json());
      }
    } catch (err) {
      console.error('Error fetching career impact:', err);
    } finally {
      setIsLoadingCareerImpact(false);
    }
  };

  // Load Skill Impact when skill detail modal opens
  const handleOpenSkillDetail = async (skill: SkillMeta) => {
    setSelectedSkillDetail(skill);
    const tok = token || localStorage.getItem('skillpilot_token');
    if (!tok) return;
    setIsLoadingSkillImpact(true);
    try {
      const res = await fetch(`/api/admin/skills/${skill.id}/impact`, { headers: { 'Authorization': `Bearer ${tok}` } });
      if (res.ok) {
        setSkillImpactData(await res.json());
      }
    } catch (err) {
      console.error('Error fetching skill impact:', err);
    } finally {
      setIsLoadingSkillImpact(false);
    }
  };

  if (userRole !== 'admin') {
    return (
      <div className="max-w-md mx-auto my-16 p-8 bg-white rounded-3xl border border-slate-200 shadow-xl text-center space-y-6">
        <div className="w-16 h-16 rounded-2xl bg-red-100 text-red-600 flex items-center justify-center mx-auto shadow-sm">
          <ShieldCheck className="w-8 h-8" />
        </div>
        <div className="space-y-2">
          <h2 className="text-2xl font-extrabold text-slate-950">Unauthorized Access</h2>
          <p className="text-xs text-slate-600 leading-relaxed font-normal">
            The Admin Console is restricted strictly to administrator accounts. Please sign in with admin credentials to continue.
          </p>
        </div>
        <button
          onClick={() => navigateTo('login')}
          className="w-full py-3 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold text-sm rounded-xl shadow-md transition-all cursor-pointer"
        >
          Go to Sign In
        </button>
      </div>
    );
  }

  // Filtering Logic
  const filteredCareers = careers.filter(c => {
    const matchesSearch = c.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.category.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesActive = activeFilter === 'all' ? true : activeFilter === 'active' ? c.isActive !== false : c.isActive === false;
    return matchesSearch && matchesActive;
  });

  const filteredSkills = skillsList.filter(s => {
    const matchesSearch = s.name.toLowerCase().includes(skillSearchTerm.toLowerCase()) ||
      s.category.toLowerCase().includes(skillSearchTerm.toLowerCase()) ||
      s.id.toLowerCase().includes(skillSearchTerm.toLowerCase());
    const matchesActive = activeFilter === 'all' ? true : activeFilter === 'active' ? (s as any).isActive !== false : (s as any).isActive === false;
    return matchesSearch && matchesActive;
  });

  const filteredQuestions = questionnaire.filter(q => {
    const matchesSearch = q.question.toLowerCase().includes(questionSearchTerm.toLowerCase()) ||
      q.section.toLowerCase().includes(questionSearchTerm.toLowerCase());
    const matchesActive = activeFilter === 'all' ? true : activeFilter === 'active' ? (q as any).isActive !== false : (q as any).isActive === false;
    return matchesSearch && matchesActive;
  });

  // Handlers for Modals
  const handleOpenCreateCareer = () => {
    setEditingCareer(null);
    setCareerFormData({
      title: '',
      category: 'Software Engineering',
      description: '',
      averageSalary: '$130,000 - $170,000 / yr',
      growthRate: '+22%',
      demandLevel: 'High',
      typicalRoles: ['Engineering Lead'],
      recommendedPrerequisites: ['Programming Basics'],
      requiredSkills: []
    });
    setIsCareerModalOpen(true);
  };

  const handleOpenEditCareer = (career: Career) => {
    setEditingCareer(career);
    setCareerFormData({ ...career });
    setIsCareerModalOpen(true);
  };

  const handleSaveCareer = (e: React.FormEvent) => {
    e.preventDefault();
    if (!careerFormData.title || !careerFormData.description) {
      showToast('Career Title and Description are required', 'warning');
      return;
    }

    if (editingCareer) {
      updateCareer(careerFormData as Career);
    } else {
      addCareer(careerFormData as Career);
    }
    setIsCareerModalOpen(false);
  };

  const handleAddRequirement = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedCareerDetail || !reqFormSkillId) {
      showToast('Select a valid skill to assign', 'warning');
      return;
    }
    await addCareerRequirement(selectedCareerDetail.id, {
      skillId: reqFormSkillId,
      requiredLevel: reqFormLevel,
      isEssential: reqFormIsEssential
    });
    setIsAddReqModalOpen(false);
    // Refresh Career detail & impact
    refreshCareers();
    handleOpenCareerDetail(selectedCareerDetail);
  };

  const handleDeleteRequirement = async (reqSkillId: string) => {
    if (!selectedCareerDetail) return;
    await deleteCareerRequirement(reqSkillId);
    refreshCareers();
    handleOpenCareerDetail(selectedCareerDetail);
  };

  const handleSaveSkill = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!skillFormData.name) {
      showToast('Skill name is required', 'warning');
      return;
    }
    if (editingSkill) {
      await updateSkill(editingSkill.id, skillFormData);
    } else {
      await addSkill(skillFormData);
    }
    setIsSkillModalOpen(false);
    refreshSkills();
  };

  const handleSaveQuestion = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!questionFormData.question) {
      showToast('Question text is required', 'warning');
      return;
    }
    if (editingQuestion) {
      await updateQuestionItem({
        id: editingQuestion.id,
        section: questionFormData.section,
        question: questionFormData.question,
        description: questionFormData.description,
        type: questionFormData.type,
        options: editingQuestion.options
      });
    } else {
      addQuestionItem({
        id: `q-${Date.now()}`,
        section: questionFormData.section,
        question: questionFormData.question,
        description: questionFormData.description,
        type: questionFormData.type,
        options: []
      });
    }
    setIsQuestionModalOpen(false);
  };

  const handleSaveOption = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!optionFormData.optionText || !targetQuestionForOption) {
      showToast('Option text is required', 'warning');
      return;
    }
    if (editingOption) {
      await updateQuestionOption(editingOption.id, optionFormData.optionText, optionFormData.displayOrder);
    } else {
      await addQuestionOption(targetQuestionForOption, optionFormData.optionText, optionFormData.displayOrder);
    }
    setIsOptionModalOpen(false);
  };

  const handleSaveMapping = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!targetOptionIdForMapping || !mappingSkillId) {
      showToast('Select a skill to map', 'warning');
      return;
    }
    await addQuestionSkillMapping({
      optionId: targetOptionIdForMapping,
      skillId: mappingSkillId,
      weight: mappingWeight
    });
    setIsMappingModalOpen(false);
  };

  const handleSaveWeights = (e: React.FormEvent) => {
    e.preventDefault();
    updateSystemConfig(weightsForm);
  };

  return (
    <div className="max-w-7xl mx-auto my-6 grid grid-cols-1 lg:grid-cols-12 gap-8 text-left pb-16">
      
      {/* Admin Sidebar Navigation */}
      <div className="lg:col-span-3 space-y-4">
        <div className="bg-slate-900 text-white rounded-3xl p-6 shadow-md border border-slate-800 space-y-4">
          <div className="flex items-center gap-2.5 pb-3 border-b border-slate-800">
            <ShieldCheck className="w-6 h-6 text-amber-400" />
            <div>
              <h2 className="text-base font-bold text-white">Admin Console</h2>
              <p className="text-[11px] text-slate-400">SkillPilot Intelligence Engine</p>
            </div>
          </div>

          <nav className="space-y-1 text-xs font-semibold">
            <button
              onClick={() => { setActiveTab('careers'); setSelectedCareerDetail(null); }}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'careers' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><Briefcase className="w-4 h-4" /> Careers Workspace</span>
              <span className="bg-slate-800 text-slate-300 text-[10px] px-2 py-0.5 rounded-full">{careers.length}</span>
            </button>

            <button
              onClick={() => { setActiveTab('skills'); setSelectedSkillDetail(null); }}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'skills' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><Sliders className="w-4 h-4" /> Skills Dictionary</span>
              <span className="bg-slate-800 text-slate-300 text-[10px] px-2 py-0.5 rounded-full">{skillsList.length}</span>
            </button>

            <button
              onClick={() => setActiveTab('questionnaire')}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'questionnaire' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><HelpCircle className="w-4 h-4" /> Questionnaire Builder</span>
              <span className="bg-slate-800 text-slate-300 text-[10px] px-2 py-0.5 rounded-full">{questionnaire.length}</span>
            </button>

            <button
              onClick={() => setActiveTab('weights')}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'weights' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><BarChart className="w-4 h-4" /> Scoring Algorithm</span>
              <span className="bg-emerald-500/20 text-emerald-300 text-[10px] px-2 py-0.5 rounded-full font-extrabold">v2.4</span>
            </button>

            <button
              onClick={() => setActiveTab('overview')}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'overview' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><Layers className="w-4 h-4" /> Platform System Health</span>
              {healthData && (
                <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold ${
                  healthData.status === 'HEALTHY' ? 'bg-emerald-500/20 text-emerald-400' : 'bg-amber-500/20 text-amber-400'
                }`}>{healthData.status}</span>
              )}
            </button>
          </nav>

          <div className="pt-4 border-t border-slate-800 text-[11px] text-slate-400 space-y-2">
            <div className="flex items-center justify-between">
              <span>Database Sync</span>
              <span className="text-emerald-400 font-bold flex items-center gap-1"><CheckCircle2 className="w-3 h-3" /> MySQL Active</span>
            </div>
            <p className="text-[10px] text-slate-500 leading-tight">
              All changes are persisted immediately to the authoritative backend database.
            </p>
          </div>
        </div>
      </div>

      {/* Main Workspace Panels */}
      <div className="lg:col-span-9 space-y-6">
        
        {/* ========================================================================= */}
        {/* 1. CAREERS WORKSPACE PANEL */}
        {/* ========================================================================= */}
        {activeTab === 'careers' && !selectedCareerDetail && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
              <div>
                <h2 className="text-xl font-extrabold text-slate-950 flex items-center gap-2">
                  <Briefcase className="w-5 h-5 text-blue-600" /> Career Intelligence Profiles
                </h2>
                <p className="text-xs text-slate-500 mt-0.5">Manage target roles, skill requirements, and readiness criteria.</p>
              </div>

              <button
                onClick={handleOpenCreateCareer}
                className="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-1.5 shrink-0"
              >
                <Plus className="w-4 h-4" /> Create Career Profile
              </button>
            </div>

            {/* Filter Controls */}
            <div className="flex flex-col sm:flex-row gap-3 items-center justify-between">
              <div className="relative w-full sm:w-80">
                <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  type="text"
                  placeholder="Search by title or category…"
                  value={searchTerm}
                  onChange={e => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-xl text-xs font-semibold self-start sm:self-auto">
                <button
                  onClick={() => setActiveFilter('all')}
                  className={`px-3 py-1 rounded-lg transition-all ${activeFilter === 'all' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'}`}
                >All ({careers.length})</button>
                <button
                  onClick={() => setActiveFilter('active')}
                  className={`px-3 py-1 rounded-lg transition-all ${activeFilter === 'active' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'}`}
                >Active</button>
                <button
                  onClick={() => setActiveFilter('inactive')}
                  className={`px-3 py-1 rounded-lg transition-all ${activeFilter === 'inactive' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'}`}
                >Inactive</button>
              </div>
            </div>

            {/* Careers Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {filteredCareers.map(career => (
                <div key={career.id} className="p-5 rounded-2xl border border-slate-200 hover:border-blue-300 bg-white transition-all space-y-4 shadow-xs">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <span className="text-[10px] font-bold text-blue-600 uppercase tracking-wide">{career.category}</span>
                      <h3 className="text-base font-bold text-slate-950 leading-snug">{career.title}</h3>
                    </div>
                    <span className={`px-2 py-0.5 text-[10px] font-extrabold rounded-full ${
                      career.isActive !== false ? 'bg-emerald-100 text-emerald-700 border border-emerald-200' : 'bg-slate-100 text-slate-500'
                    }`}>
                      {career.isActive !== false ? 'ACTIVE' : 'INACTIVE'}
                    </span>
                  </div>

                  <p className="text-xs text-slate-600 line-clamp-2">{career.description}</p>

                  <div className="pt-2 flex items-center justify-between border-t border-slate-100 text-[11px] font-medium text-slate-500">
                    <span className="flex items-center gap-1 font-bold text-slate-800">
                      <Layers className="w-3.5 h-3.5 text-blue-600" /> {career.requiredSkills?.length || 0} Required Skills
                    </span>
                    <span>{career.averageSalary}</span>
                  </div>

                  <div className="flex items-center gap-2 pt-1">
                    <button
                      onClick={() => handleOpenCareerDetail(career)}
                      className="flex-1 py-2 bg-blue-50 hover:bg-blue-100 text-blue-700 font-bold text-xs rounded-xl transition-colors flex items-center justify-center gap-1"
                    >
                      <Sliders className="w-3.5 h-3.5" /> Configure Requirements
                    </button>
                    <button
                      onClick={() => handleOpenEditCareer(career)}
                      className="p-2 text-slate-500 hover:text-blue-600 hover:bg-slate-100 rounded-xl transition-colors"
                      title="Edit Metadata"
                    >
                      <Edit className="w-4 h-4" />
                    </button>
                    {career.isActive !== false ? (
                      <button
                        onClick={() => deleteCareer(career.id)}
                        className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-xl transition-colors"
                        title="Deactivate Career"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    ) : (
                      <button
                        onClick={() => activateCareer(career.id)}
                        className="p-2 text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 rounded-xl transition-colors"
                        title="Reactivate Career"
                      >
                        <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* CAREER DETAIL & REQUIREMENTS CONFIGURATION WORKSPACE */}
        {activeTab === 'careers' && selectedCareerDetail && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-md space-y-6">
            <div className="flex items-center justify-between pb-4 border-b border-slate-100">
              <button
                onClick={() => setSelectedCareerDetail(null)}
                className="text-xs font-bold text-blue-600 hover:underline flex items-center gap-1"
              >
                ← Back to Careers List
              </button>
              <span className={`px-2.5 py-0.5 text-xs font-extrabold rounded-full ${
                selectedCareerDetail.isActive !== false ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-100 text-slate-500'
              }`}>
                {selectedCareerDetail.isActive !== false ? 'ACTIVE CAREER' : 'INACTIVE'}
              </span>
            </div>

            {/* Career Header & Impact Summary */}
            <div className="space-y-4">
              <div>
                <span className="text-xs font-bold text-blue-600 uppercase tracking-wide">{selectedCareerDetail.category}</span>
                <h2 className="text-2xl font-black text-slate-950">{selectedCareerDetail.title}</h2>
                <p className="text-xs text-slate-600 mt-1 leading-relaxed">{selectedCareerDetail.description}</p>
              </div>

              {/* Impact & Configuration Health Box */}
              {isLoadingCareerImpact ? (
                <div className="p-4 bg-slate-50 rounded-2xl flex items-center gap-2 text-xs text-slate-500">
                  <Loader2 className="w-4 h-4 animate-spin text-blue-600" /> Loading configuration impact statistics…
                </div>
              ) : careerImpactData ? (
                <div className="p-4 bg-slate-900 text-white rounded-2xl space-y-3">
                  <div className="flex items-center justify-between border-b border-slate-800 pb-2">
                    <span className="text-xs font-bold text-slate-300 flex items-center gap-1.5">
                      <Activity className="w-4 h-4 text-emerald-400" /> Configuration Health & Impact Audit
                    </span>
                    <span className={`text-[10px] font-black px-2 py-0.5 rounded-md ${
                      careerImpactData.isConfigurationComplete ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' : 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                    }`}>
                      {careerImpactData.isConfigurationComplete ? 'CONFIGURATION COMPLETE' : 'INCOMPLETE CONFIG'}
                    </span>
                  </div>

                  <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-center">
                    <div className="bg-slate-800 p-2.5 rounded-xl">
                      <p className="text-lg font-black text-blue-400">{careerImpactData.requiredSkillCount}</p>
                      <p className="text-[10px] text-slate-400">Required Skills</p>
                    </div>
                    <div className="bg-slate-800 p-2.5 rounded-xl">
                      <p className="text-lg font-black text-amber-400">{careerImpactData.essentialSkillCount}</p>
                      <p className="text-[10px] text-slate-400">Essential Skills</p>
                    </div>
                    <div className="bg-slate-800 p-2.5 rounded-xl">
                      <p className="text-lg font-black text-purple-400">{careerImpactData.activeMatchResultCount}</p>
                      <p className="text-[10px] text-slate-400">User Matches</p>
                    </div>
                    <div className="bg-slate-800 p-2.5 rounded-xl">
                      <p className="text-lg font-black text-teal-400">{careerImpactData.activeRoadmapCount}</p>
                      <p className="text-[10px] text-slate-400">Active Roadmaps</p>
                    </div>
                  </div>
                </div>
              ) : null}
            </div>

            {/* Required Skills Management Table */}
            <div className="space-y-4 pt-4 border-t border-slate-100">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-base font-bold text-slate-950 flex items-center gap-2">
                    <Sliders className="w-4 h-4 text-blue-600" /> Required Skills Matrix
                  </h3>
                  <p className="text-xs text-slate-500">Configure target skill levels (1-5) and essential penalty rules for this career.</p>
                </div>
                <button
                  onClick={() => setIsAddReqModalOpen(true)}
                  className="px-3.5 py-2 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl transition-colors flex items-center gap-1.5"
                >
                  <Plus className="w-3.5 h-3.5" /> Add Skill Requirement
                </button>
              </div>

              <div className="overflow-x-auto border border-slate-200 rounded-2xl">
                <table className="w-full text-left border-collapse text-xs">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-200 font-bold text-slate-700">
                      <th className="py-3 px-4">Skill Name</th>
                      <th className="py-3 px-4">Category</th>
                      <th className="py-3 px-4 text-center">Required Level</th>
                      <th className="py-3 px-4 text-center">Essential</th>
                      <th className="py-3 px-4 text-right">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 font-medium">
                    {selectedCareerDetail.requiredSkills?.map((req, idx) => (
                      <tr key={idx} className="hover:bg-slate-50/80 transition-colors">
                        <td className="py-3 px-4 font-bold text-slate-900">{req.skillName}</td>
                        <td className="py-3 px-4 text-slate-500">{req.category}</td>
                        <td className="py-3 px-4 text-center font-bold text-blue-600">Level {req.requiredLevel} / 5</td>
                        <td className="py-3 px-4 text-center">
                          <span className={`px-2 py-0.5 rounded-full text-[10px] font-extrabold ${
                            req.isEssential ? 'bg-amber-100 text-amber-800' : 'bg-slate-100 text-slate-600'
                          }`}>
                            {req.isEssential ? 'ESSENTIAL' : 'OPTIONAL'}
                          </span>
                        </td>
                        <td className="py-3 px-4 text-right">
                          <button
                            onClick={() => handleDeleteRequirement(req.skillId)}
                            className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                            title="Remove Requirement"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </td>
                      </tr>
                    ))}
                    {(!selectedCareerDetail.requiredSkills || selectedCareerDetail.requiredSkills.length === 0) && (
                      <tr>
                        <td colSpan={5} className="py-8 text-center text-slate-400">
                          No skill requirements configured for this career profile yet. Click "Add Skill Requirement" to assign skills.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Relevant Questionnaire Relationship Section */}
            <div className="space-y-3 pt-4 border-t border-slate-100">
              <h3 className="text-base font-bold text-slate-950 flex items-center gap-2">
                <HelpCircle className="w-4 h-4 text-blue-600" /> Career Questionnaire Relevance
              </h3>
              <p className="text-xs text-slate-500">
                Questions automatically mapped to this career based on required skill option relationships in MySQL.
              </p>

              {careerQuestionnaireData && careerQuestionnaireData.length > 0 ? (
                <div className="space-y-2">
                  {careerQuestionnaireData.map(q => (
                    <div key={q.id} className="p-3 bg-slate-50 border border-slate-200 rounded-xl space-y-1 text-xs">
                      <div className="flex items-center justify-between font-bold text-slate-900">
                        <span>{q.question}</span>
                        <span className="text-[10px] bg-blue-100 text-blue-800 px-2 py-0.5 rounded-md uppercase">{q.section}</span>
                      </div>
                      <p className="text-[11px] text-slate-500">{q.options?.length || 0} answer options mapped</p>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="p-4 bg-slate-50 rounded-xl text-xs text-slate-500 text-center">
                  No questionnaire items map to this career's skills yet. Configure option-skill mappings in the Questionnaire tab.
                </div>
              )}
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* 2. SKILLS DICTIONARY WORKSPACE PANEL */}
        {/* ========================================================================= */}
        {activeTab === 'skills' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
              <div>
                <h2 className="text-xl font-extrabold text-slate-950 flex items-center gap-2">
                  <Sliders className="w-5 h-5 text-blue-600" /> Master Skills Dictionary
                </h2>
                <p className="text-xs text-slate-500 mt-0.5">Manage skill catalog, category taxonomy, and cross-career dependencies.</p>
              </div>

              <button
                onClick={() => { setEditingSkill(null); setSkillFormData({ name: '', category: 'Technical', description: '' }); setIsSkillModalOpen(true); }}
                className="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-1.5 shrink-0"
              >
                <Plus className="w-4 h-4" /> Add New Skill
              </button>
            </div>

            {/* Filter Controls */}
            <div className="flex flex-col sm:flex-row gap-3 items-center justify-between">
              <div className="relative w-full sm:w-80">
                <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  type="text"
                  placeholder="Search skills by name or category…"
                  value={skillSearchTerm}
                  onChange={e => setSkillSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 text-xs bg-slate-50 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-xl text-xs font-semibold">
                <button
                  onClick={() => setActiveFilter('all')}
                  className={`px-3 py-1 rounded-lg transition-all ${activeFilter === 'all' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'}`}
                >All ({skillsList.length})</button>
                <button
                  onClick={() => setActiveFilter('active')}
                  className={`px-3 py-1 rounded-lg transition-all ${activeFilter === 'active' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'}`}
                >Active</button>
                <button
                  onClick={() => setActiveFilter('inactive')}
                  className={`px-3 py-1 rounded-lg transition-all ${activeFilter === 'inactive' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'}`}
                >Inactive</button>
              </div>
            </div>

            {/* Skills Table */}
            <div className="overflow-x-auto border border-slate-200 rounded-2xl">
              <table className="w-full text-left border-collapse text-xs">
                <thead>
                  <tr className="bg-slate-50 border-b border-slate-200 font-bold text-slate-700">
                    <th className="py-3 px-4">Skill Name</th>
                    <th className="py-3 px-4">Category</th>
                    <th className="py-3 px-4">Description</th>
                    <th className="py-3 px-4 text-center">Status</th>
                    <th className="py-3 px-4 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 font-medium">
                  {filteredSkills.map(skill => (
                    <tr key={skill.id} className="hover:bg-slate-50/80 transition-colors">
                      <td className="py-3.5 px-4 font-bold text-slate-900">{skill.name}</td>
                      <td className="py-3.5 px-4">
                        <span className="bg-slate-100 text-slate-700 text-[10px] font-bold px-2 py-0.5 rounded-md uppercase">{skill.category}</span>
                      </td>
                      <td className="py-3.5 px-4 text-slate-500 max-w-xs truncate">{skill.description || '—'}</td>
                      <td className="py-3.5 px-4 text-center">
                        <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-extrabold ${
                          (skill as any).isActive !== false ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-100 text-slate-500'
                        }`}>
                          {(skill as any).isActive !== false ? 'ACTIVE' : 'INACTIVE'}
                        </span>
                      </td>
                      <td className="py-3.5 px-4 text-right space-x-1">
                        <button
                          onClick={() => handleOpenSkillDetail(skill)}
                          className="px-2.5 py-1 bg-blue-50 hover:bg-blue-100 text-blue-700 font-bold text-[11px] rounded-lg transition-colors"
                        >
                          Impact Detail
                        </button>
                        <button
                          onClick={() => { setEditingSkill(skill); setSkillFormData({ name: skill.name, category: skill.category, description: skill.description || '' }); setIsSkillModalOpen(true); }}
                          className="p-1.5 text-slate-400 hover:text-blue-600 rounded-lg transition-colors"
                          title="Edit Skill"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        {(skill as any).isActive !== false ? (
                          <button
                            onClick={() => deleteSkill(skill.id)}
                            className="p-1.5 text-slate-400 hover:text-red-600 rounded-lg transition-colors"
                            title="Deactivate Skill"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        ) : (
                          <button
                            onClick={() => activateSkill(skill.id)}
                            className="p-1.5 text-slate-400 hover:text-emerald-600 rounded-lg transition-colors"
                            title="Reactivate Skill"
                          >
                            <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* 3. QUESTIONNAIRE BUILDER WORKSPACE PANEL */}
        {/* ========================================================================= */}
        {activeTab === 'questionnaire' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
              <div>
                <h2 className="text-xl font-extrabold text-slate-950 flex items-center gap-2">
                  <HelpCircle className="w-5 h-5 text-blue-600" /> Questionnaire Builder & Option Skill Mappings
                </h2>
                <p className="text-xs text-slate-500 mt-0.5">Configure survey scenario questions, answer options, and skill mapping weights.</p>
              </div>

              <button
                onClick={() => { setEditingQuestion(null); setQuestionFormData({ section: 'General', question: '', description: '', type: 'single', displayOrder: questionnaire.length + 1 }); setIsQuestionModalOpen(true); }}
                className="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-1.5 shrink-0"
              >
                <Plus className="w-4 h-4" /> New Question Item
              </button>
            </div>

            {/* Questions List */}
            <div className="space-y-4">
              {filteredQuestions.map(q => {
                const isExpanded = expandedQuestionId === q.id;
                return (
                  <div key={q.id} className="border border-slate-200 rounded-2xl overflow-hidden shadow-xs bg-white">
                    <div
                      onClick={() => setExpandedQuestionId(isExpanded ? null : q.id)}
                      className="p-4 bg-slate-50 hover:bg-slate-100/80 transition-colors flex items-center justify-between cursor-pointer"
                    >
                      <div className="flex items-center gap-3">
                        {isExpanded ? <ChevronDown className="w-4 h-4 text-blue-600" /> : <ChevronRight className="w-4 h-4 text-slate-400" />}
                        <div>
                          <div className="flex items-center gap-2">
                            <span className="text-[10px] font-extrabold bg-blue-100 text-blue-800 px-2 py-0.5 rounded-md uppercase">{q.section}</span>
                            <span className="text-[10px] font-bold bg-slate-200 text-slate-700 px-2 py-0.5 rounded-md uppercase">{q.type}</span>
                          </div>
                          <h4 className="text-sm font-bold text-slate-950 mt-1">{q.question}</h4>
                        </div>
                      </div>

                      <div className="flex items-center gap-3">
                        <span className="text-xs text-slate-500 font-bold">{q.options?.length || 0} Options</span>
                        <button
                          onClick={(e) => { e.stopPropagation(); setEditingQuestion(q); setQuestionFormData({ section: q.section, question: q.question, description: q.description || '', type: q.type, displayOrder: (q as any).displayOrder || 1 }); setIsQuestionModalOpen(true); }}
                          className="p-1.5 text-slate-400 hover:text-blue-600 rounded-lg transition-colors"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        <button
                          onClick={(e) => { e.stopPropagation(); deleteQuestionItem(q.id); }}
                          className="p-1.5 text-slate-400 hover:text-red-600 rounded-lg transition-colors"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>

                    {/* Expandable Options & Skill Mappings List */}
                    {isExpanded && (
                      <div className="p-5 border-t border-slate-200 bg-white space-y-4 text-xs">
                        <div className="flex items-center justify-between">
                          <h5 className="font-bold text-slate-800 text-xs">Answer Options & Skill Mapping Weights</h5>
                          <button
                            onClick={() => { setTargetQuestionForOption(q.id); setEditingOption(null); setOptionFormData({ optionText: '', displayOrder: (q.options?.length || 0) + 1 }); setIsOptionModalOpen(true); }}
                            className="px-3 py-1.5 bg-blue-50 text-blue-700 hover:bg-blue-100 font-bold rounded-lg transition-colors flex items-center gap-1"
                          >
                            <Plus className="w-3.5 h-3.5" /> Add Option
                          </button>
                        </div>

                        <div className="space-y-3">
                          {q.options?.map(opt => (
                            <div key={opt.id} className="p-3.5 bg-slate-50 border border-slate-200 rounded-xl space-y-2">
                              <div className="flex items-center justify-between">
                                <span className="font-bold text-slate-900">{opt.text}</span>
                                <div className="flex items-center gap-2">
                                  <button
                                    onClick={() => { setTargetOptionIdForMapping(opt.id); setIsMappingModalOpen(true); }}
                                    className="px-2.5 py-1 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 font-bold rounded-md text-[10px] flex items-center gap-1"
                                  >
                                    <Plus className="w-3 h-3" /> Map Skill
                                  </button>
                                  <button
                                    onClick={() => { setTargetQuestionForOption(q.id); setEditingOption(opt); setOptionFormData({ optionText: opt.text, displayOrder: opt.weightMultiplier || 1 }); setIsOptionModalOpen(true); }}
                                    className="text-slate-400 hover:text-blue-600 p-1"
                                  >
                                    <Edit className="w-3.5 h-3.5" />
                                  </button>
                                  <button
                                    onClick={() => deleteQuestionOption(opt.id)}
                                    className="text-slate-400 hover:text-red-600 p-1"
                                  >
                                    <Trash2 className="w-3.5 h-3.5" />
                                  </button>
                                </div>
                              </div>

                              {/* Mapped Skills List */}
                              <div className="pt-2 border-t border-slate-200 flex flex-wrap gap-2 items-center">
                                <span className="text-[10px] text-slate-400 uppercase font-bold">Mapped Skills:</span>
                                {opt.associatedSkills?.map((m: any, mIdx) => (
                                  <span key={mIdx} className="bg-white border border-slate-200 rounded-lg px-2.5 py-1 flex items-center gap-2 text-[11px] shadow-2xs font-semibold">
                                    <span>{skillsList.find(s => s.id === m.skillId)?.name || m.skillId}</span>
                                    <span className="text-blue-600 font-bold">Weight: {m.weight}</span>
                                    <button
                                      onClick={() => deleteQuestionSkillMapping(m.id || m.mappingId)}
                                      className="text-slate-300 hover:text-red-600 ml-1"
                                    >
                                      ×
                                    </button>
                                  </span>
                                ))}
                                {(!opt.associatedSkills || opt.associatedSkills.length === 0) && (
                                  <span className="text-[10px] text-slate-400 italic">No skills mapped to this option yet.</span>
                                )}
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* ========================================================================= */}
        {/* 4. SCORING ALGORITHM WORKSPACE PANEL */}
        {/* ========================================================================= */}
        {activeTab === 'weights' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="pb-4 border-b border-slate-100">
              <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                <BarChart className="w-5 h-5 text-blue-600" /> Algorithmic Formula & Weight Parameters
              </h2>
              <p className="text-xs text-slate-500">Adjust mathematical coefficients for match score generation.</p>
            </div>

            <div className="p-3.5 bg-blue-50 border border-blue-200 rounded-2xl flex items-start gap-2.5">
              <AlertCircle className="w-4 h-4 text-blue-600 shrink-0 mt-0.5" />
              <p className="text-[11px] text-blue-900 leading-relaxed font-medium">
                <strong>Scoring Calculation Impact Warning:</strong> Adjusting system config parameters updates <em>FUTURE</em> career match scores, skill-gap readiness calculations, and roadmap prioritization. Existing historical result snapshots remain preserved in MySQL.
              </p>
            </div>

            <form onSubmit={handleSaveWeights} className="space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div>
                  <label className="block text-xs font-bold text-slate-800 mb-1">Technical Skill Weight ({Math.round(weightsForm.technicalWeight * 100)}%)</label>
                  <input
                    type="range"
                    min="0.1"
                    max="0.8"
                    step="0.05"
                    value={weightsForm.technicalWeight}
                    onChange={e => setWeightsForm({ ...weightsForm, technicalWeight: parseFloat(e.target.value) })}
                    className="w-full accent-blue-600 cursor-pointer"
                  />
                  <p className="text-[10px] text-slate-500 mt-1">Weight assigned to direct user skill self-assessments.</p>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-800 mb-1">Questionnaire Interest Weight ({Math.round(weightsForm.questionnaireWeight * 100)}%)</label>
                  <input
                    type="range"
                    min="0.1"
                    max="0.8"
                    step="0.05"
                    value={weightsForm.questionnaireWeight}
                    onChange={e => setWeightsForm({ ...weightsForm, questionnaireWeight: parseFloat(e.target.value) })}
                    className="w-full accent-blue-600 cursor-pointer"
                  />
                  <p className="text-[10px] text-slate-500 mt-1">Weight assigned to survey scenario options.</p>
                </div>
              </div>

              <div className="pt-2 flex justify-end">
                <button
                  type="submit"
                  className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-2"
                >
                  <Save className="w-4 h-4" /> Save Algorithm Weights
                </button>
              </div>
            </form>
          </div>
        )}

        {/* ========================================================================= */}
        {/* 5. SYSTEM HEALTH AUDIT WORKSPACE PANEL */}
        {/* ========================================================================= */}
        {activeTab === 'overview' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="flex items-center justify-between pb-4 border-b border-slate-100">
              <div>
                <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                  <Layers className="w-5 h-5 text-blue-600" /> Platform System Health
                </h2>
                <p className="text-xs text-slate-500 mt-0.5">Live counts and database consistency health from MySQL.</p>
              </div>

              {healthData && (
                <div className="flex items-center gap-3">
                  <div className="text-right">
                    <p className="font-black text-xl text-blue-600">{healthData.healthScore ?? 100}%</p>
                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Health Score</p>
                  </div>
                  <div className={`px-3 py-1 rounded-xl text-xs font-extrabold flex items-center gap-1.5 ${
                    healthData.status === 'HEALTHY' ? 'bg-emerald-100 text-emerald-800 border border-emerald-300' :
                    healthData.status === 'WARNING' ? 'bg-amber-100 text-amber-800 border border-amber-300' :
                    'bg-red-100 text-red-800 border border-red-300'
                  }`}>
                    <ShieldCheck className="w-4 h-4" />
                    <span>HEALTH STATUS: {healthData.status}</span>
                  </div>
                </div>
              )}
            </div>

            {healthData && healthData.warnings && healthData.warnings.length > 0 && (
              <div className="p-4 bg-amber-50 border border-amber-200 rounded-2xl space-y-2">
                <h4 className="text-xs font-bold text-amber-900 flex items-center gap-1.5">
                  <AlertCircle className="w-4 h-4 text-amber-600" /> System Audit Warnings ({healthData.warnings.length})
                </h4>
                <ul className="list-disc list-inside text-[11px] text-amber-800 space-y-1">
                  {healthData.warnings.map((w: string, idx: number) => (
                    <li key={idx} className="flex items-center justify-between">
                      <span>{w}</span>
                      <button
                        onClick={() => {
                          if (w.includes("career")) setActiveTab('careers');
                          else if (w.includes("skill")) setActiveTab('skills');
                          else setActiveTab('questionnaire');
                        }}
                        className="text-[10px] font-bold text-blue-700 underline ml-2"
                      >
                        Fix / View Config
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            {isLoadingStats && (
              <div className="flex items-center justify-center gap-2 py-8 text-slate-500 text-xs">
                <Loader2 className="w-5 h-5 animate-spin text-blue-600" />
                <span>Loading system statistics from database…</span>
              </div>
            )}

            {!isLoadingStats && adminStats && (
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><Briefcase className="w-3 h-3" /> Active Career Profiles</span>
                  <p className="text-2xl font-black text-blue-400">{adminStats.activeCareers ?? '—'}</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><Sliders className="w-3 h-3" /> Active Skills Matrix</span>
                  <p className="text-2xl font-black text-sky-400">{adminStats.activeSkills ?? '—'} Items</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><HelpCircle className="w-3 h-3" /> Active Questions</span>
                  <p className="text-2xl font-black text-emerald-400">{adminStats.activeQuestions ?? '—'} Items</p>
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* ========================================================================= */}
      {/* MODALS */}
      {/* ========================================================================= */}

      {/* ADD SKILL REQUIREMENT TO CAREER MODAL */}
      {isAddReqModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 space-y-5 border border-slate-200 text-left">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">Add Skill Requirement</h3>
              <button onClick={() => setIsAddReqModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600"><X className="w-4 h-4" /></button>
            </div>
            <form onSubmit={handleAddRequirement} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Select Skill</label>
                <select
                  value={reqFormSkillId}
                  onChange={e => setReqFormSkillId(e.target.value)}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  required
                >
                  <option value="">-- Choose active skill --</option>
                  {skillsList.filter(s => (s as any).isActive !== false).map(s => (
                    <option key={s.id} value={s.id}>{s.name} ({s.category})</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Required Target Level (1 to 5)</label>
                <input
                  type="number"
                  min="1"
                  max="5"
                  value={reqFormLevel}
                  onChange={e => setReqFormLevel(parseInt(e.target.value))}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                />
              </div>
              <div className="flex items-center gap-2">
                <input
                  type="checkbox"
                  id="isEssential"
                  checked={reqFormIsEssential}
                  onChange={e => setReqFormIsEssential(e.target.checked)}
                  className="w-4 h-4 accent-blue-600"
                />
                <label htmlFor="isEssential" className="font-bold text-slate-800">Essential Skill (Applies penalty if missing)</label>
              </div>
              <div className="pt-3 flex justify-end gap-2">
                <button type="button" onClick={() => setIsAddReqModalOpen(false)} className="px-4 py-2 text-slate-500 font-bold">Cancel</button>
                <button type="submit" className="px-5 py-2 bg-blue-600 text-white font-bold rounded-xl">Assign Skill</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* SKILL IMPACT DETAIL MODAL */}
      {selectedSkillDetail && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-xl w-full p-6 space-y-5 max-h-[85vh] overflow-y-auto border border-slate-200 text-left">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <div>
                <span className="text-[10px] font-bold text-blue-600 uppercase">{selectedSkillDetail.category}</span>
                <h3 className="text-lg font-bold text-slate-900">{selectedSkillDetail.name}</h3>
              </div>
              <button onClick={() => setSelectedSkillDetail(null)} className="p-1 text-slate-400 hover:text-slate-600"><X className="w-4 h-4" /></button>
            </div>

            {isLoadingSkillImpact ? (
              <div className="p-6 text-center text-xs text-slate-500 flex items-center justify-center gap-2">
                <Loader2 className="w-4 h-4 animate-spin text-blue-600" /> Computing dependency graph…
              </div>
            ) : skillImpactData ? (
              <div className="space-y-4 text-xs">
                <div className="grid grid-cols-3 gap-3 bg-slate-900 text-white p-3 rounded-2xl text-center">
                  <div>
                    <p className="text-base font-black text-blue-400">{skillImpactData.careerCount}</p>
                    <p className="text-[10px] text-slate-400">Careers Using Skill</p>
                  </div>
                  <div>
                    <p className="text-base font-black text-sky-400">{skillImpactData.careerRequirementCount}</p>
                    <p className="text-[10px] text-slate-400">Requirement Mappings</p>
                  </div>
                  <div>
                    <p className="text-base font-black text-purple-400">{skillImpactData.questionnaireMappingCount}</p>
                    <p className="text-[10px] text-slate-400">Option Mappings</p>
                  </div>
                </div>

                <div className="space-y-2">
                  <h4 className="font-bold text-slate-900">Careers Requiring This Skill:</h4>
                  <div className="space-y-1">
                    {skillImpactData.affectedCareers?.map((cName: string, cIdx: number) => (
                      <div key={cIdx} className="p-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-800 font-medium">
                        • {cName}
                      </div>
                    ))}
                    {(!skillImpactData.affectedCareers || skillImpactData.affectedCareers.length === 0) && (
                      <p className="text-slate-400 italic text-[11px]">This skill is currently unassigned to any career profile.</p>
                    )}
                  </div>
                </div>
              </div>
            ) : null}
          </div>
        </div>
      )}

      {/* CREATE / EDIT SKILL MODAL */}
      {isSkillModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 space-y-4 border border-slate-200 text-left">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">{editingSkill ? 'Edit Skill Definition' : 'Add New Skill'}</h3>
              <button onClick={() => setIsSkillModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600"><X className="w-4 h-4" /></button>
            </div>
            <form onSubmit={handleSaveSkill} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Skill Name</label>
                <input
                  type="text"
                  value={skillFormData.name}
                  onChange={e => setSkillFormData({ ...skillFormData, name: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  required
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Category</label>
                <select
                  value={skillFormData.category}
                  onChange={e => setSkillFormData({ ...skillFormData, category: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                >
                  <option value="Technical">Technical</option>
                  <option value="Domain Knowledge">Domain Knowledge</option>
                  <option value="Tools & Frameworks">Tools & Frameworks</option>
                  <option value="Soft Skills">Soft Skills</option>
                </select>
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Description</label>
                <textarea
                  value={skillFormData.description}
                  onChange={e => setSkillFormData({ ...skillFormData, description: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  rows={3}
                />
              </div>
              <div className="pt-3 flex justify-end gap-2">
                <button type="button" onClick={() => setIsSkillModalOpen(false)} className="px-4 py-2 text-slate-500 font-bold">Cancel</button>
                <button type="submit" className="px-5 py-2 bg-blue-600 text-white font-bold rounded-xl">Save Skill</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CREATE / EDIT CAREER METADATA MODAL */}
      {isCareerModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-xl w-full p-6 space-y-4 max-h-[85vh] overflow-y-auto border border-slate-200 text-left">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">{editingCareer ? 'Edit Career Metadata' : 'Create Career Profile'}</h3>
              <button onClick={() => setIsCareerModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600"><X className="w-4 h-4" /></button>
            </div>
            <form onSubmit={handleSaveCareer} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Career Title</label>
                <input
                  type="text"
                  value={careerFormData.title || ''}
                  onChange={e => setCareerFormData({ ...careerFormData, title: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  required
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Category</label>
                <input
                  type="text"
                  value={careerFormData.category || ''}
                  onChange={e => setCareerFormData({ ...careerFormData, category: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  required
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Description</label>
                <textarea
                  value={careerFormData.description || ''}
                  onChange={e => setCareerFormData({ ...careerFormData, description: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  rows={3}
                  required
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-slate-700 mb-1">Average Salary Range</label>
                  <input
                    type="text"
                    value={careerFormData.averageSalary || ''}
                    onChange={e => setCareerFormData({ ...careerFormData, averageSalary: e.target.value })}
                    className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  />
                </div>
                <div>
                  <label className="block font-bold text-slate-700 mb-1">Growth Rate</label>
                  <input
                    type="text"
                    value={careerFormData.growthRate || ''}
                    onChange={e => setCareerFormData({ ...careerFormData, growthRate: e.target.value })}
                    className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  />
                </div>
              </div>
              <div className="pt-3 flex justify-end gap-2">
                <button type="button" onClick={() => setIsCareerModalOpen(false)} className="px-4 py-2 text-slate-500 font-bold">Cancel</button>
                <button type="submit" className="px-5 py-2 bg-blue-600 text-white font-bold rounded-xl">Save Career Profile</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CREATE / EDIT QUESTION ITEM MODAL */}
      {isQuestionModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 space-y-4 border border-slate-200 text-left">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">{editingQuestion ? 'Edit Question Item' : 'New Question Item'}</h3>
              <button onClick={() => setIsQuestionModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600"><X className="w-4 h-4" /></button>
            </div>
            <form onSubmit={handleSaveQuestion} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Section</label>
                <input
                  type="text"
                  value={questionFormData.section}
                  onChange={e => setQuestionFormData({ ...questionFormData, section: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  required
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Question Prompt</label>
                <textarea
                  value={questionFormData.question}
                  onChange={e => setQuestionFormData({ ...questionFormData, question: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  rows={3}
                  required
                />
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Question Type</label>
                <select
                  value={questionFormData.type}
                  onChange={e => setQuestionFormData({ ...questionFormData, type: e.target.value as any })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                >
                  <option value="single">Single Select</option>
                  <option value="multiple">Multiple Select</option>
                  <option value="scale">Rating Scale</option>
                </select>
              </div>
              <div className="pt-3 flex justify-end gap-2">
                <button type="button" onClick={() => setIsQuestionModalOpen(false)} className="px-4 py-2 text-slate-500 font-bold">Cancel</button>
                <button type="submit" className="px-5 py-2 bg-blue-600 text-white font-bold rounded-xl">Save Question</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CREATE / EDIT OPTION MODAL */}
      {isOptionModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 space-y-4 border border-slate-200 text-left">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">{editingOption ? 'Edit Option' : 'Add Answer Option'}</h3>
              <button onClick={() => setIsOptionModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600"><X className="w-4 h-4" /></button>
            </div>
            <form onSubmit={handleSaveOption} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Option Text</label>
                <input
                  type="text"
                  value={optionFormData.optionText}
                  onChange={e => setOptionFormData({ ...optionFormData, optionText: e.target.value })}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  required
                />
              </div>
              <div className="pt-3 flex justify-end gap-2">
                <button type="button" onClick={() => setIsOptionModalOpen(false)} className="px-4 py-2 text-slate-500 font-bold">Cancel</button>
                <button type="submit" className="px-5 py-2 bg-blue-600 text-white font-bold rounded-xl">Save Option</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* ADD OPTION-SKILL MAPPING MODAL */}
      {isMappingModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 space-y-4 border border-slate-200 text-left">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 className="text-base font-bold text-slate-900">Map Skill to Option</h3>
              <button onClick={() => setIsMappingModalOpen(false)} className="p-1 text-slate-400 hover:text-slate-600"><X className="w-4 h-4" /></button>
            </div>
            <form onSubmit={handleSaveMapping} className="space-y-3 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Select Skill</label>
                <select
                  value={mappingSkillId}
                  onChange={e => setMappingSkillId(e.target.value)}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                  required
                >
                  <option value="">-- Choose active skill --</option>
                  {skillsList.filter(s => (s as any).isActive !== false).map(s => (
                    <option key={s.id} value={s.id}>{s.name} ({s.category})</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Mapping Weight (1 to 5)</label>
                <input
                  type="number"
                  min="1"
                  max="5"
                  value={mappingWeight}
                  onChange={e => setMappingWeight(parseInt(e.target.value))}
                  className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl"
                />
              </div>
              <div className="pt-3 flex justify-end gap-2">
                <button type="button" onClick={() => setIsMappingModalOpen(false)} className="px-4 py-2 text-slate-500 font-bold">Cancel</button>
                <button type="submit" className="px-5 py-2 bg-blue-600 text-white font-bold rounded-xl">Save Mapping</button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};
