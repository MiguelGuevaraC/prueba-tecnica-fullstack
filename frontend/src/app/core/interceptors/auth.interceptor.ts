// core/interceptors/auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { AlertService } from '../services/alert.service'; 

// Decode base64url → objeto
function decodeTokenPart(part: string): any {
  try {
    const b64 = part.replace(/-/g, '+').replace(/_/g, '/')
                    .padEnd(Math.ceil(part.length / 4) * 4, '=');
    return JSON.parse(atob(b64));
  } catch {
    return null;
  }
}

function tokenIsExpired(token: string | null): boolean {
  if (!token) return true;
  const parts = token.split('.');
  if (parts.length !== 3) return true;
  const payload = decodeTokenPart(parts[1]);
  if (!payload?.exp) return true;
  const now = Math.floor(Date.now() / 1000);
  return now >= payload.exp;
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const alert = inject(AlertService); 

  const token = auth.getToken();

  // Si existe token pero expiró → forzar logout
  if (token && tokenIsExpired(token)) {
    auth.logout();
    alert.warning('Sesión expirada', 'Vuelve a iniciar sesión.')
      .then(() => router.navigate(['/login']));
    // Bloqueamos la petición, no propagamos al backend
    return next(req.clone());
  }

  // Si el token está ok, lo añadimos al header
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq);
};
