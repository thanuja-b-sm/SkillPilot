import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { 
  BarChart2, 
  Target, 
  Search, 
  Filter, 
  CheckCircle2, 
  AlertTriangle, 
  ArrowRight, 
  ShieldCheck,
  Briefcase,
  TrendingUp,
  DollarSign,
  RefreshCw,
  Loader2,
  Lock,
  LogIn,
  UserPlus
} from 'lucide-react';

export const CareerResultsPage: React.FC = () => {
  const { userRole, careerMatches, selectTargetCareer, selectedTargetCareer, navigateTo, recalculateCareerMatches, isLoadingMatches } = useApp();

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [sortBy, setSortBy] = useState<'match' | 'salary' | 'growth'>('match');

  const isGuest = userRole === 'guest';

  const categories = [
    'All', 
    'Artificial Intelligence', 
    'Cloud & Infrastructure', 
    'Software Engineering', 
    'Data & Analytics', 
    'Cybersecurity', 
    'Product & Management',
    'Healthcare & Medicine',
    'Business & Finance',
    'Engineering & Energy',
    'Marketing & Media',
    'Design & Creative'
  ];

  const filteredMatches = careerMatches
    .filter(match => {
      const matchesSearch = match.career.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                            match.career.description.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesCat = selectedCategory === 'All' || match.career.category === selectedCategory;
      return matchesSearch && matchesCat;
    })
    .sort((a, b) => {
      if (sortBy === 'match') return b.matchScore - a.matchScore;
      if (sortBy === 'salary') {
        // Parse leading number from salary string e.g. "$145,000 - $190,000 / yr"
        const parseSalary = (s: string) => parseInt(s.replace(/[^0-9]/g, '').slice(0, 6)) || 0;
        return parseSalary(b.career.averageSalary) - parseSalary(a.career.averageSalary);
      }
      if (sortBy === 'growth') {
        // Parse leading integer from growth string e.g. "+32%"
        const parseGrowth = (s: string) => parseInt(s.replace(/[^0-9]/g, '')) || 0;
        return parseGrowth(b.career.growthRate) - parseGrowth(a.career.growthRate);
      }
      return 0;
    });

  const handleSelectAndProceed = (careerId: string) => {
    selectTargetCareer(careerId);
    navigateTo('target-selection');
  };

  return (
    <div className="relative max-w-6xl mx-auto pb-16">
      <div className="space-y-8 text-left">
        {/* Header Banner */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-blue-600 bg-blue-50 px-2.5 py-0.5 rounded-md border border-blue-100">
                  Calculated Report
                </span>
                <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200 flex items-center gap-1">
                  <ShieldCheck className="w-3.5 h-3.5" /> Verifiable Scoring Engine
                </span>
              </div>
              <h1 className="text-2xl font-bold text-slate-950 mt-1">Ranked Career Matches</h1>
              <p className="text-xs text-slate-500 font-medium">
                Calculated based on your skill self-assessment ratings and questionnaire interest weighting.
              </p>
            </div>

            <div className="flex items-center gap-3 shrink-0">
              <button
                onClick={recalculateCareerMatches}
                disabled={isLoadingMatches}
                className="px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-xl flex items-center gap-1.5 transition-colors border border-slate-200 disabled:opacity-50"
                title="Recalculate career matches from backend"
              >
                {isLoadingMatches
                  ? <Loader2 className="w-3.5 h-3.5 animate-spin" />
                  : <RefreshCw className="w-3.5 h-3.5" />
                }
                {isLoadingMatches ? 'Recalculating…' : 'Recalculate'}
              </button>
              <div className="bg-slate-900 text-white p-3 rounded-2xl text-xs space-y-1 min-w-[200px]">
                <span className="text-slate-400 font-medium text-[10px] uppercase tracking-wider block">Selected Target</span>
                <p className="font-bold text-blue-400 truncate">{selectedTargetCareer?.title || 'None Selected'}</p>
                <button
                  onClick={() => navigateTo('skill-gap')}
                  className="text-[10px] font-semibold text-sky-300 hover:text-sky-200 underline block pt-0.5"
                >
                  Go to Gap Analysis →
                </button>
              </div>
            </div>
          </div>

          {/* Filter and Search Controls */}
          <div className="grid grid-cols-1 sm:grid-cols-12 gap-3 pt-2">
            {/* Search */}
            <div className="sm:col-span-5 relative">
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                placeholder="Search careers or keywords..."
                className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Category Dropdown */}
            <div className="sm:col-span-4 relative">
              <Filter className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <select
                value={selectedCategory}
                onChange={e => setSelectedCategory(e.target.value)}
                className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer"
              >
                {categories.map(cat => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>

            {/* Sort */}
            <div className="sm:col-span-3">
              <select
                value={sortBy}
                onChange={e => setSortBy(e.target.value as any)}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer"
              >
                <option value="match">Sort by Match Score</option>
                <option value="salary">Sort by Salary Range</option>
                <option value="growth">Sort by Growth Rate</option>
              </select>
            </div>
          </div>

          {/* Quick Filter Chips */}
          <div className="flex flex-wrap items-center gap-2 pt-2 border-t border-slate-100">
            <span className="text-[11px] font-bold text-slate-500 mr-1">Quick Filters:</span>
            {[
              { label: '🔥 High Growth (+20%+)', action: () => { setSelectedCategory('All'); setSortBy('growth'); } },
              { label: '💰 Top Compensation', action: () => { setSelectedCategory('All'); setSortBy('salary'); } },
              { label: '🤖 AI & Engineering', action: () => { setSelectedCategory('Artificial Intelligence'); } },
              { label: '☁️ Cloud & Infra', action: () => { setSelectedCategory('Cloud & Infrastructure'); } }
            ].map((chip, cIdx) => (
              <button
                key={cIdx}
                onClick={chip.action}
                className="px-2.5 py-1 bg-slate-100 hover:bg-blue-50 hover:text-blue-700 hover:border-blue-200 border border-slate-200 rounded-lg text-[11px] font-semibold text-slate-700 transition-all cursor-pointer"
              >
                {chip.label}
              </button>
            ))}
          </div>
        </div>

        {/* Guest Teaser Banner */}
        {isGuest && (
          <div className="p-4 rounded-2xl bg-gradient-to-r from-blue-600 via-indigo-600 to-slate-900 text-white shadow-md flex flex-col sm:flex-row items-center justify-between gap-3">
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-xl bg-white/10 shrink-0">
                <Lock className="w-5 h-5 text-sky-300" />
              </div>
              <div className="text-left">
                <h4 className="text-xs font-extrabold uppercase tracking-wider text-sky-300">Guest Teaser Mode</h4>
                <p className="text-xs text-slate-200">
                  Rank #1 is unlocked as a sample preview. Sign in to view all matches & execute gap analysis.
                </p>
              </div>
            </div>
            <div className="flex items-center gap-2 shrink-0">
              <button
                onClick={() => navigateTo('login')}
                className="px-4 py-2 bg-white text-blue-700 hover:bg-slate-100 rounded-xl font-bold text-xs shadow-xs transition-colors"
              >
                Sign In Now
              </button>
            </div>
          </div>
        )}

        {/* Career Matches List */}
        <div className="space-y-6">
          {filteredMatches.map((match, index) => {
            const isSelected = selectedTargetCareer?.id === match.career.id;
            const isLockedForGuest = isGuest && index > 0;

            return (
              <div key={match.career.id} className="relative">
                <div
                  className={`bg-white rounded-3xl p-6 sm:p-8 border transition-all space-y-6 ${
                    isSelected
                      ? 'border-blue-500 ring-2 ring-blue-500/20 shadow-lg'
                      : 'border-slate-200/90 shadow-md hover:border-slate-300'
                  } ${isLockedForGuest ? 'filter blur-md select-none pointer-events-none opacity-40' : ''}`}
                >
                  {/* Card Top Row */}
                  <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
                    
                    <div className="space-y-2">
                      <div className="flex flex-wrap items-center gap-2">
                        <span className="text-xs font-bold text-slate-950 bg-slate-100 px-2.5 py-0.5 rounded-lg border border-slate-200">
                          Rank #{index + 1}
                        </span>
                        <span className="text-xs font-semibold text-slate-600 bg-slate-50 px-2.5 py-0.5 rounded-lg border border-slate-200">
                          {match.career.category}
                        </span>
                        <span className={`text-xs font-bold px-2.5 py-0.5 rounded-lg border ${
                          match.confidenceLevel === 'High'
                            ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                            : 'bg-amber-50 text-amber-800 border-amber-200'
                        }`}>
                          {match.confidenceLevel} Fit Confidence
                        </span>
                        {index === 0 && isGuest && (
                          <span className="text-xs font-bold bg-blue-100 text-blue-800 px-2.5 py-0.5 rounded-lg border border-blue-200">
                            Unlocked Sample Match
                          </span>
                        )}
                      </div>

                      <h2 className="text-xl font-extrabold text-slate-950">{match.career.title}</h2>
                      <p className="text-xs text-slate-600 leading-relaxed max-w-3xl">{match.career.description}</p>
                    </div>

                    {/* Score Badges */}
                    <div className="flex items-center gap-3 shrink-0">
                      <div className="flex sm:flex-col items-center justify-between sm:justify-center p-3 bg-gradient-to-br from-slate-900 to-slate-800 text-white rounded-2xl min-w-[120px] text-center shadow-xs" title="Overall career compatibility (Skills + Questionnaire)">
                        <span className="text-[10px] font-medium text-slate-300 uppercase tracking-wider">Match Score</span>
                        <span className="text-2xl font-black text-blue-400">{match.matchScore}%</span>
                        <span className="text-[9px] text-slate-300 font-semibold mt-0.5">Overall Compatibility</span>
                      </div>
                      {match.readinessScore !== undefined && match.readinessScore !== null && (
                        <div className="flex sm:flex-col items-center justify-between sm:justify-center p-3 bg-slate-100 text-slate-900 rounded-2xl min-w-[120px] text-center border border-slate-200 shadow-xs" title="Current skill readiness against career requirements">
                          <span className="text-[10px] font-medium text-slate-600 uppercase tracking-wider">Readiness</span>
                          <span className="text-2xl font-black text-emerald-600">{match.readinessScore}%</span>
                          <span className="text-[9px] text-slate-500 font-semibold mt-0.5">Skill Fulfillment</span>
                        </div>
                      )}
                    </div>

                  </div>

                  {/* Strengths vs Gaps */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2">
                    
                    {/* Strengths */}
                    <div className="p-4 rounded-2xl bg-emerald-50/50 border border-emerald-200/80 space-y-2">
                      <h4 className="text-xs font-bold text-emerald-950 flex items-center gap-1.5">
                        <CheckCircle2 className="w-4 h-4 text-emerald-600" /> Key Strengths Matched ({match.keyStrengths.length})
                      </h4>
                      <ul className="space-y-1 text-xs text-emerald-900">
                        {match.keyStrengths.length > 0 ? (
                          match.keyStrengths.map((str, i) => (
                            <li key={i} className="flex items-center gap-1.5">
                              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" /> {str}
                            </li>
                          ))
                        ) : (
                          <li className="text-slate-500 italic">No direct strength overlaps identified yet.</li>
                        )}
                      </ul>
                    </div>

                    {/* Gaps */}
                    <div className="p-4 rounded-2xl bg-amber-50/50 border border-amber-200/80 space-y-2">
                      <h4 className="text-xs font-bold text-amber-950 flex items-center gap-1.5">
                        <AlertTriangle className="w-4 h-4 text-amber-600" /> Key Gaps to Bridge ({match.keyGaps.length})
                      </h4>
                      <ul className="space-y-1 text-xs text-amber-900">
                        {match.keyGaps.length > 0 ? (
                          match.keyGaps.map((gap, i) => (
                            <li key={i} className="flex items-center gap-1.5">
                              <span className="w-1.5 h-1.5 rounded-full bg-amber-500" /> {gap}
                            </li>
                          ))
                        ) : (
                          <li className="text-emerald-700 font-medium">All core skill prerequisites satisfied!</li>
                        )}
                      </ul>
                    </div>

                  </div>

                  {/* Career Metadata & Actions */}
                  <div className="pt-4 border-t border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div className="flex flex-wrap items-center gap-4 text-xs text-slate-600">
                      <span className="flex items-center gap-1 font-semibold text-slate-800">
                        <DollarSign className="w-3.5 h-3.5 text-slate-400" /> {match.career.averageSalary}
                      </span>
                      <span className="flex items-center gap-1 font-semibold text-emerald-700">
                        <TrendingUp className="w-3.5 h-3.5" /> {match.career.growthRate}
                      </span>
                    </div>

                    <div className="flex items-center gap-3">
                      <button
                        onClick={() => handleSelectAndProceed(match.career.id)}
                        className={`px-5 py-2.5 rounded-xl font-bold text-xs transition-all flex items-center gap-2 ${
                          isSelected
                            ? 'bg-emerald-600 hover:bg-emerald-700 text-white shadow-xs'
                            : 'bg-blue-600 hover:bg-blue-700 text-white shadow-xs'
                        }`}
                      >
                        <Target className="w-4 h-4" />
                        <span>{isSelected ? 'Target Career Selected ✓' : 'Select as Target Career'}</span>
                        <ArrowRight className="w-4 h-4" />
                      </button>
                    </div>
                  </div>

                </div>

                {/* Inline Lock Card on Rank #2 */}
                {isGuest && index === 1 && (
                  <div className="absolute inset-0 z-20 flex items-center justify-center p-4">
                    <div className="max-w-md w-full bg-white/95 backdrop-blur-md rounded-3xl p-6 border border-slate-200/90 shadow-2xl text-center space-y-4">
                      <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-blue-600 to-indigo-600 text-white flex items-center justify-center mx-auto shadow-md">
                        <Lock className="w-6 h-6" />
                      </div>
                      <div>
                        <h3 className="text-lg font-extrabold text-slate-950">Unlock All Career Matches</h3>
                        <p className="text-xs text-slate-600 mt-1">
                          Sign in or register for a free account to unlock full match index rankings, gap matrices, and 6-12 month roadmaps.
                        </p>
                      </div>
                      <div className="flex items-center justify-center gap-3 pt-1">
                        <button
                          onClick={() => navigateTo('login')}
                          className="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-1.5"
                        >
                          <LogIn className="w-3.5 h-3.5" /> Sign In
                        </button>
                        <button
                          onClick={() => navigateTo('register')}
                          className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-800 font-bold text-xs rounded-xl transition-colors border border-slate-200 flex items-center gap-1.5"
                        >
                          <UserPlus className="w-3.5 h-3.5 text-blue-600" /> Create Account
                        </button>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
