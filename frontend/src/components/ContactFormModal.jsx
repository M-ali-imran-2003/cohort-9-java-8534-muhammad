import { useEffect, useState } from "react";
import { useForm, useFieldArray } from "react-hook-form";
import { addContact, updateContact } from "../api/contactApi.js";
import "../styles/form.css";
import "../styles/modal.css";
import "../styles/contacts.css";

function ContactFormModal({ mode, initialData, onClose, onSaved }) {
  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    mode: "onTouched",
    defaultValues: {
      title: "",
      firstName: "",
      lastName: "",
      emails: [{ label: "", email: "" }],
      phones: [{ label: "", phone: "" }],
    },
  });

  const emailFields = useFieldArray({ control, name: "emails" });
  const phoneFields = useFieldArray({ control, name: "phones" });

  const [serverError, setServerError] = useState("");

  useEffect(() => {
    if (mode === "edit" && initialData) {
      reset({
        title: initialData.title,
        firstName: initialData.firstName,
        lastName: initialData.lastName,
        emails: initialData.emails.length
          ? initialData.emails
          : [{ label: "", email: "" }],
        phones: initialData.phones.length
          ? initialData.phones
          : [{ label: "", phone: "" }],
      });
    }
  }, [mode, initialData, reset]);

  const onSubmit = async (data) => {
    setServerError("");
    try {
      if (mode === "create") {
        await addContact(data);
      } else {
        await updateContact(initialData.id, data);
      }
      onSaved();
    } catch (err) {
      if (err.fieldErrors) {
        setServerError(Object.values(err.fieldErrors).join(", "));
      } else {
        setServerError(err.message);
      }
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-box modal-box-wide">
        <h2 className="form-title">
          {mode === "create" ? "Add Contact" : "Edit Contact"}
        </h2>

        {serverError && <p className="form-message-error">{serverError}</p>}

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-field">
            <label htmlFor="title" className="form-label">
              Title
            </label>
            <input
              id="title"
              className="form-input"
              {...register("title", { required: "Title is required" })}
            />
            {errors.title && (
              <p className="form-error-text">{errors.title.message}</p>
            )}
          </div>

          <div className="form-field">
            <label htmlFor="firstName" className="form-label">
              First Name
            </label>
            <input
              id="firstName"
              className="form-input"
              {...register("firstName", { required: "First name is required" })}
            />
            {errors.firstName && (
              <p className="form-error-text">{errors.firstName.message}</p>
            )}
          </div>

          <div className="form-field">
            <label htmlFor="lastName" className="form-label">
              Last Name
            </label>
            <input
              id="lastName"
              className="form-input"
              {...register("lastName", { required: "Last name is required" })}
            />
            {errors.lastName && (
              <p className="form-error-text">{errors.lastName.message}</p>
            )}
          </div>

          <label className="form-label">Emails</label>
          {emailFields.fields.map((field, index) => (
            <div className="field-array-row" key={field.id}>
              <input
                className="form-input"
                placeholder="Label (Work, Personal...)"
                {...register(`emails.${index}.label`, {
                  required: "Label required",
                })}
              />
              <input
                className="form-input"
                placeholder="Email address"
                {...register(`emails.${index}.email`, {
                  required: "Email required",
                })}
              />
              <button
                type="button"
                className="field-array-remove"
                onClick={() => emailFields.remove(index)}
                disabled={emailFields.fields.length === 1}
              >
                Remove
              </button>
            </div>
          ))}
          <button
            type="button"
            className="field-array-add"
            onClick={() => emailFields.append({ label: "", email: "" })}
          >
            + Add Email
          </button>

          <label className="form-label">Phones</label>
          {phoneFields.fields.map((field, index) => (
            <div className="field-array-row" key={field.id}>
              <input
                className="form-input"
                placeholder="Label (Work, Home...)"
                {...register(`phones.${index}.label`, {
                  required: "Label required",
                })}
              />
              <input
                className="form-input"
                placeholder="Phone number"
                {...register(`phones.${index}.phone`, {
                  required: "Phone required",
                })}
              />
              <button
                type="button"
                className="field-array-remove"
                onClick={() => phoneFields.remove(index)}
                disabled={phoneFields.fields.length === 1}
              >
                Remove
              </button>
            </div>
          ))}
          <button
            type="button"
            className="field-array-add"
            onClick={() => phoneFields.append({ label: "", phone: "" })}
          >
            + Add Phone
          </button>

          <div className="modal-actions">
            <button type="submit" className="form-button">
              Save
            </button>
            <button
              type="button"
              className="form-button-secondary"
              onClick={() => reset()}
            >
              Reset
            </button>
            <button
              type="button"
              className="form-button-secondary"
              onClick={onClose}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ContactFormModal;
