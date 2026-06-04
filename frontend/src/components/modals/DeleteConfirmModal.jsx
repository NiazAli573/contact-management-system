import React, { useState } from 'react';
import { contactApi } from '../../api/contactApi';

export default function DeleteConfirmModal({ contactId, onClose, onDeleted }) {
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const handleDelete = async () => {
    setLoading(true);
    setError('');
    try {
      await contactApi.delete(contactId);
      onDeleted();
    } catch {
      setError('Failed to delete the contact. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal" style={{ maxWidth: 420 }} role="dialog" aria-modal="true" aria-labelledby="delete-modal-title">
        <div className="modal-header">
          <span className="modal-title" id="delete-modal-title">🗑 Delete Contact</span>
          <button className="modal-close" onClick={onClose} id="delete-modal-close" aria-label="Close">✕</button>
        </div>

        <div style={{ textAlign: 'center', padding: '1.5rem 0' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>⚠</div>
          <h3 style={{ marginBottom: '0.5rem' }}>Are you sure?</h3>
          <p style={{ fontSize: '0.9rem' }}>
            This action cannot be undone. The contact and all associated
            emails and phone numbers will be permanently deleted.
          </p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        <div className="modal-footer">
          <button id="delete-cancel" className="btn btn-ghost" onClick={onClose} disabled={loading}>
            Cancel
          </button>
          <button id="delete-confirm" className="btn btn-danger" onClick={handleDelete} disabled={loading}>
            {loading ? <><span className="spinner" /> Deleting…</> : '🗑 Delete'}
          </button>
        </div>
      </div>
    </div>
  );
}
