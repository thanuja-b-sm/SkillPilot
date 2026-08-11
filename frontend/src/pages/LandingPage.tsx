import React from 'react';
import { useApp } from '../context/AppContext';
import { 
  Compass, 
  ArrowRight, 
  CheckCircle2, 
  BarChart2, 
  Target, 
  GitCommit, 
  Map, 
  Sparkles, 
  ShieldCheck, 
  Award,
  Layers,
  ChevronRight
} from 'lucide-react';

export const LandingPage: React.FC = () => {
  const { userRole, navigateTo, setUserRole, careers, careerMatches } = useApp();

  return (
    <div className="space-y-20 pb-16">
      {/* Hero Section */}
      <section className="relative pt-12 pb-16 md:pt-20 md:pb-24 bg-gradient-to-b from-sky-50/60 via-slate-50 to-slate-50 border-b border-slate-200/60 overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            
            {/* Hero Left Text */}
            <div className="lg:col-span-7 space-y-6 text-left">
              <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-blue-100/80 border border-blue-200 text-blue-800 text-xs font-semibold shadow-2xs">
                <Sparkles className="w-4 h-4 text-blue-600" />
                <span>Deterministic Career Intelligence & Milestone Engine</span>
              </div>

              <h1 className="text-3xl sm:text-4xl md:text-5xl font-extrabold text-slate-950 tracking-tight leading-[1.15]">
                Discover Your Ideal Career with <span className="text-blue-600 underline decoration-blue-300 decoration-wavy decoration-2">Clear Direction</span>
              </h1>

              <p className="text-base sm:text-lg text-slate-600 leading-relaxed max-w-2xl font-normal">
                SkillPilot bridges the gap between where you are and where you want to be. Discover ranked career matches, analyze granular skill gaps, and execute a milestone roadmap backed by verifiable skill matrices.
              </p>

              {/* Action CTAs */}
              <div className="pt-2 flex flex-wrap items-center gap-4">
                <button
                  onClick={() => {
                    navigateTo('questionnaire');
                  }}
                  className="px-6 py-3.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-semibold text-sm shadow-md hover:shadow-lg transition-all flex items-center gap-2.5 group"
                >
                  <span>Start Career Discovery</span>
                  <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
                </button>

                <button
                  onClick={() => {
                    navigateTo('results');
                  }}
                  className="px-6 py-3.5 rounded-xl bg-white hover:bg-slate-100 text-slate-800 font-semibold text-sm border border-slate-200 shadow-xs transition-all flex items-center gap-2"
                >
                  <BarChart2 className="w-4 h-4 text-blue-600" />
                  <span>Explore Ranked Matches</span>
                </button>
              </div>

              {/* Trust Badges */}
              <div className="pt-6 grid grid-cols-3 gap-4 border-t border-slate-200/80 text-xs text-slate-600 font-medium">
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
                  <span>Verifiable Matrices</span>
                </div>
                <div className="flex items-center gap-2">
                  <ShieldCheck className="w-4 h-4 text-blue-600 shrink-0" />
                  <span>Transparent Scoring</span>
                </div>
                <div className="flex items-center gap-2">
                  <Award className="w-4 h-4 text-amber-600 shrink-0" />
                  <span>6-12 Mo. Phased Plan</span>
                </div>
              </div>
            </div>

            {/* Hero Right Visual Image */}
            <div className="lg:col-span-5 flex justify-center items-center">
              <div className="relative w-full rounded-2xl overflow-hidden border border-slate-200/90 shadow-2xl bg-white p-2 group hover:shadow-blue-500/10 transition-all duration-300">
                {/* Decorative background glow */}
                <div className="absolute -top-24 -right-24 w-48 h-48 bg-blue-500/10 rounded-full blur-2xl pointer-events-none" />

                <img
                  src="/hero-illustration.png"
                  alt="SkillPilot Career Intelligence Dashboard Preview"
                  className="w-full h-auto object-cover rounded-xl transform group-hover:scale-[1.01] transition-transform duration-500"
                />
              </div>
            </div>

          </div>
        </div>
      </section>

      {/* "How It Works" 4-Step Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-3xl mx-auto space-y-3 mb-12">
          <span className="text-xs font-bold text-blue-600 uppercase tracking-widest bg-blue-50 px-3 py-1 rounded-full border border-blue-100">
            Structured Workflow
          </span>
          <h2 className="text-2xl sm:text-3xl font-bold text-slate-950">
            Four Steps to Strategic Career Mastery
          </h2>
          <p className="text-sm text-slate-600">
            SkillPilot guides you through a logical, data-driven journey from self-assessment to actionable execution.
          </p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200/90 shadow-xs relative hover:shadow-md transition-shadow">
            <div className="w-10 h-10 rounded-xl bg-slate-900 text-white font-bold flex items-center justify-center mb-4">
              01
            </div>
            <h3 className="text-base font-bold text-slate-900 mb-2">Build Profile</h3>
            <p className="text-xs text-slate-600 leading-relaxed">
              Log your academic baseline, domain interests, and complete your interactive skill self-assessment matrix.
            </p>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/90 shadow-xs relative hover:shadow-md transition-shadow">
            <div className="w-10 h-10 rounded-xl bg-blue-600 text-white font-bold flex items-center justify-center mb-4">
              02
            </div>
            <h3 className="text-base font-bold text-slate-900 mb-2">Discovery Questionnaire</h3>
            <p className="text-xs text-slate-600 leading-relaxed">
              Answer structured scenario questions to calibrate work preferences, technical exposure, and learning commitment.
            </p>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/90 shadow-xs relative hover:shadow-md transition-shadow">
            <div className="w-10 h-10 rounded-xl bg-slate-900 text-white font-bold flex items-center justify-center mb-4">
              03
            </div>
            <h3 className="text-base font-bold text-slate-900 mb-2">Skill Gap Analysis</h3>
            <p className="text-xs text-slate-600 leading-relaxed">
              Select your target career to view a precise, severity-coded breakdown of required vs current competencies.
            </p>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/90 shadow-xs relative hover:shadow-md transition-shadow">
            <div className="w-10 h-10 rounded-xl bg-emerald-600 text-white font-bold flex items-center justify-center mb-4">
              04
            </div>
            <h3 className="text-base font-bold text-slate-900 mb-2">Phased Roadmap</h3>
            <p className="text-xs text-slate-600 leading-relaxed">
              Execute a 6-12 month timeline with phased goals, outcomes, and optional AI-polished summary reports.
            </p>
          </div>
        </div>
      </section>

      {/* Feature Showcase Grid — User Value Driven */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-slate-900 rounded-3xl p-8 sm:p-12 text-white shadow-xl space-y-10">
          <div className="max-w-2xl space-y-3">
            <h2 className="text-2xl sm:text-3xl font-bold">
              Everything You Need to Reach Your Career Goals
            </h2>
            <p className="text-xs sm:text-sm text-slate-300">
              Clear insights, personalized guidance, and a step-by-step path tailored to your skills.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-slate-800/80 p-6 rounded-2xl border border-slate-700/80 space-y-3">
              <div className="w-10 h-10 rounded-xl bg-blue-500/20 text-blue-400 flex items-center justify-center font-bold">
                <Target className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white">Find Your Perfect Career Match</h3>
              <p className="text-xs text-slate-300 leading-relaxed">
                Discover which tech roles fit your strengths best, complete with expected salary ranges and market growth rates.
              </p>
            </div>

            <div className="bg-slate-800/80 p-6 rounded-2xl border border-slate-700/80 space-y-3">
              <div className="w-10 h-10 rounded-xl bg-sky-500/20 text-sky-400 flex items-center justify-center font-bold">
                <BarChart2 className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white">Know Exactly What to Learn</h3>
              <p className="text-xs text-slate-300 leading-relaxed">
                Get a clear breakdown of the exact skills you need for your target job so you never waste time studying the wrong things.
              </p>
            </div>

            <div className="bg-slate-800/80 p-6 rounded-2xl border border-slate-700/80 space-y-3">
              <div className="w-10 h-10 rounded-xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center font-bold">
                <Map className="w-5 h-5" />
              </div>
              <h3 className="text-base font-bold text-white">Follow a Phased Action Plan</h3>
              <p className="text-xs text-slate-300 leading-relaxed">
                Execute a realistic 6 to 12-month step-by-step roadmap with clear milestones and AI-enhanced progress summaries.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Bottom Banner */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div className="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-3xl p-8 sm:p-12 text-white shadow-lg space-y-6">
          <h2 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Ready to Map Your Career Path?
          </h2>
          <p className="text-sm text-blue-100 max-w-xl mx-auto">
            Experience SkillPilot now and explore instant career gap intelligence.
          </p>
          <div className="flex flex-wrap items-center justify-center gap-4 pt-2">
            <button
              onClick={() => {
                if (userRole === 'student') navigateTo('questionnaire');
                else navigateTo('login');
              }}
              className="px-6 py-3.5 rounded-xl bg-white hover:bg-slate-100 text-slate-900 font-bold text-sm shadow-md transition-colors"
            >
              Launch Discovery Wizard
            </button>
          </div>
        </div>
      </section>
    </div>
  );
};
