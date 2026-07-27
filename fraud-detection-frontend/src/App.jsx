import { Route, Routes } from "react-router-dom";
import Sidebar from "./components/layout/Sidebar";
import DashboardPage from "./pages/DashboardPage";
import FraudCasesPage from "./pages/FraudCasesPage";
import FraudCaseDetailsPage from "./pages/FraudCaseDetailsPage";
import StatisticsPage from "./pages/StatisticsPage";
function App() {
  return (
    <div className="app-shell">
      <Sidebar />

      <main className="main-content">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/fraud-cases" element={<FraudCasesPage />} />
          <Route path="/fraud-cases/:id" element={<FraudCaseDetailsPage />} />
          <Route path="/statistics" element={<StatisticsPage />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;