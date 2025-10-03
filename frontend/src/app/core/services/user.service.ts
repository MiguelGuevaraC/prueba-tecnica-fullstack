import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, CreateUserDto, UpdateUserDto } from '../interfaces/user.interface';
import { ApiResponse, PaginatedData } from '../../shared/interfaces/paginated-response.interface';

@Injectable({ providedIn: 'root' })
export class UsersService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/users`;

  getUsers(
    page: number,
    perPage: number,
    username?: string,
    fullName?: string,
    role?: string
  ): Observable<PaginatedData<User>> {
    let params = new HttpParams()
      .set('page', page)
      .set('per_page', perPage);

    if (username) params = params.set('username', username);
    if (fullName) params = params.set('fullName', fullName);
    if (role) params = params.set('role', role);

    return this.http
      .get<ApiResponse<PaginatedData<User>>>(this.API_URL, { params })
      .pipe(map((res) => res.data));
  }

  getById(id: number): Observable<{ data: User }> {
    return this.http.get<{ data: User }>(`${this.API_URL}/${id}`);
  }

  create(data: CreateUserDto): Observable<{ data: User }> {
    return this.http.post<{ data: User }>(this.API_URL, data);
  }

  update(id: number, data: UpdateUserDto | Partial<CreateUserDto>): Observable<{ data: User }> {
    return this.http.put<{ data: User }>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number): Observable<{ data: boolean }> {
    return this.http.delete<{ data: boolean }>(`${this.API_URL}/${id}`);
  }
}
