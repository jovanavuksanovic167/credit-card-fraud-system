import { useEffect, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import Loading from "../components/common/Loading";
import ErrorMessage from "../components/common/ErrorMessage";
import StatusBadge from "../components/fraudCases/StatusBadge";
import { getFraudCases } from "../services/fraudCaseService";
import { formatCurrency, formatNumber, formatDateTime } from "../utils/formatters";

function FraudCasesPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const initialStatus = searchParams.get("status") || "";

  const [fraudCases, setFraudCases] = useState([]);
  const [statusFilter, setStatusFilter] = useState(initialStatus);
  const [amountSort, setAmountSort] = useState("");
  const [probabilitySort, setProbabilitySort] = useState("");
  const [sortPriority, setSortPriority] = useState("AMOUNT");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadFraudCases = async (status = statusFilter) => {
    try {
      setLoading(true);
      setError("");
      const data = await getFraudCases(status);
      setFraudCases(data);
    } catch {
      setError("Failed to load fraud cases.");
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (event) => {
    const value = event.target.value;
    setStatusFilter(value);

    if (value) {
      setSearchParams({ status: value });
    } else {
      setSearchParams({});
    }

    await loadFraudCases(value);
  };

  const sortedCases = useMemo(() => {
  const data = [...fraudCases];

  data.sort((a, b) => {
    const amountA = a.transaction.amount ?? 0;
    const amountB = b.transaction.amount ?? 0;

    const probabilityA = Number(
      (a.transaction.fraudProbability ?? 0).toFixed(4)
    );
    const probabilityB = Number(
      (b.transaction.fraudProbability ?? 0).toFixed(4)
    );

    let amountComparison = 0;
    let probabilityComparison = 0;

    if (amountSort === "ASC") {
      amountComparison = amountA - amountB;
    }

    if (amountSort === "DESC") {
      amountComparison = amountB - amountA;
    }

    if (probabilitySort === "ASC") {
      probabilityComparison = probabilityA - probabilityB;
    }

    if (probabilitySort === "DESC") {
      probabilityComparison = probabilityB - probabilityA;
    }

    if (sortPriority === "AMOUNT") {
      if (amountComparison !== 0) {
        return amountComparison;
      }

      return probabilityComparison;
    }

    if (sortPriority === "PROBABILITY") {
      if (probabilityComparison !== 0) {
        return probabilityComparison;
      }

      return amountComparison;
    }

    return 0;
  });

  return data;
}, [fraudCases, amountSort, probabilitySort, sortPriority]);

  useEffect(() => {
    loadFraudCases(initialStatus);
  }, []);

  if (loading) {
    return <Loading text="Loading fraud cases..." />;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h2>Fraud Cases</h2>
      </div>

      <ErrorMessage message={error} />

      <div className="filters-panel">
  <label>
    Status
    <select value={statusFilter} onChange={handleStatusChange}>
      <option value="">All</option>
      <option value="NEW">New</option>
      <option value="IN_REVIEW">In Review</option>
      <option value="CONFIRMED_FRAUD">Confirmed Fraud</option>
      <option value="FALSE_ALERT">False Alert</option>
    </select>
  </label>

  <label>
    Amount
    <select
      value={amountSort}
      onChange={(event) => setAmountSort(event.target.value)}
    >
      <option value="">No sorting</option>
      <option value="ASC">Lowest first</option>
      <option value="DESC">Highest first</option>
    </select>
  </label>

  <label>
    Fraud probability
    <select
      value={probabilitySort}
      onChange={(event) => setProbabilitySort(event.target.value)}
    >
      <option value="">No sorting</option>
      <option value="ASC">Lowest first</option>
      <option value="DESC">Highest first</option>
    </select>
  </label>
  <label>
  Priority
  <select
    value={sortPriority}
    onChange={(event) => setSortPriority(event.target.value)}
  >
    <option value="AMOUNT">Amount first</option>
    <option value="PROBABILITY">Fraud probability first</option>
  </select>
</label>
</div>

      <section className="table-panel">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Amount</th>
              <th>Fraud probability</th>
              <th>Status</th>
              <th>Created at</th>
              <th></th>
            </tr>
          </thead>

          <tbody>
            {sortedCases.map((fraudCase) => (
              <tr key={fraudCase.id}>
                <td>#{fraudCase.id}</td>
                <td>{formatCurrency(fraudCase.transaction.amount)}</td>
                <td>{formatNumber(fraudCase.transaction.fraudProbability, 4)}</td>
                <td>
                  <StatusBadge status={fraudCase.status} />
                </td>
                <td>{formatDateTime(fraudCase.createdAt)}</td>
                <td className="table-actions">
                  <button
                    className="secondary-button"
                    onClick={() => navigate(`/fraud-cases/${fraudCase.id}`)}
                  >
                    Open
                  </button>
                </td>
              </tr>
            ))}

            {sortedCases.length === 0 && (
              <tr>
                <td colSpan="6" className="empty-table">
                  No fraud cases found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </section>
    </div>
  );
}

export default FraudCasesPage;