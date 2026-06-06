import { apiClient } from './client';

export interface User {
  id: string;
  email: string;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  user: User;
}

export async function register(email: string, password: string) {
  const { data } = await apiClient.post<AuthResponse>('/auth/register', { email, password });
  return data;
}

export async function login(email: string, password: string) {
  const { data } = await apiClient.post<AuthResponse>('/auth/login', { email, password });
  return data;
}

export async function logout() {
  await apiClient.post('/auth/logout');
}

export async function getCurrentUser() {
  const { data } = await apiClient.get<User>('/auth/me');
  return data;
}
