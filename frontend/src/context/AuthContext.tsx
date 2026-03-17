import React, { createContext, useState, useContext, useEffect } from 'react';
import { login as apiLogin, register as apiRegister, LoginRequest, AuthResponse } from '../services/api';

interface AuthContextType {
  token: string | null;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: LoginRequest) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));

  const login = async (data: LoginRequest) => {
    const response = await apiLogin(data);
    localStorage.setItem('token', response.data.token);
    setToken(response.data.token);
  };

  const register = async (data: LoginRequest) => {
    const response = await apiRegister(data);
    localStorage.setItem('token', response.data.token);
    setToken(response.data.token);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, login, register, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};
