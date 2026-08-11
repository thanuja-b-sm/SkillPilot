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

const MainContent: React.FC = () => {
  const { activePage } = useApp();

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
