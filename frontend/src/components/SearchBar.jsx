import React, { useEffect, useRef, useState } from 'react';

export default function SearchBar({ value, onChange, placeholder = 'Search…' }) {
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
      <span className="search-icon">🔍</span>
      <input
        id="contact-search"
        className="form-input search-input"
        type="search"
        value={local}
        onChange={e => setLocal(e.target.value)}
        placeholder={placeholder}
      />
      {local && (
        <button className="search-clear" onClick={() => { setLocal(''); onChange(''); }} title="Clear">✕</button>
      )}
    </div>
  );
}

const styles = `
  .search-bar-wrap {
    position: relative; margin-bottom: 1.5rem; display: flex; align-items: center;
  }
  .search-icon {
    position: absolute; left: 0.9rem; z-index: 1; font-size: 0.95rem; pointer-events: none;
  }
  .search-input { padding-left: 2.5rem !important; padding-right: 2.5rem !important; }
  .search-clear {
    position: absolute; right: 0.9rem; background: none; border: none;
    color: var(--text-muted); cursor: pointer; font-size: 0.85rem; padding: 0;
    transition: color var(--transition);
  }
  .search-clear:hover { color: var(--danger); }
`;
