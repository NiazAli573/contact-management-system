import React, { useState, useEffect, useCallback } from 'react';
import { contactApi } from '../api/contactApi';
import Navbar from '../components/Navbar';
import SearchBar from '../components/SearchBar';
import ContactCard from '../components/ContactCard';
import Pagination from '../components/Pagination';
import CreateContactModal from '../components/modals/CreateContactModal';
import UpdateContactModal from '../components/modals/UpdateContactModal';
import DeleteConfirmModal from '../components/modals/DeleteConfirmModal';

export default function ContactsPage() {
  const [contacts, setContacts]   = useState([]);
  const [page, setPage]           = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [search, setSearch]       = useState('');
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState('');

  const [showCreate, setShowCreate] = useState(false);
  const [editContact, setEditContact] = useState(null);
  const [deleteId, setDeleteId]   = useState(null);

  const PAGE_SIZE = 9;

  const fetchContacts = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const res = await contactApi.getAll(page, PAGE_SIZE, search);
      const data = res.data;
      setContacts(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch {
      setError('Failed to load contacts. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [page, search]);

  useEffect(() => { fetchContacts(); }, [fetchContacts]);

  const handleSearch = (val) => { setSearch(val); setPage(0); };
  const handleCreate = () => { fetchContacts(); setShowCreate(false); };
  const handleUpdate = () => { fetchContacts(); setEditContact(null); };
  const handleDelete = () => { fetchContacts(); setDeleteId(null); };

  return (
    <div className="page-wrapper">
      <style>{styles}</style>
      <Navbar />
      <main className="contacts-main">
        <div className="container">
          {/* Header */}
          <div className="contacts-header">
            <div>
              <h1 className="contacts-title">My Contacts</h1>
              <p style={{ fontSize: '0.9rem' }}>
                {totalElements} contact{totalElements !== 1 ? 's' : ''} total
              </p>
            </div>
            <button id="btn-create-contact" className="btn btn-primary" onClick={() => setShowCreate(true)}>
              <span>＋</span> New Contact
            </button>
          </div>

          {/* Search */}
          <SearchBar value={search} onChange={handleSearch} placeholder="Search by name…" />

          {/* Error */}
          {error && <div className="alert alert-error">{error}</div>}

          {/* Contact Grid */}
          {loading ? (
            <div className="contacts-loading">
              {[...Array(6)].map((_, i) => <div key={i} className="skeleton-card" />)}
            </div>
          ) : contacts.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">👤</div>
              <h3>{search ? 'No contacts match your search' : 'No contacts yet'}</h3>
              <p>{search ? 'Try a different search term' : 'Click "New Contact" to add your first contact'}</p>
              {!search && (
                <button className="btn btn-primary" onClick={() => setShowCreate(true)}>
                  Add First Contact
                </button>
              )}
            </div>
          ) : (
            <div className="contacts-grid">
              {contacts.map(c => (
                <ContactCard
                  key={c.id}
                  contact={c}
                  onEdit={() => setEditContact(c)}
                  onDelete={() => setDeleteId(c.id)}
                />
              ))}
            </div>
          )}

          {/* Pagination */}
          {totalPages > 1 && (
            <Pagination current={page} total={totalPages} onChange={setPage} />
          )}
        </div>
      </main>

      {/* Modals */}
      {showCreate && (
        <CreateContactModal
          onClose={() => setShowCreate(false)}
          onCreated={handleCreate}
        />
      )}
      {editContact && (
        <UpdateContactModal
          contact={editContact}
          onClose={() => setEditContact(null)}
          onUpdated={handleUpdate}
        />
      )}
      {deleteId && (
        <DeleteConfirmModal
          contactId={deleteId}
          onClose={() => setDeleteId(null)}
          onDeleted={handleDelete}
        />
      )}
    </div>
  );
}

const styles = `
  .contacts-main { flex: 1; padding: 2rem 0 4rem; }
  .contacts-header {
    display: flex; align-items: center; justify-content: space-between;
    margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem;
  }
  .contacts-title { margin-bottom: 0.2rem; }
  .contacts-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
  }
  .contacts-loading {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
  }
  .skeleton-card {
    height: 160px;
    border-radius: var(--radius-lg);
    background: linear-gradient(90deg, var(--bg-elevated) 25%, var(--bg-glass) 50%, var(--bg-elevated) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
  }
  @keyframes shimmer { 0%{background-position:200% 0} 100%{background-position:-200% 0} }
`;
