import axios from "axios";
import { API_BASE_URL } from "./config";

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status == 401) {
      const message = error.response.data?.message || "Please Login";
      localStorage.removeItem("token");
      sessionStorage.setItem("authError", message);
      window.location.href = "/login";
    }
    return Promise.reject(error);
  },
);

export default axiosClient;
