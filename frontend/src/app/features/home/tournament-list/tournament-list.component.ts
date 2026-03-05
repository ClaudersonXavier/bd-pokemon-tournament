import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../../core/services/auth.service';
import { TorneioService } from '../../../core/services/torneio.service';
import { TreinadorService } from '../../../core/services/treinador.service';
import { Time, Torneio } from '../../../core/models';
import { AppRoutes } from '../../../core/constants';

@Component({
  selector: 'app-tournament-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tournament-list.component.html',
  styleUrl: './tournament-list.component.css',
})
export class TournamentListComponent implements OnInit {
  @Input() currentUser: User | null = null;

  torneiosAbertos: Torneio[] = [];
  torneiosEmAndamento: Torneio[] = [];
  torneiosEncerrados: Torneio[] = [];
  loadingTorneios = true;
  errorTorneios = '';
  searchTorneio = '';
  filterStatus: 'ALL' | 'ABERTO' | 'EM_ANDAMENTO' | 'ENCERRADO' = 'ALL';
  timesDoUsuario: Time[] = [];
  loadingTimesDoUsuario = false;
  selectedTimePorTorneio: Record<number, number | null> = {};
  inscrevendoPorTorneio: Record<number, boolean> = {};

  constructor(
    private torneioService: TorneioService,
    private treinadorService: TreinadorService,
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadTorneios();
    this.loadTimesDoUsuario();
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

  loadTimesDoUsuario(): void {
    if (!this.currentUser) return;

    this.loadingTimesDoUsuario = true;

    this.treinadorService.listarTimesDoTreinador(this.currentUser.id).subscribe({
      next: (times: Time[]) => {
        this.timesDoUsuario = times;
        this.loadingTimesDoUsuario = false;
      },
      error: () => {
        this.timesDoUsuario = [];
        this.loadingTimesDoUsuario = false;
      },
    });
  }

  classificarTorneios(torneios: Torneio[]): void {
    this.torneiosAbertos = [];
    this.torneiosEmAndamento = [];
    this.torneiosEncerrados = [];

    torneios.forEach((torneio) => {
      const status = torneio.status;
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

  get torneiosAbertosFiltered(): Torneio[] {
    return this.filterTorneios(this.torneiosAbertos);
  }

  get torneiosEmAndamentoFiltered(): Torneio[] {
    return this.filterTorneios(this.torneiosEmAndamento);
  }

  get torneiosEncerradosFiltered(): Torneio[] {
    return this.filterTorneios(this.torneiosEncerrados);
  }

  private filterTorneios(torneios: Torneio[]): Torneio[] {
    return torneios.filter(torneio => {
      const matchesSearch = this.searchTorneio.trim() === '' ||
        torneio.nome.toLowerCase().includes(this.searchTorneio.toLowerCase());
      return matchesSearch;
    });
  }

  setFilterStatus(status: 'ALL' | 'ABERTO' | 'EM_ANDAMENTO' | 'ENCERRADO'): void {
    this.filterStatus = status;
  }

  clearTorneioFilters(): void {
    this.searchTorneio = '';
    this.filterStatus = 'ALL';
  }

  // ─── Torneio Actions ─────────────────────────────────────────────────────────

  inscreverNoTorneio(torneio: Torneio): void {
    if (!this.isInscricaoAberta(torneio)) {
      alert(`As inscrições para ${torneio.nome} estão encerradas.`);
      return;
    }

    const timeId = this.selectedTimePorTorneio[torneio.id];
    if (!timeId) {
      alert('Selecione um time para concluir a inscrição.');
      return;
    }

    this.inscrevendoPorTorneio[torneio.id] = true;

    this.torneioService.inscreverTimeNoTorneio(torneio.id, timeId).subscribe({
      next: () => {
        this.inscrevendoPorTorneio[torneio.id] = false;
        this.selectedTimePorTorneio[torneio.id] = null;
        alert(`Você se inscreveu no torneio: ${torneio.nome}`);
        this.loadTorneios();
      },
      error: (err) => {
        this.inscrevendoPorTorneio[torneio.id] = false;
        const message = this.extractApiErrorMessage(
          err,
          'Não foi possível realizar a inscrição. Tente novamente.',
        );
        alert(message);
      },
    });
  }

  verDetalhesTorneio(torneio: Torneio): void {
    this.router.navigate([AppRoutes.TOURNAMENT(torneio.id)]);
  }

  isInscricaoAberta(torneio: Torneio): boolean {
    return torneio.status === 'ABERTO';
  }

  getTimesDisponiveisParaInscricao(torneio: Torneio): Time[] {
    const idsJaInscritos = new Set((torneio.times || []).map((time) => time.id));
    return this.timesDoUsuario.filter((time) => !idsJaInscritos.has(time.id));
  }

  getSelectedTimeId(torneioId: number): number | null {
    return this.selectedTimePorTorneio[torneioId] ?? null;
  }

  selecionarTimeParaTorneio(torneioId: number, timeId: number | string | null): void {
    if (timeId === null || timeId === '' || Number.isNaN(Number(timeId))) {
      this.selectedTimePorTorneio[torneioId] = null;
      return;
    }
    this.selectedTimePorTorneio[torneioId] = Number(timeId);
  }

  podeInscreverNoTorneio(torneio: Torneio): boolean {
    if (!this.isInscricaoAberta(torneio)) return false;
    if (this.loadingTimesDoUsuario || this.inscrevendoPorTorneio[torneio.id]) return false;

    const selectedTimeId = this.selectedTimePorTorneio[torneio.id];
    if (!selectedTimeId) return false;

    return this.getTimesDisponiveisParaInscricao(torneio).some(
      (time) => time.id === selectedTimeId,
    );
  }

  getInscricaoButtonTooltip(torneio: Torneio): string {
    if (!this.isInscricaoAberta(torneio)) {
      return 'Inscrições encerradas';
    }
    if (this.loadingTimesDoUsuario) {
      return 'Carregando seus times...';
    }
    if (this.timesDoUsuario.length === 0) {
      return 'Você precisa criar um time primeiro';
    }
    if (this.getTimesDisponiveisParaInscricao(torneio).length === 0) {
      return 'Todos os seus times já estão inscritos';
    }
    const selectedTimeId = this.selectedTimePorTorneio[torneio.id];
    if (!selectedTimeId) {
      return 'Selecione um time no dropdown acima';
    }
    return 'Clique para se inscrever';
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

  private extractApiErrorMessage(err: any, fallback: string): string {
    return (
      err?.error?.message ||
      err?.error?.erro ||
      err?.error?.detail ||
      err?.message ||
      fallback
    );
  }
}
