import { Component, EventEmitter, inject, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.html',
})
export class HeaderComponent {
  auth = inject(AuthService);
  private router = inject(Router);

  @Output() toggle = new EventEmitter<void>();
  showMenu = false;

  toggleSidebar() {
    this.toggle.emit();
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/login']); // redirigir a login
  }
}
