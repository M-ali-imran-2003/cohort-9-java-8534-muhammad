import { API_BASE_URL } from "./config.js";

export async function login(identifier, password) {
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ identifier, password }),
    });

    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || "Provided credentials are incorrect");
    }
    return data;
  } catch (error) {
    console.error("API Error:", error.message);
    throw error;
  }
}

export async function registerUser(name, email, phone, password) {
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, email, phone, password }),
    });

    if (!response.ok) {
      // Parse the custom ErrorResponse JSON object from Spring Boot
      const errorData = await response.json();

      const errorInstance = new Error(
        errorData.message || "Registration failed",
      );
      // Attach the Java Map<String, String> fieldErrors to our JS error
      errorInstance.fieldErrors = errorData.fieldErrors;
      throw errorInstance;
    }

    // Success text handler ("Registration Successful")
    return await response.text();
  } catch (error) {
    console.error("API Error:", error.message);
    throw error;
  }
}
