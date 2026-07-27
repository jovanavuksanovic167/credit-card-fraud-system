import { useEffect, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import Loading from "../components/common/Loading";
import ErrorMessage from "../components/common/ErrorMessage";
import { getFraudAnalyticsStatistics } from "../services/fraudAnalyticsStatisticsService";
import { formatCurrency, formatNumber } from "../utils/formatters";

const mapToChartData = (object) => {
  if (!object) {
    return [];
  }

  return Object.entries(object).map(([name, value]) => ({
    name,
    value,
  }));
};

function StatisticsPage() {
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadStatistics = async () => {
    try {
      setError("");
      const data = await getFraudAnalyticsStatistics();
      setStatistics(data);
    } catch {
      setError("Failed to load statistics.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStatistics();
  }, []);

  if (loading) {
    return <Loading text="Loading statistics..." />;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h2>Statistics</h2>
      </div>

      <ErrorMessage message={error} />

      <section className="stat-grid analytics-stat-grid">
        <div className="stat-card">
          <p>Total cases</p>
          <h2>{statistics.totalCases}</h2>
        </div>

        <div className="stat-card">
          <p>New cases</p>
          <h2>{statistics.newCases}</h2>
        </div>

        <div className="stat-card">
          <p>In review</p>
          <h2>{statistics.inReviewCases}</h2>
        </div>

        <div className="stat-card">
          <p>Confirmed fraud</p>
          <h2>{statistics.confirmedFraudCases}</h2>
        </div>

        <div className="stat-card">
          <p>False alerts</p>
          <h2>{statistics.falseAlertCases}</h2>
        </div>
      </section>

      <section className="charts-grid single-chart-row">
        <ChartPanel
          title="Case status distribution"
          data={mapToChartData(statistics.caseStatusDistribution)}
        />
      </section>

      <section className="stat-grid analytics-stat-grid four-cards">
        <div className="stat-card highlight-card">
          <p>Prevented loss</p>
          <h2>{formatCurrency(statistics.preventedLoss)}</h2>
        </div>

        <div className="stat-card">
          <p>Average confirmed fraud amount</p>
          <h2>{formatCurrency(statistics.averageConfirmedFraudAmount)}</h2>
        </div>

        <div className="stat-card">
          <p>Highest confirmed fraud amount</p>
          <h2>{formatCurrency(statistics.highestConfirmedFraudAmount)}</h2>
        </div>

        <div className="stat-card">
          <p>Average false alert amount</p>
          <h2>{formatCurrency(statistics.averageFalseAlertAmount)}</h2>
        </div>
      </section>

      <section className="stat-grid analytics-stat-grid four-cards">
        <div className="stat-card">
          <p>PIN not used</p>
          <h2>{statistics.pinNotUsed}</h2>
        </div>

        <div className="stat-card">
          <p>Chip not used</p>
          <h2>{statistics.chipNotUsed}</h2>
        </div>

        <div className="stat-card">
          <p>Online orders</p>
          <h2>{statistics.onlineOrders}</h2>
        </div>

        <div className="stat-card">
          <p>New retailers</p>
          <h2>{statistics.newRetailers}</h2>
        </div>
      </section>

      <section className="stat-grid analytics-stat-grid three-cards">
        <div className="stat-card">
          <p>Average distance from home</p>
          <h2>{formatNumber(statistics.averageDistanceFromHome, 2)}</h2>
        </div>

        <div className="stat-card">
          <p>Average distance from last transaction</p>
          <h2>{formatNumber(statistics.averageDistanceFromLastTransaction, 2)}</h2>
        </div>

        <div className="stat-card">
          <p>Average purchase price ratio</p>
          <h2>{formatNumber(statistics.averagePurchasePriceRatio, 2)}</h2>
        </div>
      </section>

      <section className="charts-grid">
        <ChartPanel
          title="Used PIN"
          data={mapToChartData(statistics.usedPinDistribution)}
        />

        <ChartPanel
          title="Used chip"
          data={mapToChartData(statistics.usedChipDistribution)}
        />

        <ChartPanel
          title="Online order"
          data={mapToChartData(statistics.onlineOrderDistribution)}
        />

        <ChartPanel
          title="Repeat retailer"
          data={mapToChartData(statistics.repeatRetailerDistribution)}
        />

        <ChartPanel
          title="Distance from home categories"
          data={mapToChartData(statistics.distanceFromHomeCategories)}
        />

        <ChartPanel
          title="Distance from last transaction categories"
          data={mapToChartData(statistics.distanceFromLastTransactionCategories)}
        />

        <ChartPanel
          title="Purchase price ratio categories"
          data={mapToChartData(statistics.purchasePriceRatioCategories)}
        />
      </section>
    </div>
  );
}

function ChartPanel({ title, data }) {
  return (
    <div className="chart-panel">
      <h3>{title}</h3>

      <ResponsiveContainer width="100%" height={330}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" tick={{ fontSize: 12 }} />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="value" barSize={90}/>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default StatisticsPage;