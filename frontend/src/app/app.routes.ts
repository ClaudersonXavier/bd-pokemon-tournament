import { Routes } from '@angular/router';
import { authGuard, loginGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(
        (m) => m.LoginComponent,
      ),
    canActivate: [loginGuard],
  },
  {
    path: 'recover-password',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(
        (m) => m.RegisterComponent,
      ),
    canActivate: [loginGuard],
  },
  {
    path: 'home',
    loadComponent: () =>
      import('./features/home/home.component').then((m) => m.HomeComponent),
    canActivate: [authGuard],
  },
  {
    path: 'pokemons',
    loadComponent: () =>
      import('./features/pokemon-manager/pokemon-manager.component').then(
        (m) => m.PokemonManagerComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'times',
    loadComponent: () =>
      import('./features/time-manager/time-manager.component').then(
        (m) => m.TimeManagerComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'tournament/:id',
    loadComponent: () =>
      import('./features/tournament-view/tournament-view.component').then(
        (m) => m.TournamentViewComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'edit-profile',
    loadComponent: () =>
      import('./features/profile/edit-profile/edit-profile.component').then(
        (m) => m.EditProfileComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'relatorios',
    loadComponent: () =>
      import('./features/reports/reports.component').then(
        (m) => m.ReportsComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'dashboard',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' },
];
