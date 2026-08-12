import React from 'react';
import { useApp } from '../context/AppContext';
import { PageId } from '../types';
import { 
  Compass, 
  User, 
  HelpCircle, 
  BarChart2, 
  Target, 
  GitCommit, 
  Map, 
  ShieldCheck, 
  Sparkles,
  LogOut,
  ChevronRight,
  Sliders,
  Lock
} from 'lucide-react';

export const Header: React.FC = () => {
  const { userRole, setUserRole, activePage, navigateTo, userProfile, isLoadingAuth } = useApp();

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200/80 shadow-xs">

      {/* Main Header Nav */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between gap-4">
        {/* Brand Logo */}
        <div 
          onClick={() => { if (!isLoadingAuth) navigateTo(userRole === 'admin' ? 'admin' : 'landing'); }}
          className="flex items-center gap-3 cursor-pointer group shrink-0"
        >
          <div className="w-10 h-10 rounded-xl bg-slate-900 text-blue-400 flex items-center justify-center shadow-md border border-slate-800 group-hover:bg-blue-600 group-hover:text-white transition-colors">
            <Compass className="w-5 h-5 transition-transform group-hover:rotate-45" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-lg font-bold text-slate-900 tracking-tight">Skill<span className="text-blue-600">Pilot</span></span>
              <span className="text-[10px] font-semibold bg-sky-100 text-sky-800 px-1.5 py-0.5 rounded-md uppercase">v2.4</span>
            </div>
            <p className="text-[11px] text-slate-500 font-medium -mt-0.5 hidden sm:block">Career Intelligence Platform</p>
          </div>
        </div>

        {/* Navigation Bar Links */}
        <nav className="hidden lg:flex items-center gap-1 text-sm font-medium">
          {isLoadingAuth ? (
            <div className="flex items-center gap-2 text-xs text-slate-400 animate-pulse">
              <span>Syncing session...</span>
            </div>
          ) : userRole === 'guest' ? (
            <>
              <button
                onClick={() => navigateTo('landing')}
                className={`px-3 py-2 rounded-lg transition-colors flex items-center gap-1.5 ${
                  activePage === 'landing' ? 'bg-blue-50 text-blue-700 font-semibold' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                Overview
              </button>
              <button
                onClick={() => navigateTo('questionnaire')}
                className={`px-3 py-2 rounded-lg transition-colors flex items-center gap-1.5 ${
                  activePage === 'questionnaire' ? 'bg-blue-50 text-blue-700 font-semibold' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <HelpCircle className="w-4 h-4" /> Career Discovery
              </button>
              <button
                onClick={() => navigateTo('results')}
                className={`px-3 py-2 rounded-lg transition-colors flex items-center gap-1.5 ${
                  activePage === 'results' ? 'bg-blue-50 text-blue-700 font-semibold' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <BarChart2 className="w-4 h-4" /> Career Matches
                <Lock className="w-3 h-3 text-amber-500 ml-0.5" />
              </button>
              <button
                onClick={() => navigateTo('skill-gap')}
                className={`px-3 py-2 rounded-lg transition-colors flex items-center gap-1.5 ${
                  activePage === 'skill-gap' ? 'bg-blue-50 text-blue-700 font-semibold' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <GitCommit className="w-4 h-4" /> Gap Analysis
                <Lock className="w-3 h-3 text-amber-500 ml-0.5" />
              </button>
              <button
                onClick={() => navigateTo('roadmap')}
                className={`px-3 py-2 rounded-lg transition-colors flex items-center gap-1.5 ${
                  activePage === 'roadmap' ? 'bg-blue-50 text-blue-700 font-semibold' : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <Map className="w-4 h-4" /> Roadmap
                <Lock className="w-3 h-3 text-amber-500 ml-0.5" />
              </button>
            </>
          ) : (userRole === 'student' || (userRole === 'admin' && activePage !== 'admin')) ? (
            <div className="flex items-center bg-slate-100/80 p-1 rounded-xl border border-slate-200 text-xs">
              <button
                onClick={() => navigateTo('profile')}
                className={`px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 ${
                  activePage === 'profile' ? 'bg-white text-blue-700 font-semibold shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <User className="w-3.5 h-3.5" /> Profile
              </button>

              <ChevronRight className="w-3 h-3 text-slate-400" />

              <button
                onClick={() => navigateTo('questionnaire')}
                className={`px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 ${
                  activePage === 'questionnaire' ? 'bg-white text-blue-700 font-semibold shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <HelpCircle className="w-3.5 h-3.5" /> Assessment
              </button>

              <ChevronRight className="w-3 h-3 text-slate-400" />

              <button
                onClick={() => navigateTo('results')}
                className={`px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 ${
                  activePage === 'results' ? 'bg-white text-blue-700 font-semibold shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <BarChart2 className="w-3.5 h-3.5" /> Matches
              </button>

              <ChevronRight className="w-3 h-3 text-slate-400" />

              <button
                onClick={() => navigateTo('target-selection')}
                className={`px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 ${
                  activePage === 'target-selection' ? 'bg-white text-blue-700 font-semibold shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <Target className="w-3.5 h-3.5" />  Target
              </button>

              <ChevronRight className="w-3 h-3 text-slate-400" />

              <button
                onClick={() => navigateTo('skill-gap')}
                className={`px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 ${
                  activePage === 'skill-gap' ? 'bg-white text-blue-700 font-semibold shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <GitCommit className="w-3.5 h-3.5" /> Gap Analysis
              </button>

              <ChevronRight className="w-3 h-3 text-slate-400" />

              <button
                onClick={() => navigateTo('roadmap')}
                className={`px-3 py-1.5 rounded-lg transition-all flex items-center gap-1.5 ${
                  activePage === 'roadmap' ? 'bg-white text-blue-700 font-semibold shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <Map className="w-3.5 h-3.5" /> Roadmap
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <span className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-amber-50 text-amber-800 border border-amber-200 font-medium text-xs">
                <ShieldCheck className="w-4 h-4 text-amber-600" /> Admin Dataset Console
              </span>
            </div>
          )}
        </nav>

        {/* Action Buttons */}
        <div className="flex items-center gap-3">
          {isLoadingAuth ? (
            <div className="px-3.5 py-1.5 bg-slate-100 rounded-xl text-xs text-slate-500 font-medium animate-pulse">
              Restoring Session...
            </div>
          ) : userRole === 'guest' ? (
            <div className="flex items-center gap-2">
              <button
                onClick={() => navigateTo('login')}
                className="px-3.5 py-2 text-sm font-medium text-slate-700 hover:text-slate-900 hover:bg-slate-100 rounded-xl transition-colors"
              >
                Sign In
              </button>
              <button
                onClick={() => navigateTo('register')}
                className="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-xs transition-colors"
              >
                Get Started
              </button>
            </div>
          ) : userRole === 'student' ? (
            <div className="flex items-center gap-2">
              <div 
                onClick={() => navigateTo('profile')}
                className="hidden sm:flex items-center gap-2.5 px-3 py-1.5 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-xl cursor-pointer transition-colors"
              >
                <div className="w-7 h-7 rounded-lg bg-blue-600 text-white font-bold text-xs flex items-center justify-center">
                  {userProfile.name.charAt(0)}
                </div>
                <div className="text-left leading-tight">
                  <p className="text-xs font-bold text-slate-800">{userProfile.name}</p>
                  <p className="text-[10px] text-slate-500 truncate max-w-[120px]">{userProfile.title}</p>
                </div>
              </div>

              <button
                onClick={() => setUserRole('guest')}
                title="Switch back to Guest Mode"
                className="p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-xl transition-colors"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              {activePage === 'admin' ? (
                <button
                  onClick={() => navigateTo('results')}
                  className="px-3.5 py-2 text-xs font-semibold text-white bg-slate-900 hover:bg-slate-800 rounded-xl shadow-xs transition-all"
                >
                  View Student Experience
                </button>
              ) : (
                <button
                  onClick={() => navigateTo('admin')}
                  className="px-3.5 py-2 text-xs font-bold text-amber-950 bg-amber-400 hover:bg-amber-300 rounded-xl shadow-xs transition-all flex items-center gap-1.5 border border-amber-500/30"
                >
                  <ShieldCheck className="w-4 h-4 text-amber-900" />
                  <span>Return to Admin Console</span>
                </button>
              )}
              <button
                onClick={() => setUserRole('guest')}
                title="Log Out of Admin Session"
                className="px-3 py-2 text-slate-600 hover:text-red-600 hover:bg-red-50 rounded-xl transition-colors flex items-center gap-1.5 border border-slate-200 font-semibold text-xs"
              >
                <LogOut className="w-4 h-4" />
                <span>Log Out</span>
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Mobile Step Strip for Student View */}
      {!isLoadingAuth && (userRole === 'student' || (userRole === 'admin' && activePage !== 'admin')) && (
        <div className="lg:hidden bg-slate-50 border-t border-slate-200 px-4 py-2 overflow-x-auto flex items-center gap-2 text-xs">
          <button
            onClick={() => navigateTo('profile')}
            className={`px-2.5 py-1 rounded-md shrink-0 ${activePage === 'profile' ? 'bg-blue-600 text-white font-medium' : 'text-slate-600 bg-white border border-slate-200'}`}
          >
            Profile
          </button>
          <ChevronRight className="w-3 h-3 text-slate-400 shrink-0" />
          <button
            onClick={() => navigateTo('questionnaire')}
            className={`px-2.5 py-1 rounded-md shrink-0 ${activePage === 'questionnaire' ? 'bg-blue-600 text-white font-medium' : 'text-slate-600 bg-white border border-slate-200'}`}
          >
            Assessment
          </button>
          <ChevronRight className="w-3 h-3 text-slate-400 shrink-0" />
          <button
            onClick={() => navigateTo('results')}
            className={`px-2.5 py-1 rounded-md shrink-0 ${activePage === 'results' ? 'bg-blue-600 text-white font-medium' : 'text-slate-600 bg-white border border-slate-200'}`}
          >
            Matches
          </button>
          <ChevronRight className="w-3 h-3 text-slate-400 shrink-0" />
          <button
            onClick={() => navigateTo('target-selection')}
            className={`px-2.5 py-1 rounded-md shrink-0 ${activePage === 'target-selection' ? 'bg-blue-600 text-white font-medium' : 'text-slate-600 bg-white border border-slate-200'}`}
          >
            Target
          </button>
          <ChevronRight className="w-3 h-3 text-slate-400 shrink-0" />
          <button
            onClick={() => navigateTo('skill-gap')}
            className={`px-2.5 py-1 rounded-md shrink-0 ${activePage === 'skill-gap' ? 'bg-blue-600 text-white font-medium' : 'text-slate-600 bg-white border border-slate-200'}`}
          >
            Gap Analysis
          </button>
          <ChevronRight className="w-3 h-3 text-slate-400 shrink-0" />
          <button
            onClick={() => navigateTo('roadmap')}
            className={`px-2.5 py-1 rounded-md shrink-0 ${activePage === 'roadmap' ? 'bg-blue-600 text-white font-medium' : 'text-slate-600 bg-white border border-slate-200'}`}
          >
            Roadmap
          </button>
        </div>
      )}
    </header>
  );
};
