import { Component, OnInit, inject, signal, DestroyRef } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { debounceTime, switchMap, startWith, finalize } from 'rxjs/operators';
import { Subject, merge } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { ProductsService } from '../../core/services/product.service';
import { CategoriesService } from '../../core/services/category.service';
import { Product, CreateProductDto } from '../../core/interfaces/product.interface';
import { Category } from '../../core/interfaces/category.interface';
import { AlertService } from '../../core/services/alert.service';
import { PaginatedData } from '../../shared/interfaces/paginated-response.interface';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css'],
})
export class ProductsComponent implements OnInit {
  private productsService = inject(ProductsService);
  private categoriesService = inject(CategoriesService);
  private fb = inject(FormBuilder);
  private alert = inject(AlertService);
  private destroyRef = inject(DestroyRef);

  products = signal<Product[]>([]);
  loading = signal(false);
  page = signal(1);
  perPage = 10;
  total = signal(0);

  showModal = signal(false);
  isEdit = signal(false);
  currentId: number | null = null;

  selectedCategoryName = '';
  categoryOptions = signal<Category[]>([]);
  private categorySearch$ = new Subject<string>();
  private pageChanges$ = new Subject<void>();

  productForm: FormGroup = this.fb.group({
    name: this.fb.control<string>('', { nonNullable: true, validators: [Validators.required] }),
    description: this.fb.control<string>('', { nonNullable: true }),
    price: this.fb.control<number>(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    stock: this.fb.control<number>(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
    categoryId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
  });

  filterForm = this.fb.group({
    name: this.fb.control<string>('', { nonNullable: true }),
    description: this.fb.control<string>('', { nonNullable: true }),
    categoryName: this.fb.control<string>('', { nonNullable: true }),
    userName: this.fb.control<string>('', { nonNullable: true }),
  });

  ngOnInit(): void {
    merge(
      this.filterForm.valueChanges.pipe(startWith(this.filterForm.value), debounceTime(400)),
      this.pageChanges$
    )
      .pipe(
        switchMap(() => {
          this.loading.set(true);
          const { name, description, categoryName, userName } = this.filterForm.getRawValue();
          return this.productsService
            .getProducts(this.page(), this.perPage, name, description, categoryName, userName)
            .pipe(finalize(() => this.loading.set(false)));
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((res: PaginatedData<Product>) => {
        this.products.set(res.data);
        this.total.set(res.meta.total);
      });

    this.categorySearch$
      .pipe(
        debounceTime(300),
        switchMap((term) => this.categoriesService.getCategories(1, 10, term))
      )
      .subscribe((res: PaginatedData<Category>) => {
        this.categoryOptions.set(res.data);
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
    this.productForm.reset({ price: 0, stock: 0 });
    this.selectedCategoryName = '';
    this.showModal.set(true);
  }

  openEdit(product: Product) {
    this.isEdit.set(true);
    this.currentId = product.id;
    this.productForm.patchValue({
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      categoryId: product.categoryId,
    });
    this.selectedCategoryName = (product as any).categoryName ?? '';
    this.showModal.set(true);
  }

  saveProduct() {
    if (this.productForm.invalid) return;
    const formValue = this.productForm.getRawValue();

    if (this.isEdit() && this.currentId) {
      this.productsService
        .update(this.currentId, formValue)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => {
          this.alert.success('Éxito', 'Producto actualizado');
          this.pageChanges$.next();
          this.closeModal();
        });
    } else {
      const data: CreateProductDto = {
        name: formValue.name,
        description: formValue.description,
        price: formValue.price,
        stock: formValue.stock,
        categoryId: formValue.categoryId!,
      };
      this.productsService
        .create(data)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => {
          this.alert.success('Éxito', 'Producto creado');
          this.pageChanges$.next();
          this.closeModal();
        });
    }
  }

  deleteProduct(product: Product) {
    this.alert
      .confirm('¿Eliminar?', `¿Está seguro de eliminar el producto "${product.name}"?`)
      .then((result) => {
        if (result.isConfirmed) {
          this.productsService
            .delete(product.id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(() => {
              this.alert.success('Eliminado', 'El producto ha sido eliminado correctamente');
              this.products.update((list) => list.filter((p) => p.id !== product.id));
              this.total.update((t) => t - 1);
            });
        }
      });
  }

  onCategorySearch(event: Event) {
    const term = (event.target as HTMLInputElement).value;
    this.selectedCategoryName = term;
    this.categorySearch$.next(term);
  }

  selectCategory(option: Category) {
    this.productForm.patchValue({ categoryId: option.id });
    this.selectedCategoryName = option.name;
    this.categoryOptions.set([]);
  }

  getError(controlName: 'name' | 'description' | 'price' | 'stock' | 'categoryId'): string | null {
    const control = this.productForm.get(controlName);
    if (!control || !control.touched || !control.errors) return null;
    if (control.hasError('required')) {
      switch (controlName) {
        case 'name':
          return 'El nombre es obligatorio';
        case 'price':
          return 'El precio es obligatorio';
        case 'stock':
          return 'El stock es obligatorio';
        case 'categoryId':
          return 'Debe seleccionar una categoría';
      }
    }
    if (control.hasError('min')) {
      return 'El valor debe ser mayor o igual a 0';
    }
    return null;
  }

  closeModal() {
    this.showModal.set(false);
    this.productForm.reset();
    this.selectedCategoryName = '';
  }
}
