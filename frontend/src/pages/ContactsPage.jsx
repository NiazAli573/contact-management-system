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

  const PAGE_SIZE = 12;

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
          
          <div className="search-section">
            <SearchBar value={search} onChange={handleSearch} placeholder="Search destinations, err, contacts..." />
          </div>

          {/* Header */}
          <div className="contacts-header">
            <div>
              <h2 className="section-heading">
                All Contacts
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{marginLeft: '8px', display: 'inline-block', verticalAlign: 'middle'}}>
                  <polyline points="9 18 15 12 9 6"></polyline>
                </svg>
              </h2>
              <p className="section-subtext">
                {totalElements} contact{totalElements !== 1 ? 's' : ''} total
              </p>
            </div>
            <button id="btn-create-contact" className="btn btn-primary" onClick={() => setShowCreate(true)}>
              New Contact
            </button>
          </div>

          {/* Error */}
          {error && <div className="alert alert-error">{error}</div>}

          {/* Contact Grid */}
          {loading ? (
            <div className="contacts-grid">
              {[...Array(6)].map((_, i) => <div key={i} className="skeleton-card" />)}
            </div>
          ) : contacts.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
              </div>
              <h3>{search ? 'No contacts match your search' : 'No contacts yet'}</h3>
              <p>{search ? 'Try adjusting your filters' : 'Start building your network'}</p>
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
  .contacts-main { 
    flex: 1; 
    padding: var(--spacing-40) 0 var(--spacing-48); 
  }
  .search-section {
    display: flex;
    justify-content: center;
  }
  .contacts-header {
    display: flex; align-items: center; justify-content: space-between;
    margin-bottom: var(--spacing-16); flex-wrap: wrap; gap: var(--spacing-16);
  }
  .section-heading {
    font-size: var(--text-heading);
    font-weight: var(--font-weight-semibold);
    color: var(--color-carbon);
    letter-spacing: var(--tracking-heading);
    margin: 0;
  }
  .section-subtext {
    font-size: var(--text-body);
    font-weight: var(--font-weight-regular);
    color: var(--color-slate);
    margin-top: 4px;
  }
  .contacts-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: var(--spacing-24) var(--spacing-16); /* slightly more vertical gap */
    margin-bottom: var(--spacing-48);
  }
  .skeleton-card {
    height: 300px;
    border-radius: var(--radius-cards);
    background: linear-gradient(90deg, var(--color-pebble) 25%, var(--color-stone) 50%, var(--color-pebble) 75%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite;
  }
  @keyframes shimmer { 0%{background-position:200% 0} 100%{background-position:-200% 0} }
`;
