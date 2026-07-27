import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Loading from "../components/common/Loading";
import ErrorMessage from "../components/common/ErrorMessage";
import StatusBadge from "../components/fraudCases/StatusBadge";
import { getFraudCaseById, updateFraudCase } from "../services/fraudCaseService";
import { formatCurrency, formatNumber, formatDateTime } from "../utils/formatters";

const yesNo = (value) => {
  return value === 1 || value === true ? "Yes" : "No";
};

const explainDistanceFromHome = (value) => {
  if (value < 1) {
    return "Close to the user's usual location.";
  }

  if (value < 10) {
    return "Moderate distance from the user's usual location.";
  }

  if (value < 50) {
    return "Far from the user's usual location.";
  }

  return "Very far from the user's usual location.";
};

const explainDistanceFromLastTransaction = (value) => {
  if (value < 1) {
    return "Close to the location of the previous transaction.";
  }

  if (value < 10) {
    return "Moderate distance from the previous transaction.";
  }

  if (value < 50) {
    return "Far from the previous transaction.";
  }

  return "Very far from the previous transaction.";
};

const explainPurchaseRatio = (value) => {
  if (value < 1) {
    return "Lower than the user's usual purchase value.";
  }

  if (value < 3) {
    return "Similar to the user's usual purchase value.";
  }

  if (value < 10) {
    return "Higher than the user's usual purchase value.";
  }

  return "Much higher than the user's usual purchase value.";
};

function FraudCaseDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [fraudCase, setFraudCase] = useState(null);
  const [status, setStatus] = useState("NEW");
  const [comment, setComment] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const loadCase = async () => {
    try {
      setError("");
      const data = await getFraudCaseById(id);
      setFraudCase(data);
      setStatus(data.status);
      setComment(data.comment || "");
    } catch {
      setError("Failed to load fraud case.");
    } finally {
      setLoading(false);
    }
  };

  const saveCase = async (payload) => {
    try {
      setSaving(true);
      setError("");
      const updated = await updateFraudCase(id, payload);
      setFraudCase(updated);
      setStatus(updated.status);
      setComment(updated.comment || "");
    } catch {
      setError("Failed to update fraud case.");
    } finally {
      setSaving(false);
    }
  };

  const saveStatus = async () => {
    await saveCase({
      status,
      comment,
      cardBlocked: fraudCase.cardBlocked,
      transactionBlocked: fraudCase.transactionBlocked,
    });
  };

  const blockCard = async () => {
    await saveCase({
      status: "CONFIRMED_FRAUD",
      comment,
      cardBlocked: true,
      transactionBlocked: fraudCase.transactionBlocked,
    });
  };

  const blockTransaction = async () => {
    await saveCase({
      status: "CONFIRMED_FRAUD",
      comment,
      cardBlocked: fraudCase.cardBlocked,
      transactionBlocked: true,
    });
  };

  const markFalseAlert = async () => {
    await saveCase({
      status: "FALSE_ALERT",
      comment,
      cardBlocked: false,
      transactionBlocked: false,
    });
  };

  useEffect(() => {
    loadCase();
  }, [id]);

  if (loading) {
    return <Loading text="Loading fraud case..." />;
  }

  if (!fraudCase) {
    return (
      <div className="page">
        <ErrorMessage message={error || "Fraud case not found."} />
      </div>
    );
  }

  const transaction = fraudCase.transaction;

  return (
    <div className="page">
      <div className="page-header">
        <h2>Fraud Case #{fraudCase.id}</h2>

        <button className="secondary-button" onClick={() => navigate("/fraud-cases")}>
          Back
        </button>
      </div>

      <ErrorMessage message={error} />

      <section className="details-layout">
        <div className="panel">
          <div className="case-title-row">
            <h3>Case Information</h3>
            <StatusBadge status={fraudCase.status} />
          </div>

          <div className="details-grid">
            <span>Status</span>
            <strong>{fraudCase.status}</strong>

            <span>Card blocked</span>
            <strong>{yesNo(fraudCase.cardBlocked)}</strong>

            <span>Transaction blocked</span>
            <strong>{yesNo(fraudCase.transactionBlocked)}</strong>

            <span>Created at</span>
            <strong>{formatDateTime(fraudCase.createdAt)}</strong>

            <span>Updated at</span>
            <strong>{formatDateTime(fraudCase.updatedAt)}</strong>
          </div>

          <div className="edit-section">
            <label>
              Status
              <select
                value={status}
                onChange={(event) => setStatus(event.target.value)}
              >
                <option value="NEW">New</option>
                <option value="IN_REVIEW">In Review</option>
              </select>
            </label>

            <label>
              Comment
              <textarea
                value={comment}
                onChange={(event) => setComment(event.target.value)}
                rows="4"
              />
            </label>

            <button className="primary-button" onClick={saveStatus} disabled={saving}>
              Save
            </button>
          </div>
        </div>

        <div className="panel">
          <h3>Transaction Information</h3>

          <div className="details-grid">
            <span>Amount</span>
            <strong>{formatCurrency(transaction.amount)}</strong>

            <span>Fraud probability</span>
            <strong>{formatNumber(transaction.fraudProbability, 4)}</strong>

            <span>Distance from home</span>
            <strong>
              {formatNumber(transaction.distanceFromHome, 2)} —{" "}
              {explainDistanceFromHome(transaction.distanceFromHome)}
            </strong>

            <span>Distance from last transaction</span>
            <strong>
              {formatNumber(transaction.distanceFromLastTransaction, 2)} —{" "}
              {explainDistanceFromLastTransaction(
                transaction.distanceFromLastTransaction
              )}
            </strong>

            <span>Purchase price ratio</span>
            <strong>
              {formatNumber(transaction.ratioToMedianPurchasePrice, 2)} —{" "}
              {explainPurchaseRatio(transaction.ratioToMedianPurchasePrice)}
            </strong>

            <span>Repeat retailer</span>
            <strong>{yesNo(transaction.repeatRetailer)}</strong>

            <span>Used chip</span>
            <strong>{yesNo(transaction.usedChip)}</strong>

            <span>Used PIN</span>
            <strong>{yesNo(transaction.usedPinNumber)}</strong>

            <span>Online order</span>
            <strong>{yesNo(transaction.onlineOrder)}</strong>
          </div>
        </div>
      </section>

      <section className="actions-panel">
        <button className="danger-button" onClick={blockCard} disabled={saving}>
          Block Card
        </button>

        <button className="danger-button" onClick={blockTransaction} disabled={saving}>
          Block Transaction
        </button>

        <button className="warning-button" onClick={markFalseAlert} disabled={saving}>
          False Alert
        </button>
      </section>
    </div>
  );
}

export default FraudCaseDetailsPage;