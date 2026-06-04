import api from './axiosConfig';

export const userApi = {
  getMe: () => api.get('/users/me'),
};
