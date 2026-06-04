import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm]       = useState({ email: '', password: '' });
  const [error, setError]     = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) =>
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await authApi.login(form);
      login(res.data);
      navigate('/contacts');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <style>{authStyles}</style>
      <div className="auth-container">
        <div className="auth-brand">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor" className="auth-logo">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
          </svg>
          <h1>ContactHub</h1>
          <p>Welcome back — sign in to your account</p>
        </div>

        <form className="card card-elevated auth-card" onSubmit={handleSubmit} noValidate>
          <h2 className="auth-card-title">Sign In</h2>

          {error && <div className="alert alert-error" role="alert">{error}</div>}

          <div className="form-group">
            <label className="form-label" htmlFor="login-email">Email Address</label>
            <input
              id="login-email"
              className="form-input"
              type="email"
              name="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={handleChange}
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="login-password">Password</label>
            <input
              id="login-password"
              className="form-input"
              type="password"
              name="password"
              placeholder="••••••••"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          <button
            id="login-submit"
            className="btn btn-primary btn-lg"
            type="submit"
            disabled={loading}
            style={{ width: '100%', marginTop: '0.5rem', padding: '14px 24px' }}
          >
            {loading ? <><span className="spinner" /> Signing in…</> : 'Sign In'}
          </button>

          <p className="auth-switch">
            Don't have an account?&nbsp;
            <Link to="/register" id="goto-register">Create one</Link>
          </p>
        </form>
      </div>
    </div>
  );
}

const authStyles = `
  .auth-page {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: var(--spacing-32);
    background: var(--color-fog);
  }
  .auth-container {
    width: 100%;
    max-width: 440px;
    display: flex;
    flex-direction: column;
    gap: var(--spacing-32);
  }
  .auth-brand {
    text-align: center;
  }
  .auth-logo {
    color: var(--color-rausch-coral);
    margin-bottom: var(--spacing-8);
  }
  .auth-brand h1 {
    color: var(--color-rausch-coral);
    margin-bottom: var(--spacing-8);
    font-size: var(--text-display);
    letter-spacing: var(--tracking-display);
  }
  .auth-brand p { font-size: var(--text-body); color: var(--color-slate); }
  .auth-card { display: flex; flex-direction: column; gap: var(--spacing-20); padding: var(--spacing-32); }
  .auth-card-title { margin-bottom: var(--spacing-4); }
  .auth-switch {
    text-align: center;
    font-size: var(--text-body);
    color: var(--color-slate);
    margin-top: var(--spacing-4);
  }
  .auth-switch a {
    color: var(--color-carbon);
    text-decoration: underline;
    font-weight: var(--font-weight-semibold);
  }
  .auth-switch a:hover { color: var(--color-rausch-coral); }
`;
