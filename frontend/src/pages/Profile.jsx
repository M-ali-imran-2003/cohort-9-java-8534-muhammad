import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getProfile } from "../api/userApi.js";
import Layout from "../components/Layout.jsx";
import ChangePasswordModal from "../components/ChangePasswordModal.jsx";
import "../styles/form.css";
import "../styles/profile.css";

function Profile() {
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState("");
  const [showModal, setShowModal] = useState(false);
  const navigate = useNavigate();
  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  useEffect(() => {
    getProfile()
      .then((data) => setProfile(data))
      .catch((err) => setError(err.message));
  }, []);

  return (
    <Layout>
      <div className="profile-card">
        <h2 className="form-title">My Profile</h2>

        {error && <p className="form-message-error">{error}</p>}

        {profile && (
          <div className="profile-details">
            <div className="profile-field">
              <span className="profile-label">Name</span>
              <span className="profile-value">{profile.name}</span>
            </div>
            <div className="profile-field">
              <span className="profile-label">Email</span>
              <span className="profile-value">{profile.email}</span>
            </div>
            <div className="profile-field">
              <span className="profile-label">Phone</span>
              <span className="profile-value">{profile.phone}</span>
            </div>
            <div className="profile-field">
              <span className="profile-label">Joined</span>
              <span className="profile-value">
                {new Date(profile.joinedAt).toLocaleDateString()}
              </span>
            </div>
          </div>
        )}

        <button
          type="button"
          className="form-button"
          onClick={() => setShowModal(true)}
        >
          Change Password
        </button>
        <button type="button" className="logout-button" onClick={handleLogout}>
          Logout
        </button>
      </div>

      {showModal && <ChangePasswordModal onClose={() => setShowModal(false)} />}
    </Layout>
  );
}

export default Profile;
