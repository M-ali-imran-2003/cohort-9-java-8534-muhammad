import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { login } from "../api/authApi.js";
import { useNavigate } from "react-router-dom";

function Login() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ mode: "onTouched" });

  const [serverError, setServerError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const onSubmit = async (data) => {
    setServerError("");
    setSuccess("");

    try {
      const result = await login(data.identifier, data.password);
      localStorage.setItem("token", result.token);

      setSuccess("Login Successful! Redirecting...");
      console.log("Login success:", result);
    } catch (err) {
      setServerError(err.message || "Login failed");
    }
  };

  return (
    <div
      style={{ maxWidth: "350px", margin: "40px auto", textAlign: "center" }}
    >
      <h2>Login</h2>

      {serverError && <p style={{ color: "red" }}>{serverError}</p>}
      {success && <p style={{ color: "green" }}>{success}</p>}

      <form onSubmit={handleSubmit(onSubmit)}>
        <div style={{ marginBottom: "10px" }}>
          <input
            type="text"
            placeholder="Email or Phone"
            {...register("identifier", {
              required: "Email or Phone is required",
            })}
          />
          {errors.identifier && (
            <p style={{ color: "red", fontSize: "12px" }}>
              {errors.identifier.message}
            </p>
          )}
        </div>

        <div style={{ marginBottom: "10px" }}>
          <input
            type="password"
            placeholder="Password"
            {...register("password", { required: "Password is required" })}
          />
          {errors.password && (
            <p style={{ color: "red", fontSize: "12px" }}>
              {errors.password.message}
            </p>
          )}
        </div>

        <button type="submit">Login</button>
        <button
          type="button"
          onClick={() => navigate("/register")}
          style={{ marginLeft: "10px" }}
        >
          Go to Register
        </button>
      </form>
    </div>
  );
}

export default Login;
