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
          <h1 className="profile-page-title">My Profile</h1>

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
                  <h2 className="profile-name">{profile.firstName} {profile.lastName}</h2>
                  <span className="badge badge-teal">{profile.role}</span>
                </div>
              </div>

              {/* Info Grid */}
              <div className="profile-info-section">
                <h3 className="profile-section-title">Account Details</h3>
                <div className="profile-grid">
                  <ProfileField label="Email" value={profile.email} icon="✉" />
                  <ProfileField label="Phone" value={profile.phoneNumber || '—'} icon="📞" />
                  <ProfileField label="Member since" value={formatDate(profile.createdAt)} icon="📅" />
                  <ProfileField label="Total Contacts" value={profile.totalContacts} icon="👥" />
                </div>
              </div>

              {/* Actions */}
              <div className="profile-actions">
                <button id="btn-change-password" className="btn btn-pill"
                  onClick={() => setShowPwdModal(true)}>
                  Change Password
                </button>
                <button id="btn-logout" className="btn btn-danger-solid" onClick={handleLogout}>
                  Logout
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
  .profile-main { flex: 1; padding: var(--spacing-40) 0 var(--spacing-48); }
  .profile-page-title {
    font-size: var(--text-display);
    font-weight: var(--font-weight-semibold);
    letter-spacing: var(--tracking-display);
    margin-bottom: var(--spacing-24);
  }
  .profile-banner {
    display: flex; align-items: center; gap: var(--spacing-24); margin-bottom: var(--spacing-32);
    padding: var(--spacing-24); border: 1px solid var(--color-mist);
  }
  .profile-avatar {
    width: 80px; height: 80px; border-radius: 50%;
    background: var(--color-rausch-coral);
    display: flex; align-items: center; justify-content: center;
    font-size: var(--text-display); font-weight: var(--font-weight-semibold); color: var(--color-cloud);
    flex-shrink: 0; text-transform: uppercase;
  }
  .profile-name {
    font-size: var(--text-heading);
    font-weight: var(--font-weight-semibold);
    margin-bottom: 4px;
  }
  .profile-info-section { margin-bottom: var(--spacing-32); }
  .profile-section-title {
    margin-bottom: var(--spacing-16);
    color: var(--color-slate);
    font-weight: var(--font-weight-medium);
  }
  .profile-grid {
    display: grid; grid-template-columns: 1fr 1fr; gap: var(--spacing-16);
  }
  .profile-field {
    display: flex; align-items: flex-start; gap: var(--spacing-12);
    padding: var(--spacing-16); border-radius: var(--radius-cards);
    background: var(--color-cloud);
    border: 1px solid var(--color-mist);
    transition: border-color var(--transition);
  }
  .profile-field:hover { border-color: var(--color-stone); }
  .profile-field-icon { font-size: 1.2rem; filter: grayscale(1); opacity: 0.6; }
  .profile-field-label { font-size: var(--text-caption); color: var(--color-slate); text-transform: uppercase; font-weight: var(--font-weight-semibold); letter-spacing: 0.05em; }
  .profile-field-value { font-size: var(--text-body); font-weight: var(--font-weight-medium); color: var(--color-carbon); margin-top: 2px; }
  .profile-actions { display: flex; gap: var(--spacing-16); margin-top: var(--spacing-32); padding-top: var(--spacing-24); border-top: 1px solid var(--color-mist); }
  @media (max-width: 500px) {
    .profile-grid { grid-template-columns: 1fr; }
    .profile-banner { flex-direction: column; text-align: center; }
    .profile-actions { flex-direction: column; width: 100%; }
    .profile-actions .btn { width: 100%; justify-content: center; }
  }
`;
