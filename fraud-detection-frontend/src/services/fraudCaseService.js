import api from "./api";

export const getFraudCases = async (status = "") => {
  const url = status ? `/fraud-cases?status=${status}` : "/fraud-cases";
  const response = await api.get(url);
  return response.data;
};

export const getFraudCaseById = async (id) => {
  const response = await api.get(`/fraud-cases/${id}`);
  return response.data;
};

export const updateFraudCase = async (id, data) => {
  const response = await api.put(`/fraud-cases/${id}`, data);
  return response.data;
};