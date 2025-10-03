import { Component, OnInit, inject, signal, DestroyRef } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { debounceTime, switchMap, startWith, finalize } from 'rxjs/operators';
import { Subject, merge } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { UsersService } from '../../core/services/user.service';
import { User, CreateUserDto } from '../../core/interfaces/user.interface';
import { AlertService } from '../../core/services/alert.service';
import { PaginatedData } from '../../shared/interfaces/paginated-response.interface';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
})
export class UsersComponent implements OnInit {
  private usersService = inject(UsersService);
  private fb = inject(FormBuilder);
  private alert = inject(AlertService);
  private destroyRef = inject(DestroyRef);

  users = signal<User[]>([]);
  loading = signal(false);
  page = signal(1);
  perPage = 10;
  total = signal(0);

  showModal = signal(false);
  isEdit = signal(false);
  currentId: number | null = null;
  showPassword = signal(false);

  userForm: FormGroup = this.fb.group({
    username: this.fb.control<string>('', { nonNullable: true, validators: [Validators.required] }),
    fullName: this.fb.control<string>('', { nonNullable: true, validators: [Validators.required] }),
    role: this.fb.control<string>('', { nonNullable: true, validators: [Validators.required] }),
    password: this.fb.control<string>('', { nonNullable: true }),
  });

  filterForm = this.fb.group({
    username: this.fb.control<string>('', { nonNullable: true }),
    fullName: this.fb.control<string>('', { nonNullable: true }),
    role: this.fb.control<string>('', { nonNullable: true }),
  });

  private pageChanges$ = new Subject<void>();

  ngOnInit(): void {
    merge(
      this.filterForm.valueChanges.pipe(startWith(this.filterForm.value), debounceTime(400)),
      this.pageChanges$
    )
      .pipe(
        switchMap(() => {
          this.loading.set(true);
          const { username, fullName, role } = this.filterForm.getRawValue();
          return this.usersService
            .getUsers(this.page(), this.perPage, username, fullName, role)
            .pipe(finalize(() => this.loading.set(false)));
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((res: PaginatedData<User>) => {
        this.users.set(res.data);
        this.total.set(res.meta.total);
      });
  }

  changePage(delta: number) {
    const newPage = this.page() + delta;
    if (newPage < 1 || (newPage - 1) * this.perPage >= this.total()) return;
    this.page.set(newPage);
    this.pageChanges$.next();
  }

  openCreate() {
    this.isEdit.set(false);
    this.currentId = null;
    this.userForm.reset();
    this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.userForm.get('password')?.updateValueAndValidity();
    this.showPassword.set(false);
    this.showModal.set(true);
  }

  openEdit(user: User) {
    this.isEdit.set(true);
    this.currentId = user.id;
    this.usersService
      .getById(user.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res: { data: User }) => {
        this.userForm.patchValue({
          username: res.data.username,
          fullName: res.data.fullName,
          role: res.data.role,
          password: '',
        });
        this.userForm.get('password')?.setValidators([Validators.minLength(6)]);
        this.userForm.get('password')?.updateValueAndValidity();
        this.showPassword.set(false);
        this.showModal.set(true);
      });
  }

  saveUser() {
    if (this.userForm.invalid) return;
    const formValue = this.userForm.getRawValue();

    if (this.isEdit() && this.currentId) {
      this.usersService
        .update(this.currentId, formValue)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => {
          this.alert.success('Éxito', 'Usuario actualizado');
          this.pageChanges$.next();
          this.closeModal();
        });
    } else {
      const data: CreateUserDto = {
        username: formValue.username,
        fullName: formValue.fullName,
        role: formValue.role,
        password: formValue.password,
      };
      this.usersService
        .create(data)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => {
          this.alert.success('Éxito', 'Usuario creado');
          this.pageChanges$.next();
          this.closeModal();
        });
    }
  }

  deleteUser(user: User) {
    this.alert
      .confirm('¿Eliminar?', `¿Está seguro de eliminar al usuario "${user.fullName}"?`)
      .then((result) => {
        if (result.isConfirmed) {
          this.usersService
            .delete(user.id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => {
              this.alert.success('Eliminado', 'El usuario ha sido eliminado correctamente');
              this.users.update((list) => list.filter((u) => u.id !== user.id));
              this.total.update((t) => t - 1);
            });
        }
      });
  }

  getError(controlName: 'username' | 'fullName' | 'role' | 'password'): string | null {
    const control = this.userForm.get(controlName);
    if (!control || !control.touched || !control.errors) return null;
    if (control.hasError('required')) {
      switch (controlName) {
        case 'username':
          return 'El usuario es obligatorio';
        case 'fullName':
          return 'El nombre completo es obligatorio';
        case 'role':
          return 'Debe seleccionar un rol';
        case 'password':
          return 'La contraseña es obligatoria';
      }
    }
    if (control.hasError('minlength') && controlName === 'password') {
      return 'La contraseña debe tener al menos 6 caracteres';
    }
    return null;
  }

  closeModal() {
    this.showModal.set(false);
    this.userForm.reset();
  }
}
