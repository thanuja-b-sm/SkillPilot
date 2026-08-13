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
  UserPlus,
  AlertTriangle,
  Edit3,
  Sliders,
  Award
} from 'lucide-react';
import { RoadmapMilestone } from '../types';

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

  const [selectedDuration, setSelectedDuration] = useState<3 | 6 | 12>(6);
  const [editingMilestoneId, setEditingMilestoneId] = useState<string | null>(null);
  const [milestoneNotes, setMilestoneNotes] = useState<Record<string, string>>({});
  const [isUpdatingProgress, setIsUpdatingProgress] = useState<Record<string, boolean>>({});

  const isGuest = userRole === 'guest';
  const isAuthenticated = !!(token || localStorage.getItem('skillpilot_token'));

  useEffect(() => {
    if (activeRoadmap?.phases) {
      const initialNotes: Record<string, string> = {};
      activeRoadmap.phases.forEach(ms => {
        if (ms.notes) initialNotes[ms.id] = ms.notes;
      });
      setMilestoneNotes(initialNotes);
    }
  }, [activeRoadmap]);

  const handleUpdateMilestone = async (milestoneId: string, status: string, completionPercentage: number, notes?: string) => {
    if (!activeRoadmap?.id) return;
    setIsUpdatingProgress(prev => ({ ...prev, [milestoneId]: true }));
    const currentToken = token || localStorage.getItem('skillpilot_token');
    
    try {
      const res = await fetch(`/api/user/roadmaps/${activeRoadmap.id}/milestones/${milestoneId}/progress`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${currentToken}`
        },
        body: JSON.stringify({
          status,
          completionPercentage,
          notes: notes !== undefined ? notes : milestoneNotes[milestoneId]
        })
      });

      if (res.ok) {
        const updatedMs: RoadmapMilestone = await res.json();
        // Mutate local state activeRoadmap phases
        if (activeRoadmap.phases) {
          const idx = activeRoadmap.phases.findIndex(m => m.id === milestoneId);
          if (idx !== -1) {
            activeRoadmap.phases[idx] = updatedMs;
          }
        }
        showToast(`Updated milestone "${updatedMs.phaseTitle}" progress (${updatedMs.completionPercentage}%)`, 'success');
      } else {
        showToast('Failed to persist milestone progress', 'error');
      }
    } catch (err) {
      console.error('Error updating milestone progress:', err);
      showToast('Error persisting milestone tracking to server', 'error');
    } finally {
      setIsUpdatingProgress(prev => ({ ...prev, [milestoneId]: false }));
      setEditingMilestoneId(null);
    }
  };

  const handlePrintExport = () => {
    window.print();
    showToast('Triggered print view', 'info');
  };

  const handleShare = async () => {
    if (navigator.clipboard) {
      await navigator.clipboard.writeText(window.location.href);
      showToast('Roadmap link copied to clipboard!', 'success');
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

  const overallMilestonesCount = activeRoadmap?.phases?.length || 0;
  const completedMilestonesCount = activeRoadmap?.phases?.filter(m => m.status === 'completed' || m.completionPercentage === 100).length || 0;
  const overallProgressPercentage = overallMilestonesCount > 0 ? Math.round((completedMilestonesCount / overallMilestonesCount) * 100) : 0;

  return (
    <div className="relative max-w-5xl mx-auto pb-16">
      <div className={`space-y-8 text-left transition-all duration-300 ${
        isGuest ? 'filter blur-md select-none pointer-events-none opacity-40 max-h-[75vh] overflow-hidden' : ''
      }`}>
        
        {/* Stale Roadmap Warning Banner */}
        {activeRoadmap?.isStale && (
          <div className="bg-amber-50 border border-amber-200 rounded-2xl p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs text-amber-900">
            <div className="flex items-center gap-2">
              <AlertTriangle className="w-5 h-5 text-amber-600 shrink-0" />
              <div>
                <span className="font-bold">Roadmap Context Stale Notice</span>
                <p className="text-amber-800 text-[11px]">Your skills or profile information changed since this roadmap was generated.</p>
              </div>
            </div>
            <button
              onClick={() => generateRoadmap(selectedDuration)}
              className="px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white font-bold text-xs rounded-xl shrink-0 transition-colors"
            >
              Update Roadmap Now
            </button>
          </div>
        )}

        {/* Roadmap Duration & Generation Controls */}
        {isAuthenticated && (
          <div className="bg-white rounded-3xl p-6 border border-slate-200/90 shadow-md">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div>
                <h2 className="text-base font-bold text-slate-950 flex items-center gap-2">
                  <Calendar className="w-4 h-4 text-blue-600" /> Duration & Planning Strategy
                </h2>
                <p className="text-xs text-slate-500 mt-0.5">
                  Select roadmap duration (3, 6, or 12 months) and generate a tailored milestone plan.
                </p>
              </div>
              <div className="flex items-center gap-3 shrink-0">
                <div className="flex items-center gap-1 p-1 bg-slate-100 rounded-xl border border-slate-200">
                  <button
                    onClick={() => setSelectedDuration(3)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                      selectedDuration === 3 ? 'bg-white text-blue-700 shadow-xs' : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    3 Mo (Rapid)
                  </button>
                  <button
                    onClick={() => setSelectedDuration(6)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                      selectedDuration === 6 ? 'bg-white text-blue-700 shadow-xs' : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    6 Mo (Standard)
                  </button>
                  <button
                    onClick={() => setSelectedDuration(12)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                      selectedDuration === 12 ? 'bg-white text-blue-700 shadow-xs' : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    12 Mo (Mastery)
                  </button>
                </div>
                <button
                  onClick={() => generateRoadmap(selectedDuration)}
                  disabled={isLoadingRoadmap}
                  className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-2 disabled:opacity-60 cursor-pointer"
                >
                  {isLoadingRoadmap ? <Loader2 className="w-4 h-4 animate-spin" /> : <RefreshCw className="w-4 h-4" />}
                  <span>{isLoadingRoadmap ? 'Generating…' : 'Generate Roadmap'}</span>
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

        {/* Roadmap Content */}
        {!isLoadingRoadmap && activeRoadmap && (
          <>
            {/* Top Header Card & Progress */}
            <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-6">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-bold text-blue-600 bg-blue-50 px-2.5 py-0.5 rounded-md border border-blue-100">
                      {activeRoadmap.overallTimeline || `${selectedDuration} Months Roadmap`}
                    </span>
                    <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200 flex items-center gap-1">
                      <ShieldCheck className="w-3.5 h-3.5" /> Database Authoritative
                    </span>
                  </div>
                  <h1 className="text-2xl font-bold text-slate-950 mt-1">Target Roadmap: {selectedTargetCareer.title}</h1>
                  <p className="text-xs text-slate-500 font-medium">
                    Calculated Overall Readiness: {activeRoadmap.overallReadiness}% | Progress: {completedMilestonesCount} / {overallMilestonesCount} Stages Completed
                  </p>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <button
                    onClick={handlePrintExport}
                    className="px-3.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-semibold rounded-xl flex items-center gap-1.5 transition-colors cursor-pointer"
                  >
                    <Printer className="w-4 h-4" /> Print
                  </button>
                  <button
                    onClick={handleShare}
                    className="px-3.5 py-2 bg-blue-50 hover:bg-blue-100 text-blue-700 text-xs font-semibold rounded-xl flex items-center gap-1.5 transition-colors cursor-pointer"
                  >
                    <Share2 className="w-4 h-4" /> Share
                  </button>
                </div>
              </div>

              {/* Progress Meter Bar */}
              <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 space-y-2">
                <div className="flex items-center justify-between text-xs font-bold">
                  <span className="text-slate-800 flex items-center gap-1.5">
                    <Award className="w-4 h-4 text-blue-600" /> Roadmap Execution Progress
                  </span>
                  <span className="text-blue-700">{overallProgressPercentage}%</span>
                </div>
                <div className="w-full h-3 bg-slate-200 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-blue-600 rounded-full transition-all duration-500"
                    style={{ width: `${overallProgressPercentage}%` }}
                  />
                </div>
              </div>

              {/* AI Narrative Support Panel */}
              <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 rounded-2xl p-5 text-white space-y-3 shadow-lg border border-slate-800">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Bot className="w-4 h-4 text-blue-400" />
                    <span className="text-xs font-bold uppercase tracking-wider text-blue-300">
                      AI Narrative Summary (Gemini 3.6 Flash)
                    </span>
                  </div>

                  <button
                    onClick={enhanceRoadmapSummaryWithAI}
                    disabled={aiEnhancing}
                    className="px-3 py-1.5 bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold rounded-xl transition-all flex items-center gap-1.5 shadow-2xs disabled:opacity-50 cursor-pointer"
                  >
                    <Sparkles className="w-3.5 h-3.5 text-blue-300 animate-spin-slow" />
                    <span>{aiEnhancing ? 'Enhancing...' : 'Polish Wording'}</span>
                  </button>
                </div>

                <p className="text-xs text-slate-200 leading-relaxed bg-slate-800/60 p-3.5 rounded-xl border border-slate-700/60">
                  {activeRoadmap.aiExplanation || 'Click "Polish Wording" to generate an AI explanation for this roadmap.'}
                </p>
              </div>
            </div>

            {/* Phased Milestone Timeline */}
            <div className="space-y-6">
              <h2 className="text-lg font-bold text-slate-950 flex items-center gap-2">
                <Clock className="w-5 h-5 text-blue-600" /> Phased Milestone Sequence & Interactive Progress Tracking
              </h2>

              <div className="space-y-6">
                {activeRoadmap.phases.map((phase, idx) => {
                  const isDone = phase.status === 'completed' || phase.completionPercentage === 100;
                  const isInProgress = phase.status === 'in_progress' || (phase.completionPercentage && phase.completionPercentage > 0 && phase.completionPercentage < 100);
                  const isUpdating = isUpdatingProgress[phase.id];
                  const isEditingNote = editingMilestoneId === phase.id;

                  return (
                    <div
                      key={phase.id}
                      className={`bg-white rounded-3xl p-6 sm:p-8 border transition-all space-y-5 ${
                        isDone
                          ? 'border-emerald-200 bg-emerald-50/10 shadow-xs'
                          : isInProgress
                          ? 'border-blue-500 ring-2 ring-blue-500/20 shadow-md'
                          : 'border-slate-200/90 shadow-xs'
                      }`}
                    >
                      {/* Header Row */}
                      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                        <div className="space-y-1">
                          <div className="flex flex-wrap items-center gap-2">
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
                              {isDone ? 'Completed ✓' : isInProgress ? 'In Progress' : 'Not Started'}
                            </span>
                            
                            {/* Traceability Badges */}
                            {phase.targetSkillId && (
                              <span className="text-[10px] font-bold text-blue-700 bg-blue-50 px-2 py-0.5 rounded-md border border-blue-200">
                                Traceability: {phase.targetSkillId} (Lvl {phase.currentLevel ?? 0} → {phase.requiredLevel ?? 1})
                              </span>
                            )}
                            {phase.gapSeverity && (
                              <span className={`text-[9px] font-extrabold px-1.5 py-0.5 rounded-md uppercase ${
                                phase.gapSeverity === 'CRITICAL' ? 'bg-red-100 text-red-800' : 'bg-amber-100 text-amber-800'
                              }`}>
                                {phase.gapSeverity} Gap
                              </span>
                            )}
                          </div>

                          <h3 className="text-lg font-bold text-slate-950">{phase.phaseTitle}</h3>
                          <p className="text-xs font-semibold text-blue-600">Focus Area: {phase.focusArea}</p>
                        </div>

                        <div className="w-8 h-8 rounded-full bg-slate-900 text-white font-bold text-xs flex items-center justify-center shrink-0">
                          0{phase.phaseOrder || idx + 1}
                        </div>
                      </div>

                      {/* Goals List */}
                      <div className="space-y-2 pt-2">
                        <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider">Milestone Objectives:</h4>
                        <ul className="space-y-2 text-xs text-slate-800">
                          {phase.goals.map((goal, gIdx) => (
                            <li key={gIdx} className="flex items-start gap-2.5 p-2 bg-slate-50 rounded-xl border border-slate-200/60">
                              <CheckCircle2 className={`w-4 h-4 shrink-0 mt-0.5 ${isDone ? 'text-emerald-600' : 'text-slate-400'}`} />
                              <span>{goal}</span>
                            </li>
                          ))}
                        </ul>
                      </div>

                      {/* Interactive Progress Tracking Toolbar */}
                      <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 space-y-3 text-xs">
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                          <span className="font-bold text-slate-800">Stage Status & Completion:</span>
                          <div className="flex items-center gap-2">
                            <button
                              type="button"
                              disabled={isUpdating}
                              onClick={() => handleUpdateMilestone(phase.id, 'not_started', 0)}
                              className={`px-3 py-1.5 rounded-xl font-bold transition-all ${
                                phase.status === 'not_started' ? 'bg-slate-800 text-white' : 'bg-white text-slate-700 border border-slate-200'
                              }`}
                            >
                              Not Started
                            </button>
                            <button
                              type="button"
                              disabled={isUpdating}
                              onClick={() => handleUpdateMilestone(phase.id, 'in_progress', 50)}
                              className={`px-3 py-1.5 rounded-xl font-bold transition-all ${
                                phase.status === 'in_progress' ? 'bg-blue-600 text-white' : 'bg-white text-slate-700 border border-slate-200'
                              }`}
                            >
                              In Progress
                            </button>
                            <button
                              type="button"
                              disabled={isUpdating}
                              onClick={() => handleUpdateMilestone(phase.id, 'completed', 100)}
                              className={`px-3 py-1.5 rounded-xl font-bold transition-all ${
                                phase.status === 'completed' ? 'bg-emerald-600 text-white' : 'bg-white text-slate-700 border border-slate-200'
                              }`}
                            >
                              Completed ✓
                            </button>
                          </div>
                        </div>

                        {/* Completion Percentage Slider */}
                        <div className="space-y-1 pt-1">
                          <div className="flex items-center justify-between text-[11px]">
                            <span className="font-semibold text-slate-600">Completion %</span>
                            <span className="font-extrabold text-blue-700">{phase.completionPercentage || 0}%</span>
                          </div>
                          <input
                            type="range"
                            min="0"
                            max="100"
                            step="10"
                            value={phase.completionPercentage || 0}
                            onChange={e => handleUpdateMilestone(phase.id, parseInt(e.target.value) === 100 ? 'completed' : 'in_progress', parseInt(e.target.value))}
                            className="w-full accent-blue-600 cursor-pointer"
                          />
                        </div>

                        {/* Notes Section */}
                        <div className="pt-2 border-t border-slate-200">
                          {isEditingNote ? (
                            <div className="space-y-2">
                              <textarea
                                rows={2}
                                value={milestoneNotes[phase.id] || ''}
                                onChange={e => setMilestoneNotes({ ...milestoneNotes, [phase.id]: e.target.value })}
                                placeholder="Add personal notes or links for this milestone..."
                                className="w-full p-2 bg-white border border-slate-300 rounded-xl text-xs"
                              />
                              <div className="flex items-center gap-2">
                                <button
                                  type="button"
                                  onClick={() => handleUpdateMilestone(phase.id, phase.status, phase.completionPercentage || 0, milestoneNotes[phase.id])}
                                  className="px-3 py-1 bg-blue-600 text-white text-[11px] font-bold rounded-lg"
                                >
                                  Save Note
                                </button>
                                <button
                                  type="button"
                                  onClick={() => setEditingMilestoneId(null)}
                                  className="px-3 py-1 bg-slate-200 text-slate-700 text-[11px] font-semibold rounded-lg"
                                >
                                  Cancel
                                </button>
                              </div>
                            </div>
                          ) : (
                            <div className="flex items-center justify-between">
                              <p className="text-[11px] text-slate-600 italic">
                                {phase.notes ? `Note: "${phase.notes}"` : 'No personal notes recorded.'}
                              </p>
                              <button
                                type="button"
                                onClick={() => setEditingMilestoneId(phase.id)}
                                className="text-[11px] text-blue-600 font-bold hover:underline flex items-center gap-1"
                              >
                                <Edit3 className="w-3 h-3" /> {phase.notes ? 'Edit Note' : 'Add Note'}
                              </button>
                            </div>
                          )}
                        </div>
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
                Personalized 3, 6, and 12 month execution roadmaps, milestone tracking persistence, and notes are reserved for signed-in members.
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
          </div>
        </div>
      )}
    </div>
  );
};
