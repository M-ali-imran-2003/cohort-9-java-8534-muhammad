import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { registerUser } from "../api/authApi.js";
import { useNavigate } from "react-router-dom";
import "../styles/form.css";

function Register() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm({ mode: "onTouched" });

  const [serverError, setServerError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const onSubmit = async (data) => {
    setServerError("");
    setSuccess("");

    try {
      const result = await registerUser(
        data.name,
        data.email,
        data.phone,
        data.password,
      );
      setSuccess(result);
      reset();

      setTimeout(() => {
        navigate("/login");
      }, 2000);
    } catch (err) {
      if (err.fieldErrors) {
        const allMessages = Object.values(err.fieldErrors).join(", ");
        setServerError(allMessages);
      } else {
        setServerError(err.message || "An unexpected error occurred.");
      }
    }
  };

  return (
    <div className="form-container">
      <h2 className="form-title">Register</h2>

      {serverError && <p className="form-message-error">{serverError}</p>}
      {success && <p className="form-message-success">{success}</p>}

      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="form-field">
          <input
            className="form-input"
            type="text"
            placeholder="Name"
            {...register("name", { required: "Name is required" })}
          />
          {errors.name && (
            <p className="form-error-text">{errors.name.message}</p>
          )}
        </div>

        <div className="form-field">
          <input
            className="form-input"
            type="email"
            placeholder="Email Address"
            {...register("email", { required: "Email is required" })}
          />
          {errors.email && (
            <p className="form-error-text">{errors.email.message}</p>
          )}
        </div>

        <div className="form-field">
          <input
            className="form-input"
            type="text"
            placeholder="Phone Number"
            {...register("phone", { required: "Phone number is required" })}
          />
          {errors.phone && (
            <p className="form-error-text">{errors.phone.message}</p>
          )}
        </div>

        <div className="form-field">
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

        <button className="form-button" type="submit">
          Submit
        </button>
        <button
          type="button"
          onClick={() => navigate("/login")}
          className="form-button-secondary"
        >
          Back to Login
        </button>
      </form>
    </div>
  );
}

export default Register;
