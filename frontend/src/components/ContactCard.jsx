import React from 'react';

export default function ContactCard({ contact, onEdit, onDelete }) {
  const initials = `${contact.firstName?.[0] ?? ''}${contact.lastName?.[0] ?? ''}`.toUpperCase();
  const primaryEmail = contact.emails?.[0];
  const primaryPhone = contact.phones?.[0];
  const titleStr = contact.title || 'Contact';

  // Use a softer color palette for the Airbnb theme placeholder
  const colors = ['#fde0e3','#e0f2fe','#fae8ff','#ffedd5','#fce7f3','#d1fae5'];
  const textColors = ['#e00b41','#0284c7','#c026d3','#ea580c','#db2777','#059669'];
  const colorIndex = (contact.id ?? 0) % colors.length;
  const bgColor = colors[colorIndex];
  const textColor = textColors[colorIndex];

  return (
    <div className="contact-card card">
      <style>{styles}</style>
      
      {/* 1:1 Image Placeholder */}
      <div className="contact-card-image" style={{ backgroundColor: bgColor, color: textColor }}>
        {/* Guest Favorite Badge equivalent */}
        <div className="contact-badge">Contact</div>
        
        {/* Actions (Carousel navigation buttons mapped to Edit/Delete) */}
        <div className="contact-actions">
          <button
            id={`btn-edit-${contact.id}`}
            className="btn-icon btn-icon-elevated"
            onClick={onEdit}
            title="Edit contact"
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path>
            </svg>
          </button>
          <button
            id={`btn-delete-${contact.id}`}
            className="btn-icon btn-icon-elevated btn-action-delete"
            onClick={onDelete}
            title="Delete contact"
          >
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
            </svg>
          </button>
        </div>

        <div className="contact-initials">{initials}</div>
      </div>

      {/* Info Section */}
      <div className="contact-info">
        <div className="contact-header">
          <h3 className="contact-name">{contact.firstName} {contact.lastName}</h3>
          <div className="contact-rating">
            <svg width="10" height="10" viewBox="0 0 24 24" fill="currentColor" stroke="none">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
            <span>5.0</span>
          </div>
        </div>
        
        <div className="contact-metadata">{titleStr}</div>
        
        <div className="contact-details">
          {primaryPhone && <div className="contact-price">{primaryPhone.phoneNumber}</div>}
          {!primaryPhone && primaryEmail && <div className="contact-price">{primaryEmail.email}</div>}
          {!primaryPhone && !primaryEmail && <div className="contact-price" style={{color: 'var(--color-slate)'}}>No details</div>}
        </div>
      </div>
    </div>
  );
}

const styles = `
  .contact-card {
    display: flex; 
    flex-direction: column; 
    cursor: pointer;
    overflow: hidden;
    /* padding removed because Airbnb cards have full-bleed images */
  }
  .contact-card:hover .contact-actions {
    opacity: 1;
  }
  .contact-card-image {
    width: 100%;
    aspect-ratio: 1 / 1;
    border-radius: var(--radius-cards);
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--color-pebble); /* fallback */
    overflow: hidden;
  }
  .contact-badge {
    position: absolute;
    top: 12px;
    left: 12px;
    background: var(--color-cloud);
    border-radius: var(--radius-badges);
    padding: 6px 10px;
    font-size: var(--text-caption);
    font-weight: var(--font-weight-semibold);
    color: var(--color-carbon);
    box-shadow: var(--shadow-badge);
    letter-spacing: 0.04em;
    z-index: 2;
  }
  .contact-actions {
    position: absolute;
    top: 12px;
    right: 12px;
    display: flex;
    gap: 8px;
    opacity: 0;
    transition: opacity var(--transition);
    z-index: 2;
  }
  .btn-action-delete:hover {
    color: var(--color-rausch-coral);
  }
  .contact-initials {
    font-size: 3rem;
    font-weight: var(--font-weight-bold);
    letter-spacing: -0.02em;
    opacity: 0.8;
  }
  .contact-info {
    padding: var(--spacing-12) 0;
    display: flex;
    flex-direction: column;
    gap: 2px;
  }
  .contact-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }
  .contact-name {
    font-size: var(--text-body);
    font-weight: var(--font-weight-semibold);
    color: var(--color-carbon);
    line-height: var(--leading-body);
    margin: 0;
  }
  .contact-rating {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    font-weight: var(--font-weight-semibold);
    color: var(--color-carbon);
  }
  .contact-metadata {
    font-size: 12px;
    font-weight: var(--font-weight-regular);
    color: var(--color-slate);
    line-height: var(--leading-body);
  }
  .contact-price {
    font-size: var(--text-body);
    font-weight: var(--font-weight-semibold);
    color: var(--color-carbon);
    margin-top: 4px;
  }
`;
