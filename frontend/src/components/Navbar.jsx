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
    <header className="global-header">
      <style>{styles}</style>
      <div className="header-container">
        
        {/* Brand / Logo (Left) */}
        <Link to="/contacts" className="header-brand">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor" className="brand-logo">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
          </svg>
          <span className="brand-text">contacthub</span>
        </Link>

        {/* Center Navigation */}
        <nav className="header-nav">
          <Link
            to="/contacts"
            className={`nav-link ${location.pathname === '/contacts' ? 'active' : ''}`}
          >
            Contacts
          </Link>
          <Link
            to="/profile"
            className={`nav-link ${location.pathname === '/profile' ? 'active' : ''}`}
          >
            Profile
          </Link>
        </nav>

        {/* User Utilities (Right) */}
        <div className="header-utilities">
          <div className="utility-item host-text">Become a user</div>
          <button className="btn-icon header-globe" aria-label="Language">
            <svg viewBox="0 0 16 16" width="16" height="16" fill="currentColor">
              <path d="M8 0a8 8 0 1 0 0 16A8 8 0 0 0 8 0zm.01 1.51c1.55 0 2.87.5 4.01 1.48-1 .92-2.3 1.48-4.01 1.48-1.7 0-3.01-.56-4.01-1.48 1.14-.98 2.46-1.48 4.01-1.48zm-5.46 2.5a6.45 6.45 0 0 1 10.9 0 6.47 6.47 0 0 1 0 7.98 6.45 6.45 0 0 1-10.9 0 6.47 6.47 0 0 1 0-7.98zM8 14.49c-1.7 0-3.01-.56-4.01-1.48 1-.92 2.3-1.48 4.01-1.48 1.7 0 3.01.56 4.01 1.48-1.14.98-2.46 1.48-4.01 1.48z"></path>
            </svg>
          </button>
          
          <div className="user-menu-pill" onClick={() => location.pathname === '/profile' ? handleLogout() : navigate('/profile')}>
            <svg className="menu-bars" viewBox="0 0 32 32" width="16" height="16" fill="currentColor" stroke="currentColor" strokeWidth="3">
              <g><path d="M2 16h28M2 24h28M2 8h28"></path></g>
            </svg>
            <div className="user-avatar-small">{initials}</div>
          </div>
        </div>
      </div>
    </header>
  );
}

const styles = `
  .global-header {
    position: sticky;
    top: 0;
    z-index: 100;
    background: var(--color-cloud);
    height: 96px;
    border-bottom: 1px solid var(--color-mist);
    display: flex;
    align-items: center;
  }
  .header-container {
    max-width: var(--page-max-width);
    margin: 0 auto;
    padding: 0 var(--spacing-24);
    width: 100%;
    display: grid;
    grid-template-columns: 1fr auto 1fr;
    align-items: center;
  }
  
  /* Brand */
  .header-brand {
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--color-rausch-coral);
    text-decoration: none;
  }
  .brand-logo {
    color: var(--color-rausch-coral);
  }
  .brand-text {
    font-size: 22px;
    font-weight: 700;
    letter-spacing: -0.5px;
  }

  /* Nav Links */
  .header-nav {
    display: flex;
    gap: 24px;
    align-items: center;
  }
  .nav-link {
    font-size: 16px;
    font-weight: 400;
    color: var(--color-slate);
    text-decoration: none;
    padding: 10px 0;
    position: relative;
    transition: color var(--transition);
  }
  .nav-link:hover {
    color: var(--color-carbon);
  }
  .nav-link.active {
    font-weight: 600;
    color: var(--color-carbon);
  }
  .nav-link.active::after {
    content: '';
    position: absolute;
    bottom: -8px;
    left: 0;
    right: 0;
    height: 2px;
    background-color: var(--color-carbon);
  }

  /* Utilities */
  .header-utilities {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
  }
  .utility-item {
    font-size: 14px;
    font-weight: 600;
    color: var(--color-carbon);
    padding: 10px 14px;
    border-radius: 22px;
    cursor: pointer;
  }
  .utility-item:hover {
    background: var(--color-fog);
  }
  .host-text {
    display: none;
  }
  @media (min-width: 768px) {
    .host-text { display: block; }
  }

  .header-globe {
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    cursor: pointer;
    background: transparent;
    border: none;
    color: var(--color-carbon);
  }
  .header-globe:hover {
    background: var(--color-fog);
  }

  .user-menu-pill {
    display: flex;
    align-items: center;
    gap: 12px;
    border: 1px solid var(--color-pebble);
    border-radius: 24px;
    padding: 6px 6px 6px 14px;
    cursor: pointer;
    background: var(--color-cloud);
    transition: box-shadow var(--transition);
    margin-left: 8px;
  }
  .user-menu-pill:hover {
    box-shadow: 0 2px 4px rgba(0,0,0,0.18);
  }
  .menu-bars {
    color: var(--color-carbon);
  }
  .user-avatar-small {
    background: var(--color-carbon);
    color: var(--color-cloud);
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 600;
  }
`;
