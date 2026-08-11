import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { 
  HelpCircle, 
  ArrowLeft, 
  ArrowRight, 
  CheckCircle2, 
  RotateCcw, 
  BarChart2, 
  Bookmark
} from 'lucide-react';

export const QuestionnairePage: React.FC = () => {
  const { questionnaire, questionnaireAnswers, saveQuestionAnswer, resetQuestionnaire, navigateTo, showToast } = useApp();

  const [currentStep, setCurrentStep] = useState(0);

  const totalSteps = questionnaire.length;
  const currentQuestion = questionnaire[currentStep];

  const currentAnswer = questionnaireAnswers[currentQuestion?.id] || (currentQuestion?.type === 'multiple' ? [] : '');

  const progressPercentage = Math.round(((currentStep + 1) / totalSteps) * 100);

  const handleSingleSelect = (optionId: string) => {
    saveQuestionAnswer(currentQuestion.id, optionId);
  };

  const handleMultipleToggle = (optionId: string) => {
    const list = Array.isArray(currentAnswer) ? [...currentAnswer] : [];
    if (list.includes(optionId)) {
      saveQuestionAnswer(currentQuestion.id, list.filter(id => id !== optionId));
    } else {
      saveQuestionAnswer(currentQuestion.id, [...list, optionId]);
    }
  };

  const handleNext = () => {
    if (currentStep < totalSteps - 1) {
      setCurrentStep(prev => prev + 1);
    } else {
      showToast('Questionnaire completed! Recalculating career matches.', 'success');
      navigateTo('results');
    }
  };

  const handleBack = () => {
    if (currentStep > 0) {
      setCurrentStep(prev => prev - 1);
    }
  };

  if (!currentQuestion) return null;

  return (
    <div className="max-w-3xl mx-auto space-y-8 text-left pb-16">
      {/* Wizard Header Card */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-md space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-slate-100">
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold text-blue-600 bg-blue-50 px-2.5 py-0.5 rounded-md border border-blue-100">
                Discovery Wizard
              </span>
              <span className="text-xs text-slate-500 font-medium">
                Step {currentStep + 1} of {totalSteps}
              </span>
            </div>
            <h1 className="text-xl font-bold text-slate-950 mt-1">Career Intelligence Questionnaire</h1>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={resetQuestionnaire}
              className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-xl flex items-center gap-1 transition-colors"
            >
              <RotateCcw className="w-3.5 h-3.5" /> Reset
            </button>
            <span
              className="px-3 py-1.5 bg-emerald-50 text-emerald-800 border border-emerald-200 text-xs font-semibold rounded-xl flex items-center gap-1"
              title="Each answer is saved to the backend automatically on selection"
            >
              <Bookmark className="w-3.5 h-3.5 text-emerald-600" /> Auto-Saved
            </span>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="space-y-1">
          <div className="flex justify-between text-xs font-semibold text-slate-600">
            <span>{currentQuestion.section}</span>
            <span>{progressPercentage}% Complete</span>
          </div>
          <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
            <div 
              className="h-full bg-blue-600 rounded-full transition-all duration-300"
              style={{ width: `${progressPercentage}%` }}
            />
          </div>
        </div>
      </div>

      {/* Main Question Card */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-lg space-y-6">
        <div className="space-y-2">
          <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
            {currentQuestion.section}
          </span>
          <h2 className="text-lg sm:text-xl font-bold text-slate-950 leading-snug">
            {currentQuestion.question}
          </h2>
          {currentQuestion.description && (
            <p className="text-xs text-slate-500">{currentQuestion.description}</p>
          )}
        </div>

        {/* Options List */}
        <div className="space-y-3 pt-2">
          {currentQuestion.options.map(opt => {
            const isSelected = currentQuestion.type === 'multiple'
              ? Array.isArray(currentAnswer) && currentAnswer.includes(opt.id)
              : currentAnswer === opt.id;

            return (
              <div
                key={opt.id}
                onClick={() => {
                  if (currentQuestion.type === 'multiple') {
                    handleMultipleToggle(opt.id);
                  } else {
                    handleSingleSelect(opt.id);
                  }
                }}
                className={`p-4 rounded-2xl border cursor-pointer transition-all flex items-start gap-3.5 ${
                  isSelected
                    ? 'bg-blue-50/80 border-blue-500 shadow-xs ring-1 ring-blue-500/30'
                    : 'bg-slate-50/60 border-slate-200 hover:bg-slate-100/80'
                }`}
              >
                <div className={`w-5 h-5 rounded-full flex items-center justify-center shrink-0 mt-0.5 border ${
                  isSelected 
                    ? 'bg-blue-600 border-blue-600 text-white' 
                    : 'border-slate-300 bg-white'
                }`}>
                  {isSelected && <CheckCircle2 className="w-3.5 h-3.5" />}
                </div>

                <div className="flex-1 text-xs">
                  <p className={`font-semibold ${isSelected ? 'text-blue-950' : 'text-slate-800'}`}>
                    {opt.text}
                  </p>
                  {opt.associatedSkills.length > 0 && (
                    <div className="mt-2 flex flex-wrap items-center gap-1">
                      <span className="text-[10px] text-slate-400 font-medium">Impacts:</span>
                      {opt.associatedSkills.map(sk => (
                        <span key={sk.skillId} className="text-[10px] bg-slate-200/70 text-slate-700 px-1.5 py-0.5 rounded font-medium">
                          {sk.skillId} (+{sk.weight})
                        </span>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>

        {/* Wizard Navigation Footer */}
        <div className="pt-6 border-t border-slate-100 flex items-center justify-between gap-4">
          <button
            onClick={handleBack}
            disabled={currentStep === 0}
            className={`px-4 py-2.5 rounded-xl font-semibold text-xs flex items-center gap-2 transition-colors ${
              currentStep === 0
                ? 'opacity-40 cursor-not-allowed bg-slate-100 text-slate-400'
                : 'bg-slate-100 hover:bg-slate-200 text-slate-800'
            }`}
          >
            <ArrowLeft className="w-4 h-4" /> Previous
          </button>

          <button
            onClick={handleNext}
            className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-2"
          >
            <span>{currentStep === totalSteps - 1 ? 'Calculate Matches' : 'Next Question'}</span>
            {currentStep === totalSteps - 1 ? <BarChart2 className="w-4 h-4" /> : <ArrowRight className="w-4 h-4" />}
          </button>
        </div>
      </div>
    </div>
  );
};
