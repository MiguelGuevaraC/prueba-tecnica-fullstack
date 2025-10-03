import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css']
})
export class ModalComponent {
  @Input() show = false;        // abrir/cerrar
  @Input() title = '';          // título dinámico
  @Output() closed = new EventEmitter<void>(); // evento cerrar

  close() {
    this.closed.emit();
  }
}
