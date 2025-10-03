export interface User {
  id: number;
  username: string;
  fullName: string;
  role: 'ADMIN' | 'OTRO' | string; 
  createdAt: string; 
  updatedAt?: string;
}

export interface CreateUserDto {
  username: string;
  fullName: string;
  password: string;
  role: string;
}

export interface UpdateUserDto {
  fullName?: string;
  password?: string;
  role?: string;
  username: string;
}
