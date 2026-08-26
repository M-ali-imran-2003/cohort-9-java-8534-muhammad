import { useEffect, useState, useCallback, useRef } from "react";
import {
  getAllContacts,
  getContact,
  importContacts,
  exportContacts,
} from "../api/contactApi.js";
import Layout from "../components/Layout.jsx";
import ContactFormModal from "../components/ContactFormModal.jsx";
import ContactDetailModal from "../components/ContactDetailModal.jsx";
import DeleteConfirmModal from "../components/DeleteConfirmModal.jsx";
import "../styles/form.css";
import "../styles/contacts.css";

function Contacts() {
  const [contacts, setContacts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchInput, setSearchInput] = useState("");
  const [search, setSearch] = useState("");
  const [error, setError] = useState("");
  const [editError, setEditError] = useState("");
  const [formModal, setFormModal] = useState(null);
  const [viewingId, setViewingId] = useState(null);
  const [deletingContact, setDeletingContact] = useState(null);
  const [importError, setImportError] = useState("");
  const [importSuccess, setImportSuccess] = useState("");

  const requestIdRef = useRef(0);
  const fileInputRef = useRef(null);
  const editRequestIdRef = useRef(0);

  const loadContacts = useCallback(() => {
    const currentRequestId = ++requestIdRef.current;

    getAllContacts(page, search)
      .then((data) => {
        if (currentRequestId !== requestIdRef.current) return;

        if (data.totalPages > 0 && page >= data.totalPages) {
          setPage(data.totalPages - 1);
          return;
        }

        setContacts(data.content);
        setTotalPages(data.totalPages);
        setError("");
      })
      .catch((err) => {
        if (currentRequestId !== requestIdRef.current) return;
        setError(err.message);
      });
  }, [page, search]);

  useEffect(() => {
    loadContacts();
  }, [loadContacts]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    setSearch(searchInput);
  };

  const openEdit = async (contactId) => {
    const currentRequestId = ++editRequestIdRef.current;
    setEditError("");
    try {
      const data = await getContact(contactId);
      if (currentRequestId !== editRequestIdRef.current) return;
      setFormModal({ mode: "edit", data });
    } catch (err) {
      if (currentRequestId !== editRequestIdRef.current) return;
      setEditError(err.message);
    }
  };

  const closeFormModal = () => {
    editRequestIdRef.current++;
    setEditError("");
    setFormModal(null);
  };

  const openCreate = () => {
    editRequestIdRef.current++;
    setEditError("");
    setFormModal({ mode: "create" });
  };

  const handleSaved = () => {
    editRequestIdRef.current++;
    setFormModal(null);
    loadContacts();
  };

  const handleExport = async () => {
    try {
      await exportContacts();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleImportClick = () => {
    fileInputRef.current.click();
  };

  const handleFileSelected = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setImportError("");
    setImportSuccess("");

    try {
      const result = await importContacts(file);
      setImportSuccess(result.message || "Import successful");
      loadContacts();
    } catch (err) {
      setImportError(err.message);
    } finally {
      e.target.value = "";
    }
  };

  return (
    <Layout>
      <div className="contacts-header">
        <h2 className="form-title">Contacts</h2>
        <form className="contacts-search" onSubmit={handleSearchSubmit}>
          <input
            className="form-input"
            placeholder="Search by name, email, phone..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
          />
          <button type="submit" className="form-button">
            Search
          </button>
        </form>
        <div className="contacts-actions-group">
          <button
            type="button"
            className="form-button-secondary"
            onClick={handleExport}
          >
            Export CSV
          </button>
          <button
            type="button"
            className="form-button-secondary"
            onClick={handleImportClick}
          >
            Import CSV
          </button>
          <input
            type="file"
            accept=".csv"
            ref={fileInputRef}
            onChange={handleFileSelected}
            style={{ display: "none" }}
          />
          <button className="form-button" type="button" onClick={openCreate}>
            + Add Contact
          </button>
        </div>
      </div>

      {importError && <p className="form-message-error">{importError}</p>}
      {importSuccess && <p className="form-message-success">{importSuccess}</p>}
      {error && <p className="form-message-error">{error}</p>}
      {editError && <p className="form-message-error">{editError}</p>}
      {contacts.length === 0 ? (
        <div className="contacts-empty">No contacts found.</div>
      ) : (
        <table className="contacts-table">
          <thead>
            <tr>
              <th>Title</th>
              <th>Name</th>
              <th>Created At</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {contacts.map((c) => (
              <tr key={c.id}>
                <td>{c.title}</td>
                <td>
                  {c.firstName} {c.lastName}
                </td>
                <td> {new Date(c.createdAt).toLocaleDateString("en-GB")}</td>
                <td className="contact-actions">
                  <button
                    type="button"
                    className="contact-action-btn"
                    onClick={() => {
                      editRequestIdRef.current++;
                      setViewingId(c.id);
                    }}
                  >
                    View
                  </button>
                  <button
                    type="button"
                    className="contact-action-btn"
                    onClick={() => openEdit(c.id)}
                  >
                    Edit
                  </button>
                  <button
                    type="button"
                    className="contact-action-btn danger"
                    onClick={() => {
                      editRequestIdRef.current++;
                      setDeletingContact(c);
                    }}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button
            type="button"
            disabled={page === 0}
            onClick={() => setPage((p) => p - 1)}
          >
            Previous
          </button>
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <button
            type="button"
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </button>
        </div>
      )}

      {formModal && (
        <ContactFormModal
          mode={formModal.mode}
          initialData={formModal.data}
          onClose={closeFormModal}
          onSaved={handleSaved}
        />
      )}

      {viewingId && (
        <ContactDetailModal
          contactId={viewingId}
          onClose={() => setViewingId(null)}
        />
      )}

      {deletingContact && (
        <DeleteConfirmModal
          contact={deletingContact}
          onClose={() => setDeletingContact(null)}
          onDeleted={() => {
            setDeletingContact(null);
            loadContacts();
          }}
        />
      )}
    </Layout>
  );
}

export default Contacts;
