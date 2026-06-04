import React from 'react';

export default function ContactCard({ contact, onEdit, onDelete }) {
  const initials = `${contact.firstName?.[0] ?? ''}${contact.lastName?.[0] ?? ''}`.toUpperCase();
  const primaryEmail = contact.emails?.[0];
  const primaryPhone = contact.phones?.[0];
  const colors = ['#00d4aa','#60a5fa','#a78bfa','#fb923c','#f472b6','#34d399'];
  const color  = colors[(contact.id ?? 0) % colors.length];

  return (
    <div className="contact-card card">
      <style>{styles}</style>
      <div className="contact-card-top">
        <div className="contact-avatar" style={{ background: `linear-gradient(135deg, ${color}33, ${color}22)`, color }}>
          {initials}
        </div>
        <div className="contact-actions">
          <button
            id={`btn-edit-${contact.id}`}
            className="btn btn-ghost btn-icon btn-sm"
            onClick={onEdit}
            title="Edit contact"
          >✏</button>
          <button
            id={`btn-delete-${contact.id}`}
            className="btn btn-danger btn-icon btn-sm"
            onClick={onDelete}
            title="Delete contact"
          >🗑</button>
        </div>
      </div>

      <div className="contact-info">
        <h3 className="contact-name">{contact.firstName} {contact.lastName}</h3>
        {contact.title && <div className="contact-title">{contact.title}</div>}
        <div className="contact-details">
          {primaryEmail && (
            <div className="contact-detail-row">
              <span className="contact-detail-icon">✉</span>
              <span className="contact-detail-text">{primaryEmail.email}</span>
              <span className="badge badge-teal">{primaryEmail.label}</span>
            </div>
          )}
          {primaryPhone && (
            <div className="contact-detail-row">
              <span className="contact-detail-icon">📞</span>
              <span className="contact-detail-text">{primaryPhone.phoneNumber}</span>
              <span className="badge badge-blue">{primaryPhone.label}</span>
            </div>
          )}
          {!primaryEmail && !primaryPhone && (
            <div style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>No contact details added</div>
          )}
        </div>
        {(contact.emails?.length > 1 || contact.phones?.length > 1) && (
          <div className="contact-more">
            +{(contact.emails?.length - 1) + (contact.phones?.length - 1)} more
          </div>
        )}
      </div>
    </div>
  );
}

const styles = `
  .contact-card {
    display: flex; flex-direction: column; gap: 0.75rem;
    cursor: default; transition: all var(--transition); padding: 1.25rem;
  }
  .contact-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-lg); }
  .contact-card-top { display: flex; align-items: flex-start; justify-content: space-between; }
  .contact-avatar {
    width: 48px; height: 48px; border-radius: 50%;
    display: flex; align-items: center; justify-content: center;
    font-size: 1rem; font-weight: 700; border: 1px solid currentColor;
  }
  .contact-actions { display: flex; gap: 0.35rem; }
  .contact-info { display: flex; flex-direction: column; gap: 0.4rem; }
  .contact-name { font-size: 1rem; font-weight: 600; color: var(--text-primary); }
  .contact-title { font-size: 0.8rem; color: var(--text-muted); font-weight: 500; }
  .contact-details { display: flex; flex-direction: column; gap: 0.35rem; margin-top: 0.25rem; }
  .contact-detail-row {
    display: flex; align-items: center; gap: 0.5rem;
    font-size: 0.82rem; overflow: hidden;
  }
  .contact-detail-icon { flex-shrink: 0; font-size: 0.9rem; }
  .contact-detail-text {
    color: var(--text-secondary); flex: 1; white-space: nowrap;
    overflow: hidden; text-overflow: ellipsis;
  }
  .contact-more {
    font-size: 0.75rem; color: var(--accent); font-weight: 500; margin-top: 0.1rem;
  }
`;
