import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Request interceptor to add token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
}

export const login = (data: LoginRequest) =>
  api.post<AuthResponse>('/auth/login', data);

export const register = (data: LoginRequest) =>
  api.post<AuthResponse>('/auth/register', data);

// Portfolio and orders (to be implemented)
export const getPortfolio = () => api.get('/portfolio');
export const getOrders = () => api.get('/orders');
