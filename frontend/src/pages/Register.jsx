import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { registerUser } from "../api/authApi.js";
import { useNavigate } from "react-router-dom";

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
    <div
      style={{ maxWidth: "350px", margin: "40px auto", textAlign: "center" }}
    >
      <h2>Register</h2>

      {serverError && (
        <p style={{ color: "red", fontWeight: "bold" }}>{serverError}</p>
      )}
      {success && (
        <p style={{ color: "green", fontWeight: "bold" }}>{success}</p>
      )}

      <form onSubmit={handleSubmit(onSubmit)}>
        <div style={{ marginBottom: "10px" }}>
          <input
            type="text"
            placeholder="Name"
            {...register("name", { required: "Name is required" })}
          />
          {errors.name && (
            <p style={{ color: "red", fontSize: "12px" }}>
              {errors.name.message}
            </p>
          )}
        </div>

        <div style={{ marginBottom: "10px" }}>
          <input
            type="email"
            placeholder="Email Address"
            {...register("email", { required: "Email is required" })}
          />
          {errors.email && (
            <p style={{ color: "red", fontSize: "12px" }}>
              {errors.email.message}
            </p>
          )}
        </div>

        <div style={{ marginBottom: "10px" }}>
          <input
            type="text"
            placeholder="Phone Number"
            {...register("phone", { required: "Phone number is required" })}
          />
          {errors.phone && (
            <p style={{ color: "red", fontSize: "12px" }}>
              {errors.phone.message}
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

        <button type="submit">Submit</button>
        <button
          type="button"
          onClick={() => navigate("/login")}
          style={{ marginLeft: "10px" }}
        >
          Back to Login
        </button>
      </form>
    </div>
  );
}

export default Register;
