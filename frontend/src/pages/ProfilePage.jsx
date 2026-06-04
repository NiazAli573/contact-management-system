import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { userApi } from '../api/userApi';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import ChangePasswordModal from '../components/modals/ChangePasswordModal';

export default function ProfilePage() {
  const navigate = useNavigate();
  const { logout } = useAuth();
  const [profile, setProfile]         = useState(null);
  const [loading, setLoading]         = useState(true);
  const [error, setError]             = useState('');
  const [showPwdModal, setShowPwdModal] = useState(false);

  useEffect(() => {
    userApi.getMe()
      .then(res => setProfile(res.data))
      .catch(() => setError('Failed to load profile.'))
      .finally(() => setLoading(false));
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="page-wrapper">
      <style>{styles}</style>
      <Navbar />
      <main className="profile-main">
        <div className="container" style={{ maxWidth: 680 }}>
          <h1 style={{ marginBottom: '1.5rem' }}>My Profile</h1>

          {error && <div className="alert alert-error">{error}</div>}

          {loading ? (
            <div className="card" style={{ padding: '3rem', textAlign: 'center' }}>
              <span className="spinner" style={{ width: 32, height: 32, borderWidth: 3 }} />
            </div>
          ) : profile && (
            <>
              {/* Avatar + Name Banner */}
              <div className="card profile-banner">
                <div className="profile-avatar">
                  {profile.firstName?.[0]}{profile.lastName?.[0]}
                </div>
                <div>
                  <h2>{profile.firstName} {profile.lastName}</h2>
                  <span className="badge badge-teal">{profile.role}</span>
                </div>
              </div>

              {/* Info Grid */}
              <div className="card profile-info">
                <h3 style={{ marginBottom: '1.25rem', color: 'var(--text-secondary)' }}>Account Details</h3>
                <div className="profile-grid">
                  <ProfileField label="Email" value={profile.email} icon="✉" />
                  <ProfileField label="Phone" value={profile.phoneNumber || '—'} icon="📞" />
                  <ProfileField label="Member since" value={formatDate(profile.createdAt)} icon="📅" />
                  <ProfileField label="Total Contacts" value={profile.totalContacts} icon="👥" />
                </div>
              </div>

              {/* Actions */}
              <div className="profile-actions">
                <button id="btn-change-password" className="btn btn-ghost"
                  onClick={() => setShowPwdModal(true)}>
                  🔐 Change Password
                </button>
                <button id="btn-logout" className="btn btn-danger" onClick={handleLogout}>
                  ⬡ Logout
                </button>
              </div>
            </>
          )}
        </div>
      </main>

      {showPwdModal && (
        <ChangePasswordModal onClose={() => setShowPwdModal(false)} />
      )}
    </div>
  );
}

function ProfileField({ label, value, icon }) {
  return (
    <div className="profile-field">
      <div className="profile-field-icon">{icon}</div>
      <div>
        <div className="profile-field-label">{label}</div>
        <div className="profile-field-value">{value}</div>
      </div>
    </div>
  );
}

function formatDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
}

const styles = `
  .profile-main { flex: 1; padding: 2rem 0 4rem; }
  .profile-banner {
    display: flex; align-items: center; gap: 1.5rem; margin-bottom: 1rem;
  }
  .profile-avatar {
    width: 72px; height: 72px; border-radius: 50%;
    background: linear-gradient(135deg, var(--accent), #60a5fa);
    display: flex; align-items: center; justify-content: center;
    font-size: 1.5rem; font-weight: 700; color: #0a0f1e;
    flex-shrink: 0; text-transform: uppercase;
    box-shadow: var(--shadow-accent);
  }
  .profile-info { margin-bottom: 1rem; }
  .profile-grid {
    display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;
  }
  .profile-field {
    display: flex; align-items: flex-start; gap: 0.75rem;
    padding: 0.75rem; border-radius: var(--radius-sm);
    background: var(--bg-glass-light);
    border: 1px solid var(--border);
    transition: border-color var(--transition);
  }
  .profile-field:hover { border-color: var(--border-accent); }
  .profile-field-icon { font-size: 1.2rem; }
  .profile-field-label { font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; }
  .profile-field-value { font-size: 0.95rem; font-weight: 500; color: var(--text-primary); margin-top: 0.15rem; }
  .profile-actions { display: flex; gap: 0.75rem; margin-top: 1rem; }
  @media (max-width: 500px) {
    .profile-grid { grid-template-columns: 1fr; }
    .profile-banner { flex-direction: column; text-align: center; }
    .profile-actions { flex-direction: column; }
  }
`;
