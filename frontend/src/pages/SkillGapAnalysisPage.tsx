import React from 'react';
import { useApp } from '../context/AppContext';
import { 
  GitCommit, 
  CheckCircle2, 
  AlertOctagon, 
  AlertTriangle, 
  Info, 
  ArrowRight, 
  ShieldCheck, 
  Map, 
  RefreshCw,
  Loader2,
  Lock,
  LogIn,
  UserPlus
} from 'lucide-react';

export const SkillGapAnalysisPage: React.FC = () => {
  const { userRole, selectedTargetCareer, skillGaps, navigateTo, userProfile, backendSkillGap, isLoadingSkillGap, token, updateUserSkill } = useApp();

  const isGuest = userRole === 'guest';

  if (isLoadingSkillGap) {
    return (
      <div className="max-w-2xl mx-auto my-12 p-8 bg-white rounded-3xl border border-slate-200 text-center space-y-4 shadow-md">
        <Loader2 className="w-10 h-10 animate-spin text-blue-600 mx-auto" />
        <h2 className="text-lg font-bold text-slate-900">Synchronizing Skill Gap Analysis…</h2>
        <p className="text-xs text-slate-500">Evaluating your proficiency against required target career benchmarks.</p>
      </div>
    );
  }

  if (!selectedTargetCareer) {
    return (
      <div className="max-w-2xl mx-auto my-12 p-8 bg-white rounded-3xl border border-slate-200 text-center space-y-4">
        <GitCommit className="w-12 h-12 text-slate-400 mx-auto" />
        <h2 className="text-xl font-bold text-slate-900">No Target Career Selected</h2>
        <p className="text-xs text-slate-500">Please choose a target career track first to perform a gap analysis.</p>
        <button
          onClick={() => navigateTo('results')}
          className="px-5 py-2.5 bg-blue-600 text-white font-bold text-xs rounded-xl cursor-pointer"
        >
          View Ranked Career Matches
        </button>
      </div>
    );
  }

  const isAuthenticated = !!(token || localStorage.getItem('skillpilot_token'));

  // Group gaps by severity
  const criticalGaps = skillGaps.filter(g => g.severity === 'critical');
  const highGaps = skillGaps.filter(g => g.severity === 'high');
  const mediumGaps = skillGaps.filter(g => g.severity === 'medium');
  const satisfiedSkills = skillGaps.filter(g => g.gapAmount === 0);

  const totalRequired = skillGaps.length;
  const totalSatisfied = satisfiedSkills.length;

  // Readiness: always use backend value when authenticated
  const readinessRatio = isAuthenticated && backendSkillGap?.readinessScore != null
    ? backendSkillGap.readinessScore
    : Math.round((totalSatisfied / (totalRequired || 1)) * 100);

  return (
    <div className="relative max-w-6xl mx-auto pb-16">
      <div className={`space-y-8 text-left transition-all duration-300 ${
        isGuest ? 'filter blur-md select-none pointer-events-none opacity-40 max-h-[75vh] overflow-hidden' : ''
      }`}>
        {/* Header Banner */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-blue-600 bg-blue-50 px-2.5 py-0.5 rounded-md border border-blue-100">
                  Granular Skill Matrix
                </span>
                <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200 flex items-center gap-1">
                  <ShieldCheck className="w-3.5 h-3.5" /> Severity-Coded Analysis
                </span>
              </div>
              <h1 className="text-2xl font-bold text-slate-950 mt-1">
                Skill Gap Analysis: <span className="text-blue-600">{selectedTargetCareer.title}</span>
              </h1>
              <p className="text-xs text-slate-500 font-medium">
                Comparing your current ratings against required industry proficiency benchmark.
              </p>
            </div>

            <div className="flex items-center gap-3 shrink-0">
              <button
                onClick={() => navigateTo('roadmap')}
                className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl flex items-center gap-2 transition-all shadow-xs"
              >
                <Map className="w-4 h-4" />
                <span>Generate Roadmap</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Overview Metric Bar */}
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 pt-2">
            <div className="p-4 rounded-2xl bg-slate-50 border border-slate-200/70">
              <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider">Overall Readiness</span>
              <div className="text-2xl font-black text-slate-900 mt-1">{readinessRatio}%</div>
              <div className="w-full bg-slate-200 h-1.5 rounded-full mt-2 overflow-hidden">
                <div className="bg-blue-600 h-full rounded-full" style={{ width: `${readinessRatio}%` }} />
              </div>
            </div>

            <div className="p-4 rounded-2xl bg-red-50/50 border border-red-200/80">
              <span className="text-[10px] font-bold text-red-700 uppercase tracking-wider">Critical Deficits</span>
              <div className="text-2xl font-black text-red-900 mt-1">{criticalGaps.length}</div>
              <p className="text-[10px] text-red-600 mt-1 font-medium">Immediate priority</p>
            </div>

            <div className="p-4 rounded-2xl bg-amber-50/50 border border-amber-200/80">
              <span className="text-[10px] font-bold text-amber-700 uppercase tracking-wider">High/Medium Gaps</span>
              <div className="text-2xl font-black text-amber-900 mt-1">{highGaps.length + mediumGaps.length}</div>
              <p className="text-[10px] text-amber-600 mt-1 font-medium">Core milestones</p>
            </div>

            <div className="p-4 rounded-2xl bg-emerald-50/50 border border-emerald-200/80">
              <span className="text-[10px] font-bold text-emerald-700 uppercase tracking-wider">Satisfied Skills</span>
              <div className="text-2xl font-black text-emerald-900 mt-1">{totalSatisfied} / {totalRequired}</div>
              <p className="text-[10px] text-emerald-600 mt-1 font-medium">Benchmark met</p>
            </div>
          </div>
        </div>

        {/* Detailed Gap Cards */}
        {skillGaps.length > 0 && (
          <div className="space-y-4">
            <h2 className="text-lg font-bold text-slate-900">Required Skill Breakdown</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {skillGaps.map((gap, idx) => {
                const isCritical = gap.severity === 'critical';
                const isHigh = gap.severity === 'high';
                const isSatisfied = gap.gapAmount === 0;

                return (
                  <div
                    key={gap.skillId || idx}
                    className={`bg-white rounded-2xl p-5 border space-y-3 ${
                      isSatisfied
                        ? 'border-emerald-200 bg-emerald-50/20'
                        : isCritical
                        ? 'border-red-200 bg-red-50/10'
                        : isHigh
                        ? 'border-amber-200 bg-amber-50/10'
                        : 'border-slate-200'
                    }`}
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="text-xs font-semibold text-slate-500">{gap.category}</span>
                          <span className={`text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-md border ${
                            isSatisfied
                              ? 'bg-emerald-100 text-emerald-800 border-emerald-200'
                              : isCritical
                              ? 'bg-red-100 text-red-800 border-red-200'
                              : isHigh
                              ? 'bg-amber-100 text-amber-800 border-amber-200'
                              : 'bg-slate-100 text-slate-700 border-slate-200'
                          }`}>
                            {isSatisfied ? 'Satisfied' : `${gap.severity} Deficit`}
                          </span>
                        </div>
                        <h3 className="text-base font-bold text-slate-950 mt-1">{gap.skillName}</h3>
                      </div>

                      <div className="text-right shrink-0 space-y-1">
                        <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">Your Level</span>
                        <div className="flex items-center gap-1.5 bg-slate-100 p-1 rounded-xl border border-slate-200">
                          <button
                            onClick={() => updateUserSkill(gap.skillId, Math.max(0, gap.currentLevel - 1))}
                            disabled={gap.currentLevel <= 0}
                            className="w-5 h-5 rounded-lg bg-white border border-slate-200 text-slate-700 font-bold text-xs flex items-center justify-center hover:bg-slate-50 disabled:opacity-40 cursor-pointer shadow-2xs"
                            title="Decrease skill rating"
                          >
                            -
                          </button>
                          <span className="text-xs font-extrabold text-slate-900 px-1">{gap.currentLevel} / {gap.requiredLevel}</span>
                          <button
                            onClick={() => updateUserSkill(gap.skillId, Math.min(5, gap.currentLevel + 1))}
                            disabled={gap.currentLevel >= 5}
                            className="w-5 h-5 rounded-lg bg-white border border-slate-200 text-slate-700 font-bold text-xs flex items-center justify-center hover:bg-slate-50 disabled:opacity-40 cursor-pointer shadow-2xs"
                            title="Increase skill rating"
                          >
                            +
                          </button>
                        </div>
                      </div>
                    </div>

                    <div className="space-y-1">
                      <div className="flex justify-between text-xs text-slate-600 font-semibold">
                        <span>Current Rating: {gap.currentLevel}/5</span>
                        <span>Benchmark Target: {gap.requiredLevel}/5</span>
                      </div>
                      <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden border border-slate-200">
                        <div
                          className={`h-full rounded-full ${isSatisfied ? 'bg-emerald-500' : isCritical ? 'bg-red-500' : 'bg-amber-500'}`}
                          style={{ width: `${Math.min(100, (gap.currentLevel / gap.requiredLevel) * 100)}%` }}
                        />
                      </div>
                    </div>

                    <div className="pt-2 border-t border-slate-100 flex items-center justify-between text-xs">
                      <span className="font-semibold text-slate-700">Remedy Action:</span>
                      <span className="text-slate-600 font-medium truncate max-w-[240px]">{gap.recommendedAction}</span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {/* Guest Lock Overlay */}
      {isGuest && (
        <div className="absolute inset-0 z-30 flex items-start sm:items-center justify-center p-4 pt-16 sm:pt-0">
          <div className="max-w-md w-full bg-white/95 backdrop-blur-md rounded-3xl p-8 border border-slate-200/90 shadow-2xl text-center space-y-6">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-blue-600 to-indigo-600 text-white flex items-center justify-center mx-auto shadow-lg shadow-blue-500/30">
              <Lock className="w-8 h-8" />
            </div>

            <div className="space-y-2">
              <span className="text-[11px] font-bold text-blue-700 bg-blue-50 px-3 py-1 rounded-full uppercase tracking-wider border border-blue-100">
                Restricted Access
              </span>
              <h2 className="text-2xl font-extrabold text-slate-950 pt-1">Sign In Required</h2>
              <p className="text-xs text-slate-600 leading-relaxed font-normal">
                Granular skill gap analyses, deficit severity ratings, and learning remedies are available exclusively to signed in members.
              </p>
            </div>

            <div className="space-y-3 pt-2">
              <button
                onClick={() => navigateTo('login')}
                className="w-full py-3.5 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold text-sm rounded-xl shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2 group cursor-pointer"
              >
                <LogIn className="w-4 h-4" />
                <span>Sign In to Access Gap Matrix</span>
                <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
              </button>

              <button
                onClick={() => navigateTo('register')}
                className="w-full py-3.5 px-4 bg-slate-100 hover:bg-slate-200 text-slate-800 font-bold text-sm rounded-xl transition-all border border-slate-200 flex items-center justify-center gap-2 cursor-pointer"
              >
                <UserPlus className="w-4 h-4 text-blue-600" />
                <span>Create Free Account</span>
              </button>
            </div>

            <div className="pt-2 border-t border-slate-100">
              <button
                onClick={() => navigateTo('landing')}
                className="text-xs font-semibold text-slate-500 hover:text-slate-700 underline cursor-pointer"
              >
                ← Back to Home Page
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
