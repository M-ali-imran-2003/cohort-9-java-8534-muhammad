import { API_BASE_URL } from "./config.js";

async function parseResponseBody(response) {
  const text = await response.text();
  if (!text) {
    return {};
  }
  try {
    return JSON.parse(text);
  } catch {
    return {};
  }
}

export async function login(identifier, password) {
  const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ identifier, password }),
  });

  const data = await parseResponseBody(response);

  if (!response.ok) {
    throw new Error(data.message || "Provided credentials are incorrect");
  }

  return data;
}

export async function registerUser(name, email, phone, password) {
  const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, phone, password }),
  });

  if (!response.ok) {
    const errorData = await parseResponseBody(response);

    const errorInstance = new Error(errorData.message || "Registration failed");
    errorInstance.fieldErrors = errorData.fieldErrors;
    throw errorInstance;
  }

  return await response.text();
}
