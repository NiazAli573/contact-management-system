import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => { logout(); navigate('/login'); };
  const initials = user ? `${user.firstName?.[0] ?? ''}${user.lastName?.[0] ?? ''}` : '?';

  return (
    <nav className="navbar">
      <style>{styles}</style>
      <div className="container navbar-inner">
        {/* Brand */}
        <Link to="/contacts" className="navbar-brand">
          <span className="navbar-logo">✦</span>
          <span>ContactHub</span>
        </Link>

        {/* Nav Links */}
        <div className="navbar-links">
          <Link
            to="/contacts"
            id="nav-contacts"
            className={`navbar-link ${location.pathname === '/contacts' ? 'active' : ''}`}
          >
            Contacts
          </Link>
          <Link
            to="/profile"
            id="nav-profile"
            className={`navbar-link ${location.pathname === '/profile' ? 'active' : ''}`}
          >
            Profile
          </Link>
        </div>

        {/* User Avatar */}
        <div className="navbar-user">
          <span className="navbar-username">{user?.firstName}</span>
          <Link to="/profile" className="navbar-avatar" title="My Profile">{initials}</Link>
        </div>
      </div>
    </nav>
  );
}

const styles = `
  .navbar {
    background: var(--bg-glass);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-bottom: 1px solid var(--border);
    position: sticky; top: 0; z-index: 100;
  }
  .navbar-inner {
    display: flex; align-items: center; justify-content: space-between;
    height: 64px; gap: 1rem;
  }
  .navbar-brand {
    display: flex; align-items: center; gap: 0.5rem;
    text-decoration: none; font-weight: 800; font-size: 1.15rem;
    color: var(--text-primary); letter-spacing: -0.02em;
  }
  .navbar-logo {
    color: var(--accent); font-size: 1.2rem;
    filter: drop-shadow(0 0 6px var(--accent-glow));
  }
  .navbar-links { display: flex; gap: 0.25rem; }
  .navbar-link {
    text-decoration: none; padding: 0.45rem 0.9rem; border-radius: var(--radius-sm);
    font-size: 0.9rem; font-weight: 500; color: var(--text-secondary);
    transition: all var(--transition);
  }
  .navbar-link:hover { color: var(--text-primary); background: var(--bg-glass-light); }
  .navbar-link.active { color: var(--accent); background: var(--accent-soft); }
  .navbar-user { display: flex; align-items: center; gap: 0.75rem; }
  .navbar-username { font-size: 0.88rem; color: var(--text-secondary); }
  .navbar-avatar {
    width: 38px; height: 38px; border-radius: 50%;
    background: linear-gradient(135deg, var(--accent), #60a5fa);
    display: flex; align-items: center; justify-content: center;
    font-size: 0.82rem; font-weight: 700; color: #0a0f1e;
    text-decoration: none; cursor: pointer; text-transform: uppercase;
    transition: box-shadow var(--transition);
  }
  .navbar-avatar:hover { box-shadow: var(--shadow-accent); }
`;
