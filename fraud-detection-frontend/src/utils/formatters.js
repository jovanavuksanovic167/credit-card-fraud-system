export const formatNumber = (value, digits = 2) => {
  if (value === null || value === undefined) return "-";
  return Number(value).toFixed(digits);
};

export const formatCurrency = (value) => {
  if (value === null || value === undefined) return "-";
  return `${Number(value).toFixed(2)} €`;
};

export const formatPercent = (value) => {
  if (value === null || value === undefined) return "-";
  return `${(Number(value) * 100).toFixed(2)}%`;
};

export const formatDateTime = (value) => {
  if (!value) return "-";

  return new Date(value).toLocaleString("sr-RS", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};