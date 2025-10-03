import { Component, OnInit, inject, signal, DestroyRef } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { debounceTime, switchMap, startWith, finalize, map } from 'rxjs/operators';
import { Subject, merge } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { CategoriesService } from '../../core/services/category.service';
import { Category, CreateCategoryDto } from '../../core/interfaces/category.interface';
import { AlertService } from '../../core/services/alert.service';
import { PaginatedData } from '../../shared/interfaces/paginated-response.interface';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css'],
})
export class CategoriesComponent implements OnInit {
  private categoriesService = inject(CategoriesService);
  private fb = inject(FormBuilder);
  private alert = inject(AlertService);
  private destroyRef = inject(DestroyRef);

  categories = signal<Category[]>([]);
  loading = signal(false);
  page = signal(1);
  perPage = 10;
  total = signal(0);

  showModal = signal(false);
  isEdit = signal(false);
  currentId: number | null = null;

  categoryForm: FormGroup = this.fb.group({
    name: this.fb.control<string>('', { nonNullable: true, validators: [Validators.required] }),
    description: this.fb.control<string>('', { nonNullable: true }),
  });

  filterForm = this.fb.group({
    name: this.fb.control<string>('', { nonNullable: true }),
    description: this.fb.control<string>('', { nonNullable: true }),
    userName: this.fb.control<string>('', { nonNullable: true }),
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
          const { name, description, userName } = this.filterForm.getRawValue();
          return this.categoriesService
            .getCategories(this.page(), this.perPage, name, description, userName)
            .pipe(finalize(() => this.loading.set(false))); 
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((res: PaginatedData<Category>) => {
        this.categories.set(res.data);
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
    this.categoryForm.reset();
    this.showModal.set(true);
  }

  openEdit(category: Category) {
    this.isEdit.set(true);
    this.currentId = category.id;
    this.categoriesService
      .getById(category.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((res: { data: Category }) => {
        this.categoryForm.patchValue({
          name: res.data.name,
          description: res.data.description ?? '',
        });
        this.showModal.set(true);
      });
  }

  saveCategory() {
    if (this.categoryForm.invalid) return;
    const formValue = this.categoryForm.getRawValue();

    if (this.isEdit() && this.currentId) {
      const data: Partial<CreateCategoryDto> = {
        name: formValue.name,
        description: formValue.description,
      };
      this.categoriesService
        .update(this.currentId, data)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => {
          this.alert.success('Éxito', 'Categoría actualizada');
          this.pageChanges$.next();
          this.closeModal();
        });
    } else {
      const data: CreateCategoryDto = {
        name: formValue.name,
        description: formValue.description,
      };
      this.categoriesService
        .create(data)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => {
          this.alert.success('Éxito', 'Categoría creada');
          this.pageChanges$.next();
          this.closeModal();
        });
    }
  }

  deleteCategory(category: Category) {
    this.alert
      .confirm('¿Eliminar?', `¿Está seguro de eliminar la categoría "${category.name}"?`)
      .then((result) => {
        if (result.isConfirmed) {
          this.categoriesService
            .delete(category.id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => {
              this.alert.success('Eliminado', 'La categoría ha sido eliminada correctamente');
              this.categories.update((list) => list.filter((c) => c.id !== category.id));
              this.total.update((t) => t - 1);
            });
        }
      });
  }

  getError(controlName: 'name' | 'description'): string | null {
    const control = this.categoryForm.get(controlName);
    if (!control || !control.touched || !control.errors) return null;
    if (control.hasError('required') && controlName === 'name') {
      return 'El nombre de la categoría es obligatorio';
    }
    return null;
  }

  closeModal() {
    this.showModal.set(false);
    this.categoryForm.reset();
  }
}
