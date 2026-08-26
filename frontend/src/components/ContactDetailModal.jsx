import { useEffect, useState } from "react";
import { getContact } from "../api/contactApi.js";
import "../styles/form.css";
import "../styles/modal.css";
import "../styles/contacts.css";

function ContactDetailModal({ contactId, onClose }) {
  const [contact, setContact] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    getContact(contactId)
      .then(setContact)
      .catch((err) => setError(err.message));
  }, [contactId]);

  return (
    <div className="modal-overlay">
      <div className="modal-box modal-box-wide">
        <h2 className="form-title">Contact Details</h2>

        {error && <p className="form-message-error">{error}</p>}

        {contact && (
          <>
            <div className="view-field">
              <span className="view-label">Title</span>
              <span className="view-value">{contact.title}</span>
            </div>
            <div className="view-field">
              <span className="view-label">Name</span>
              <span className="view-value">
                {contact.firstName} {contact.lastName}
              </span>
            </div>
            <div className="view-field">
              <span className="view-label">Created At</span>
              <span className="view-value">
                {new Date(contact.createdAt).toLocaleDateString("en-GB")}
              </span>
            </div>
            <div className="view-field">
              <span className="view-label">Emails</span>
              {contact.emails.map((e, i) => (
                <div className="view-value" key={i}>
                  {e.label}: {e.email}
                </div>
              ))}
            </div>
            <div className="view-field">
              <span className="view-label">Phones</span>
              {contact.phones.map((p, i) => (
                <div className="view-value" key={i}>
                  {p.label}: {p.phone}
                </div>
              ))}
            </div>
          </>
        )}

        <div className="modal-actions">
          <button
            type="button"
            className="form-button-secondary"
            onClick={onClose}
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}

export default ContactDetailModal;
