import React from 'react';
import { useApp } from '../context/AppContext';
import { Target, CheckCircle2, ArrowRight, ShieldCheck, Award, GitCommit } from 'lucide-react';

export const TargetCareerSelectionPage: React.FC = () => {
  const { careers, selectedTargetCareer, selectTargetCareer, navigateTo, careerMatches } = useApp();

  return (
    <div className="max-w-5xl mx-auto space-y-8 text-left pb-16">
      {/* Target Confirmation Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 rounded-3xl p-6 sm:p-8 text-white shadow-xl space-y-4">
        <div className="flex items-center gap-2">
          <span className="text-xs font-bold text-sky-400 uppercase tracking-widest bg-sky-950 px-2.5 py-0.5 rounded-md border border-sky-800">
            Target Focus Step
          </span>
          <span className="text-[11px] font-semibold text-emerald-400 bg-emerald-950/80 px-2 py-0.5 rounded-md border border-emerald-800 flex items-center gap-1">
            <ShieldCheck className="w-3.5 h-3.5" /> Confirmed Selection
          </span>
        </div>

        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 pt-2">
          <div className="space-y-2">
            <span className="text-xs text-slate-400 font-medium">Active Target Career:</span>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-blue-400">
              {selectedTargetCareer ? selectedTargetCareer.title : 'Select a Target Career Below'}
            </h1>
            <p className="text-xs text-slate-300 max-w-2xl leading-relaxed">
              {selectedTargetCareer?.description}
            </p>
          </div>

          <button
            onClick={() => navigateTo('skill-gap')}
            className="px-6 py-3.5 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl shadow-md transition-colors flex items-center justify-center gap-2 shrink-0"
          >
            <GitCommit className="w-4 h-4" />
            <span>Generate Skill Gap Analysis</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Target Career Selection Grid */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-bold text-slate-950">
            Choose or Switch Your Primary Target Goal
          </h2>
          <span className="text-xs text-slate-500 font-medium">
            {careers.length} Available Career Tracks
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {careers.map(career => {
            const isSelected = selectedTargetCareer?.id === career.id;
            const matchInfo = careerMatches.find(m => m.career.id === career.id);

            return (
              <div
                key={career.id}
                onClick={() => selectTargetCareer(career.id)}
                className={`p-6 rounded-3xl border cursor-pointer transition-all space-y-4 ${
                  isSelected
                    ? 'bg-blue-50/90 border-blue-600 ring-2 ring-blue-500/30 shadow-md'
                    : 'bg-white border-slate-200/90 hover:border-slate-300 shadow-xs'
                }`}
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="space-y-1">
                    <span className="text-[10px] font-bold uppercase tracking-wider text-slate-500 bg-slate-100 px-2 py-0.5 rounded-md">
                      {career.category}
                    </span>
                    <h3 className="text-base font-bold text-slate-950">{career.title}</h3>
                  </div>

                  <div className={`w-6 h-6 rounded-full flex items-center justify-center shrink-0 border ${
                    isSelected ? 'bg-blue-600 border-blue-600 text-white' : 'border-slate-300 bg-white'
                  }`}>
                    {isSelected && <CheckCircle2 className="w-4 h-4" />}
                  </div>
                </div>

                <p className="text-xs text-slate-600 line-clamp-2">{career.description}</p>

                <div className="flex items-center justify-between pt-2 border-t border-slate-100/80 text-xs">
                  <span className="font-bold text-blue-700 bg-blue-100/60 px-2.5 py-1 rounded-lg">
                    {matchInfo?.matchScore || 80}% Calculated Match
                  </span>
                  <span className="text-slate-500 font-medium">{career.averageSalary}</span>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
