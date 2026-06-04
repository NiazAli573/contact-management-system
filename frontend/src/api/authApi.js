import api from './axiosConfig';

export const authApi = {
  register: (data)         => api.post('/auth/register', data),
  login:    (data)         => api.post('/auth/login', data),
  changePassword: (data)   => api.post('/auth/change-password', data),
};
