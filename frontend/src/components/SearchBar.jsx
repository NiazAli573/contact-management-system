import React, { useEffect, useRef, useState } from 'react';

export default function SearchBar({ value, onChange, placeholder = 'Search contacts...' }) {
  const [local, setLocal] = useState(value);
  const timerRef = useRef(null);

  // Debounce 350 ms
  useEffect(() => {
    clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => onChange(local), 350);
    return () => clearTimeout(timerRef.current);
  }, [local]);

  useEffect(() => { setLocal(value); }, [value]);

  return (
    <div className="search-bar-wrap">
      <style>{styles}</style>
      <div className="search-bar-pill">
        
        <div className="search-segment">
          <div className="search-label">Search</div>
          <input
            id="contact-search"
            className="search-input-raw"
            type="text"
            value={local}
            onChange={e => setLocal(e.target.value)}
            placeholder={placeholder}
          />
        </div>

        <button className="search-trigger-btn" aria-label="Search">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
        </button>

      </div>
    </div>
  );
}

const styles = `
  .search-bar-wrap {
    display: flex;
    justify-content: center;
    margin-bottom: var(--spacing-48);
    position: relative;
    z-index: 10;
  }
  .search-bar-pill {
    display: inline-flex;
    align-items: center;
    background: var(--color-cloud);
    border-radius: var(--radius-searchbar); /* 20px */
    box-shadow: var(--shadow-subtle);
    border: 1px solid var(--color-mist);
    padding: 6px 8px 6px 24px;
    transition: box-shadow var(--transition);
  }
  .search-bar-pill:hover {
    box-shadow: rgba(0, 0, 0, 0.02) 0px 0px 0px 1px, rgba(0, 0, 0, 0.04) 0px 4px 12px 0px, rgba(0, 0, 0, 0.15) 0px 8px 16px 0px;
  }
  
  .search-segment {
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 0 16px 0 0;
  }
  .search-label {
    font-size: 12px;
    font-weight: 600;
    color: var(--color-carbon);
    margin-bottom: 2px;
  }
  .search-input-raw {
    border: none;
    background: transparent;
    font-family: var(--font-airbnb-cereal-vf);
    font-size: 14px;
    font-weight: 400;
    color: var(--color-carbon);
    outline: none;
    padding: 0;
    width: 200px;
  }
  .search-input-raw::placeholder {
    color: var(--color-slate);
  }
  .search-placeholder-text {
    font-size: 14px;
    font-weight: 400;
    color: var(--color-slate);
  }
  
  .search-divider {
    width: 1px;
    height: 32px;
    background-color: var(--color-mist);
    margin: 0 16px;
  }

  .search-trigger-btn {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: var(--color-rausch-coral);
    color: var(--color-cloud);
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: background var(--transition);
  }
  .search-trigger-btn:hover {
    background: var(--color-rausch-deep);
  }

  @media (max-width: 768px) {
    .d-none-mobile {
      display: none;
    }
    .search-segment {
      padding: 0 8px 0 0;
    }
    .search-input-raw {
      width: 140px;
    }
  }
`;
