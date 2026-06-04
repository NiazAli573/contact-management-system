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
          <div className="auth-logo">✦</div>
          <h1>ContactHub</h1>
          <p>Welcome back — sign in to your account</p>
        </div>

        <form className="card auth-card" onSubmit={handleSubmit} noValidate>
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
            style={{ width: '100%', marginTop: '0.5rem' }}
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
    padding: 2rem;
  }
  .auth-container {
    width: 100%;
    max-width: 440px;
    display: flex;
    flex-direction: column;
    gap: 2rem;
  }
  .auth-brand {
    text-align: center;
  }
  .auth-logo {
    font-size: 2.5rem;
    color: var(--accent);
    margin-bottom: 0.5rem;
    filter: drop-shadow(0 0 12px var(--accent-glow));
    animation: pulse 3s ease-in-out infinite;
  }
  .auth-brand h1 {
    background: linear-gradient(135deg, var(--accent), #60a5fa);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 0.4rem;
  }
  .auth-brand p { font-size: 0.95rem; }
  .auth-card { display: flex; flex-direction: column; gap: 1.25rem; }
  .auth-card-title { margin-bottom: 0.25rem; }
  .auth-switch {
    text-align: center;
    font-size: 0.88rem;
    color: var(--text-muted);
    margin-top: 0.25rem;
  }
  .auth-switch a {
    color: var(--accent);
    text-decoration: none;
    font-weight: 500;
  }
  .auth-switch a:hover { text-decoration: underline; }
`;
