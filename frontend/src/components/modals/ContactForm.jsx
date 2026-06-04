import React, { useState } from 'react';

const EMAIL_LABELS = ['PERSONAL', 'WORK', 'OTHER'];
const PHONE_LABELS = ['MOBILE', 'HOME', 'WORK', 'OTHER'];

export default function ContactForm({ initial, onSubmit, onCancel, loading, submitLabel, submitId }) {
  const [form, setForm] = useState({
    firstName: initial?.firstName ?? '',
    lastName:  initial?.lastName  ?? '',
    title:     initial?.title     ?? '',
    emails:    initial?.emails?.length
      ? initial.emails.map(e => ({ email: e.email, label: e.label }))
      : [{ email: '', label: 'PERSONAL' }],
    phones:    initial?.phones?.length
      ? initial.phones.map(p => ({ phoneNumber: p.phoneNumber, label: p.label }))
      : [{ phoneNumber: '', label: 'MOBILE' }],
  });
  const [errors, setErrors] = useState({});

  const setField = (field, value) =>
    setForm(prev => ({ ...prev, [field]: value }));

  // Emails
  const setEmail = (i, key, val) =>
    setForm(prev => {
      const emails = [...prev.emails];
      emails[i] = { ...emails[i], [key]: val };
      return { ...prev, emails };
    });
  const addEmail  = () => setForm(prev => ({ ...prev, emails: [...prev.emails, { email: '', label: 'PERSONAL' }] }));
  const rmEmail   = (i) => setForm(prev => ({ ...prev, emails: prev.emails.filter((_, idx) => idx !== i) }));

  // Phones
  const setPhone  = (i, key, val) =>
    setForm(prev => {
      const phones = [...prev.phones];
      phones[i] = { ...phones[i], [key]: val };
      return { ...prev, phones };
    });
  const addPhone  = () => setForm(prev => ({ ...prev, phones: [...prev.phones, { phoneNumber: '', label: 'MOBILE' }] }));
  const rmPhone   = (i) => setForm(prev => ({ ...prev, phones: prev.phones.filter((_, idx) => idx !== i) }));

  const validate = () => {
    const errs = {};
    if (!form.firstName.trim()) errs.firstName = 'First name is required';
    if (!form.lastName.trim())  errs.lastName  = 'Last name is required';
    form.emails.forEach((e, i) => {
      if (e.email && !/\S+@\S+\.\S+/.test(e.email)) errs[`email_${i}`] = 'Invalid email';
    });
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;
    const payload = {
      firstName: form.firstName.trim(),
      lastName:  form.lastName.trim(),
      title:     form.title.trim() || undefined,
      emails:    form.emails.filter(e => e.email.trim()),
      phones:    form.phones.filter(p => p.phoneNumber.trim()),
    };
    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit} noValidate style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      {/* Name row */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
        <div className="form-group">
          <label className="form-label" htmlFor="cf-firstName">First Name *</label>
          <input id="cf-firstName" className="form-input" type="text" value={form.firstName}
            onChange={e => setField('firstName', e.target.value)} placeholder="Alice" />
          {errors.firstName && <span className="form-error">⚠ {errors.firstName}</span>}
        </div>
        <div className="form-group">
          <label className="form-label" htmlFor="cf-lastName">Last Name *</label>
          <input id="cf-lastName" className="form-input" type="text" value={form.lastName}
            onChange={e => setField('lastName', e.target.value)} placeholder="Smith" />
          {errors.lastName && <span className="form-error">⚠ {errors.lastName}</span>}
        </div>
      </div>

      {/* Title */}
      <div className="form-group">
        <label className="form-label" htmlFor="cf-title">Title / Job</label>
        <input id="cf-title" className="form-input" type="text" value={form.title}
          onChange={e => setField('title', e.target.value)} placeholder="Software Engineer" />
      </div>

      <div className="divider" style={{ margin: '0' }} />

      {/* Emails */}
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.6rem' }}>
          <span className="form-label">✉ Email Addresses</span>
          <button type="button" className="btn btn-ghost btn-sm" onClick={addEmail} id="add-email">+ Add</button>
        </div>
        {form.emails.map((e, i) => (
          <div key={i} className="multi-input-row-3">
            <input
              id={`cf-email-${i}`}
              className="form-input"
              type="email"
              value={e.email}
              onChange={ev => setEmail(i, 'email', ev.target.value)}
              placeholder="email@example.com"
            />
            <select
              id={`cf-email-label-${i}`}
              className="form-select"
              style={{ width: 'auto', minWidth: 100 }}
              value={e.label}
              onChange={ev => setEmail(i, 'label', ev.target.value)}
            >
              {EMAIL_LABELS.map(l => <option key={l} value={l}>{l}</option>)}
            </select>
            {form.emails.length > 1 && (
              <button type="button" className="btn btn-danger btn-icon btn-sm"
                onClick={() => rmEmail(i)} id={`rm-email-${i}`}>✕</button>
            )}
          </div>
        ))}
        {Object.entries(errors).filter(([k]) => k.startsWith('email_')).map(([k, v]) => (
          <div key={k} className="form-error">⚠ {v}</div>
        ))}
      </div>

      {/* Phones */}
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.6rem' }}>
          <span className="form-label">📞 Phone Numbers</span>
          <button type="button" className="btn btn-ghost btn-sm" onClick={addPhone} id="add-phone">+ Add</button>
        </div>
        {form.phones.map((p, i) => (
          <div key={i} className="multi-input-row-3">
            <input
              id={`cf-phone-${i}`}
              className="form-input"
              type="tel"
              value={p.phoneNumber}
              onChange={ev => setPhone(i, 'phoneNumber', ev.target.value)}
              placeholder="+1 555 000 0000"
            />
            <select
              id={`cf-phone-label-${i}`}
              className="form-select"
              style={{ width: 'auto', minWidth: 100 }}
              value={p.label}
              onChange={ev => setPhone(i, 'label', ev.target.value)}
            >
              {PHONE_LABELS.map(l => <option key={l} value={l}>{l}</option>)}
            </select>
            {form.phones.length > 1 && (
              <button type="button" className="btn btn-danger btn-icon btn-sm"
                onClick={() => rmPhone(i)} id={`rm-phone-${i}`}>✕</button>
            )}
          </div>
        ))}
      </div>

      {/* Footer */}
      <div className="modal-footer" style={{ paddingTop: '0.5rem' }}>
        <button type="button" className="btn btn-ghost" onClick={onCancel} id="cf-cancel" disabled={loading}>
          Cancel
        </button>
        <button type="submit" className="btn btn-primary" id={submitId} disabled={loading}>
          {loading ? <><span className="spinner" /> Saving…</> : submitLabel}
        </button>
      </div>
    </form>
  );
}
