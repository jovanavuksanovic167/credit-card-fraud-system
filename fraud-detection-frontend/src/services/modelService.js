import axios from "axios";

const aiApi = axios.create({
  baseURL: "http://localhost:8000",
});

export const getAiStatus = async () => {
  const response = await aiApi.get("/status");
  return response.data;
};

export const retrainModel = async () => {
  const response = await aiApi.post("/retrain-model");
  return response.data;
};