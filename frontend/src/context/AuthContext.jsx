import React, { createContext, useContext, useState, useCallback } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('cms_token'));
  const [user,  setUser]  = useState(() => {
    try { return JSON.parse(localStorage.getItem('cms_user')); }
    catch { return null; }
  });

  const login = useCallback((authResponse) => {
    localStorage.setItem('cms_token', authResponse.token);
    localStorage.setItem('cms_user',  JSON.stringify({
      id:        authResponse.userId,
      firstName: authResponse.firstName,
      lastName:  authResponse.lastName,
      email:     authResponse.email,
    }));
    setToken(authResponse.token);
    setUser({
      id:        authResponse.userId,
      firstName: authResponse.firstName,
      lastName:  authResponse.lastName,
      email:     authResponse.email,
    });
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('cms_token');
    localStorage.removeItem('cms_user');
    setToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
