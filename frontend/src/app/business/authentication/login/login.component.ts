import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormControl, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { AlertService } from '../../../core/services/alert.service';

interface LoginForm {
  username: FormControl<string>;
  password: FormControl<string>;
}

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private alert = inject(AlertService);

  loginForm: FormGroup<LoginForm> = this.fb.group<LoginForm>({
    username: this.fb.control('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    password: this.fb.control('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(4)],
    }),
  });

  passwordFieldType = signal<'password' | 'text'>('password');

  togglePassword() {
    this.passwordFieldType.update((v) => (v === 'password' ? 'text' : 'password'));
  }

  getError(controlName: keyof LoginForm): string | null {
    const control = this.loginForm.controls[controlName];
    if ((control.touched || control.dirty) && control.invalid) {
      if (control.errors?.['required']) return 'Este campo es obligatorio.';
      if (control.errors?.['minlength']) {
        return `Debe tener al menos ${control.errors['minlength'].requiredLength} caracteres.`;
      }
    }
    return null;
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { username, password } = this.loginForm.getRawValue();

    this.authService.login(username, password).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          const { username, rol } = res.data;
          this.router.navigate(['/dashboard']).then(() => {
            this.alert.success(
              `Bienvenido, ${username}`,
              `Su rol actual es ${rol}. Acceso concedido correctamente.`
            );
          });
        } else {
          this.alert.warning('Atención', res.message || 'Credenciales inválidas');
        }
      },
    });
  }
}
