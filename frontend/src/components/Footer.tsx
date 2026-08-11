import React from 'react';
import { useApp } from '../context/AppContext';
import { Compass, ShieldCheck, Sparkles, Github, Linkedin, Twitter, Globe, ArrowUpRight } from 'lucide-react';

export const Footer: React.FC = () => {
  const { navigateTo, userRole } = useApp();

  return (
    <footer className="bg-slate-950 text-slate-400 border-t border-slate-800/80 pt-16 pb-12 mt-24">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Main Footer Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10 pb-12 border-b border-slate-800/80">
          
          {/* Brand Column (2 cols wide on large screens) */}
          <div className="lg:col-span-2 space-y-4 pr-0 lg:pr-8">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-blue-600 text-white flex items-center justify-center font-bold shadow-md shadow-blue-500/20">
                <Compass className="w-5 h-5" />
              </div>
              <span className="text-xl font-extrabold text-white tracking-tight">
                Skill<span className="text-blue-500">Pilot</span>
              </span>
            </div>

            <p className="text-xs text-slate-400 leading-relaxed max-w-sm">
              Empowering students and professionals to discover their ideal career path, analyze granular skill gaps, and follow a structured milestone roadmap to success.
            </p>

            {/* Social / Link Icons */}
            <div className="pt-2 flex items-center gap-3">
              <a
                href="https://github.com/thanuja-b-sm/SkillPilot"
                target="_blank"
                rel="noreferrer"
                className="w-8 h-8 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-white hover:border-slate-700 flex items-center justify-center transition-colors"
                title="GitHub Repository"
              >
                <Github className="w-4 h-4" />
              </a>
              <a
                href="#"
                onClick={(e) => e.preventDefault()}
                className="w-8 h-8 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-white hover:border-slate-700 flex items-center justify-center transition-colors"
                title="LinkedIn"
              >
                <Linkedin className="w-4 h-4" />
              </a>
              <a
                href="#"
                onClick={(e) => e.preventDefault()}
                className="w-8 h-8 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-white hover:border-slate-700 flex items-center justify-center transition-colors"
                title="Twitter"
              >
                <Twitter className="w-4 h-4" />
              </a>
              <a
                href="#"
                onClick={(e) => e.preventDefault()}
                className="w-8 h-8 rounded-lg bg-slate-900 border border-slate-800 text-slate-400 hover:text-white hover:border-slate-700 flex items-center justify-center transition-colors"
                title="Global"
              >
                <Globe className="w-4 h-4" />
              </a>
            </div>
          </div>

          {/* Platform Quick Links */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold text-slate-200 uppercase tracking-wider">Platform</h4>
            <ul className="space-y-2.5 text-xs">
              <li>
                <button onClick={() => navigateTo('landing')} className="hover:text-blue-400 transition-colors">
                  Overview
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo('questionnaire')} className="hover:text-blue-400 transition-colors">
                  Career Assessment
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo('results')} className="hover:text-blue-400 transition-colors">
                  Career Matches
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo('skill-gap')} className="hover:text-blue-400 transition-colors">
                  Skill Gap Analysis
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo('roadmap')} className="hover:text-blue-400 transition-colors">
                  Phased Roadmap
                </button>
              </li>
            </ul>
          </div>

          {/* Portals & Roles */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold text-slate-200 uppercase tracking-wider">Portals</h4>
            <ul className="space-y-2.5 text-xs">
              <li>
                <button onClick={() => navigateTo(userRole === 'student' ? 'profile' : 'login')} className="hover:text-blue-400 transition-colors">
                  Student Profile
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo('login')} className="hover:text-blue-400 transition-colors">
                  User Sign In
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo(userRole === 'admin' ? 'admin' : 'login')} className="hover:text-blue-400 transition-colors flex items-center gap-1.5">
                  <ShieldCheck className="w-3.5 h-3.5 text-amber-400 shrink-0" />
                  <span>Admin Portal</span>
                </button>
              </li>
              <li>
                <button onClick={() => navigateTo('target-selection')} className="hover:text-blue-400 transition-colors">
                  Target Career Selection
                </button>
              </li>
            </ul>
          </div>

          {/* Technology & Resources */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold text-slate-200 uppercase tracking-wider">Technology</h4>
            <ul className="space-y-2.5 text-xs">
              <li className="flex items-center gap-1 text-slate-400">
                <span>Spring Boot 3.2 Backend</span>
              </li>
              <li className="flex items-center gap-1 text-slate-400">
                <span>React 19 & Tailwind CSS</span>
              </li>
              <li className="flex items-center gap-1 text-slate-400">
                <Sparkles className="w-3 h-3 text-sky-400 shrink-0" />
                <span>Gemini AI Insights</span>
              </li>
              <li className="flex items-center gap-1 text-slate-400">
                <span>MySQL & Flyway Migration</span>
              </li>
            </ul>
          </div>

        </div>

        {/* Bottom Strip */}
        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <p>© {new Date().getFullYear()} SkillPilot Platform. All rights reserved.</p>

          {/* System Status Pill */}
          <div className="flex items-center gap-6">
            <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-slate-900 border border-slate-800 text-[11px] text-slate-300">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              <span>System Online v2.4</span>
            </div>
            <div className="flex items-center gap-4 text-xs">
              <span className="hover:text-slate-300 cursor-pointer">Privacy Policy</span>
              <span className="hover:text-slate-300 cursor-pointer">Terms of Service</span>
            </div>
          </div>
        </div>

      </div>
    </footer>
  );
};

