import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { Compass, Mail, Lock, ArrowRight, Info, X, CheckCircle2, KeyRound, AlertCircle, Sparkles } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const { setUserRole, navigateTo, showToast, setUserProfile, setToken } = useApp();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  // Forgot Password Modal State
  const [showForgotModal, setShowForgotModal] = useState(false);
  const [forgotStep, setForgotStep] = useState<1 | 2>(1);
  const [forgotEmail, setForgotEmail] = useState('');
  const [resetCode, setResetCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [forgotLoading, setForgotLoading] = useState(false);
  const [forgotError, setForgotError] = useState<string | null>(null);
  const [forgotSuccess, setForgotSuccess] = useState<string | null>(null);

  const handleStudentLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      showToast('Please enter your email and password.', 'warning');
      return;
    }
    setIsLoading(true);
    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });
      if (!res.ok) {
        const errorData = await res.json().catch(() => null);
        showToast(errorData?.message || 'Invalid email or password', 'error');
        return;
      }
      const data = await res.json();
      setToken(data.token);
      if (data.userProfile) {
        setUserProfile(data.userProfile);
      }
      const isAdmin = data.userRole?.toLowerCase() === 'admin' || data.userProfile?.roles?.includes('ADMIN') || data.userProfile?.role?.toLowerCase() === 'admin';
      const role = isAdmin ? 'admin' : 'student';
      setUserRole(role);
      navigateTo(role === 'admin' ? 'admin' : 'results');
      showToast(`Welcome back, ${data.userProfile?.name || 'User'}!`, 'success');
    } catch (err) {
      showToast('Unable to authenticate with backend server', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  const openForgotModal = () => {
    setForgotStep(1);
    setForgotEmail(email);
    setResetCode('');
    setNewPassword('');
    setForgotError(null);
    setForgotSuccess(null);
    setShowForgotModal(true);
  };

  const handleRequestResetCode = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!forgotEmail) {
      setForgotError('Please enter your registered email address');
      return;
    }
    setForgotLoading(true);
    setForgotError(null);
    setForgotSuccess(null);
    try {
      const res = await fetch('/api/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: forgotEmail })
      });
      const data = await res.json().catch(() => null);
      if (!res.ok) {
        setForgotError(data?.message || 'Unable to generate reset code');
        return;
      }
      if (data?.resetCode) {
        setResetCode(data.resetCode);
      }
      setForgotStep(2);
      setForgotSuccess(`Verification code sent! ${data?.resetCode ? `(Code: ${data.resetCode})` : ''}`);
    } catch {
      setForgotError('Failed to connect to authentication server');
    } finally {
      setForgotLoading(false);
    }
  };

  const handleResetPasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!resetCode || !newPassword) {
      setForgotError('Verification code and new password are required');
      return;
    }
    setForgotLoading(true);
    setForgotError(null);
    try {
      const res = await fetch('/api/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: forgotEmail,
          resetCode,
          newPassword
        })
      });
      const data = await res.json().catch(() => null);
      if (!res.ok) {
        setForgotError(data?.message || 'Password reset failed');
        return;
      }
      // Success! Auto-fill email into login form
      setEmail(forgotEmail);
      setPassword(newPassword);
      setForgotSuccess('Password updated successfully! You can now log in.');
      setTimeout(() => {
        setShowForgotModal(false);
      }, 2000);
    } catch {
      setForgotError('Failed to complete password reset request');
    } finally {
      setForgotLoading(false);
    }
  };

  return (
    <>
      <div className="max-w-md mx-auto my-12 bg-white p-8 rounded-3xl border border-slate-200/90 shadow-xl space-y-6 text-left">
        {/* Brand Header */}
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-2xl bg-slate-900 text-blue-400 flex items-center justify-center mx-auto shadow-md">
            <Compass className="w-6 h-6" />
          </div>
          <h2 className="text-2xl font-bold text-slate-950">Welcome Back</h2>
          <p className="text-xs text-slate-500">Sign in to your SkillPilot career intelligence workspace.</p>
        </div>

        {/* Standard Form */}
        <form onSubmit={handleStudentLogin} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Email Address</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="email"
                required
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="your@email.com"
                className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="password"
                required
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="flex items-center justify-between text-xs text-slate-600">
            <span />
            <button
              type="button"
              onClick={openForgotModal}
              className="text-blue-600 hover:underline cursor-pointer font-medium"
            >
              Forgot password?
            </button>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center justify-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {isLoading ? (
              <span className="animate-pulse">Authenticating…</span>
            ) : (
              <>
                <span>Sign In</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </form>

        <div className="pt-2 text-center text-xs text-slate-500 border-t border-slate-100">
          Don't have an account?{' '}
          <button
            onClick={() => navigateTo('register')}
            className="font-bold text-blue-600 hover:underline cursor-pointer"
          >
            Create student account
          </button>
        </div>
      </div>

      {/* Forgot Password Modal */}
      {showForgotModal && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-sm w-full p-6 sm:p-8 space-y-5 border border-slate-200 shadow-2xl text-left">
            
            {/* Modal Title Bar */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2.5">
                <div className="w-9 h-9 rounded-xl bg-blue-50 text-blue-600 border border-blue-200 flex items-center justify-center">
                  <KeyRound className="w-4 h-4" />
                </div>
                <div>
                  <h3 className="text-base font-bold text-slate-950">Password Recovery</h3>
                  <p className="text-[11px] text-slate-500 font-medium">Step {forgotStep} of 2</p>
                </div>
              </div>
              <button
                onClick={() => setShowForgotModal(false)}
                className="p-1 hover:bg-slate-100 rounded-lg text-slate-400 hover:text-slate-600 transition-colors"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Error & Success Messages */}
            {forgotError && (
              <div className="p-3 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs flex items-start gap-2">
                <AlertCircle className="w-4 h-4 text-red-500 shrink-0 mt-0.5" />
                <span>{forgotError}</span>
              </div>
            )}
            {forgotSuccess && (
              <div className="p-3 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs flex items-start gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />
                <span>{forgotSuccess}</span>
              </div>
            )}

            {/* Step 1: Request Verification Code */}
            {forgotStep === 1 && (
              <form onSubmit={handleRequestResetCode} className="space-y-4">
                <p className="text-xs text-slate-600 leading-relaxed">
                  Enter your registered account email address to receive a 6-digit verification reset code.
                </p>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Email Address</label>
                  <div className="relative">
                    <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                    <input
                      type="email"
                      required
                      value={forgotEmail}
                      onChange={e => setForgotEmail(e.target.value)}
                      placeholder="your@email.com"
                      className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
                <button
                  type="submit"
                  disabled={forgotLoading}
                  className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center justify-center gap-2 disabled:opacity-60"
                >
                  {forgotLoading ? 'Sending Code...' : 'Send Reset Code'}
                </button>
              </form>
            )}

            {/* Step 2: Enter Code & New Password */}
            {forgotStep === 2 && (
              <form onSubmit={handleResetPasswordSubmit} className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">6-Digit Verification Code</label>
                  <input
                    type="text"
                    required
                    maxLength={6}
                    value={resetCode}
                    onChange={e => setResetCode(e.target.value)}
                    placeholder="123456"
                    className="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-mono tracking-widest text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 text-center"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">New Password</label>
                  <div className="relative">
                    <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                    <input
                      type="password"
                      required
                      minLength={8}
                      value={newPassword}
                      onChange={e => setNewPassword(e.target.value)}
                      placeholder="At least 8 chars, 1 number, 1 uppercase"
                      className="w-full pl-9 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>

                <div className="pt-1 flex items-center justify-between gap-2">
                  <button
                    type="button"
                    onClick={() => {
                      setForgotStep(1);
                      setForgotError(null);
                    }}
                    className="text-xs text-slate-500 hover:text-slate-800 underline cursor-pointer"
                  >
                    ← Back
                  </button>
                  <button
                    type="submit"
                    disabled={forgotLoading}
                    className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors disabled:opacity-60"
                  >
                    {forgotLoading ? 'Updating...' : 'Reset Password'}
                  </button>
                </div>
              </form>
            )}

          </div>
        </div>
      )}
    </>
  );
};
