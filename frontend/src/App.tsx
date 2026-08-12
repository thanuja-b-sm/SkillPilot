/**
 * SkillPilot – AI-Powered Career Intelligence & Roadmap Platform
 */

import React from 'react';
import { AppProvider, useApp } from './context/AppContext';
import { Header } from './components/Header';
import { Footer } from './components/Footer';
import { ToastContainer } from './components/ToastContainer';

// Page Components
import { LandingPage } from './pages/LandingPage';
import { RegistrationPage } from './pages/RegistrationPage';
import { LoginPage } from './pages/LoginPage';
import { ProfilePage } from './pages/ProfilePage';
import { QuestionnairePage } from './pages/QuestionnairePage';
import { CareerResultsPage } from './pages/CareerResultsPage';
import { TargetCareerSelectionPage } from './pages/TargetCareerSelectionPage';
import { SkillGapAnalysisPage } from './pages/SkillGapAnalysisPage';
import { RoadmapPage } from './pages/RoadmapPage';
import { AdminDashboardPage } from './pages/AdminDashboardPage';

import { Compass } from 'lucide-react';

const MainContent: React.FC = () => {
  const { activePage, isLoadingAuth } = useApp();

  const isProtectedRoute = ['admin', 'profile', 'skill-gap', 'roadmap', 'target-selection'].includes(activePage);

  if (isLoadingAuth && isProtectedRoute) {
    return (
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-16 flex flex-col items-center justify-center space-y-4">
        <div className="w-12 h-12 rounded-2xl bg-slate-900 text-blue-400 flex items-center justify-center shadow-md animate-pulse">
          <Compass className="w-6 h-6 animate-spin" />
        </div>
        <div className="text-center space-y-1">
          <p className="text-sm font-bold text-slate-900">SkillPilot Session Restoration</p>
          <p className="text-xs text-slate-500">Verifying secure authentication and loading your profile context...</p>
        </div>
      </main>
    );
  }

  return (
    <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {activePage === 'landing' && <LandingPage />}
      {activePage === 'register' && <RegistrationPage />}
      {activePage === 'login' && <LoginPage />}
      {activePage === 'profile' && <ProfilePage />}
      {activePage === 'questionnaire' && <QuestionnairePage />}
      {activePage === 'results' && <CareerResultsPage />}
      {activePage === 'target-selection' && <TargetCareerSelectionPage />}
      {activePage === 'skill-gap' && <SkillGapAnalysisPage />}
      {activePage === 'roadmap' && <RoadmapPage />}
      {activePage === 'admin' && <AdminDashboardPage />}
    </main>
  );
};

export default function App() {
  return (
    <AppProvider>
      <div className="min-h-screen bg-slate-50 text-slate-900 flex flex-col font-sans selection:bg-blue-100 selection:text-blue-900">
        <Header />
        <MainContent />
        <Footer />
        <ToastContainer />
      </div>
    </AppProvider>
  );
}
