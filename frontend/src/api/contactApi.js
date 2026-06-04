import api from './axiosConfig';

export const contactApi = {
  getAll:  (page = 0, size = 10, search = '') =>
    api.get('/contacts', { params: { page, size, search: search || undefined } }),
  getById: (id)           => api.get(`/contacts/${id}`),
  create:  (data)         => api.post('/contacts', data),
  update:  (id, data)     => api.put(`/contacts/${id}`, data),
  delete:  (id)           => api.delete(`/contacts/${id}`),
};
