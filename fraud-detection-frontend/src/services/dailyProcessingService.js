import api from "./api";

export const processDailyTransactions = async () => {
  const response = await api.post("/daily-processing");
  return response.data;
};
