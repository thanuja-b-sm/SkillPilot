import React, { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { Career, QuestionItem } from '../types';
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
  Map
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
    activateSkill,
    addCareerRequirement,
    deleteCareerRequirement,
    addQuestionSkillMapping,
    deleteQuestionSkillMapping,
    addQuestionItem, 
    deleteQuestionItem,
    showToast,
    setUserRole,
    navigateTo,
    token
  } = useApp();

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
        <div className="pt-2">
          <button
            onClick={() => navigateTo('login')}
            className="w-full py-3 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold text-sm rounded-xl shadow-md transition-all cursor-pointer"
          >
            Go to Sign In
          </button>
        </div>
      </div>
    );
  }
  const [activeTab, setActiveTab] = useState<'careers' | 'skills' | 'questionnaire' | 'weights' | 'overview'>('careers');
  const [searchTerm, setSearchTerm] = useState('');
  const [skillSearchTerm, setSkillSearchTerm] = useState('');
  const [activeFilter, setActiveFilter] = useState<'all' | 'active' | 'inactive'>('all');
  const [adminStats, setAdminStats] = useState<Record<string, any> | null>(null);
  const [weightsForm, setWeightsForm] = useState({ ...systemConfig });
  const [isLoadingStats, setIsLoadingStats] = useState(false);

  useEffect(() => {
    if (activeTab === 'overview' && !adminStats) {
      const tok = token || localStorage.getItem('skillpilot_token');
      if (!tok) return;
      setIsLoadingStats(true);
      fetch('/api/admin/stats', {
        headers: { 'Authorization': `Bearer ${tok}` }
      })
      .then(r => r.ok ? r.json() : null)
      .then(data => { if (data) setAdminStats(data); })
      .catch(err => console.warn('Failed to fetch admin stats:', err))
      .finally(() => setIsLoadingStats(false));
    }
  }, [activeTab]);

  // Career Modal State
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

  const handleOpenNewCareer = () => {
    setEditingCareer(null);
    setCareerFormData({
      id: `career-${Date.now()}`,
      title: '',
      category: 'Software Engineering',
      description: '',
      averageSalary: '$130,000 - $170,000 / yr',
      growthRate: '+22%',
      demandLevel: 'High',
      typicalRoles: ['Engineering Lead'],
      recommendedPrerequisites: ['Programming Basics'],
      requiredSkills: [
        { skillId: 'python', skillName: 'Python Programming', category: 'Technical', requiredLevel: 4, isEssential: true },
        { skillId: 'typescript', skillName: 'TypeScript / JavaScript', category: 'Technical', requiredLevel: 4, isEssential: true }
      ]
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
              <p className="text-[11px] text-slate-400">SkillPilot Dataset Management</p>
            </div>
          </div>

          <nav className="space-y-1 text-xs font-semibold">
            <button
              onClick={() => setActiveTab('careers')}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'careers' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><Briefcase className="w-4 h-4" /> Careers Dataset</span>
              <span className="bg-slate-800 text-slate-300 text-[10px] px-2 py-0.5 rounded-full">{careers.length}</span>
            </button>

            <button
              onClick={() => setActiveTab('skills')}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'skills' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><Sliders className="w-4 h-4" /> Skills Master List</span>
              <span className="bg-slate-800 text-slate-300 text-[10px] px-2 py-0.5 rounded-full">{skillsList.length}</span>
            </button>

            <button
              onClick={() => setActiveTab('questionnaire')}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'questionnaire' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><HelpCircle className="w-4 h-4" /> Questionnaire Items</span>
              <span className="bg-slate-800 text-slate-300 text-[10px] px-2 py-0.5 rounded-full">{questionnaire.length}</span>
            </button>

            <button
              onClick={() => setActiveTab('weights')}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'weights' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><BarChart className="w-4 h-4" /> Algorithm Weights</span>
              <span className="text-[10px] text-emerald-400">v2.4</span>
            </button>

            <button
              onClick={() => setActiveTab('overview')}
              className={`w-full px-3.5 py-2.5 rounded-xl transition-all flex items-center justify-between ${
                activeTab === 'overview' ? 'bg-blue-600 text-white shadow-xs' : 'text-slate-300 hover:bg-slate-800'
              }`}
            >
              <span className="flex items-center gap-2"><Layers className="w-4 h-4" /> System Metrics</span>
              <span className="text-[10px] text-slate-400">Live</span>
            </button>
          </nav>

          <div className="pt-4 border-t border-slate-800 space-y-2">
            <button
              onClick={() => {
                setUserRole('student');
                navigateTo('results');
              }}
              className="w-full py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl text-xs font-semibold transition-colors flex items-center justify-center gap-1.5"
            >
              <UserCheck className="w-3.5 h-3.5 text-blue-400" /> Switch to Student View
            </button>
          </div>
        </div>
      </div>

      {/* Admin Content Area */}
      <div className="lg:col-span-9 space-y-6">
        
        {/* Careers Dataset Tab */}
        {activeTab === 'careers' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
              <div>
                <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                  <Briefcase className="w-5 h-5 text-blue-600" /> Career Profile Records
                </h2>
                <p className="text-xs text-slate-500">Manage career titles, descriptions, salary ranges, and required skills.</p>
              </div>

              <button
                onClick={handleOpenNewCareer}
                className="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl shadow-xs transition-colors flex items-center gap-1.5 shrink-0"
              >
                <Plus className="w-4 h-4" /> Add Career Profile
              </button>
            </div>

            {/* Search Filter */}
            <div className="relative">
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                placeholder="Search career profiles..."
                className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Careers Table */}
            <div className="overflow-x-auto border border-slate-200 rounded-2xl">
              <table className="w-full text-left text-xs text-slate-800">
                <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 font-bold uppercase tracking-wider text-[10px]">
                  <tr>
                    <th className="p-3.5">Career Title</th>
                    <th className="p-3.5">Category</th>
                    <th className="p-3.5">Avg Salary</th>
                    <th className="p-3.5">Growth</th>
                    <th className="p-3.5">Required Skills</th>
                    <th className="p-3.5 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {filteredCareers.map(career => (
                    <tr key={career.id} className="hover:bg-slate-50/80 transition-colors">
                      <td className="p-3.5 font-bold text-slate-950 max-w-[200px] truncate">{career.title}</td>
                      <td className="p-3.5">
                        <span className="bg-slate-100 text-slate-700 font-medium px-2 py-0.5 rounded-md border border-slate-200">
                          {career.category}
                        </span>
                      </td>
                      <td className="p-3.5 text-slate-600 font-medium">{career.averageSalary}</td>
                      <td className="p-3.5 text-emerald-700 font-semibold">{career.growthRate}</td>
                      <td className="p-3.5 text-slate-500">
                        {career.requiredSkills.length} defined skills
                      </td>
                      <td className="p-3.5 text-right space-x-2">
                        {career.isActive === false ? (
                          <button
                            onClick={() => activateCareer(career.id)}
                            className="px-2.5 py-1 text-[10px] bg-amber-600 hover:bg-amber-700 text-white font-bold rounded-lg transition-colors"
                          >
                            Reactivate
                          </button>
                        ) : (
                          <>
                            <button
                              onClick={() => handleOpenEditCareer(career)}
                              className="p-1.5 text-slate-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                              title="Edit Profile"
                            >
                              <Edit className="w-4 h-4" />
                            </button>
                            <button
                              onClick={() => deleteCareer(career.id)}
                              className="p-1.5 text-slate-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                              title="Deactivate Profile"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Skills Master List Tab */}
        {activeTab === 'skills' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
              <div>
                <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                  <Sliders className="w-5 h-5 text-blue-600" /> Skills Master Dictionary
                </h2>
                <p className="text-xs text-slate-500">Global taxonomy of technical, tool, domain, and soft skills.</p>
              </div>
            </div>

            {/* Skill Search & Filter */}
            <div className="relative">
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                value={skillSearchTerm}
                onChange={e => setSkillSearchTerm(e.target.value)}
                placeholder="Search skills by name, category, or ID..."
                className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
              {filteredSkills.map(skill => {
                const isActive = (skill as any).isActive !== false;
                return (
                  <div key={skill.id} className={`p-3.5 rounded-2xl border space-y-1.5 transition-colors ${
                    isActive ? 'bg-slate-50 border-slate-200/80' : 'bg-amber-50/50 border-amber-200/80'
                  }`}>
                    <div className="flex items-center justify-between">
                      <h4 className="text-xs font-bold text-slate-950">{skill.name}</h4>
                      <span className="text-[9px] bg-blue-100 text-blue-800 font-bold px-1.5 py-0.5 rounded">
                        {skill.id}
                      </span>
                    </div>
                    <div className="flex items-center justify-between">
                      <p className="text-[10px] text-slate-500 font-medium">{skill.category}</p>
                      {!isActive && (
                        <button
                          onClick={() => activateSkill(skill.id)}
                          className="text-[9px] bg-amber-600 hover:bg-amber-700 text-white font-bold px-2 py-0.5 rounded transition-colors"
                        >
                          Reactivate
                        </button>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {/* Questionnaire Editor Tab */}
        {activeTab === 'questionnaire' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="flex items-center justify-between pb-4 border-b border-slate-100">
              <div>
                <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                  <HelpCircle className="w-5 h-5 text-blue-600" /> Questionnaire Items & Skill Mappings
                </h2>
                <p className="text-xs text-slate-500">Manage discovery survey questions and option skill weight bonuses.</p>
              </div>
            </div>

            <div className="space-y-4">
              {questionnaire.map((q, idx) => (
                <div key={q.id} className="p-5 rounded-2xl bg-slate-50 border border-slate-200/80 space-y-3">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <span className="text-[10px] font-bold text-slate-500 uppercase">
                        Question #{idx + 1} • {q.section}
                      </span>
                      <h4 className="text-sm font-bold text-slate-950 mt-0.5">{q.question}</h4>
                    </div>

                    <button
                      onClick={() => deleteQuestionItem(q.id)}
                      className="p-1.5 text-slate-400 hover:text-red-600 rounded-lg"
                      title="Delete Question"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>

                  <div className="space-y-1.5 pl-3 border-l-2 border-slate-200 text-xs">
                    {q.options.map(opt => (
                      <div key={opt.id} className="flex items-center justify-between text-slate-700">
                        <span>• {opt.text}</span>
                        <span className="text-[10px] text-blue-600 font-semibold bg-blue-50 px-1.5 py-0.5 rounded">
                          {opt.associatedSkills.length} skill mappings
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Weights Configuration Tab */}
        {activeTab === 'weights' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="pb-4 border-b border-slate-100">
              <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                <BarChart className="w-5 h-5 text-blue-600" /> Algorithmic Formula & Weight Parameters
              </h2>
              <p className="text-xs text-slate-500">Adjust mathematical coefficients for match score generation.</p>
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

        {/* System Overview Metrics Tab */}
        {activeTab === 'overview' && (
          <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
            <div className="pb-4 border-b border-slate-100">
              <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                <Layers className="w-5 h-5 text-blue-600" /> Platform System Health
              </h2>
              <p className="text-xs text-slate-500 mt-0.5">Live counts from the database — refreshed on tab open.</p>
            </div>

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
                  <p className="text-[10px] text-slate-500">In MySQL careers table</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><Sliders className="w-3 h-3" /> Active Skills Matrix</span>
                  <p className="text-2xl font-black text-sky-400">{adminStats.activeSkills ?? '—'} Items</p>
                  <p className="text-[10px] text-slate-500">In MySQL skills table</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><HelpCircle className="w-3 h-3" /> Active Questions</span>
                  <p className="text-2xl font-black text-emerald-400">{adminStats.activeQuestions ?? '—'} Items</p>
                  <p className="text-[10px] text-slate-500">In MySQL questions table</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><Layers className="w-3 h-3" /> Career Requirements</span>
                  <p className="text-2xl font-black text-teal-400">{adminStats.careerSkillRequirementCount ?? '—'} Mappings</p>
                  <p className="text-[10px] text-slate-500">In MySQL requirements table</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><Sliders className="w-3 h-3" /> Option Skill Mappings</span>
                  <p className="text-2xl font-black text-purple-400">{adminStats.questionSkillMappingCount ?? '—'} Mappings</p>
                  <p className="text-[10px] text-slate-500">In MySQL mappings table</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><Users className="w-3 h-3" /> Registered Users</span>
                  <p className="text-2xl font-black text-violet-400">{adminStats.totalUsers ?? '—'}</p>
                  <p className="text-[10px] text-slate-500">All roles combined</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400 flex items-center gap-1"><Map className="w-3 h-3" /> Total Roadmaps</span>
                  <p className="text-2xl font-black text-amber-400">{adminStats.totalRoadmaps ?? '—'}</p>
                  <p className="text-[10px] text-slate-500">Generated & persisted</p>
                </div>
                <div className="p-4 rounded-2xl bg-slate-900 text-white space-y-1">
                  <span className="text-[10px] font-bold uppercase text-slate-400">Scoring Engine</span>
                  <p className="text-2xl font-black text-emerald-400">{adminStats.scoringVersion ?? 'v2.4'}</p>
                  <p className="text-[10px] text-slate-500">Active algorithm version</p>
                </div>
              </div>
            )}

            {!isLoadingStats && !adminStats && (
              <div className="text-center py-8 text-xs text-slate-400">
                Unable to load statistics. Ensure you are authenticated as Admin.
              </div>
            )}
          </div>
        )}

      </div>

      {/* Career Create / Edit Modal */}
      {isCareerModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-xl w-full p-6 sm:p-8 space-y-6 max-h-[90vh] overflow-y-auto border border-slate-200 text-left">
            <div className="flex items-center justify-between pb-3 border-b border-slate-100">
              <h3 className="text-lg font-bold text-slate-950">
                {editingCareer ? 'Edit Career Profile' : 'Add New Career Profile'}
              </h3>
              <button
                onClick={() => setIsCareerModalOpen(false)}
                className="p-1 hover:bg-slate-100 rounded-lg text-slate-500"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveCareer} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Career Title *</label>
                <input
                  type="text"
                  required
                  value={careerFormData.title}
                  onChange={e => setCareerFormData({ ...careerFormData, title: e.target.value })}
                  placeholder="e.g. MLOps Engineer"
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Category</label>
                <input
                  type="text"
                  value={careerFormData.category}
                  onChange={e => setCareerFormData({ ...careerFormData, category: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Description *</label>
                <textarea
                  rows={3}
                  required
                  value={careerFormData.description}
                  onChange={e => setCareerFormData({ ...careerFormData, description: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Average Salary</label>
                  <input
                    type="text"
                    value={careerFormData.averageSalary}
                    onChange={e => setCareerFormData({ ...careerFormData, averageSalary: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Growth Rate</label>
                  <input
                    type="text"
                    value={careerFormData.growthRate}
                    onChange={e => setCareerFormData({ ...careerFormData, growthRate: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  />
                </div>
              </div>

              <div className="pt-4 flex justify-end gap-2 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsCareerModalOpen(false)}
                  className="px-4 py-2 bg-slate-100 text-slate-700 font-semibold text-xs rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl"
                >
                  Save Record
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};
