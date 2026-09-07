import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { 
  User, 
  BookOpen, 
  Briefcase, 
  Sliders, 
  ArrowRight,
  Save,
  Award,
  Sparkles,
  Info,
  Clock,
  Globe,
  GraduationCap,
  Calendar,
  CheckCircle2,
  AlertCircle,
  Bookmark,
  Trash2,
  ExternalLink,
  TrendingUp,
  DollarSign,
  Target
} from 'lucide-react';

export const ProfilePage: React.FC = () => {
  const { 
    userProfile, 
    setUserProfile, 
    updateUserSkill, 
    skillsList, 
    selectedTargetCareer, 
    navigateTo, 
    showToast,
    savedCareers,
    toggleSaveCareer,
    selectTargetCareer
  } = useApp();

  const [activeTab, setActiveTab] = useState<'personal' | 'education' | 'experience' | 'skills' | 'preferences' | 'learning' | 'saved'>('personal');

  const [activeSkillCategory, setActiveSkillCategory] = useState<string>('All');
  const [isSaving, setIsSaving] = useState(false);

  const [profileForm, setProfileForm] = useState({
    name: userProfile.name || '',
    title: userProfile.title || 'Student Profile',
    location: userProfile.location || '',
    country: userProfile.country || '',
    dateOfBirth: userProfile.dateOfBirth || '',
    bio: userProfile.bio || '',

    education: userProfile.education || '',
    institutionName: userProfile.institutionName || '',
    degreeLevel: userProfile.degreeLevel || "Bachelor's Degree",
    majorFieldOfStudy: userProfile.majorFieldOfStudy || '',
    graduationYear: userProfile.graduationYear || 2026,
    educationStatus: userProfile.educationStatus || 'Enrolled / In Progress',

    experienceYears: userProfile.experienceYears || 0,
    relevantExperienceYears: userProfile.relevantExperienceYears || 0,
    employmentStatus: userProfile.employmentStatus || 'Student / Learning',
    currentJobTitle: userProfile.currentJobTitle || '',
    currentIndustry: userProfile.currentIndustry || '',
    certifications: userProfile.certifications || '',
    portfolioUrl: userProfile.portfolioUrl || '',

    targetFocus: userProfile.targetFocus || '',
    preferredWorkMode: userProfile.preferredWorkMode || 'Hybrid',
    preferredEmploymentType: userProfile.preferredEmploymentType || 'Full-Time',
    careerGoal: userProfile.careerGoal || '',

    weeklyHoursAvailable: userProfile.weeklyHoursAvailable || 10,
    preferredLearningPace: userProfile.preferredLearningPace || 'Steady',
    preferredRoadmapDuration: userProfile.preferredRoadmapDuration || 6
  });

  const skillCategories = ['All', 'Technical', 'Tools & Frameworks', 'Domain Knowledge', 'Soft Skills'];

  const filteredSkills = skillsList.filter(s => 
    activeSkillCategory === 'All' || s.category === activeSkillCategory
  );

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    const token = localStorage.getItem('skillpilot_token');
    if (token) {
      try {
        const res = await fetch('/api/user/profile', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(profileForm)
        });
        if (res.ok) {
          const updatedProf = await res.json();
          setUserProfile(updatedProf);
          showToast('Profile updated & completeness score recalculated', 'success');
          setIsSaving(false);
          return;
        }
      } catch (err) {
        console.error('Error saving profile info:', err);
      }
    }
    setUserProfile(prev => ({
      ...prev,
      ...profileForm
    }));
    setIsSaving(false);
    showToast('Saved profile changes locally', 'success');
  };

  const getUserSkillLevel = (skillId: string) => {
    const s = userProfile.skills.find(sk => sk.skillId === skillId);
    return s ? s.level : 0;
  };

  const getLevelLabel = (lvl: number) => {
    switch (lvl) {
      case 0: return 'None / Unrated';
      case 1: return 'Beginner (Basic Theory)';
      case 2: return 'Elementary (Practiced)';
      case 3: return 'Intermediate (Competent)';
      case 4: return 'Advanced (Proficient)';
      case 5: return 'Expert (Mastery)';
      default: return 'Unrated';
    }
  };

  return (
    <div className="max-w-6xl mx-auto space-y-8 text-left pb-12">
      {/* Profile Header & Completeness Card */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 pb-6 border-b border-slate-100">
          
          <div className="flex items-center gap-5">
            <div className="w-16 h-16 rounded-2xl bg-slate-900 text-blue-400 font-extrabold text-2xl flex items-center justify-center shadow-md shrink-0 border border-slate-800">
              {userProfile.name ? userProfile.name.charAt(0) : 'U'}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-2xl font-bold text-slate-950">{userProfile.name || 'Student User'}</h1>
                <span className="text-xs font-bold bg-blue-50 text-blue-700 px-2.5 py-0.5 rounded-full border border-blue-200">
                  {userProfile.role || 'Student'}
                </span>
              </div>
              <p className="text-xs text-slate-500 font-medium mt-0.5">{userProfile.title || 'Career Candidate'}</p>
              <div className="flex flex-wrap items-center gap-3 text-xs text-slate-600 mt-2">
                {userProfile.location && (
                  <span className="flex items-center gap-1"><Globe className="w-3.5 h-3.5 text-slate-400" /> {userProfile.location}</span>
                )}
                {userProfile.education && (
                  <span className="flex items-center gap-1"><GraduationCap className="w-3.5 h-3.5 text-slate-400" /> {userProfile.education}</span>
                )}
                {selectedTargetCareer && (
                  <span className="flex items-center gap-1 font-bold text-blue-700"><Briefcase className="w-3.5 h-3.5 text-blue-600" /> Target: {selectedTargetCareer.title}</span>
                )}
              </div>
            </div>
          </div>

          {/* Profile Completeness Meter */}
          <div className="bg-gradient-to-br from-slate-50 to-blue-50/50 p-4 rounded-2xl border border-blue-100 min-w-[240px] space-y-2">
            <div className="flex items-center justify-between text-xs">
              <span className="font-bold text-slate-800 flex items-center gap-1.5">
                <Sparkles className="w-4 h-4 text-blue-600" /> Profile Completeness
              </span>
              <span className="font-extrabold text-blue-700 text-sm">{userProfile.completionPercentage || 0}%</span>
            </div>
            <div className="w-full h-3 bg-slate-200 rounded-full overflow-hidden">
              <div 
                className="h-full bg-blue-600 rounded-full transition-all duration-500"
                style={{ width: `${userProfile.completionPercentage || 0}%` }}
              />
            </div>
            <p className="text-[10px] text-slate-500">
              {userProfile.completionPercentage >= 80 
                ? '✓ High intelligence detail ready' 
                : 'Complete sections to improve career analysis precision'}
            </p>
          </div>

        </div>

        {/* Tab Navigation */}
        <div className="flex flex-wrap gap-2 border-b border-slate-100 pb-2">
          {[
            { id: 'personal', label: '1. Personal Info', icon: User },
            { id: 'education', label: '2. Education', icon: GraduationCap },
            { id: 'experience', label: '3. Professional', icon: Briefcase },
            { id: 'skills', label: '4. Skills Matrix', icon: Sliders },
            { id: 'preferences', label: '5. Career Preferences', icon: Briefcase },
            { id: 'learning', label: '6. Learning Pace', icon: Clock },
            { id: 'saved', label: `7. Saved Careers (${savedCareers.length})`, icon: Bookmark }
          ].map(tab => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                type="button"
                onClick={() => setActiveTab(tab.id as any)}
                className={`px-3.5 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer ${
                  isActive 
                    ? 'bg-slate-900 text-white shadow-xs' 
                    : 'bg-slate-50 text-slate-600 hover:bg-slate-100 border border-slate-200/80'
                }`}
              >
                <Icon className={`w-3.5 h-3.5 ${isActive ? (tab.id === 'saved' ? 'text-rose-400' : 'text-blue-400') : 'text-slate-400'}`} />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>


        {/* Tab Content Forms */}
        <form onSubmit={handleSaveProfile} className="space-y-6 pt-2">

          {/* TAB 1: PERSONAL */}
          {activeTab === 'personal' && (
            <div className="space-y-4">
              <h3 className="text-sm font-bold text-slate-900">Personal & Identity Context</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Full Name *</label>
                  <input
                    type="text"
                    required
                    value={profileForm.name}
                    onChange={e => setProfileForm({ ...profileForm, name: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Professional Title</label>
                  <input
                    type="text"
                    value={profileForm.title}
                    onChange={e => setProfileForm({ ...profileForm, title: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. Aspiring Software Engineer"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">City / Region</label>
                  <input
                    type="text"
                    value={profileForm.location}
                    onChange={e => setProfileForm({ ...profileForm, location: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. San Francisco, CA"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Country</label>
                  <input
                    type="text"
                    value={profileForm.country}
                    onChange={e => setProfileForm({ ...profileForm, country: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. United States"
                  />
                </div>

                <div>
                  <label htmlFor="profile-date-of-birth" className="block text-xs font-semibold text-slate-700 mb-1">Date of Birth</label>
                  <input
                    id="profile-date-of-birth"
                    type="date"
                    min="1900-01-01"
                    max={new Date().toISOString().split('T')[0]}
                    value={profileForm.dateOfBirth || ''}
                    onChange={e => setProfileForm({ ...profileForm, dateOfBirth: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer"
                  />
                  <p className="text-[10px] text-slate-400 mt-1">Used for student career stage calibration.</p>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Short Professional Summary</label>
                <textarea
                  rows={3}
                  value={profileForm.bio}
                  onChange={e => setProfileForm({ ...profileForm, bio: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  placeholder="Describe your background, core technical focus, and primary career aspirations..."
                />
              </div>
            </div>
          )}

          {/* TAB 2: EDUCATION */}
          {activeTab === 'education' && (
            <div className="space-y-4">
              <h3 className="text-sm font-bold text-slate-900">Academic & Educational Background</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Highest Degree / Level</label>
                  <select
                    value={profileForm.degreeLevel}
                    onChange={e => setProfileForm({ ...profileForm, degreeLevel: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 font-medium"
                  >
                    <option value="High School">High School</option>
                    <option value="Associate's Degree">Associate's Degree</option>
                    <option value="Bachelor's Degree">Bachelor's Degree</option>
                    <option value="Master's Degree">Master's Degree</option>
                    <option value="Doctorate / PhD">Doctorate / PhD</option>
                    <option value="Self-Taught / Bootcamp">Self-Taught / Bootcamp</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">University / Institution Name</label>
                  <input
                    type="text"
                    value={profileForm.institutionName}
                    onChange={e => setProfileForm({ ...profileForm, institutionName: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. Stanford University"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Major / Field of Study</label>
                  <input
                    type="text"
                    value={profileForm.majorFieldOfStudy}
                    onChange={e => setProfileForm({ ...profileForm, majorFieldOfStudy: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. Computer Science"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Graduation Year</label>
                  <input
                    type="number"
                    value={profileForm.graduationYear}
                    onChange={e => setProfileForm({ ...profileForm, graduationYear: parseInt(e.target.value) || 2026 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Education Status</label>
                  <select
                    value={profileForm.educationStatus}
                    onChange={e => setProfileForm({ ...profileForm, educationStatus: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 font-medium"
                  >
                    <option value="Enrolled / In Progress">Enrolled / In Progress</option>
                    <option value="Completed / Graduated">Completed / Graduated</option>

                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">General Education Summary</label>
                  <input
                    type="text"
                    value={profileForm.education}
                    onChange={e => setProfileForm({ ...profileForm, education: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. B.S. Computer Science"
                  />
                </div>
              </div>
            </div>
          )}

          {/* TAB 3: EXPERIENCE */}
          {activeTab === 'experience' && (
            <div className="space-y-4">
              <h3 className="text-sm font-bold text-slate-900">Employment & Work History</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Employment Status</label>
                  <select
                    value={profileForm.employmentStatus}
                    onChange={e => setProfileForm({ ...profileForm, employmentStatus: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 font-medium"
                  >
                    <option value="Student / Learning">Student / Learning</option>
                    <option value="Employed Full-Time">Employed Full-Time</option>
                    <option value="Employed Part-Time">Employed Part-Time</option>
                    <option value="Freelance / Contractor">Freelance / Contractor</option>
                    <option value="Job Seeking / Transitioning">Job Seeking / Transitioning</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Current Job Title</label>
                  <input
                    type="text"
                    value={profileForm.currentJobTitle}
                    onChange={e => setProfileForm({ ...profileForm, currentJobTitle: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. Junior Developer"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Current Industry</label>
                  <input
                    type="text"
                    value={profileForm.currentIndustry}
                    onChange={e => setProfileForm({ ...profileForm, currentIndustry: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. Software & IT Services"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Total Years Experience</label>
                  <input
                    type="number"
                    min="0"
                    max="40"
                    value={profileForm.experienceYears}
                    onChange={e => setProfileForm({ ...profileForm, experienceYears: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Relevant Target Domain Experience (Yrs)</label>
                  <input
                    type="number"
                    min="0"
                    max="40"
                    value={profileForm.relevantExperienceYears}
                    onChange={e => setProfileForm({ ...profileForm, relevantExperienceYears: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Portfolio / GitHub URL</label>
                  <input
                    type="url"
                    value={profileForm.portfolioUrl}
                    onChange={e => setProfileForm({ ...profileForm, portfolioUrl: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="https://github.com/username"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Certifications & Credentials</label>
                <textarea
                  rows={2}
                  value={profileForm.certifications}
                  onChange={e => setProfileForm({ ...profileForm, certifications: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  placeholder="e.g. AWS Certified Developer Associate, Oracle Certified Professional Java SE"
                />
              </div>
            </div>
          )}

          {/* TAB 4: SKILLS MATRIX */}
          {activeTab === 'skills' && (
            <div className="space-y-4">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2">
                <div>
                  <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
                    <Sliders className="w-4 h-4 text-blue-600" /> Interactive Skill Self-Assessment Matrix
                  </h3>
                  <p className="text-xs text-slate-500">
                    Adjust sliders to rate your baseline level (0 = None, 5 = Expert).
                  </p>
                </div>
                
                {/* Category Filters */}
                <div className="flex flex-wrap items-center gap-1.5">
                  {skillCategories.map(cat => (
                    <button
                      key={cat}
                      type="button"
                      onClick={() => setActiveSkillCategory(cat)}
                      className={`px-3 py-1 rounded-lg text-xs font-semibold transition-all ${
                        activeSkillCategory === cat
                          ? 'bg-blue-600 text-white shadow-2xs'
                          : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                      }`}
                    >
                      {cat}
                    </button>
                  ))}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-3 max-h-[500px] overflow-y-auto pr-1">
                {filteredSkills.map(skill => {
                  const currentLevel = getUserSkillLevel(skill.id);
                  const req = selectedTargetCareer?.requiredSkills?.find(r => r.skillId === skill.id || (r as any).skill?.id === skill.id);
                  const requiredLevel = req ? req.requiredLevel : 0;
                  const isEssential = req ? req.isEssential : false;
                  const gapAmount = requiredLevel > 0 ? Math.max(0, requiredLevel - currentLevel) : 0;

                  return (
                    <div 
                      key={skill.id}
                      className={`p-3.5 rounded-2xl border space-y-2.5 transition-colors ${
                        req ? 'bg-white border-blue-200 shadow-xs' : 'bg-slate-50/80 border-slate-200/80'
                      }`}
                    >
                      <div className="flex items-start justify-between gap-2">
                        <div>
                          <h4 className="text-xs font-bold text-slate-900">{skill.name}</h4>
                          <div className="flex items-center gap-1.5 mt-0.5">
                            <span className="text-[10px] font-medium text-slate-500 bg-slate-200/60 px-1.5 py-0.5 rounded-md">
                              {skill.category}
                            </span>
                            {req && (
                              <span className="text-[10px] font-bold text-blue-700 bg-blue-100 px-1.5 py-0.5 rounded-md">
                                Req: Lvl {requiredLevel}
                              </span>
                            )}
                            {isEssential && (
                              <span className="text-[9px] font-extrabold text-red-700 bg-red-100 px-1 py-0.5 rounded-md uppercase">
                                Essential
                              </span>
                            )}
                          </div>
                        </div>

                        <span className={`text-xs font-bold px-2 py-0.5 rounded-lg border ${
                          currentLevel >= 4 
                            ? 'bg-emerald-50 text-emerald-800 border-emerald-200' 
                            : currentLevel >= 2
                            ? 'bg-blue-50 text-blue-800 border-blue-200'
                            : 'bg-slate-100 text-slate-600 border-slate-200'
                        }`}>
                          Lvl {currentLevel}/5
                        </span>
                      </div>

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
                        <div className="flex justify-between text-[9px] text-slate-400 font-medium">
                          <span>0: None</span>
                          <span>1: Basic</span>
                          <span>2: Elem</span>
                          <span>3: Inter</span>
                          <span>4: Adv</span>
                          <span>5: Expert</span>
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* TAB 5: CAREER PREFERENCES */}
          {activeTab === 'preferences' && (
            <div className="space-y-4">
              <h3 className="text-sm font-bold text-slate-900">Career & Work Preferences</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Target Focus Domain</label>
                  <input
                    type="text"
                    value={profileForm.targetFocus}
                    onChange={e => setProfileForm({ ...profileForm, targetFocus: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                    placeholder="e.g. Backend Engineering, Data Analytics"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Preferred Work Mode</label>
                  <select
                    value={profileForm.preferredWorkMode}
                    onChange={e => setProfileForm({ ...profileForm, preferredWorkMode: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 font-medium"
                  >
                    <option value="Remote">Remote</option>
                    <option value="Hybrid">Hybrid</option>
                    <option value="On-Site">On-Site</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Preferred Employment Type</label>
                  <select
                    value={profileForm.preferredEmploymentType}
                    onChange={e => setProfileForm({ ...profileForm, preferredEmploymentType: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 font-medium"
                  >
                    <option value="Full-Time">Full-Time</option>
                    <option value="Contract">Contract</option>
                    <option value="Internship">Internship</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Primary Career Goal / Objective</label>
                <input
                  type="text"
                  value={profileForm.careerGoal}
                  onChange={e => setProfileForm({ ...profileForm, careerGoal: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  placeholder="e.g. Transition into Senior Java Backend Developer role within 12 months"
                />
              </div>
            </div>
          )}

          {/* TAB 6: LEARNING PACE */}
          {activeTab === 'learning' && (
            <div className="space-y-4">
              <h3 className="text-sm font-bold text-slate-900">Learning Commitment & Roadmap Planning</h3>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Weekly Learning Availability (Hours)</label>
                  <input
                    type="number"
                    min="1"
                    max="60"
                    value={profileForm.weeklyHoursAvailable}
                    onChange={e => setProfileForm({ ...profileForm, weeklyHoursAvailable: parseInt(e.target.value) || 10 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Preferred Learning Pace</label>
                  <select
                    value={profileForm.preferredLearningPace}
                    onChange={e => setProfileForm({ ...profileForm, preferredLearningPace: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 font-medium"
                  >
                    <option value="Steady">Steady (5-10 hrs/wk)</option>
                    <option value="Accelerated">Accelerated (10-20 hrs/wk)</option>
                    <option value="Intensive">Intensive (20+ hrs/wk)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Preferred Roadmap Duration</label>
                  <select
                    value={profileForm.preferredRoadmapDuration}
                    onChange={e => setProfileForm({ ...profileForm, preferredRoadmapDuration: parseInt(e.target.value) || 6 })}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 font-bold"
                  >
                    <option value={3}>3 Months (Rapid Intensive)</option>
                    <option value={6}>6 Months (Standard Acceleration)</option>
                    <option value={12}>12 Months (Comprehensive Mastery)</option>
                  </select>
                </div>
              </div>
            </div>
          )}

          {/* TAB 7: SAVED CAREERS */}
          {activeTab === 'saved' && (
            <div className="space-y-6">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-2 border-b border-slate-100">
                <div>
                  <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
                    <Bookmark className="w-4 h-4 text-rose-500 fill-rose-500" />
                    Saved & Favorite Careers ({savedCareers.length})
                  </h3>
                  <p className="text-xs text-slate-500 mt-0.5">
                    Track aspirational roles, compare compensation and demand, or switch target career roadmaps.
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => navigateTo('results')}
                  className="px-3.5 py-1.5 bg-blue-50 hover:bg-blue-100 text-blue-700 font-bold text-xs rounded-xl border border-blue-200 transition-colors flex items-center gap-1.5 self-start sm:self-auto cursor-pointer"
                >
                  <Sparkles className="w-3.5 h-3.5" />
                  <span>Discover More Careers</span>
                </button>
              </div>

              {savedCareers.length === 0 ? (
                <div className="p-8 text-center bg-slate-50 rounded-2xl border border-slate-200/80 space-y-3">
                  <div className="w-12 h-12 rounded-2xl bg-rose-50 border border-rose-100 text-rose-500 flex items-center justify-center mx-auto shadow-xs">
                    <Bookmark className="w-6 h-6" />
                  </div>
                  <div>
                    <h4 className="text-sm font-bold text-slate-900">No Saved Careers Yet</h4>
                    <p className="text-xs text-slate-500 max-w-md mx-auto mt-1">
                      Explore career matches from your personalized questionnaire and bookmark the ones you find interesting!
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={() => navigateTo('results')}
                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors cursor-pointer"
                  >
                    View Career Recommendations
                  </button>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {savedCareers.map(sc => {
                    const isTarget = selectedTargetCareer?.id === sc.careerId;
                    return (
                      <div
                        key={sc.id}
                        className={`p-5 rounded-2xl border transition-all space-y-4 bg-white ${
                          isTarget
                            ? 'border-blue-500 ring-2 ring-blue-500/20 shadow-md'
                            : 'border-slate-200 shadow-xs hover:border-slate-300'
                        }`}
                      >
                        <div className="flex items-start justify-between gap-3">
                          <div className="space-y-1">
                            <div className="flex flex-wrap items-center gap-1.5">
                              <span className="text-[10px] font-bold text-slate-600 bg-slate-100 px-2 py-0.5 rounded-md border border-slate-200">
                                {sc.category}
                              </span>
                              <span className="text-[10px] font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200">
                                {sc.demandLevel} Demand
                              </span>
                              {isTarget && (
                                <span className="text-[10px] font-extrabold text-blue-700 bg-blue-50 px-2 py-0.5 rounded-md border border-blue-200">
                                  Current Target ✓
                                </span>
                              )}
                            </div>
                            <h4 className="text-base font-extrabold text-slate-950">{sc.title}</h4>
                          </div>

                          <button
                            type="button"
                            onClick={() => toggleSaveCareer(sc.careerId)}
                            title="Remove from saved"
                            className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors cursor-pointer"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>

                        <p className="text-xs text-slate-600 leading-relaxed line-clamp-2">
                          {sc.description}
                        </p>

                        <div className="flex items-center gap-4 text-xs text-slate-600 pt-2 border-t border-slate-100">
                          <span className="flex items-center gap-1 font-semibold text-slate-800">
                            <DollarSign className="w-3.5 h-3.5 text-slate-400" /> {sc.averageSalary}
                          </span>
                          <span className="flex items-center gap-1 font-semibold text-emerald-700">
                            <TrendingUp className="w-3.5 h-3.5" /> {sc.growthRate}
                          </span>
                        </div>

                        <div className="flex items-center gap-2 pt-1">
                          <button
                            type="button"
                            onClick={() => {
                              selectTargetCareer(sc.careerId);
                              navigateTo('target-selection');
                            }}
                            className={`flex-1 py-2 px-3 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-1.5 cursor-pointer ${
                              isTarget
                                ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                                : 'bg-blue-600 hover:bg-blue-700 text-white shadow-xs'
                            }`}
                          >
                            <Target className="w-3.5 h-3.5" />
                            <span>{isTarget ? 'Active Target' : 'Set as Target'}</span>
                          </button>

                          <button
                            type="button"
                            onClick={() => {
                              selectTargetCareer(sc.careerId);
                              navigateTo('skill-gap');
                            }}
                            className="py-2 px-3 bg-slate-100 hover:bg-slate-200 text-slate-800 font-semibold text-xs rounded-xl transition-colors flex items-center gap-1 cursor-pointer"
                          >
                            <ExternalLink className="w-3.5 h-3.5" />
                            <span>Gaps & Roadmap</span>
                          </button>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}


          {/* Action Bar */}
          <div className="flex items-center justify-between pt-4 border-t border-slate-100">
            <button
              type="submit"
              disabled={isSaving}
              className="px-6 py-2.5 bg-blue-600 text-white font-bold text-xs rounded-xl hover:bg-blue-700 flex items-center gap-2 shadow-xs transition-colors"
            >
              <Save className="w-4 h-4" />
              <span>{isSaving ? 'Saving Changes...' : 'Save All Profile Changes'}</span>
            </button>

            <button
              type="button"
              onClick={() => navigateTo('roadmap')}
              className="px-4 py-2.5 bg-slate-100 text-slate-700 hover:bg-slate-200 font-semibold text-xs rounded-xl flex items-center gap-1.5 transition-colors"
            >
              <span>View My Roadmap</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>

        </form>
      </div>
    </div>
  );
};
