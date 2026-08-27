import { useForm, useWatch } from "react-hook-form";
import { useState, useRef, useEffect } from "react";
import { changePassword } from "../api/userApi.js";
import { useToast } from "../context/useToast.js";

import "../styles/modal.css";
import "../styles/form.css";

function ChangePasswordModal({ onClose }) {
  const modalRef = useRef(null);
  const { showToast } = useToast();

  const previousFocusRef = useRef(null);
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    control,
  } = useForm({ mode: "onChange" });

  const [serverError, setServerError] = useState("");

  const newPassword = useWatch({
    control,
    name: "newPassword",
    defaultValue: "",
  });

  const handleCancel = () => {
    reset();
    onClose();
  };

  useEffect(() => {
    previousFocusRef.current = document.activeElement;
    modalRef.current?.focus();

    const handleKeyDown = (e) => {
      if (e.key === "Escape") {
        handleCancel();
        return;
      }
      if (e.key === "Tab") {
        const focusable = modalRef.current.querySelectorAll(
          "input, button, [tabindex]:not([tabindex='-1'])",
        );
        const first = focusable[0];
        const last = focusable[focusable.length - 1];

        if (e.shiftKey && document.activeElement === first) {
          e.preventDefault();
          last.focus();
        } else if (!e.shiftKey && document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => {
      document.removeEventListener("keydown", handleKeyDown);
      previousFocusRef.current?.focus();
    };
  }, []);

  const onSubmit = async (data) => {
    setServerError("");
    try {
      const result = await changePassword(
        data.currentPassword,
        data.newPassword,
      );
      showToast(result.message, "success");
      reset();
      setTimeout(() => {
        onClose();
      }, 1500);
    } catch (err) {
      setServerError(err.message || "Failed to change password");
    }
  };

  return (
    <div className="modal-overlay">
      <div
        className="modal-box"
        role="dialog"
        aria-modal="true"
        aria-labelledby="change-password-title"
        tabIndex={-1}
        ref={modalRef}
      >
        <h2 className="form-title">Change Password</h2>

        {serverError && <p className="form-message-error">{serverError}</p>}

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
