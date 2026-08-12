import { API_BASE_URL } from "./config.js";

export async function login(identifier, password) {
  const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ identifier, password }),
  });

  const data = await response.json();

  if (!response.ok) {
    const error = new Error(
      data.message || "Provided credentials are incorrect",
    );
    if (data.fieldErrors) error.fieldErrors = data.fieldErrors;
    throw error;
  }

  return data;
}

export async function registerUser(name, email, phone, password) {
  const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, phone, password }),
  });

  const data = await response.json();

  if (!response.ok) {
    const error = new Error(data.message || "Registration failed");
    if (data.fieldErrors) error.fieldErrors = data.fieldErrors;
    throw error;
  }

  return data;
}
