import { Injectable } from '@angular/core';
import Swal, { SweetAlertResult } from 'sweetalert2';

@Injectable({ providedIn: 'root' })
export class AlertService {
  private baseConfig = Swal.mixin({
    customClass: {
      popup:
        'rounded-3xl shadow-2xl border border-gray-200 bg-white dark:bg-gray-900',
      title:
        'text-xl font-bold text-gray-800 dark:text-gray-100 flex items-center gap-2',
      htmlContainer: 'text-base text-gray-600 dark:text-gray-300',
      confirmButton:
        'bg-red-700 hover:bg-red-800 text-white font-semibold px-6 py-2 rounded-lg shadow transition duration-300 ease-in-out',
      cancelButton:
        'bg-gray-200 hover:bg-gray-300 dark:bg-gray-700 dark:hover:bg-gray-600 text-gray-700 dark:text-gray-200 font-medium px-6 py-2 rounded-lg ml-2 transition duration-300 ease-in-out',
    },
    buttonsStyling: false,
    showClass: {
      popup: 'animate__animated animate__fadeInDown animate__faster',
    },
    hideClass: {
      popup: 'animate__animated animate__fadeOutUp animate__faster',
    },
  });

  success(titulo: string, mensaje?: string): Promise<SweetAlertResult<any>> {
    return this.baseConfig.fire({
      title: titulo,
      text: mensaje,
      confirmButtonText: 'Aceptar',
    });
  }

  error(titulo: string, mensaje?: string): Promise<SweetAlertResult<any>> {
    return this.baseConfig.fire({
      title: titulo,
      text: mensaje,
      confirmButtonText: 'Cerrar',
    });
  }

  warning(titulo: string, mensaje?: string): Promise<SweetAlertResult<any>> {
    return this.baseConfig.fire({
      title: titulo,
      text: mensaje,
      confirmButtonText: 'Entendido',
    });
  }

  info(titulo: string, mensaje?: string): Promise<SweetAlertResult<any>> {
    return this.baseConfig.fire({
      title: titulo,
      text: mensaje,
      confirmButtonText: 'Aceptar',
    });
  }

  confirm(titulo: string, mensaje?: string): Promise<SweetAlertResult<any>> {
    return this.baseConfig.fire({
      title: titulo,
      text: mensaje,
      showCancelButton: true,
      confirmButtonText: 'Sí',
      cancelButtonText: 'Cancelar',
    });
  }

  toast(titulo: string): Promise<SweetAlertResult<any>> {
    return this.baseConfig.fire({
      title: titulo,
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      timerProgressBar: true,
      background: '#ffffff',
      color: '#1f2937',
      showClass: {
        popup: 'animate__animated animate__fadeInRight',
      },
      hideClass: {
        popup: 'animate__animated animate__fadeOutRight',
      },
    });
  }
}
