import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { Compass, CheckCircle2, ArrowRight, Lock, Mail, User, BookOpen, Shield } from 'lucide-react';

export const RegistrationPage: React.FC = () => {
  const { navigateTo, setUserRole, setUserProfile, userProfile, showToast, setToken } = useApp();

  const [formData, setFormData] = useState({
    name: userProfile.name || '',
    email: userProfile.email || '',
    password: '',
    education: userProfile.education || 'Computer Science Senior',
    targetFocus: userProfile.targetFocus || 'Artificial Intelligence'
  });

  const [isSuccess, setIsSuccess] = useState(false);

  // Password rules check
  const hasMinLength = formData.password.length >= 8;
  const hasNumber = /\d/.test(formData.password);
  const hasUpper = /[A-Z]/.test(formData.password);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name || !formData.email || !formData.password) {
      showToast('Please complete all required fields.', 'warning');
      return;
    }

    try {
      const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: formData.name,
          email: formData.email,
          password: formData.password,
          education: formData.education,
          targetFocus: formData.targetFocus
        })
      });

      if (!res.ok) {
        const errorData = await res.json().catch(() => null);
        showToast(errorData?.message || 'Registration failed', 'error');
        return;
      }

      const data = await res.json();
      setToken(data.token);
      setUserProfile(data.userProfile);
      setIsSuccess(true);
      showToast('Account created successfully!', 'success');
    } catch (err) {
      showToast('Unable to connect to registration service', 'error');
    }
  };

  if (isSuccess) {
    return (
      <div className="max-w-md mx-auto my-12 bg-white p-8 rounded-3xl border border-slate-200/90 shadow-xl text-center space-y-5">
        <div className="w-16 h-16 rounded-2xl bg-emerald-50 text-emerald-600 border border-emerald-200 flex items-center justify-center mx-auto shadow-xs">
          <CheckCircle2 className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-bold text-slate-950">Welcome to SkillPilot!</h2>
        <p className="text-xs text-slate-600 leading-relaxed">
          Your account <strong className="text-slate-900">{formData.email}</strong> is ready. Set up your skill profile to unlock personalized career matches and questionnaire discovery.
        </p>
        <div className="pt-2">
          <button
            onClick={() => {
              setUserRole('student');
              navigateTo('profile');
            }}
            className="w-full py-3.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold text-sm shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2.5 group"
          >
            <span>Set Up Your Skill Profile</span>
            <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto my-8 grid grid-cols-1 md:grid-cols-12 gap-8 items-center">
      {/* Left Column Brand Feature */}
      <div className="md:col-span-5 space-y-6 text-left p-6 bg-slate-900 text-white rounded-3xl shadow-lg border border-slate-800">
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center font-bold text-white">
            <Compass className="w-5 h-5" />
          </div>
          <span className="text-xl font-bold">SkillPilot</span>
        </div>

        <h2 className="text-xl font-extrabold text-white leading-snug">
          Join the Academic Career Discovery Platform
        </h2>

        <ul className="space-y-3 text-xs text-slate-300">
          <li className="flex items-start gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
            <span>Verifiable skill gap analysis against 10+ tech domains.</span>
          </li>
          <li className="flex items-start gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
            <span>Calculated milestone roadmaps for 6-12 month execution.</span>
          </li>
          <li className="flex items-start gap-2">
            <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
            <span>AI explanation summary reports for executive portfolio sharing.</span>
          </li>
        </ul>

        <div className="pt-4 border-t border-slate-800 text-[11px] text-slate-400">
          <p>Already have an active account?</p>
          <button
            onClick={() => navigateTo('login')}
            className="mt-1 font-bold text-blue-400 hover:text-blue-300 underline"
          >
            Sign in to existing workspace
          </button>
        </div>
      </div>

      {/* Right Column Registration Form */}
      <div className="md:col-span-7 bg-white p-8 rounded-3xl border border-slate-200/90 shadow-md">
        <div className="mb-6 space-y-1 text-left">
          <h2 className="text-xl font-bold text-slate-950">Create Student Account</h2>
          <p className="text-xs text-slate-500">Fill in your baseline details to start your career assessment.</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4 text-left">
          {/* Full Name */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Full Name *</label>
            <div className="relative">
              <User className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                required
                value={formData.name}
                onChange={e => setFormData({ ...formData, name: e.target.value })}
                placeholder="e.g. Alex Rivera"
                className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Email */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Academic Email *</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="email"
                required
                value={formData.email}
                onChange={e => setFormData({ ...formData, email: e.target.value })}
                placeholder="alex.rivera@university.edu"
                className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          {/* Education & Target Focus */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Current Degree / Level</label>
              <div className="relative">
                <BookOpen className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                <input
                  type="text"
                  value={formData.education}
                  onChange={e => setFormData({ ...formData, education: e.target.value })}
                  placeholder="e.g. Senior CS Major"
                  className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">Target Focus Domain</label>
              <select
                value={formData.targetFocus}
                onChange={e => setFormData({ ...formData, targetFocus: e.target.value })}
                className="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="Artificial Intelligence">Artificial Intelligence / ML</option>
                <option value="Cloud Architecture">Cloud & Infrastructure</option>
                <option value="Full Stack Web">Full Stack Engineering</option>
                <option value="Data Analytics">Data Science & Analytics</option>
                <option value="Cybersecurity">Cybersecurity & Risk</option>
              </select>
            </div>
          </div>

          {/* Password & Validation Indicator */}
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Password *</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="password"
                required
                value={formData.password}
                onChange={e => setFormData({ ...formData, password: e.target.value })}
                placeholder="••••••••"
                className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* Password Validation Chips */}
            <div className="mt-2 flex flex-wrap items-center gap-2 text-[10px]">
              <span className={`px-2 py-0.5 rounded-full border ${hasMinLength ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : 'bg-slate-100 text-slate-500 border-slate-200'}`}>
                {hasMinLength ? '✓' : '○'} 8+ characters
              </span>
              <span className={`px-2 py-0.5 rounded-full border ${hasNumber ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : 'bg-slate-100 text-slate-500 border-slate-200'}`}>
                {hasNumber ? '✓' : '○'} Contains number
              </span>
              <span className={`px-2 py-0.5 rounded-full border ${hasUpper ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : 'bg-slate-100 text-slate-500 border-slate-200'}`}>
                {hasUpper ? '✓' : '○'} Uppercase letter
              </span>
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors mt-2"
          >
            Create Account & Initialize Assessment
          </button>
        </form>
      </div>
    </div>
  );
};
