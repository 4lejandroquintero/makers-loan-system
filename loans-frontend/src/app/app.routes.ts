import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { UserLoans } from './features/loans/user-loans/user-loans';
import { AdminLoans } from './features/admin/admin-loans/admin-loans';
import { authGuard, adminGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'loans', component: UserLoans, canActivate: [authGuard] },
  { path: 'admin', component: AdminLoans, canActivate: [adminGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];