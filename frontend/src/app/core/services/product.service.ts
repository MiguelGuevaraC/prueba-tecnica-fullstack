import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product, CreateProductDto, UpdateProductDto } from '../interfaces/product.interface';
import { ApiResponse, PaginatedData } from '../../shared/interfaces/paginated-response.interface';

@Injectable({ providedIn: 'root' })
export class ProductsService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/products`;

  getProducts(
    page: number,
    perPage: number,
    name?: string,
    description?: string,
    categoryName?: string,
    userName?: string
  ): Observable<PaginatedData<Product>> {
    let params = new HttpParams()
      .set('page', page)
      .set('per_page', perPage);

    if (name) params = params.set('name', name);
    if (description) params = params.set('description', description);
    if (categoryName) params = params.set('category$name', categoryName);
    if (userName) params = params.set('user$fullName', userName);

    return this.http
      .get<ApiResponse<PaginatedData<Product>>>(this.API_URL, { params })
      .pipe(map((res) => res.data));
  }

  getById(id: number): Observable<{ data: Product }> {
    return this.http.get<{ data: Product }>(`${this.API_URL}/${id}`);
  }

  create(data: CreateProductDto): Observable<{ data: Product }> {
    return this.http.post<{ data: Product }>(this.API_URL, data);
  }

  update(id: number, data: UpdateProductDto | Partial<CreateProductDto>): Observable<{ data: Product }> {
    return this.http.put<{ data: Product }>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number): Observable<{ data: boolean }> {
    return this.http.delete<{ data: boolean }>(`${this.API_URL}/${id}`);
  }
}
