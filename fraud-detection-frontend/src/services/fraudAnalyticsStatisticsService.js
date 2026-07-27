import api from "./api";

export const getFraudAnalyticsStatistics = async () => {
  const response = await api.get("/fraud-analytics-statistics");
  return response.data;
};