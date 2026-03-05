import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../../core/services/auth.service';
import { TorneioService } from '../../../core/services/torneio.service';
import { TreinadorService } from '../../../core/services/treinador.service';
import { EstatisticasService } from '../../../core/services/estatisticas.service';
import {
  Torneio, Treinador,
  EstatisticasGerais, TopTreinador, TorneioResumo,
  TreinadorUserDTO,
} from '../../../core/models';
import { AppRoutes } from '../../../core/constants';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.css',
})
export class AdminPanelComponent implements OnInit {
  @Input() isAdmin = false;
  @Input() currentUser: User | null = null;

  readonly ADMIN_EMAIL = 'admin@pokemon.com';

  showStatistics = false;
  showUserManagement = false;
  showCreateTournament = false;
  searchTerm = '';
  filterRole = 'all';
  currentPage = 1;
  itemsPerPage = 10;
  selectedUser: TreinadorUserDTO | null = null;
  showEditModal = false;
  showDeleteModal = false;
  userToDelete: TreinadorUserDTO | null = null;

  // Torneios (needed for display in admin panel)
  torneiosAbertos: Torneio[] = [];
  torneiosEmAndamento: Torneio[] = [];
  torneiosEncerrados: Torneio[] = [];
  loadingTorneios = true;

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
    private treinadorService: TreinadorService,
    private estatisticasService: EstatisticasService,
    private torneioService: TorneioService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadTorneios();
  }

  // ─── Torneios (admin list) ────────────────────────────────────────────────

  loadTorneios(): void {
    this.loadingTorneios = true;
    this.torneioService.listarTorneios().subscribe({
      next: (torneios: Torneio[]) => {
        this.torneiosAbertos = [];
        this.torneiosEmAndamento = [];
        this.torneiosEncerrados = [];
        torneios.forEach((t) => {
          if (t.status === 'ENCERRADO') this.torneiosEncerrados.push(t);
          else if (t.status === 'EM_ANDAMENTO') this.torneiosEmAndamento.push(t);
          else this.torneiosAbertos.push(t);
        });
        this.loadingTorneios = false;
      },
      error: () => {
        this.loadingTorneios = false;
      },
    });
  }

  verDetalhesTorneio(torneio: Torneio): void {
    this.router.navigate([AppRoutes.TOURNAMENT(torneio.id)]);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
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
          alert('Erro ao salvar alterações. Tente novamente.');
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
        alert('Erro ao remover treinador. Tente novamente.');
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
      status: 'ABERTO',
    };

    this.torneioService.criarTorneio(torneioParaSalvar).subscribe({
      next: (torneio) => {
        this.savingTournament = false;
        alert(`Torneio "${torneio.nome}" criado com sucesso!`);
        this.loadTorneios();
        this.closeCreateTournament();
      },
      error: () => {
        this.savingTournament = false;
        alert('Erro ao criar torneio. Tente novamente.');
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

  // ─── Helpers ─────────────────────────────────────────────────────────────────

  private parseDateMs(dateValue: string): number | null {
    const parsedDate = this.parseDate(dateValue);
    if (!parsedDate) return null;
    parsedDate.setHours(0, 0, 0, 0);
    const timestamp = parsedDate.getTime();
    return Number.isNaN(timestamp) ? null : timestamp;
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
