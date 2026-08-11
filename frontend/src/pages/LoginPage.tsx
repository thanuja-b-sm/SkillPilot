import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { Compass, Mail, Lock, ArrowRight, Info, X } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const { setUserRole, navigateTo, showToast, setUserProfile, setToken } = useApp();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showForgotModal, setShowForgotModal] = useState(false);

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
      // Verify role from response (data.userRole is 'admin' or 'ADMIN')
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
              onClick={() => setShowForgotModal(true)}
              className="text-blue-600 hover:underline cursor-pointer"
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
          <div className="bg-white rounded-3xl max-w-sm w-full p-8 space-y-5 border border-slate-200 shadow-xl text-left">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-xl bg-amber-100 text-amber-600 flex items-center justify-center">
                  <Info className="w-4 h-4" />
                </div>
                <h3 className="text-base font-bold text-slate-950">Password Reset</h3>
              </div>
              <button
                onClick={() => setShowForgotModal(false)}
                className="p-1 hover:bg-slate-100 rounded-lg text-slate-500"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
            <p className="text-xs text-slate-600 leading-relaxed">
              Password self-reset is not available in this version of SkillPilot.
            </p>
            <p className="text-xs text-slate-600 leading-relaxed">
              Please contact your <strong className="text-slate-900">system administrator</strong> to have your password reset manually.
            </p>
            <button
              onClick={() => setShowForgotModal(false)}
              className="w-full py-2.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold rounded-xl transition-colors"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </>
  );
};
