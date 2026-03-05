import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../core/services/auth.service';
import { AppRoutes, REDIRECT_DELAY_MS, TRAINER_AVATAR_KEY } from '../../core/constants';
import { AdminPanelComponent } from './admin-panel/admin-panel.component';
import { TournamentListComponent } from './tournament-list/tournament-list.component';

type Tab = 'tournaments' | 'profile' | 'admin';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminPanelComponent, TournamentListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  currentUser: User | null = null;
  activeTab: Tab = 'tournaments';
  isAdmin = false;
  selectedTrainer = 'unknown.png';
  showChangePassword = false;

  // Alterar Senha
  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  };
  savingPassword = false;
  passwordError = '';
  passwordSuccess = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const userSignal = this.authService.currentUser();
    this.currentUser = userSignal;

    if (!this.currentUser) {
      this.router.navigate([AppRoutes.LOGIN]);
      return;
    }

    this.isAdmin = this.currentUser.admin;
    this.loadSelectedTrainer();
  }

  // ─── Alterar Senha ───────────────────────────────────────────────────────────

  openChangePassword(): void {
    this.showChangePassword = true;
    this.resetPasswordForm();
  }

  closeChangePassword(): void {
    this.showChangePassword = false;
    this.resetPasswordForm();
  }

  resetPasswordForm(): void {
    this.passwordForm = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    };
    this.passwordError = '';
    this.passwordSuccess = '';
  }

  saveNewPassword(): void {
    this.passwordError = '';
    this.passwordSuccess = '';

    if (!this.isPasswordFormValid) {
      this.passwordError = 'Por favor, preencha todos os campos.';
      return;
    }

    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.passwordError = 'As senhas não coincidem.';
      return;
    }

    if (this.passwordForm.newPassword.length < 6) {
      this.passwordError = 'A nova senha deve ter pelo menos 6 caracteres.';
      return;
    }

    this.savingPassword = true;

    this.authService
      .changePassword(
        this.passwordForm.currentPassword,
        this.passwordForm.newPassword,
      )
      .subscribe({
        next: () => {
          this.savingPassword = false;
          this.passwordSuccess = 'Senha alterada com sucesso!';
          setTimeout(() => this.closeChangePassword(), REDIRECT_DELAY_MS);
        },
        error: (err) => {
          this.savingPassword = false;
          const msg = err?.error?.erro;
          this.passwordError =
            msg || 'Erro ao alterar senha. Tente novamente.';
        },
      });
  }

  get isPasswordFormValid(): boolean {
    return !!(
      this.passwordForm.currentPassword.trim() &&
      this.passwordForm.newPassword.trim() &&
      this.passwordForm.confirmPassword.trim()
    );
  }

  get passwordStrength(): string {
    const password = this.passwordForm.newPassword;
    if (!password) return 'none';
    if (password.length < 6) return 'weak';
    if (password.length < 10) return 'medium';
    return 'strong';
  }

  // ─── Navegação ───────────────────────────────────────────────────────────────

  setActiveTab(tab: Tab): void {
    this.activeTab = tab;
  }

  logout(): void {
    this.authService.logout();
  }

  isTabActive(tab: Tab): boolean {
    return this.activeTab === tab;
  }

  goToEditProfile(): void {
    this.router.navigate([AppRoutes.EDIT_PROFILE]);
  }

  goToPokemons(): void {
    this.router.navigate([AppRoutes.POKEMONS]);
  }

  goToTimes(): void {
    this.router.navigate([AppRoutes.TIMES]);
  }

  goToReports(): void {
    this.router.navigate([AppRoutes.RELATORIOS]);
  }

  // ─── Trainer Avatar ──────────────────────────────────────────────────────────

  getSelectedTrainerPath(): string {
    return `/images/trainers/${this.selectedTrainer}`;
  }

  private loadSelectedTrainer(): void {
    const savedTrainer = localStorage.getItem(TRAINER_AVATAR_KEY(this.currentUser?.id || this.currentUser?.email || 'default'));
    if (savedTrainer) {
      this.selectedTrainer = savedTrainer;
    }
  }
}
