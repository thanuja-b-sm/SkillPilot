import React from 'react';
import { useApp } from '../context/AppContext';
import { Compass, ShieldCheck, Sparkles, CheckCircle2 } from 'lucide-react';

export const Footer: React.FC = () => {
  const { navigateTo, userRole, setUserRole } = useApp();

  return (
    <footer className="bg-slate-900 text-slate-400 border-t border-slate-800 pt-12 pb-8 mt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 pb-10 border-b border-slate-800">
          
          {/* Brand Col */}
          <div className="md:col-span-1 space-y-4">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-blue-600 text-white flex items-center justify-center font-bold">
                <Compass className="w-5 h-5" />
              </div>
              <span className="text-xl font-bold text-white tracking-tight">Skill<span className="text-blue-400">Pilot</span></span>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed">
              Academic-grade career intelligence and milestone roadmap planning system. Helping students and professionals bridge skill gaps with clarity and confidence.
            </p>
            <div className="pt-2 flex items-center gap-2 text-xs text-emerald-400 bg-emerald-950/40 border border-emerald-800/50 px-3 py-1.5 rounded-lg w-fit">
              <CheckCircle2 className="w-3.5 h-3.5" /> Deterministic Scoring Engine
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-xs font-bold text-white uppercase tracking-wider mb-3">Platform Navigation</h4>
            <ul className="space-y-2 text-xs">
              <li>
                <button onClick={() => navigateTo('landing')} className="hover:text-white transition-colors">
                  Overview & Value Proposition
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo(userRole === 'student' ? 'questionnaire' : 'login')} className="hover:text-white transition-colors">
                  Career Discovery Questionnaire
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo(userRole === 'student' ? 'results' : 'login')} className="hover:text-white transition-colors">
                  Ranked Career Matches
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo(userRole === 'student' ? 'skill-gap' : 'login')} className="hover:text-white transition-colors">
                  Skill Gap Matrix & Severity
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo(userRole === 'student' ? 'roadmap' : 'login')} className="hover:text-white transition-colors">
                  Milestone Roadmap Generator
                </button>
              </li>
            </ul>
          </div>

          {/* Access Modes */}
          <div>
            <h4 className="text-xs font-bold text-white uppercase tracking-wider mb-3">Target Workspaces</h4>
            <ul className="space-y-2 text-xs">
              <li>
                <button onClick={() => navigateTo('landing')} className="hover:text-white transition-colors flex items-center gap-1.5">
                  Guest Public Workspace
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo(userRole === 'student' ? 'profile' : 'login')} className="hover:text-white transition-colors flex items-center gap-1.5">
                  Student Profile Workspace
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo(userRole === 'admin' ? 'admin' : 'login')} className="hover:text-white transition-colors flex items-center gap-1.5">
                  <ShieldCheck className="w-3.5 h-3.5 text-amber-400" /> Admin Dataset Console
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo(userRole === 'student' ? 'profile' : 'login')} className="hover:text-white transition-colors">
                  Skill Self-Assessment Matrix
                </button>
              </li>
            </ul>
          </div>

          {/* Algorithmic Integrity Note */}
          <div className="bg-slate-800/60 p-4 rounded-xl border border-slate-700/60 space-y-2">
            <div className="flex items-center gap-1.5 text-xs font-bold text-blue-300">
              <Sparkles className="w-3.5 h-3.5 text-blue-400" /> System Integrity Guarantee
            </div>
            <p className="text-[11px] text-slate-400 leading-relaxed">
              Skill matches, gap calculations, and milestone sequencing are executed via verifiable mathematical weight formulas. AI is used solely to refine narrative explanations for maximum readability.
            </p>
          </div>

        </div>

        <div className="pt-6 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <p>© {new Date().getFullYear()} SkillPilot Academic Career Intelligence. All rights reserved.</p>
          <div className="flex items-center gap-4">
            <span className="hover:text-slate-300 cursor-pointer">Privacy & Data Governance</span>
            <span className="hover:text-slate-300 cursor-pointer">Verification Matrix</span>
            <span className="hover:text-slate-300 cursor-pointer">API Docs</span>
          </div>
        </div>
      </div>
    </footer>
  );
};
