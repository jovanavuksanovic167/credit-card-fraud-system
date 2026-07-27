import api from "./api";

export const getStatistics = async () => {
  const response = await api.get("/statistics");
  return response.data;
};