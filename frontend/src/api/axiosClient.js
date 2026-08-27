import axios from "axios";
import { API_BASE_URL } from "./config";

const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const message =
        error.response.data?.message || "Session expired. Please log in again.";

      if (window.location.pathname !== "/login") {
        sessionStorage.setItem("authError", message);
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  },
);

export default axiosClient;
