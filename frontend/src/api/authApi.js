import axiosClient from "./axiosClient.js";

export async function login(identifier, password) {
  try {
    const response = await axiosClient.post("/api/auth/login", {
      identifier,
      password,
    });
    return response.data;
  } catch (error) {
    const message = error.response?.data?.message || "Login Failed";
    throw new Error(message, { cause: error });
  }
}

export async function registerUser(name, email, phone, password) {
  try {
    const response = await axiosClient.post("/api/auth/register", {
      name,
      email,
      phone,
      password,
    });
    return response.data;
  } catch (error) {
    const data = error.response?.data || {};
    const err = new Error(data.message || "Registration failed");
    err.fieldErrors = data.fieldErrors;
    throw err;
  }
}

export async function logout() {
  try {
    await axiosClient.post("/api/auth/logout");
  } catch {
    // even if the request fails, proceed with client-side cleanup
  }
}
