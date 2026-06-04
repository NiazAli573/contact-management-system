import React, { useState } from 'react';
import { authApi } from '../../api/authApi';

export default function ChangePasswordModal({ onClose }) {
  const [form, setForm]       = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');
  const [success, setSuccess] = useState(false);

  const handleChange = e =>
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (form.newPassword !== form.confirmPassword) {
      setError('New passwords do not match.');
      return;
    }
    if (form.newPassword.length < 6) {
      setError('New password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    try {
      await authApi.changePassword({
        currentPassword: form.currentPassword,
        newPassword: form.newPassword,
        confirmPassword: form.confirmPassword,
      });
      setSuccess(true);
      setTimeout(onClose, 1500);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal" style={{ maxWidth: 420 }} role="dialog" aria-modal="true" aria-labelledby="pwd-modal-title">
        <div className="modal-header">
          <span className="modal-title" id="pwd-modal-title">🔐 Change Password</span>
          <button className="modal-close" onClick={onClose} id="pwd-modal-close" aria-label="Close">✕</button>
        </div>

        {success ? (
          <div className="alert alert-success" style={{ textAlign: 'center', padding: '1.5rem' }}>
            ✅ Password changed successfully!
          </div>
        ) : (
          <form onSubmit={handleSubmit} noValidate style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {error && <div className="alert alert-error">{error}</div>}

            <div className="form-group">
              <label className="form-label" htmlFor="pwd-current">Current Password</label>
              <input id="pwd-current" className="form-input" type="password" name="currentPassword"
                placeholder="••••••••" value={form.currentPassword} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="pwd-new">New Password</label>
              <input id="pwd-new" className="form-input" type="password" name="newPassword"
                placeholder="••••••••" value={form.newPassword} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="pwd-confirm">Confirm New Password</label>
              <input id="pwd-confirm" className="form-input" type="password" name="confirmPassword"
                placeholder="••••••••" value={form.confirmPassword} onChange={handleChange} required />
            </div>

            <div className="modal-footer" style={{ paddingTop: 0 }}>
              <button id="pwd-cancel" type="button" className="btn btn-ghost" onClick={onClose} disabled={loading}>
                Cancel
              </button>
              <button id="pwd-submit" type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? <><span className="spinner" /> Saving…</> : 'Reset Password'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
