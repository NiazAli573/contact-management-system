import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm]       = useState({
    firstName: '', lastName: '', email: '', phoneNumber: '', password: '', confirmPassword: ''
  });
  const [error, setError]     = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) =>
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (form.password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    try {
      const { confirmPassword, ...payload } = form;
      const res = await authApi.register(payload);
      login(res.data);
      navigate('/contacts');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <style>{authStyles}</style>
      <div className="auth-container" style={{ maxWidth: 480 }}>
        <div className="auth-brand">
          <div className="auth-logo">✦</div>
          <h1>ContactHub</h1>
          <p>Create your free account today</p>
        </div>

        <form className="card auth-card" onSubmit={handleSubmit} noValidate>
          <h2 className="auth-card-title">Create Account</h2>

          {error && <div className="alert alert-error" role="alert">{error}</div>}

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label" htmlFor="reg-firstName">First Name *</label>
              <input id="reg-firstName" className="form-input" type="text" name="firstName"
                placeholder="John" value={form.firstName} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label className="form-label" htmlFor="reg-lastName">Last Name *</label>
              <input id="reg-lastName" className="form-input" type="text" name="lastName"
                placeholder="Doe" value={form.lastName} onChange={handleChange} required />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="reg-email">Email Address *</label>
            <input id="reg-email" className="form-input" type="email" name="email"
              placeholder="you@example.com" value={form.email} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="reg-phone">Phone Number</label>
            <input id="reg-phone" className="form-input" type="tel" name="phoneNumber"
              placeholder="+1 555 000 0000" value={form.phoneNumber} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="reg-password">Password *</label>
            <input id="reg-password" className="form-input" type="password" name="password"
              placeholder="••••••••" value={form.password} onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="reg-confirm">Confirm Password *</label>
            <input id="reg-confirm" className="form-input" type="password" name="confirmPassword"
              placeholder="••••••••" value={form.confirmPassword} onChange={handleChange} required />
          </div>

          <button id="reg-submit" className="btn btn-primary btn-lg" type="submit"
            disabled={loading} style={{ width: '100%', marginTop: '0.5rem' }}>
            {loading ? <><span className="spinner" /> Creating account…</> : 'Create Account'}
          </button>

          <p className="auth-switch">
            Already have an account?&nbsp;
            <Link to="/login" id="goto-login">Sign in</Link>
          </p>
        </form>
      </div>
    </div>
  );
}

const authStyles = `
  .auth-page {
    min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 2rem;
  }
  .auth-container { width: 100%; display: flex; flex-direction: column; gap: 2rem; }
  .auth-brand { text-align: center; }
  .auth-logo {
    font-size: 2.5rem; color: var(--accent); margin-bottom: 0.5rem;
    filter: drop-shadow(0 0 12px var(--accent-glow)); animation: pulse 3s ease-in-out infinite;
  }
  .auth-brand h1 {
    background: linear-gradient(135deg, var(--accent), #60a5fa);
    -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;
    margin-bottom: 0.4rem;
  }
  .auth-brand p { font-size: 0.95rem; }
  .auth-card { display: flex; flex-direction: column; gap: 1.1rem; }
  .auth-card-title { margin-bottom: 0.25rem; }
  .auth-switch { text-align: center; font-size: 0.88rem; color: var(--text-muted); margin-top: 0.25rem; }
  .auth-switch a { color: var(--accent); text-decoration: none; font-weight: 500; }
  .auth-switch a:hover { text-decoration: underline; }
`;
