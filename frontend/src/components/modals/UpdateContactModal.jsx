import React, { useState } from 'react';
import { contactApi } from '../../api/contactApi';
import ContactForm from './ContactForm';

export default function UpdateContactModal({ contact, onClose, onUpdated }) {
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const handleSubmit = async (data) => {
    setError('');
    setLoading(true);
    try {
      await contactApi.update(contact.id, data);
      onUpdated();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update contact.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="update-modal-title">
        <div className="modal-header">
          <span className="modal-title" id="update-modal-title">✏ Edit Contact</span>
          <button className="modal-close" onClick={onClose} id="update-modal-close" aria-label="Close">✕</button>
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        <ContactForm
          initial={contact}
          onSubmit={handleSubmit}
          onCancel={onClose}
          loading={loading}
          submitLabel="Save Changes"
          submitId="update-contact-submit"
        />
      </div>
    </div>
  );
}
