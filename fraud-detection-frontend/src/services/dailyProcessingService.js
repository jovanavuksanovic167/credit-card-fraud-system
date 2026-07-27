import api from "./api";

export const processDailyTransactions = async (count = 1000) => {
  const response = await api.post(`/daily-processing?count=${count}`);
  return response.data;
};