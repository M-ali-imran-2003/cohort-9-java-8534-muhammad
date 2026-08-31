import { useState } from "react";
import { deleteContact } from "../api/contactApi.js";
import { useToast } from "../context/useToast.js";

import "../styles/form.css";
import "../styles/modal.css";

function DeleteConfirmModal({ contact, onClose, onDeleted }) {
  const [error, setError] = useState("");
  const { showToast } = useToast();

  const handleConfirm = async () => {
    try {
      const result = await deleteContact(contact.id);
      showToast(result.message, "success");
      onDeleted();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h2 className="form-title">Delete Contact</h2>

        {error && <p className="form-message-error">{error}</p>}

        <p>
          Are you sure you want to delete{" "}
          <strong>
            {contact.firstName} {contact.lastName}
          </strong>
          ?
        </p>

        <div className="modal-actions">
          <button type="button" className="form-button" onClick={handleConfirm}>
            Confirm
          </button>
          <button
            type="button"
            className="form-button-secondary"
            onClick={onClose}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}

export default DeleteConfirmModal;
