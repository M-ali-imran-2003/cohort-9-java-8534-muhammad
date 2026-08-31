import { useState, useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import { login } from "../api/authApi.js";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/useAuth.js";
import { useToast } from "../context/useToast.js";

import "../styles/form.css";

function Login() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ mode: "onChange" });

  const [serverError, setServerError] = useState(() => {
    try {
      const authError = sessionStorage.getItem("authError");
      if (authError) {
        sessionStorage.removeItem("authError");
        return authError;
      }
    } catch {
      return "";
    }
    return "";
  });

  const navigate = useNavigate();
  const { checkAuth } = useAuth();
  const redirectTimerRef = useRef(null);
  const { showToast } = useToast();

  useEffect(() => {
    // eslint-disable-next-line no-useless-assignment
    let authError = "";
    try {
      authError = sessionStorage.getItem("authError") || "";
      if (authError) {
        sessionStorage.removeItem("authError");
      }
    } catch {
      authError = "";
    }
    if (authError) {
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setServerError(authError);
    }
  }, []);

  const onSubmit = async (data) => {
    setServerError("");
    try {
      const result = await login(data.identifier, data.password);
      await checkAuth();
      showToast(result.message, "success");
      redirectTimerRef.current = setTimeout(() => {
        navigate("/contacts");
      }, 800);
    } catch (err) {
      setServerError(err.message);
    }
  };

  useEffect(() => {
    return () => {
      if (redirectTimerRef.current) {
        clearTimeout(redirectTimerRef.current);
      }
    };
  }, []);

  return (
    <div className="form-container">
      <h2 className="form-title">Login</h2>

      {serverError && <p className="form-message-error">{serverError}</p>}

      {/* eslint-disable-next-line react-hooks/refs */}
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="form-field">
          <label htmlFor="identifier" className="form-label">
            Email or Phone
          </label>
          <input
            id="identifier"
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
            id="password"
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
