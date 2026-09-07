import React, { useState, useEffect, useRef } from 'react';
import { useApp } from '../context/AppContext';
import { Compass, Mail, Lock, ArrowRight, Info, X, CheckCircle2, KeyRound, AlertCircle, Sparkles, Eye, EyeOff, Clock, RotateCw, ShieldCheck } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const { loginWithAuthData, navigateTo, showToast } = useApp();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showLoginPassword, setShowLoginPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);


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
      const roleStr = data.userRole || data.userProfile?.userRole || data.userProfile?.role;
      loginWithAuthData(data.token, data.userProfile, roleStr);
      showToast(`Welcome back, ${data.userProfile?.name || 'User'}!`, 'success');
    } catch (err) {
      showToast('Unable to authenticate with backend server', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  // Forgot Password Enhanced States
  const [showForgotModal, setShowForgotModal] = useState(false);
  const [forgotStep, setForgotStep] = useState<1 | 2 | 3>(1); // 1: Request, 2: OTP & Reset, 3: Success
  const [forgotEmail, setForgotEmail] = useState('');
  const [otpValues, setOtpValues] = useState<string[]>(['', '', '', '', '', '']);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [forgotLoading, setForgotLoading] = useState(false);
  const [forgotError, setForgotError] = useState<string | null>(null);
  const [forgotSuccess, setForgotSuccess] = useState<string | null>(null);
  
  // Timers
  const [expirySeconds, setExpirySeconds] = useState<number>(15 * 60); // 15 mins
  const [resendCooldown, setResendCooldown] = useState<number>(0);

  // OTP input refs
  const otpInputRefs = React.useRef<(HTMLInputElement | null)[]>([]);

  // Expiry & cooldown interval
  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (showForgotModal && forgotStep === 2) {
      timer = setInterval(() => {
        setExpirySeconds(prev => (prev > 0 ? prev - 1 : 0));
        setResendCooldown(prev => (prev > 0 ? prev - 1 : 0));
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [showForgotModal, forgotStep]);

  const formatTimer = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const openForgotModal = () => {
    setForgotStep(1);
    setForgotEmail(email || '');
    setOtpValues(['', '', '', '', '', '']);
    setNewPassword('');
    setConfirmPassword('');
    setShowNewPassword(false);
    setShowConfirmPassword(false);
    setForgotError(null);
    setForgotSuccess(null);
    setExpirySeconds(15 * 60);
    setResendCooldown(0);
    setShowForgotModal(true);
  };

  const handleOtpChange = (index: number, val: string) => {
    const cleanVal = val.replace(/\D/g, '');
    const newOtp = [...otpValues];
    
    if (cleanVal.length > 1) {
      // Handle multi-character typing / paste
      const digits = cleanVal.slice(0, 6).split('');
      digits.forEach((d, i) => {
        if (i < 6) newOtp[i] = d;
      });
      setOtpValues(newOtp);
      const nextFocus = Math.min(digits.length, 5);
      otpInputRefs.current[nextFocus]?.focus();
      return;
    }

    newOtp[index] = cleanVal;
    setOtpValues(newOtp);

    if (cleanVal && index < 5) {
      otpInputRefs.current[index + 1]?.focus();
    }
  };

  const handleOtpKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !otpValues[index] && index > 0) {
      otpInputRefs.current[index - 1]?.focus();
    }
  };

  const handleOtpPaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pasteData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (!pasteData) return;
    const newOtp = [...otpValues];
    pasteData.split('').forEach((char, idx) => {
      if (idx < 6) newOtp[idx] = char;
    });
    setOtpValues(newOtp);
    const focusIdx = Math.min(pasteData.length, 5);
    otpInputRefs.current[focusIdx]?.focus();
  };

  // Password strength checker
  const getPasswordStrength = (pass: string) => {
    if (!pass) return { score: 0, label: 'None', color: 'bg-slate-200' };
    let score = 0;
    if (pass.length >= 8) score += 1;
    if (/[0-9]/.test(pass)) score += 1;
    if (/[A-Z]/.test(pass)) score += 1;
    if (/[^A-Za-z0-9]/.test(pass)) score += 1;

    if (score <= 1) return { score, label: 'Weak', color: 'bg-rose-500' };
    if (score === 2) return { score, label: 'Fair', color: 'bg-amber-500' };
    if (score === 3) return { score, label: 'Good', color: 'bg-blue-500' };
    return { score, label: 'Strong', color: 'bg-emerald-500' };
  };

  const handleRequestResetCode = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
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
        body: JSON.stringify({ email: forgotEmail.trim() })
      });
      const data = await res.json().catch(() => null);
      if (!res.ok) {
        setForgotError(data?.message || 'Unable to process password recovery request');
        return;
      }
      setForgotStep(2);
      setExpirySeconds(15 * 60);
      setResendCooldown(60);
      setForgotSuccess(data?.message || `If an account with that email address is registered, a 6-digit verification code has been sent.`);
      setTimeout(() => {
        otpInputRefs.current[0]?.focus();
      }, 100);
    } catch {
      setForgotError('Failed to connect to authentication server');
    } finally {
      setForgotLoading(false);
    }
  };

  const handleResendCode = async () => {
    if (resendCooldown > 0 || forgotLoading) return;
    setOtpValues(['', '', '', '', '', '']);
    await handleRequestResetCode();
  };

  const handleResetPasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const fullCode = otpValues.join('');
    if (fullCode.length !== 6) {
      setForgotError('Please enter the complete 6-digit verification code');
      return;
    }
    if (!newPassword) {
      setForgotError('New password is required');
      return;
    }
    if (newPassword !== confirmPassword) {
      setForgotError('New password and confirm password do not match');
      return;
    }
    if (newPassword.length < 8 || !/\d/.test(newPassword) || !/[A-Z]/.test(newPassword)) {
      setForgotError('Password must be at least 8 characters with 1 number and 1 uppercase letter');
      return;
    }

    setForgotLoading(true);
    setForgotError(null);
    try {
      const res = await fetch('/api/auth/reset-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: forgotEmail.trim(),
          resetCode: fullCode,
          newPassword
        })
      });
      const data = await res.json().catch(() => null);
      if (!res.ok) {
        setForgotError(data?.message || 'Password reset failed');
        return;
      }
      // Step 3: Success Screen
      setForgotStep(3);
      setEmail(forgotEmail);
      setPassword(newPassword);
    } catch {
      setForgotError('Failed to complete password reset request');
    } finally {
      setForgotLoading(false);
    }
  };

  const strength = getPasswordStrength(newPassword);


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
                type={showLoginPassword ? "text" : "password"}
                required
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full pl-9 pr-10 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                type="button"
                onClick={() => setShowLoginPassword(!showLoginPassword)}
                aria-label={showLoginPassword ? "Hide password" : "Show password"}
                className="absolute right-3 top-2.5 text-slate-400 hover:text-slate-600 focus:outline-none focus:text-blue-600 cursor-pointer"
              >
                {showLoginPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
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
          <div className="bg-white rounded-3xl max-w-md w-full p-6 sm:p-8 space-y-5 border border-slate-200 shadow-2xl text-left transition-all">
            
            {/* Modal Title Bar */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2.5">
                <div className="w-10 h-10 rounded-2xl bg-blue-50 text-blue-600 border border-blue-200 flex items-center justify-center shadow-xs">
                  <KeyRound className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-base font-bold text-slate-950">Password Recovery</h3>
                  <p className="text-[11px] text-slate-500 font-medium">
                    {forgotStep === 1 && 'Step 1: Request Verification Code'}
                    {forgotStep === 2 && 'Step 2: Enter OTP & Set Password'}
                    {forgotStep === 3 && 'Completed: Ready to Login'}
                  </p>
                </div>
              </div>
              <button
                onClick={() => setShowForgotModal(false)}
                className="p-1.5 hover:bg-slate-100 rounded-xl text-slate-400 hover:text-slate-600 transition-colors cursor-pointer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Error & Success Messages */}
            {forgotError && (
              <div className="p-3.5 rounded-2xl bg-rose-50 border border-rose-200 text-rose-800 text-xs flex items-start gap-2.5 animate-fadeIn">
                <AlertCircle className="w-4 h-4 text-rose-600 shrink-0 mt-0.5" />
                <span className="leading-relaxed">{forgotError}</span>
              </div>
            )}
            {forgotSuccess && forgotStep !== 3 && (
              <div className="p-3.5 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs flex items-start gap-2.5 animate-fadeIn">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />
                <span className="leading-relaxed">{forgotSuccess}</span>
              </div>
            )}

            {/* Step 1: Request Verification Code */}
            {forgotStep === 1 && (
              <form onSubmit={handleRequestResetCode} className="space-y-4">
                <p className="text-xs text-slate-600 leading-relaxed">
                  Enter your registered account email address to receive a secure 6-digit verification code.
                </p>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Registered Email Address</label>
                  <div className="relative">
                    <Mail className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
                    <input
                      type="email"
                      required
                      value={forgotEmail}
                      onChange={e => setForgotEmail(e.target.value)}
                      placeholder="you@example.com"
                      className="w-full pl-10 pr-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
                <button
                  type="submit"
                  disabled={forgotLoading}
                  className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center justify-center gap-2 disabled:opacity-60 cursor-pointer"
                >
                  {forgotLoading ? (
                    <>
                      <RotateCw className="w-4 h-4 animate-spin" />
                      <span>Sending Verification Code...</span>
                    </>
                  ) : (
                    <>
                      <span>Send Verification Code</span>
                      <ArrowRight className="w-4 h-4" />
                    </>
                  )}
                </button>
              </form>
            )}

            {/* Step 2: 6-Digit OTP Box + Password Reset */}
            {forgotStep === 2 && (
              <form onSubmit={handleResetPasswordSubmit} className="space-y-4">
                {/* Timer & Resend Bar */}
                <div className="flex items-center justify-between bg-slate-50 p-2.5 rounded-xl border border-slate-200 text-xs">
                  <div className="flex items-center gap-1.5 text-slate-600">
                    <Clock className="w-3.5 h-3.5 text-blue-600" />
                    <span>Code expires in: <strong className="font-mono text-blue-700">{formatTimer(expirySeconds)}</strong></span>
                  </div>
                  <button
                    type="button"
                    onClick={handleResendCode}
                    disabled={resendCooldown > 0 || forgotLoading}
                    className="text-blue-600 hover:text-blue-800 font-semibold disabled:text-slate-400 disabled:cursor-not-allowed cursor-pointer transition-colors text-[11px]"
                  >
                    {resendCooldown > 0 ? `Resend (${resendCooldown}s)` : 'Resend Code'}
                  </button>
                </div>

                {/* 6 OTP Inputs */}
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-2">6-Digit Verification Code</label>
                  <div className="flex items-center justify-between gap-1.5 sm:gap-2" onPaste={handleOtpPaste}>
                    {[0, 1, 2, 3, 4, 5].map(idx => (
                      <input
                        key={idx}
                        ref={el => { otpInputRefs.current[idx] = el; }}
                        type="text"
                        inputMode="numeric"
                        maxLength={1}
                        value={otpValues[idx]}
                        onChange={e => handleOtpChange(idx, e.target.value)}
                        onKeyDown={e => handleOtpKeyDown(idx, e)}
                        className="w-10 sm:w-12 h-12 text-center text-lg font-bold font-mono bg-slate-50 border-2 border-slate-200 focus:border-blue-500 rounded-xl text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-200 transition-all"
                      />
                    ))}
                  </div>
                </div>

                {/* New Password */}
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">New Password</label>
                  <div className="relative">
                    <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                    <input
                      type={showNewPassword ? 'text' : 'password'}
                      required
                      minLength={8}
                      value={newPassword}
                      onChange={e => setNewPassword(e.target.value)}
                      placeholder="At least 8 chars, 1 number, 1 uppercase"
                      className="w-full pl-9 pr-10 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <button
                      type="button"
                      onClick={() => setShowNewPassword(!showNewPassword)}
                      className="absolute right-3 top-3 text-slate-400 hover:text-slate-600"
                    >
                      {showNewPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  {/* Strength Bar */}
                  {newPassword && (
                    <div className="mt-2 space-y-1">
                      <div className="flex items-center justify-between text-[10px] text-slate-500">
                        <span>Strength: <strong className="text-slate-800">{strength.label}</strong></span>
                        <span>{strength.score}/4 requirements</span>
                      </div>
                      <div className="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden flex gap-1">
                        {[1, 2, 3, 4].map(s => (
                          <div
                            key={s}
                            className={`h-full flex-1 rounded-full transition-all ${
                              strength.score >= s ? strength.color : 'bg-slate-200'
                            }`}
                          />
                        ))}
                      </div>
                    </div>
                  )}
                </div>

                {/* Confirm Password */}
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Confirm New Password</label>
                  <div className="relative">
                    <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
                    <input
                      type={showConfirmPassword ? 'text' : 'password'}
                      required
                      minLength={8}
                      value={confirmPassword}
                      onChange={e => setConfirmPassword(e.target.value)}
                      placeholder="Re-enter your new password"
                      className="w-full pl-9 pr-10 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <button
                      type="button"
                      onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      className="absolute right-3 top-3 text-slate-400 hover:text-slate-600"
                    >
                      {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                </div>

                <div className="pt-2 flex items-center justify-between gap-2">
                  <button
                    type="button"
                    onClick={() => {
                      setForgotStep(1);
                      setForgotError(null);
                    }}
                    className="text-xs text-slate-500 hover:text-slate-800 underline cursor-pointer"
                  >
                    ← Change Email
                  </button>
                  <button
                    type="submit"
                    disabled={forgotLoading}
                    className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors flex items-center gap-2 disabled:opacity-60 cursor-pointer"
                  >
                    {forgotLoading ? (
                      <>
                        <RotateCw className="w-4 h-4 animate-spin" />
                        <span>Updating...</span>
                      </>
                    ) : (
                      <>
                        <span>Reset Password</span>
                        <ShieldCheck className="w-4 h-4" />
                      </>
                    )}
                  </button>
                </div>
              </form>
            )}

            {/* Step 3: Success Screen */}
            {forgotStep === 3 && (
              <div className="text-center py-4 space-y-4">
                <div className="w-14 h-14 bg-emerald-100 text-emerald-600 rounded-2xl flex items-center justify-center mx-auto shadow-inner">
                  <CheckCircle2 className="w-8 h-8" />
                </div>
                <div className="space-y-1">
                  <h4 className="text-base font-bold text-slate-900">Password Reset Successful!</h4>
                  <p className="text-xs text-slate-500 max-w-xs mx-auto">
                    Your password has been updated. You can now log in securely with your new credentials.
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => setShowForgotModal(false)}
                  className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs rounded-xl shadow-xs transition-colors cursor-pointer"
                >
                  Back to Login
                </button>
              </div>
            )}

          </div>
        </div>
      )}
    </>
  );
};
