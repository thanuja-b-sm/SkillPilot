import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { 
  User, 
  BookOpen, 
  Briefcase, 
  CheckCircle2, 
  Sliders, 
  ArrowRight,
  Plus,
  Save,
  Award,
  Sparkles
} from 'lucide-react';

export const ProfilePage: React.FC = () => {
  const { userProfile, setUserProfile, updateUserSkill, skillsList, selectedTargetCareer, navigateTo, showToast } = useApp();

  const [activeCategory, setActiveCategory] = useState<string>('All');
  const [isEditingInfo, setIsEditingInfo] = useState(false);

  const [infoForm, setInfoForm] = useState({
    name: userProfile.name,
    title: userProfile.title,
    education: userProfile.education,
    location: userProfile.location,
    targetFocus: userProfile.targetFocus,
    bio: userProfile.bio
  });

  const categories = ['All', 'Technical', 'Tools & Frameworks', 'Domain Knowledge', 'Soft Skills'];

  const filteredSkills = skillsList.filter(s => 
    activeCategory === 'All' || s.category === activeCategory
  );

  const handleSaveInfo = async (e: React.FormEvent) => {
    e.preventDefault();
    const token = localStorage.getItem('skillpilot_token');
    if (token) {
      try {
        const res = await fetch('/api/user/profile', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(infoForm)
        });
        if (res.ok) {
          const updatedProf = await res.json();
          setUserProfile(updatedProf);
          setIsEditingInfo(false);
          showToast('Saved profile information to database', 'success');
          return;
        }
      } catch (err) {
        console.error('Error saving profile info:', err);
      }
    }
    setUserProfile(prev => ({
      ...prev,
      ...infoForm
    }));
    setIsEditingInfo(false);
    showToast('Saved profile information', 'success');
  };

  const getUserSkillLevel = (skillId: string) => {
    const s = userProfile.skills.find(sk => sk.skillId === skillId);
    return s ? s.level : 0;
  };

  const getLevelLabel = (lvl: number) => {
    switch (lvl) {
      case 0: return 'None / Unfamiliar';
      case 1: return 'Beginner (Basic)';
      case 2: return 'Elementary (Practiced)';
      case 3: return 'Intermediate (Competent)';
      case 4: return 'Advanced (Proficient)';
      case 5: return 'Expert (Mastery)';
      default: return 'Unrated';
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-8 text-left pb-12">
      {/* Top Banner & Completion Card */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-slate-100">
          
          {/* User Meta */}
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 rounded-2xl bg-slate-900 text-white font-extrabold text-2xl flex items-center justify-center shadow-md shrink-0">
              {userProfile.name.charAt(0)}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-2xl font-bold text-slate-950">{userProfile.name}</h1>
                <span className="text-xs font-semibold bg-blue-50 text-blue-700 px-2.5 py-0.5 rounded-full border border-blue-200">
                  Student Assessment Mode
                </span>
              </div>
              <p className="text-xs text-slate-500 font-medium mt-0.5">{userProfile.title}</p>
              <div className="flex items-center gap-3 text-xs text-slate-600 mt-2">
                <span className="flex items-center gap-1"><BookOpen className="w-3.5 h-3.5 text-slate-400" /> {userProfile.education}</span>
                <span className="flex items-center gap-1"><Briefcase className="w-3.5 h-3.5 text-slate-400" /> Target: {userProfile.targetFocus}</span>
              </div>
            </div>
          </div>

          {/* Completion Meter */}
          <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200/80 min-w-[220px] space-y-2">
            <div className="flex items-center justify-between text-xs">
              <span className="font-bold text-slate-700">Profile Readiness</span>
              <span className="font-extrabold text-blue-600">{userProfile.completionPercentage}%</span>
            </div>
            <div className="w-full h-2.5 bg-slate-200 rounded-full overflow-hidden">
              <div 
                className="h-full bg-blue-600 rounded-full transition-all duration-500"
                style={{ width: `${userProfile.completionPercentage}%` }}
              />
            </div>
            <p className="text-[10px] text-slate-500 text-right">
              {userProfile.skills.length} skills self-assessed
            </p>
          </div>

        </div>

        {/* Profile Info Details Form */}
        {isEditingInfo ? (
          <form onSubmit={handleSaveInfo} className="space-y-4 pt-2">
            <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
              <User className="w-4 h-4 text-blue-600" /> Edit Personal Details
            </h3>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Full Name</label>
                <input
                  type="text"
                  value={infoForm.name}
                  onChange={e => setInfoForm({ ...infoForm, name: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Professional Title</label>
                <input
                  type="text"
                  value={infoForm.title}
                  onChange={e => setInfoForm({ ...infoForm, title: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Education Background</label>
                <input
                  type="text"
                  value={infoForm.education}
                  onChange={e => setInfoForm({ ...infoForm, education: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Target Focus Domain</label>
                <input
                  type="text"
                  value={infoForm.targetFocus}
                  onChange={e => setInfoForm({ ...infoForm, targetFocus: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Short Professional Summary</label>
              <textarea
                rows={2}
                value={infoForm.bio}
                onChange={e => setInfoForm({ ...infoForm, bio: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
              />
            </div>

            <div className="flex items-center gap-2 pt-2">
              <button
                type="submit"
                className="px-4 py-2 bg-blue-600 text-white font-semibold text-xs rounded-xl hover:bg-blue-700 flex items-center gap-1.5"
              >
                <Save className="w-3.5 h-3.5" /> Save Changes
              </button>
              <button
                type="button"
                onClick={() => setIsEditingInfo(false)}
                className="px-4 py-2 bg-slate-100 text-slate-700 font-semibold text-xs rounded-xl hover:bg-slate-200"
              >
                Cancel
              </button>
            </div>
          </form>
        ) : (
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <p className="text-xs text-slate-600 leading-relaxed max-w-3xl">
              "{userProfile.bio}"
            </p>
            <button
              onClick={() => setIsEditingInfo(true)}
              className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-semibold rounded-xl transition-colors shrink-0"
            >
              Edit Details
            </button>
          </div>
        )}
      </div>

      {/* Interactive Skill Self-Assessment Matrix */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
          <div>
            <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
              <Sliders className="w-5 h-5 text-blue-600" /> Interactive Skill Self-Assessment Matrix
            </h2>
            <p className="text-xs text-slate-500 mt-0.5">
              Adjust sliders to rate your baseline level (0 = None, 5 = Expert). Changes dynamically update career matches.
            </p>
            {selectedTargetCareer && (
              <div className="mt-2 inline-flex items-center gap-1.5 px-2.5 py-1 bg-blue-50 text-blue-800 text-[11px] font-bold rounded-lg border border-blue-200">
                <Sparkles className="w-3.5 h-3.5 text-blue-600" />
                <span>Showing skills relevant to target: <strong>{selectedTargetCareer.title}</strong></span>
              </div>
            )}
          </div>

          <button
            onClick={() => navigateTo('questionnaire')}
            className="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl shadow-xs transition-colors flex items-center gap-2 shrink-0"
          >
            <span>Proceed to Questionnaire</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

        {/* Category Filters */}
        <div className="flex flex-wrap items-center gap-2">
          {categories.map(cat => (
            <button
              key={cat}
              onClick={() => setActiveCategory(cat)}
              className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition-all ${
                activeCategory === cat
                  ? 'bg-slate-900 text-white shadow-2xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* Skill Rating Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {filteredSkills.map(skill => {
            const currentLevel = getUserSkillLevel(skill.id);

            return (
              <div 
                key={skill.id}
                className="p-4 rounded-2xl bg-slate-50/80 border border-slate-200/80 space-y-3 hover:border-blue-300 transition-colors"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h4 className="text-sm font-bold text-slate-900">{skill.name}</h4>
                    <span className="text-[10px] font-medium text-slate-500 bg-slate-200/60 px-2 py-0.5 rounded-md">
                      {skill.category}
                    </span>
                  </div>

                  <span className={`text-xs font-bold px-2.5 py-1 rounded-lg border ${
                    currentLevel >= 4 
                      ? 'bg-emerald-50 text-emerald-800 border-emerald-200' 
                      : currentLevel >= 2
                      ? 'bg-blue-50 text-blue-800 border-blue-200'
                      : 'bg-slate-100 text-slate-600 border-slate-200'
                  }`}>
                    Lvl {currentLevel}/5
                  </span>
                </div>

                {/* Level Label */}
                <p className="text-[11px] font-semibold text-slate-600">
                  {getLevelLabel(currentLevel)}
                </p>

                {/* Level Slider */}
                <div className="space-y-1">
                  <input
                    type="range"
                    min="0"
                    max="5"
                    step="1"
                    value={currentLevel}
                    onChange={e => updateUserSkill(skill.id, parseInt(e.target.value))}
                    className="w-full accent-blue-600 cursor-pointer"
                  />
                  <div className="flex justify-between text-[9px] text-slate-400 font-medium px-1">
                    <span>0: None</span>
                    <span>1: Basic</span>
                    <span>2: Elem</span>
                    <span>3: Inter</span>
                    <span>4: Adv</span>
                    <span>5: Master</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
