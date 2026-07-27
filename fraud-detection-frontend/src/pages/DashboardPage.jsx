import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import StatCard from "../components/dashboard/StatCard";
import Loading from "../components/common/Loading";
import ErrorMessage from "../components/common/ErrorMessage";
import { getStatistics } from "../services/statisticsService";
import { processDailyTransactions } from "../services/dailyProcessingService";

function DashboardPage() {
  const navigate = useNavigate();

  const [statistics, setStatistics] = useState(null);
  const [processing, setProcessing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadStatistics = async () => {
    try {
      setError("");
      const data = await getStatistics();
      setStatistics(data);
    } catch {
      setError("Failed to load statistics.");
    } finally {
      setLoading(false);
    }
  };

  const handleDailyProcessing = async () => {
    try {
      setProcessing(true);
      setError("");
      await processDailyTransactions(100);
      await loadStatistics();
    } catch {
      setError("Daily transaction processing failed.");
    } finally {
      setProcessing(false);
    }
  };

  useEffect(() => {
    loadStatistics();
  }, []);

  if (loading) {
    return <Loading text="Loading..." />;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h2>Dashboard</h2>

        <button
          className="primary-button"
          onClick={handleDailyProcessing}
          disabled={processing}
        >
          {processing ? "Processing..." : "Process Daily Transactions"}
        </button>
      </div>

      <ErrorMessage message={error} />

      <section className="stat-grid dashboard-stat-grid">
        <StatCard
          title="Fraud Cases"
          value={statistics?.totalFraudCases ?? 0}
          onClick={() => navigate("/fraud-cases")}
        />

        <StatCard
          title="New Cases"
          value={statistics?.newCases ?? 0}
          onClick={() => navigate("/fraud-cases?status=NEW")}
        />

        <StatCard
          title="In Review"
          value={statistics?.inReviewCases ?? 0}
          onClick={() => navigate("/fraud-cases?status=IN_REVIEW")}
        />
      </section>
    </div>
  );
}

export default DashboardPage;