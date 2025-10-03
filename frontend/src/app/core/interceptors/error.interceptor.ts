// core/interceptors/error.interceptor.ts
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AlertService } from '../services/alert.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(AuthService);
  const alert = inject(AlertService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const status = error.status;
      const message =
        (error?.error && (error.error.message || error.error.error)) ||
        'Ocurrió un error inesperado';

      if (status === 0) {
        alert.error('Sin conexión', 'No se pudo contactar con el servidor.');
      } else if (status === 400) {
        alert.warning('Solicitud inválida', message);
      } else if (status === 401) {
        auth.logout();
        alert
          .error('No autorizado', message || 'Tu sesión no es válida o expiró.')
          .then(() => router.navigate(['/login']));
      } else if (status === 403) {
        alert.warning('Acceso Denegado No tienes permisos para esta acción.');
      } else if (status === 500) {
        alert.error('Error del servidor', message || 'Ocurrió un problema en el servidor.');
      } else {
        alert.error(`Error ${status}`, message);
      }

      return throwError(() => error);
    })
  );
};
