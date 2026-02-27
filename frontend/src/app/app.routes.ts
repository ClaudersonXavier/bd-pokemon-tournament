import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component')
        .then(m => m.LoginComponent)
  },
  {
    path: 'recover-password',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component')
        .then(m => m.RegisterComponent)
  },
  {
    path: 'home',
    loadComponent: () =>
      import('./features/home/home.component')
        .then(m => m.HomeComponent)
  },
  {
    path: 'tournament/:id',
    loadComponent: () =>
      import('./features/tournament-view/tournament-view.component')
        .then(m => m.TournamentViewComponent)
  },
  {
    path: 'edit-profile',
    loadComponent: () =>
      import('./features/profile/edit-profile/edit-profile.component')
        .then(m => m.EditProfileComponent)
  },
  {
    path: 'dashboard',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'home' }
];