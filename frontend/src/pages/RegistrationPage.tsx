import React, { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { Compass, CheckCircle2, ArrowRight, Lock, Mail, User, BookOpen, KeyRound, AlertCircle, Eye, EyeOff, RotateCw, Clock, ShieldCheck } from 'lucide-react';

export const RegistrationPage: React.FC = () => {
  const { navigateTo, setUserRole, userProfile, showToast, loginWithAuthData } = useApp();

  const [formData, setFormData] = useState({
    name: userProfile.name || '',
    email: userProfile.email || '',
    password: '',
    education: userProfile.education || 'Computer Science Senior',
    targetFocus: userProfile.targetFocus || 'Artificial Intelligence'
  });

  const [showPassword, setShowPassword] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState('');
  const [verificationCode, setVerificationCode] = useState('');
  const [isSubmittingVerification, setIsSubmittingVerification] = useState(false);
  const [isResending, setIsResending] = useState(false);
  const [resendCooldown, setResendCooldown] = useState(0);
  const [verificationError, setVerificationError] = useState<string | null>(null);
  const [verificationSuccessMessage, setVerificationSuccessMessage] = useState<string | null>(null);

  // Resend cooldown timer
  useEffect(() => {
    if (resendCooldown <= 0) return;
    const timer = setInterval(() => {
      setResendCooldown(prev => prev - 1);
    }, 1000);
    return () => clearInterval(timer);
  }, [resendCooldown]);

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
      if (data.requiresVerification) {
        setRegisteredEmail(formData.email.trim().toLowerCase());
        setIsVerifying(true);
        setResendCooldown(60);
        showToast('Account created! A 6-digit verification code has been dispatched to your email.', 'info');
      } else {
        // Fallback for immediate token issue if verification is bypassed
        loginWithAuthData(data.token, data.userProfile, data.userRole || 'student', 'profile');
        setIsSuccess(true);
        showToast('Account created successfully!', 'success');
      }
    } catch (err) {
      showToast('Unable to connect to registration service', 'error');
    }
  };

  const handleVerifySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!verificationCode.trim()) {
      setVerificationError('Please enter the 6-digit verification code.');
      return;
    }

    setVerificationError(null);
    setVerificationSuccessMessage(null);
    setIsSubmittingVerification(true);

    try {
      const res = await fetch('/api/auth/verify-email', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: registeredEmail,
          verificationCode: verificationCode.trim()
        })
      });

      const data = await res.json().catch(() => null);
      if (!res.ok) {
        setVerificationError(data?.message || 'Invalid or expired verification code.');
        return;
      }

      loginWithAuthData(data.token, data.userProfile, data.userRole || 'student', 'profile');
      setIsVerifying(false);
      setIsSuccess(true);
      showToast('Account email verified successfully! Welcome to SkillPilot.', 'success');
    } catch (err) {
      setVerificationError('Unable to connect to verification service. Please try again.');
    } finally {
      setIsSubmittingVerification(false);
    }
  };

  const handleResendVerification = async () => {
    if (resendCooldown > 0 || isResending) return;
    setIsResending(true);
    setVerificationError(null);
    setVerificationSuccessMessage(null);

    try {
      const res = await fetch('/api/auth/resend-verification', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: registeredEmail })
      });

      const data = await res.json().catch(() => null);
      setResendCooldown(60);
      setVerificationSuccessMessage(data?.message || 'A new verification code has been dispatched to your email.');
      showToast('New verification code sent.', 'info');
    } catch (err) {
      setVerificationError('Unable to dispatch verification email. Please try again later.');
    } finally {
      setIsResending(false);
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
          Your account <strong className="text-slate-900">{registeredEmail || formData.email}</strong> is fully verified and active. Set up your skill profile to unlock personalized career matches and questionnaire discovery.
        </p>
        <div className="pt-2">
          <button
            onClick={() => {
              setUserRole('student');
              navigateTo('profile');
            }}
            className="w-full py-3.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold text-sm shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-2.5 group cursor-pointer"
          >
            <span>Set Up Your Skill Profile</span>
            <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
          </button>
        </div>
      </div>
    );
  }

  if (isVerifying) {
    return (
      <div className="max-w-md mx-auto my-12 bg-white p-8 rounded-3xl border border-slate-200/90 shadow-xl text-left space-y-5">
        <div className="text-center space-y-2">
          <div className="w-14 h-14 rounded-2xl bg-blue-50 text-blue-600 border border-blue-200 flex items-center justify-center mx-auto shadow-xs">
            <KeyRound className="w-7 h-7" />
          </div>
          <h2 className="text-2xl font-bold text-slate-950">Verify Your Email</h2>
          <p className="text-xs text-slate-600 leading-relaxed">
            We dispatched a 6-digit verification code via Brevo SMTP to <strong className="text-slate-900">{registeredEmail}</strong>. Enter it below to activate your account:
          </p>
        </div>

        {verificationError && (
          <div className="p-3 bg-rose-50 border border-rose-200 rounded-xl flex items-start gap-2 text-rose-700 text-xs">
            <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
            <span>{verificationError}</span>
          </div>
        )}

        {verificationSuccessMessage && (
          <div className="p-3 bg-emerald-50 border border-emerald-200 rounded-xl flex items-start gap-2 text-emerald-700 text-xs">
            <CheckCircle2 className="w-4 h-4 shrink-0 mt-0.5" />
            <span>{verificationSuccessMessage}</span>
          </div>
        )}

        <form onSubmit={handleVerifySubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">6-Digit Verification Code</label>
            <div className="relative">
              <KeyRound className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                maxLength={6}
                autoFocus
                required
                value={verificationCode}
                onChange={e => setVerificationCode(e.target.value.replace(/\D/g, ''))}
                placeholder="123456"
                className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-center text-lg font-mono tracking-widest text-slate-900 font-bold focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="flex items-center justify-between text-[11px] text-slate-500 mt-1">
              <span className="flex items-center gap-1">
                <Clock className="w-3 h-3 text-slate-400" />
                Valid for 15 minutes
              </span>
              <span>5 attempts allowed</span>
            </div>
          </div>

          <button
            type="submit"
            disabled={isSubmittingVerification || verificationCode.length !== 6}
            className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
          >
            {isSubmittingVerification ? (
              <span className="animate-pulse flex items-center gap-1.5">
                <RotateCw className="w-3.5 h-3.5 animate-spin" /> Verifying Code…
              </span>
            ) : (
              <span>Verify & Activate Workspace</span>
            )}
          </button>
        </form>

        <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-600">
          <span className="text-[11px] text-slate-500">Didn't receive the email?</span>
          <button
            type="button"
            disabled={resendCooldown > 0 || isResending}
            onClick={handleResendVerification}
            className="font-semibold text-blue-600 hover:text-blue-700 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
          >
            {isResending ? 'Sending…' : resendCooldown > 0 ? `Resend in ${resendCooldown}s` : 'Resend Code'}
          </button>
        </div>

        <div className="bg-slate-50 p-3 rounded-xl border border-slate-200 text-[11px] text-slate-500 flex items-start gap-2">
          <ShieldCheck className="w-4 h-4 text-slate-400 shrink-0 mt-0.5" />
          <span>Email verification protects your academic profile, skill assessments, and AI-generated roadmaps from unauthorized access.</span>
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
            className="mt-1 font-bold text-blue-400 hover:text-blue-300 underline cursor-pointer"
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
                type={showPassword ? "text" : "password"}
                required
                value={formData.password}
                onChange={e => setFormData({ ...formData, password: e.target.value })}
                placeholder="••••••••"
                className="w-full pl-9 pr-10 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={showPassword ? "Hide password" : "Show password"}
                className="absolute right-3 top-2.5 text-slate-400 hover:text-slate-600 focus:outline-none focus:text-blue-600 cursor-pointer"
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
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
            className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors mt-2 cursor-pointer"
          >
            Create Account & Send Verification Code
          </button>
        </form>
      </div>
    </div>
  );
};
