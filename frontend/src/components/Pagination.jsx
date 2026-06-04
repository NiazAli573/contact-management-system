import React from 'react';

export default function Pagination({ current, total, onChange }) {
  const pages = buildPageList(current, total);

  return (
    <div className="pagination">
      <style>{styles}</style>
      <button
        id="page-prev"
        className="btn btn-ghost btn-sm pagination-btn"
        disabled={current === 0}
        onClick={() => onChange(current - 1)}
      >
        ← Prev
      </button>

      <div className="pagination-pages">
        {pages.map((p, i) =>
          p === '…' ? (
            <span key={`ellipsis-${i}`} className="pagination-ellipsis">…</span>
          ) : (
            <button
              key={p}
              id={`page-${p}`}
              className={`pagination-page ${p === current ? 'active' : ''}`}
              onClick={() => onChange(p)}
            >
              {p + 1}
            </button>
          )
        )}
      </div>

      <button
        id="page-next"
        className="btn btn-ghost btn-sm pagination-btn"
        disabled={current === total - 1}
        onClick={() => onChange(current + 1)}
      >
        Next →
      </button>
    </div>
  );
}

function buildPageList(current, total) {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i);
  const pages = [];
  if (current <= 3) {
    pages.push(0, 1, 2, 3, 4, '…', total - 1);
  } else if (current >= total - 4) {
    pages.push(0, '…', total - 5, total - 4, total - 3, total - 2, total - 1);
  } else {
    pages.push(0, '…', current - 1, current, current + 1, '…', total - 1);
  }
  return pages;
}

const styles = `
  .pagination {
    display: flex; align-items: center; justify-content: center; gap: 0.5rem; margin-top: 2rem;
  }
  .pagination-pages { display: flex; gap: 0.25rem; align-items: center; }
  .pagination-page {
    width: 36px; height: 36px; border-radius: var(--radius-sm); border: 1px solid var(--border);
    background: var(--bg-glass-light); color: var(--text-secondary); cursor: pointer;
    font-size: 0.85rem; font-weight: 500; font-family: var(--font);
    transition: all var(--transition); display: flex; align-items: center; justify-content: center;
  }
  .pagination-page:hover { border-color: var(--accent); color: var(--accent); }
  .pagination-page.active {
    background: var(--accent); color: #0a0f1e; border-color: var(--accent); font-weight: 700;
  }
  .pagination-ellipsis { color: var(--text-muted); padding: 0 0.25rem; }
  .pagination-btn { min-width: 80px; }
`;
