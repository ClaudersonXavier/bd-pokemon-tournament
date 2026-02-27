import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../core/services/auth.service';
import { TorneioService } from '../../core/services/torneio.service';
import { TreinadorService } from '../../core/services/treinador.service';
import {
  EstatisticasService,
  EstatisticasGerais,
  TopTreinador,
  TorneioResumo,
} from '../../core/services/estatisticas.service';
import { Torneio, Treinador } from '../../core/models';

export interface TreinadorUserDTO {
  id: number;
  nome: string;
  email: string;
  isAdmin: boolean;
}

type Tab = 'tournaments' | 'profile' | 'admin';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  currentUser: User | null = null;
  activeTab: Tab = 'tournaments';
  isAdmin = false;
  selectedTrainer = 'unknown.png';
  showStatistics = false;
  showUserManagement = false;
  showCreateTournament = false;
  showChangePassword = false;
  searchTerm = '';
  filterRole = 'all';
  currentPage = 1;
  itemsPerPage = 10;
  selectedUser: TreinadorUserDTO | null = null;
  showEditModal = false;
  showDeleteModal = false;
  userToDelete: TreinadorUserDTO | null = null;
  readonly ADMIN_EMAIL = 'admin@pokemon.com';

  // Torneios
  torneiosAbertos: Torneio[] = [];
  torneiosEmAndamento: Torneio[] = [];
  torneiosEncerrados: Torneio[] = [];
  loadingTorneios = true;
  errorTorneios = '';

  // Treinadores (user management)
  treinadores: TreinadorUserDTO[] = [];
  loadingTreinadores = false;
  errorTreinadores = '';
  savingUser = false;
  deletingUser = false;

  // Estatísticas
  loadingStats = false;
  statsGerais: EstatisticasGerais | null = null;
  topTreinadores: TopTreinador[] = [];
  resumoTorneios: TorneioResumo[] = [];

  // Alterar Senha
  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  };
  savingPassword = false;
  passwordError = '';
  passwordSuccess = '';

  // Criar Torneio
  newTournament = {
    name: '',
    startDate: '',
    endDate: '',
    registrationStartDate: '',
    registrationEndDate: '',
    maxParticipants: 16,
  };
  savingTournament = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private torneioService: TorneioService,
    private treinadorService: TreinadorService,
    private estatisticasService: EstatisticasService,
  ) {}

  ngOnInit(): void {
    const userSignal = this.authService.currentUser();
    this.currentUser = userSignal;

    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.isAdmin = this.currentUser.admin;
    this.loadSelectedTrainer();
    this.loadTorneios();
  }

  // ─── Torneios ────────────────────────────────────────────────────────────────

  loadTorneios(): void {
    this.loadingTorneios = true;
    this.errorTorneios = '';

    this.torneioService.listarTorneios().subscribe({
      next: (torneios: Torneio[]) => {
        this.classificarTorneios(torneios);
        this.loadingTorneios = false;
      },
      error: () => {
        this.errorTorneios =
          'Não foi possível carregar os torneios. Tente novamente mais tarde.';
        this.loadingTorneios = false;
      },
    });
  }

  classificarTorneios(torneios: Torneio[]): void {
    this.torneiosAbertos = [];
    this.torneiosEmAndamento = [];
    this.torneiosEncerrados = [];

    torneios.forEach((torneio) => {
      const status = this.getStatusTorneio(torneio);
      if (status === 'ENCERRADO') {
        this.torneiosEncerrados.push(torneio);
      } else if (status === 'EM_ANDAMENTO') {
        this.torneiosEmAndamento.push(torneio);
      } else {
        this.torneiosAbertos.push(torneio);
      }
    });

    this.torneiosAbertos.sort(
      (a, b) =>
        new Date(a.dataInicio).getTime() - new Date(b.dataInicio).getTime(),
    );
    this.torneiosEmAndamento.sort(
      (a, b) =>
        new Date(a.dataInicio).getTime() - new Date(b.dataInicio).getTime(),
    );
    this.torneiosEncerrados.sort(
      (a, b) => new Date(b.dataFim).getTime() - new Date(a.dataFim).getTime(),
    );
  }

  // ─── User Management ─────────────────────────────────────────────────────────

  loadTreinadores(): void {
    this.loadingTreinadores = true;
    this.errorTreinadores = '';

    this.treinadorService.listarTreinadores().subscribe({
      next: (treinadores: Treinador[]) => {
        this.treinadores = treinadores.map((t) => ({
          id: t.id,
          nome: t.nome,
          email: (t as any).credenciais?.email ?? '',
          isAdmin:
            ((t as any).credenciais?.email ?? '').toLowerCase() ===
            this.ADMIN_EMAIL,
        }));
        this.loadingTreinadores = false;
      },
      error: () => {
        this.errorTreinadores = 'Não foi possível carregar os treinadores.';
        this.loadingTreinadores = false;
      },
    });
  }

  get filteredUsers(): TreinadorUserDTO[] {
    return this.treinadores.filter((user) => {
      const matchesSearch =
        user.nome.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesRole =
        this.filterRole === 'all' ||
        (this.filterRole === 'ADMIN' && user.isAdmin) ||
        (this.filterRole === 'USER' && !user.isAdmin);
      return matchesSearch && matchesRole;
    });
  }

  get paginatedUsers(): TreinadorUserDTO[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredUsers.slice(start, start + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredUsers.length / this.itemsPerPage);
  }

  get userOverview() {
    return {
      totalTreinadores: this.treinadores.length,
      totalAdmins: this.treinadores.filter((u) => u.isAdmin).length,
    };
  }

  toggleUserManagement(): void {
    this.showUserManagement = !this.showUserManagement;
    if (this.showUserManagement && this.treinadores.length === 0) {
      this.loadTreinadores();
    }
  }

  closeUserManagement(): void {
    this.showUserManagement = false;
    this.searchTerm = '';
    this.filterRole = 'all';
    this.currentPage = 1;
  }

  editUser(user: TreinadorUserDTO): void {
    this.selectedUser = { ...user };
    this.showEditModal = true;
  }

  saveUser(): void {
    if (!this.selectedUser || this.savingUser) return;
    this.savingUser = true;

    this.treinadorService
      .atualizarNomeTreinador(this.selectedUser.id, this.selectedUser.nome)
      .subscribe({
        next: () => {
          const idx = this.treinadores.findIndex(
            (u) => u.id === this.selectedUser!.id,
          );
          if (idx !== -1) {
            this.treinadores[idx] = { ...this.selectedUser! };
          }
          this.savingUser = false;
          this.closeEditModal();
        },
        error: () => {
          this.savingUser = false;
          alert('❌ Erro ao salvar alterações. Tente novamente.');
        },
      });
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedUser = null;
  }

  confirmDelete(user: TreinadorUserDTO): void {
    this.userToDelete = user;
    this.showDeleteModal = true;
  }

  deleteUser(): void {
    if (!this.userToDelete || this.deletingUser) return;
    this.deletingUser = true;

    this.treinadorService.deletarTreinador(this.userToDelete.id).subscribe({
      next: () => {
        this.treinadores = this.treinadores.filter(
          (u) => u.id !== this.userToDelete!.id,
        );
        this.deletingUser = false;
        this.closeDeleteModal();
      },
      error: () => {
        this.deletingUser = false;
        alert('❌ Erro ao remover treinador. Tente novamente.');
      },
    });
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.userToDelete = null;
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.filterRole = 'all';
    this.currentPage = 1;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  // ─── Estatísticas ────────────────────────────────────────────────────────────

  loadStatistics(): void {
    this.loadingStats = true;

    this.estatisticasService.obterEstatisticasGerais().subscribe({
      next: (data) => {
        this.statsGerais = data;
      },
      error: () => {
        this.statsGerais = null;
      },
    });

    this.estatisticasService.obterTopTreinadores().subscribe({
      next: (data) => {
        this.topTreinadores = data;
      },
      error: () => {
        this.topTreinadores = [];
      },
    });

    this.estatisticasService.obterResumoTorneios().subscribe({
      next: (data) => {
        this.resumoTorneios = data;
        this.loadingStats = false;
      },
      error: () => {
        this.resumoTorneios = [];
        this.loadingStats = false;
      },
    });
  }

  toggleStatistics(): void {
    this.showStatistics = !this.showStatistics;
    if (this.showStatistics) {
      this.loadStatistics();
    }
  }

  closeStatistics(): void {
    this.showStatistics = false;
  }

  // ─── Criar Torneio ───────────────────────────────────────────────────────────

  toggleCreateTournament(): void {
    this.showCreateTournament = !this.showCreateTournament;
    if (this.showCreateTournament) {
      this.resetTournamentForm();
    }
  }

  closeCreateTournament(): void {
    this.showCreateTournament = false;
    this.resetTournamentForm();
  }

  resetTournamentForm(): void {
    this.newTournament = {
      name: '',
      startDate: '',
      endDate: '',
      registrationStartDate: '',
      registrationEndDate: '',
      maxParticipants: 16,
    };
  }

  saveTournament(): void {
    if (!this.isTournamentFormValid || this.savingTournament) return;
    this.savingTournament = true;

    const torneioParaSalvar: any = {
      nome: this.newTournament.name,
      maxParticipantes: this.newTournament.maxParticipants,
      dataAberturaInscricoes: this.newTournament.registrationStartDate,
      dataEncerramentoInscricoes: this.newTournament.registrationEndDate,
      dataInicio: this.newTournament.startDate,
      dataFim: this.newTournament.endDate,
    };

    this.torneioService.criarTorneio(torneioParaSalvar).subscribe({
      next: (torneio) => {
        this.savingTournament = false;
        alert(`✅ Torneio "${torneio.nome}" criado com sucesso!`);
        this.loadTorneios();
        this.closeCreateTournament();
      },
      error: () => {
        this.savingTournament = false;
        alert('❌ Erro ao criar torneio. Tente novamente.');
      },
    });
  }

  get isTournamentFormValid(): boolean {
    const registrationStart = this.parseDateMs(
      this.newTournament.registrationStartDate,
    );
    const registrationEnd = this.parseDateMs(
      this.newTournament.registrationEndDate,
    );
    const startDate = this.parseDateMs(this.newTournament.startDate);
    const endDate = this.parseDateMs(this.newTournament.endDate);
    const hasValidDateOrder =
      registrationStart !== null &&
      registrationEnd !== null &&
      startDate !== null &&
      endDate !== null &&
      registrationStart <= registrationEnd &&
      registrationEnd <= startDate &&
      startDate <= endDate;

    return !!(
      this.newTournament.name.trim() &&
      this.newTournament.startDate &&
      this.newTournament.endDate &&
      this.newTournament.registrationStartDate &&
      this.newTournament.registrationEndDate &&
      hasValidDateOrder &&
      this.newTournament.maxParticipants > 0
    );
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
          setTimeout(() => this.closeChangePassword(), 1500);
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
    this.router.navigate(['/edit-profile']);
  }

  // ─── Torneio Actions ─────────────────────────────────────────────────────────

  inscreverNoTorneio(torneio: Torneio): void {
    if (!this.isInscricaoAberta(torneio)) {
      alert(`As inscrições para ${torneio.nome} estão encerradas.`);
      return;
    }
    alert(`Você se inscreveu no torneio: ${torneio.nome}`);
  }

  verDetalhesTorneio(torneio: Torneio): void {
    this.router.navigate(['/tournament', torneio.id]);
  }

  isInscricaoAberta(torneio: Torneio): boolean {
    if (typeof torneio.inscricoesAbertas === 'boolean') {
      return torneio.inscricoesAbertas;
    }

    const agora = this.getTodayMs();
    const abertura = this.parseDateMs(torneio.dataAberturaInscricoes);
    const encerramento = this.parseDateMs(torneio.dataEncerramentoInscricoes);

    if (abertura === null || encerramento === null) return false;
    return agora >= abertura && agora <= encerramento;
  }

  // ─── Trainer Avatar ──────────────────────────────────────────────────────────

  getSelectedTrainerPath(): string {
    return `/images/trainers/${this.selectedTrainer}`;
  }

  private loadSelectedTrainer(): void {
    const savedTrainer = localStorage.getItem(this.getTrainerStorageKey());
    if (savedTrainer) {
      this.selectedTrainer = savedTrainer;
    }
  }

  private getTrainerStorageKey(): string {
    const userId = this.currentUser?.id || this.currentUser?.email || 'default';
    return `trainer-avatar-${userId}`;
  }

  // ─── Helpers ─────────────────────────────────────────────────────────────────

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }

  private getStatusTorneio(
    torneio: Torneio,
  ): 'ABERTO' | 'EM_ANDAMENTO' | 'ENCERRADO' {
    if (
      torneio.statusAtual === 'ABERTO' ||
      torneio.statusAtual === 'EM_ANDAMENTO' ||
      torneio.statusAtual === 'ENCERRADO'
    ) {
      return torneio.statusAtual;
    }

    const agora = this.getTodayMs();
    const inicio = this.parseDateMs(torneio.dataInicio);
    const fim = this.parseDateMs(torneio.dataFim);

    if (fim !== null && agora > fim) return 'ENCERRADO';
    if (inicio !== null && agora >= inicio) return 'EM_ANDAMENTO';
    return 'ABERTO';
  }

  private parseDateMs(dateValue: string): number | null {
    const parsedDate = this.parseDate(dateValue);
    if (!parsedDate) return null;
    parsedDate.setHours(0, 0, 0, 0);
    const timestamp = parsedDate.getTime();
    return Number.isNaN(timestamp) ? null : timestamp;
  }

  private getTodayMs(): number {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today.getTime();
  }

  private parseDate(dateValue: string): Date | null {
    const onlyDateMatch = /^(\d{4})-(\d{2})-(\d{2})$/.exec(dateValue);
    if (onlyDateMatch) {
      const year = Number(onlyDateMatch[1]);
      const month = Number(onlyDateMatch[2]) - 1;
      const day = Number(onlyDateMatch[3]);
      return new Date(year, month, day);
    }

    const parsed = new Date(dateValue);
    return Number.isNaN(parsed.getTime()) ? null : parsed;
  }
}
