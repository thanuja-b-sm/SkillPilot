import React, { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { 
  Map, 
  CheckCircle2, 
  Clock, 
  Sparkles, 
  Printer, 
  Share2, 
  ArrowRight,
  ShieldCheck,
  Bot,
  Loader2,
  RefreshCw,
  Calendar,
  Lock,
  LogIn,
  UserPlus
} from 'lucide-react';

export const RoadmapPage: React.FC = () => {
  const { 
    userRole,
    navigateTo,
    activeRoadmap, 
    selectedTargetCareer, 
    aiEnhancing, 
    enhanceRoadmapSummaryWithAI, 
    showToast,
    generateRoadmap,
    isLoadingRoadmap,
    userProfile,
    token
  } = useApp();

  const [selectedDuration, setSelectedDuration] = useState<6 | 12>(6);
  
  const storageKey = `skillpilot_completed_goals_${userProfile?.id || 'guest'}_${selectedTargetCareer?.id || 'default'}`;
  
  const [completedGoals, setCompletedGoals] = useState<Record<string, boolean>>(() => {
    const saved = localStorage.getItem(storageKey);
    if (saved) {
      try { return JSON.parse(saved); } catch {}
    }
    return {};
  });

  const isGuest = userRole === 'guest';
  const isAuthenticated = !!(token || localStorage.getItem('skillpilot_token'));

  useEffect(() => {
    const saved = localStorage.getItem(storageKey);
    if (saved) {
      try { setCompletedGoals(JSON.parse(saved)); } catch {}
    } else {
      setCompletedGoals({});
    }
  }, [userProfile?.id, selectedTargetCareer?.id]);

  const toggleGoal = (goalKey: string) => {
    setCompletedGoals(prev => {
      const nextVal = !prev[goalKey];
      const updated = { ...prev, [goalKey]: nextVal };
      localStorage.setItem(storageKey, JSON.stringify(updated));
      showToast(nextVal ? 'Marked milestone objective as completed! ✓' : 'Unchecked milestone objective', 'info');
      return updated;
    });
  };

  const handlePrintExport = () => {
    window.print();
    showToast('Triggered document print view', 'info');
  };

  const handleShare = async () => {
    if (navigator.clipboard) {
      await navigator.clipboard.writeText(window.location.href);
      showToast('Roadmap link copied to clipboard!', 'success');
    } else {
      showToast('Sharing not supported on this browser', 'warning');
    }
  };

  if (!selectedTargetCareer) {
    return (
      <div className="max-w-2xl mx-auto my-12 p-8 bg-white rounded-3xl border border-slate-200 text-center space-y-4">
        <Map className="w-12 h-12 text-slate-400 mx-auto" />
        <h2 className="text-xl font-bold text-slate-900">No Target Career Selected</h2>
        <p className="text-xs text-slate-500">Please choose a target career track first to generate your milestone roadmap.</p>
        <button
          onClick={() => navigateTo('results')}
          className="px-5 py-2.5 bg-blue-600 text-white font-bold text-xs rounded-xl cursor-pointer"
        >
          View Ranked Career Matches
        </button>
      </div>
    );
  }

  return (
    <div className="relative max-w-5xl mx-auto pb-16">
      <div className={`space-y-8 text-left transition-all duration-300 ${
        isGuest ? 'filter blur-md select-none pointer-events-none opacity-40 max-h-[75vh] overflow-hidden' : ''
      }`}>
        {/* Roadmap Generator Controls */}
        {isAuthenticated && (
          <div className="bg-white rounded-3xl p-6 border border-slate-200/90 shadow-md">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div>
                <h2 className="text-base font-bold text-slate-950 flex items-center gap-2">
                  <Calendar className="w-4 h-4 text-blue-600" /> Roadmap Generation
                </h2>
                <p className="text-xs text-slate-500 mt-0.5">
                  Select your target completion timeline, then generate a fresh milestone plan.
                </p>
              </div>
              <div className="flex items-center gap-3 shrink-0">
                <div className="flex items-center gap-1 p-1 bg-slate-100 rounded-xl border border-slate-200">
                  <button
                    onClick={() => setSelectedDuration(6)}
                    className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${
                      selectedDuration === 6
                        ? 'bg-white text-blue-700 shadow-xs'
                        : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    6 Months
                  </button>
                  <button
                    onClick={() => setSelectedDuration(12)}
                    className={`px-4 py-1.5 rounded-lg text-xs font-bold transition-all ${
                      selectedDuration === 12
                        ? 'bg-white text-blue-700 shadow-xs'
                        : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    12 Months
                  </button>
                </div>
                <button
                  onClick={() => generateRoadmap(selectedDuration)}
                  disabled={isLoadingRoadmap}
                  className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed cursor-pointer"
                >
                  {isLoadingRoadmap
                    ? <Loader2 className="w-4 h-4 animate-spin" />
                    : <RefreshCw className="w-4 h-4" />
                  }
                  {isLoadingRoadmap ? 'Generating…' : 'Generate Roadmap'}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Loading State */}
        {isLoadingRoadmap && (
          <div className="bg-white rounded-3xl p-12 border border-slate-200/90 shadow-md flex flex-col items-center justify-center gap-4">
            <Loader2 className="w-10 h-10 text-blue-600 animate-spin" />
            <p className="text-sm font-semibold text-slate-600">Generating {selectedDuration}-month milestone roadmap…</p>
            <p className="text-xs text-slate-400">The backend engine is calculating skill gap priorities and phase timelines.</p>
          </div>
        )}

        {/* No Roadmap Yet */}
        {!isLoadingRoadmap && !activeRoadmap && (
          <div className="max-w-2xl mx-auto my-12 p-8 bg-white rounded-3xl border border-slate-200 text-center space-y-4">
            <Map className="w-12 h-12 text-slate-400 mx-auto" />
            <h2 className="text-xl font-bold text-slate-900">No Roadmap Generated</h2>
            <p className="text-xs text-slate-500">
              {isAuthenticated
                ? 'Click "Generate Roadmap" above to create your personalised milestone plan.'
                : 'Sign in to generate your personalised roadmap from the backend engine.'}
            </p>
            {!isAuthenticated && (
              <button
                onClick={() => generateRoadmap(6)}
                className="px-5 py-2.5 bg-blue-600 text-white font-bold text-xs rounded-xl cursor-pointer"
              >
                Preview Roadmap (Guest Mode)
              </button>
            )}
          </div>
        )}

        {/* Roadmap Content */}
        {!isLoadingRoadmap && activeRoadmap && (
          <>
            {/* Top Header Card */}
            <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-bold text-blue-600 bg-blue-50 px-2.5 py-0.5 rounded-md border border-blue-100">
                      Action Plan
                    </span>
                    <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200 flex items-center gap-1">
                      <ShieldCheck className="w-3.5 h-3.5" /> Calculated Timeline Matrix
                    </span>
                  </div>
                  <h1 className="text-2xl font-bold text-slate-950 mt-1">Milestone Roadmap: {selectedTargetCareer.title}</h1>
                  <p className="text-xs text-slate-500 font-medium">
                    Overall Timeline: {activeRoadmap.overallTimeline} | Calculated Baseline Readiness: {activeRoadmap.overallReadiness}%
                  </p>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <button
                    onClick={handlePrintExport}
                    className="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-semibold rounded-xl flex items-center gap-1.5 transition-colors cursor-pointer"
                  >
                    <Printer className="w-4 h-4" /> Export / Print
                  </button>
                  <button
                    onClick={handleShare}
                    className="px-3.5 py-2 bg-blue-50 hover:bg-blue-100 text-blue-700 text-xs font-semibold rounded-xl flex items-center gap-1.5 transition-colors cursor-pointer"
                  >
                    <Share2 className="w-4 h-4" /> Share
                  </button>
                </div>
              </div>

              {/* AI Explanation Summary Support Panel */}
              <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 rounded-2xl p-6 text-white space-y-3 relative overflow-hidden shadow-lg border border-slate-800">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="p-1.5 bg-blue-500/20 rounded-lg text-blue-400">
                      <Bot className="w-4 h-4" />
                    </span>
                    <span className="text-xs font-bold uppercase tracking-wider text-blue-300">
                      AI Narrative Support (Gemini 3.6 Flash)
                    </span>
                  </div>

                  <button
                    onClick={enhanceRoadmapSummaryWithAI}
                    disabled={aiEnhancing}
                    className="px-3 py-1.5 bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold rounded-xl transition-all flex items-center gap-1.5 shadow-2xs disabled:opacity-50 cursor-pointer"
                  >
                    <Sparkles className="w-3.5 h-3.5 text-blue-300 animate-spin-slow" />
                    <span>{aiEnhancing ? 'Enhancing Readability...' : 'Polish Wording with AI'}</span>
                  </button>
                </div>

                <p className="text-xs text-slate-200 leading-relaxed font-normal bg-slate-800/60 p-3.5 rounded-xl border border-slate-700/60">
                  {activeRoadmap.aiExplanation || 'Click "Polish Wording with AI" to generate an AI narrative summary for this roadmap.'}
                </p>

                <div className="flex items-center gap-1.5 text-[10px] text-slate-400 italic">
                  <ShieldCheck className="w-3 h-3 text-emerald-400" />
                  <span>AI is used exclusively for narrative clarity. Underlying milestone dates and scoring remain 100% deterministic system calculations.</span>
                </div>
              </div>
            </div>

            {/* Phased Milestone Timeline */}
            <div className="space-y-6">
              <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                <Clock className="w-5 h-5 text-blue-600" /> Phased Milestone Sequence
              </h2>

              <div className="space-y-6">
                {activeRoadmap.phases.map((phase, idx) => {
                  const isDone = phase.status === 'completed';
                  const isInProgress = phase.status === 'in_progress';

                  return (
                    <div
                      key={phase.id}
                      className={`bg-white rounded-3xl p-6 sm:p-8 border transition-all space-y-5 ${
                        isInProgress
                          ? 'border-blue-500 ring-2 ring-blue-500/20 shadow-md'
                          : 'border-slate-200/90 shadow-xs'
                      }`}
                    >
                      {/* Header Row */}
                      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                        <div className="space-y-1">
                          <div className="flex items-center gap-2">
                            <span className="text-xs font-bold text-slate-900 bg-slate-100 px-2.5 py-0.5 rounded-md border border-slate-200">
                              {phase.monthRange}
                            </span>
                            <span className={`text-xs font-bold px-2.5 py-0.5 rounded-md border ${
                              isDone 
                                ? 'bg-emerald-50 text-emerald-800 border-emerald-200' 
                                : isInProgress
                                ? 'bg-blue-50 text-blue-800 border-blue-200'
                                : 'bg-slate-100 text-slate-600 border-slate-200'
                            }`}>
                              {isDone ? 'Completed Stage ✓' : isInProgress ? 'Active Stage In-Progress' : 'Upcoming Stage'}
                            </span>
                          </div>

                          <h3 className="text-lg font-bold text-slate-950">{phase.phaseTitle}</h3>
                          <p className="text-xs font-semibold text-blue-600">Focus: {phase.focusArea}</p>
                        </div>

                        <div className="w-8 h-8 rounded-full bg-slate-900 text-white font-bold text-xs flex items-center justify-center shrink-0">
                          0{idx + 1}
                        </div>
                      </div>

                      {/* Goals List */}
                      <div className="space-y-2 pt-2">
                        <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider">Milestone Objectives:</h4>
                        <ul className="space-y-2 text-xs text-slate-800">
                          {phase.goals.map((goal, gIdx) => {
                            const goalKey = `${idx}-${gIdx}`;
                            const isGoalCompleted = isDone || completedGoals[goalKey];

                            return (
                              <li
                                key={gIdx}
                                onClick={() => toggleGoal(goalKey)}
                                className={`flex items-start gap-2.5 p-2.5 rounded-xl border transition-all cursor-pointer select-none ${
                                  isGoalCompleted
                                    ? 'bg-emerald-50/70 border-emerald-200 text-emerald-950 font-medium'
                                    : 'bg-slate-50 border-slate-200/60 text-slate-800 hover:bg-blue-50/50 hover:border-blue-200'
                                }`}
                              >
                                <CheckCircle2 className={`w-4 h-4 shrink-0 mt-0.5 transition-colors ${isGoalCompleted ? 'text-emerald-600' : 'text-slate-300'}`} />
                                <span className={isGoalCompleted ? 'line-through text-emerald-900 opacity-90' : ''}>{goal}</span>
                              </li>
                            );
                          })}
                        </ul>
                      </div>

                      {/* Expected Outcome */}
                      <div className="p-3 bg-sky-50/70 border border-sky-200 rounded-xl space-y-1 text-xs">
                        <span className="font-bold text-sky-950 block">Target Verified Outcome:</span>
                        <p className="text-sky-900 leading-relaxed">{phase.expectedOutcome}</p>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </>
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
                Personalized 6-12 month execution roadmaps, milestone directives, and AI wording polishing are reserved for signed-in members.
              </p>
            </div>

            <div className="space-y-3 pt-2">
              <button
                onClick={() => navigateTo('login')}
                className="w-full py-3.5 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold text-sm rounded-xl shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2 group cursor-pointer"
              >
                <LogIn className="w-4 h-4" />
                <span>Sign In to Access Roadmap</span>
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
