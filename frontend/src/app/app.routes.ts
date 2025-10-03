import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth-guard';
import { authenticatedGuard } from './core/guards/authenticated-guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./shared/components/layout/layout').then((c) => c.LayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./business/dashboard/dashboard').then((c) => c.DashboardComponent),
        canActivate: [AuthGuard],
      },
      {
        path: 'usuarios',
        loadComponent: () => import('./business/users/users.component').then((c) => c.UsersComponent),
        canActivate: [AuthGuard],
      },
      {
        path: 'categorias',
        loadComponent: () => import('./business/categories/categories.component').then((c) => c.CategoriesComponent),
        canActivate: [AuthGuard],
      },
      {
        path: 'productos',
        loadComponent: () => import('./business/products/products.component').then((c) => c.ProductsComponent),
        canActivate: [AuthGuard],
      },
    ],
  },

  {
    path: 'login',
    loadComponent: () =>
      import('./business/authentication/login/login.component').then((c) => c.LoginComponent),
    canActivate: [authenticatedGuard],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
