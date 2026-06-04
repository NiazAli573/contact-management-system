import React, { useState } from 'react';
import { contactApi } from '../../api/contactApi';
import ContactForm from './ContactForm';

export default function CreateContactModal({ onClose, onCreated }) {
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const handleSubmit = async (data) => {
    setError('');
    setLoading(true);
    try {
      await contactApi.create(data);
      onCreated();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create contact.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="create-modal-title">
        <div className="modal-header">
          <span className="modal-title" id="create-modal-title">➕ New Contact</span>
          <button className="modal-close" onClick={onClose} id="create-modal-close" aria-label="Close">✕</button>
        </div>
        {error && <div className="alert alert-error">{error}</div>}
        <ContactForm
          onSubmit={handleSubmit}
          onCancel={onClose}
          loading={loading}
          submitLabel="Create Contact"
          submitId="create-contact-submit"
        />
      </div>
    </div>
  );
}
