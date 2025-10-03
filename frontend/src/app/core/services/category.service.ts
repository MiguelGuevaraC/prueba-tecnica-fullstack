import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Category,
  CreateCategoryDto,
  UpdateCategoryDto,
} from '../interfaces/category.interface';
import {
  ApiResponse,
  PaginatedData,
} from '../../shared/interfaces/paginated-response.interface';

@Injectable({ providedIn: 'root' })
export class CategoriesService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/categories`;

  getCategories(
    page: number,
    perPage: number,
    name?: string,
    description?: string,
    userName?: string
  ): Observable<PaginatedData<Category>> {
    let params = new HttpParams()
      .set('page', page)
      .set('per_page', perPage);

    if (name) params = params.set('name', name);
    if (description) params = params.set('description', description);
    if (userName) params = params.set('userName', userName);

    return this.http
      .get<ApiResponse<PaginatedData<Category>>>(this.API_URL, { params })
      .pipe(map((res) => res.data));
  }

  getById(id: number): Observable<{ data: Category }> {
    return this.http.get<{ data: Category }>(`${this.API_URL}/${id}`);
  }

  create(data: CreateCategoryDto): Observable<{ data: Category }> {
    return this.http.post<{ data: Category }>(this.API_URL, data);
  }

  update(id: number, data: UpdateCategoryDto | Partial<CreateCategoryDto>): Observable<{ data: Category }> {
    return this.http.put<{ data: Category }>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number): Observable<{ data: boolean }> {
    return this.http.delete<{ data: boolean }>(`${this.API_URL}/${id}`);
  }
}
