import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { login } from "../api/authApi.js";
import { useNavigate } from "react-router-dom";
import "../styles/form.css";

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

      setSuccess("Login Successful!");
    } catch (err) {
      setServerError(err.message || "Login failed");
    }
  };

  return (
    <div className="form-container">
      <h2 className="form-title">Login</h2>

      {serverError && <p className="form-message-error">{serverError}</p>}
      {success && <p className="form-message-success">{success}</p>}

      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="form-field">
          <label htmlFor="identifier" className="form-label">
            Email or Phone
          </label>
          <input
            className="form-input"
            type="text"
            placeholder="Email or Phone"
            {...register("identifier", {
              required: "Email or Phone is required",
            })}
          />
          {errors.identifier && (
            <p className="form-error-text">{errors.identifier.message}</p>
          )}
        </div>

        <div className="form-field">
          <label htmlFor="password" className="form-label">
            Password
          </label>
          <input
            className="form-input"
            type="password"
            placeholder="Password"
            {...register("password", { required: "Password is required" })}
          />
          {errors.password && (
            <p className="form-error-text">{errors.password.message}</p>
          )}
        </div>

        <button type="submit" className="form-button">
          Login
        </button>
        <button
          className="form-button-secondary"
          type="button"
          onClick={() => navigate("/register")}
        >
          Go to Register
        </button>
      </form>
    </div>
  );
}

export default Login;
