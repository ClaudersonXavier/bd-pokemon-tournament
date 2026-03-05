import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegistroRequest, LoginResponse, User } from '../models/auth';

export type { LoginRequest, RegistroRequest, LoginResponse, User };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'authToken';

  private isAuthenticatedSignal = signal(false);
  private currentUserSignal = signal<User | null>(null);

  constructor(private http: HttpClient, private router: Router) {
    this.checkAuthStatus();
  }

  get isAuthenticated() {
    return this.isAuthenticatedSignal.asReadonly();
  }

  get currentUser() {
    return this.currentUserSignal.asReadonly();
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/login`, credentials)
      .pipe(tap(response => this.handleAuthResponse(response)));
  }

  registro(data: RegistroRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${environment.apiUrl}/auth/registro`, data)
      .pipe(tap(response => this.handleAuthResponse(response)));
  }

  changePassword(senhaAtual: string, novaSenha: string): Observable<{ mensagem: string }> {
    return this.http.put<{ mensagem: string }>(
      `${environment.apiUrl}/auth/change-password`,
      { senhaAtual, novaSenha }
    );
  }

  updateNome(nome: string): Observable<{ mensagem: string; nome: string }> {
    return this.http.put<{ mensagem: string; nome: string }>(
      `${environment.apiUrl}/auth/update-nome`,
      { nome }
    ).pipe(
      tap(response => {
        const current = this.currentUserSignal();
        if (current) {
          this.currentUserSignal.set({ ...current, nome: response.nome });
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.isAuthenticatedSignal.set(false);
    this.currentUserSignal.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  setAuthUser(user: User): void {
    this.currentUserSignal.set(user);
  }

  private handleAuthResponse(response: LoginResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    this.isAuthenticatedSignal.set(true);
    this.currentUserSignal.set({
      id: response.id,
      email: response.email,
      nome: response.nome,
      admin: response.admin,
    });
  }

  private checkAuthStatus(): void {
    const token = localStorage.getItem(this.TOKEN_KEY);
    if (!token) return;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirado = payload.exp * 1000 < Date.now();

      if (expirado) {
        localStorage.removeItem(this.TOKEN_KEY);
        return;
      }

      this.isAuthenticatedSignal.set(true);
      this.currentUserSignal.set({
        id: payload.id,
        email: payload.sub,
        nome: payload.nome,
        admin: payload.admin === true,
      });
    } catch {
      localStorage.removeItem(this.TOKEN_KEY);
    }
  }
}
