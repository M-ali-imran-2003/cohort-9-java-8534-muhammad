import { useForm, useWatch } from "react-hook-form";
import { useState } from "react";
import { changePassword } from "../api/userApi.js";
import "../styles/modal.css";
import "../styles/form.css";

function ChangePasswordModal({ onClose }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = useForm({ mode: "onTouched" });

  const [serverError, setServerError] = useState("");
  const [success, setSuccess] = useState("");

  const newPassword = useWatch({
    control,
    name: "newPassword",
    defaultValue: "",
  });

  const onSubmit = async (data) => {
    setServerError("");
    setSuccess("");
    try {
      const result = await changePassword(
        data.currentPassword,
        data.newPassword,
      );
      setSuccess(result.message);
      reset();
      setTimeout(() => {
        onClose();
      }, 1500);
    } catch (err) {
      setServerError(err.message || "Failed to change password");
    }
  };

  const handleCancel = () => {
    reset();
    onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h2 className="form-title">Change Password</h2>

        {serverError && <p className="form-message-error">{serverError}</p>}
        {success && <p className="form-message-success">{success}</p>}

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-field">
            <label htmlFor="currentPassword" className="form-label">
              Current Password
            </label>
            <input
              id="currentPassword"
              className="form-input"
              type="password"
              {...register("currentPassword", {
                required: "Current password is required",
              })}
            />
            {errors.currentPassword && (
              <p className="form-error-text">
                {errors.currentPassword.message}
              </p>
            )}
          </div>

          <div className="form-field">
            <label htmlFor="newPassword" className="form-label">
              New Password
            </label>
            <input
              id="newPassword"
              className="form-input"
              type="password"
              {...register("newPassword", {
                required: "New password is required",
                minLength: {
                  value: 8,
                  message: "Must be at least 8 characters",
                },
                maxLength: {
                  value: 15,
                  message: "Must be under 15 characters",
                },
              })}
            />
            {errors.newPassword && (
              <p className="form-error-text">{errors.newPassword.message}</p>
            )}
          </div>

          <div className="form-field">
            <label htmlFor="confirmPassword" className="form-label">
              Confirm New Password
            </label>
            <input
              id="confirmPassword"
              className="form-input"
              type="password"
              {...register("confirmPassword", {
                required: "Please confirm your new password",
                validate: (value) =>
                  value === newPassword || "Passwords do not match",
              })}
            />
            {errors.confirmPassword && (
              <p className="form-error-text">
                {errors.confirmPassword.message}
              </p>
            )}
          </div>

          <div className="modal-actions">
            <button type="submit" className="form-button">
              Reset
            </button>
            <button
              type="button"
              className="form-button-secondary"
              onClick={handleCancel}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ChangePasswordModal;
