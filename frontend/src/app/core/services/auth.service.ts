// core/services/auth.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable, PLATFORM_ID, inject, signal, computed } from '@angular/core'; // ← quité "Inject"
import { Observable, catchError, tap, throwError } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

export interface LoginResponse {
  success?: boolean;
  message?: string;
  data?: {
    token: string;
    username: string;
    rol: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);

  private readonly LOGIN_URL = 'http://localhost:8080/api/auth/login';
  private readonly tokenKey = 'token';
  private readonly userKey = 'username'; 

  // Estado reactivo
  private _token = signal<string | null>(this.getToken());
  readonly isAuthenticated = computed(() => !!this._token());

  private _username = signal<string | null>(this.getUsername());
  readonly username = computed(() => this._username());

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.LOGIN_URL, { username, password }).pipe(
      tap((res) => {
        if (res?.success && res.data?.token) {
          this.setToken(res.data.token);
          this.setUsername(res.data.username); 
        }
      }),
      catchError((error) => throwError(() => error))
    );
  }

  logout(): void {
    this.setToken(null);
    this.setUsername(null); 
  }

  // helpers
  private setToken(token: string | null): void {
    if (!isPlatformBrowser(this.platformId)) return;
    try {
      token ? localStorage.setItem(this.tokenKey, token) : localStorage.removeItem(this.tokenKey);
      this._token.set(token);
    } catch {
      /* noop */
    }
  }

  getToken(): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    try {
      return localStorage.getItem(this.tokenKey);
    } catch {
      return null;
    }
  }

  private setUsername(username: string | null): void {
    if (!isPlatformBrowser(this.platformId)) return;
    try {
      username
        ? localStorage.setItem(this.userKey, username)
        : localStorage.removeItem(this.userKey);
      this._username.set(username);
    } catch {
      /* noop */
    }
  }

  getUsername(): string | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    try {
      return localStorage.getItem(this.userKey);
    } catch {
      return null;
    }
  }
}
