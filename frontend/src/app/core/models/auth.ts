export interface LoginRequest {
  email: string;
  senha: string;
}

export interface RegistroRequest {
  nome: string;
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  id: number;
  nome: string;
  email: string;
  admin: boolean;
}

export interface User {
  id: number;
  email: string;
  nome: string;
  admin: boolean;
}
