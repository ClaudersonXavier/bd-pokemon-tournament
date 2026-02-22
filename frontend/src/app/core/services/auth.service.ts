import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, of, catchError, delay } from 'rxjs';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface User {
  id: string;
  email: string;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticatedSignal = signal(false);
  private currentUserSignal = signal<User | null>(null);

  constructor(private router: Router) {
    // Check if user is already logged in (e.g., from localStorage)
    this.checkAuthStatus();
  }

  get isAuthenticated() {
    return this.isAuthenticatedSignal.asReadonly();
  }

  get currentUser() {
    return this.currentUserSignal.asReadonly();
  }

  login(credentials: LoginCredentials): Observable<User> {
    // Simulate API call with delay
    return of({
      id: '1',
      email: credentials.email,
      name: 'Pokemon Trainer'
    }).pipe(
      delay(1500), // Simulate network delay
      catchError(error => {
        throw new Error('Login failed');
      })
    );
  }

  logout(): void {
    this.isAuthenticatedSignal.set(false);
    this.currentUserSignal.set(null);
    localStorage.removeItem('authToken');
    this.router.navigate(['/login']);
  }

  private checkAuthStatus(): void {
    // Check localStorage for existing auth token
    const token = localStorage.getItem('authToken');
    if (token) {
      // In a real app, you'd validate the token with the server
      this.isAuthenticatedSignal.set(true);
      this.currentUserSignal.set({
        id: '1',
        email: 'trainer@pokemon.com',
        name: 'Pokemon Trainer'
      });
    }
  }

  setAuthUser(user: User): void {
    this.isAuthenticatedSignal.set(true);
    this.currentUserSignal.set(user);
    localStorage.setItem('authToken', 'fake-jwt-token');
  }
}