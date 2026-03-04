import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EstatisticasService } from '../../core/services/estatisticas.service';
import { TreinadorService } from '../../core/services/treinador.service';
import { TorneioService } from '../../core/services/torneio.service';
import {
  ResumoBatalhaTorneio,
  Time,
  TimePokemonDetalhado,
  Torneio,
  TreinadorDesempenho,
} from '../../core/models';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css',
})
export class ReportsComponent implements OnInit {
  loadingDesempenho = false;
  loadingResumoBatalhas = false;
  loadingPokemonsTime = false;

  errorDesempenho = '';
  errorResumoBatalhas = '';
  errorPokemonsTime = '';

  torneios: Torneio[] = [];
  times: Time[] = [];

  torneioDesempenhoSelecionadoId: number | null = null;
  torneioResumoSelecionadoId: number | null = null;
  timeSelecionadoId: number | null = null;

  desempenhoTorneio: TreinadorDesempenho[] = [];
  resumoBatalhasTorneio: ResumoBatalhaTorneio[] = [];
  pokemonsDoTime: TimePokemonDetalhado[] = [];

  constructor(
    private estatisticasService: EstatisticasService,
    private torneioService: TorneioService,
    private treinadorService: TreinadorService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.carregarEntidadesBase();
  }

  voltarHome(): void {
    this.router.navigate(['/home']);
  }

  carregarEntidadesBase(): void {
    this.torneioService.listarTorneios().subscribe({
      next: (data) => {
        this.torneios = data;

        if (this.torneios.length > 0) {
          this.torneioDesempenhoSelecionadoId =
            this.torneioDesempenhoSelecionadoId ?? this.torneios[0].id;
          this.torneioResumoSelecionadoId =
            this.torneioResumoSelecionadoId ?? this.torneios[0].id;
          this.carregarDesempenhoTorneio();
          this.carregarResumoBatalhasTorneio();
        }
      },
      error: () => {
        this.torneios = [];
      },
    });

    this.treinadorService.listarTimes().subscribe({
      next: (data) => {
        this.times = data;

        if (this.times.length > 0) {
          this.timeSelecionadoId = this.timeSelecionadoId ?? this.times[0].id;
          this.carregarPokemonsDoTime();
        }
      },
      error: () => {
        this.times = [];
      },
    });
  }

  onTorneioDesempenhoSelecionado(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.torneioDesempenhoSelecionadoId = value ? Number(value) : null;
    this.carregarDesempenhoTorneio();
  }

  onTorneioResumoSelecionado(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.torneioResumoSelecionadoId = value ? Number(value) : null;
    this.carregarResumoBatalhasTorneio();
  }

  onTimeSelecionado(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.timeSelecionadoId = value ? Number(value) : null;
    this.carregarPokemonsDoTime();
  }

  carregarDesempenhoTorneio(): void {
    if (!this.torneioDesempenhoSelecionadoId) {
      this.desempenhoTorneio = [];
      return;
    }

    this.loadingDesempenho = true;
    this.errorDesempenho = '';

    this.estatisticasService
      .obterDesempenhoTreinadores(this.torneioDesempenhoSelecionadoId)
      .subscribe({
        next: (data) => {
          this.desempenhoTorneio = data;
          this.loadingDesempenho = false;
        },
        error: () => {
          this.desempenhoTorneio = [];
          this.errorDesempenho =
            'Não foi possível carregar o desempenho dos treinadores.';
          this.loadingDesempenho = false;
        },
      });
  }

  carregarResumoBatalhasTorneio(): void {
    if (!this.torneioResumoSelecionadoId) {
      this.resumoBatalhasTorneio = [];
      return;
    }

    this.loadingResumoBatalhas = true;
    this.errorResumoBatalhas = '';

    this.estatisticasService
      .obterResumoBatalhasTorneio(this.torneioResumoSelecionadoId)
      .subscribe({
        next: (data) => {
          this.resumoBatalhasTorneio = data;
          this.loadingResumoBatalhas = false;
        },
        error: () => {
          this.resumoBatalhasTorneio = [];
          this.errorResumoBatalhas =
            'Não foi possível carregar o resumo das batalhas do torneio.';
          this.loadingResumoBatalhas = false;
        },
      });
  }

  carregarPokemonsDoTime(): void {
    if (!this.timeSelecionadoId) {
      this.pokemonsDoTime = [];
      return;
    }

    this.loadingPokemonsTime = true;
    this.errorPokemonsTime = '';

    this.estatisticasService.obterPokemonsDoTime(this.timeSelecionadoId).subscribe({
      next: (data) => {
        this.pokemonsDoTime = data;
        this.loadingPokemonsTime = false;
      },
      error: () => {
        this.pokemonsDoTime = [];
        this.errorPokemonsTime =
          'Não foi possível carregar os pokémons detalhados do time.';
        this.loadingPokemonsTime = false;
      },
    });
  }

  formatDateTime(dateString: string | null | undefined): string {
    if (!dateString) return '-';

    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) return '-';

    return date.toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  formatPercent(value: number | null | undefined): string {
    if (value === null || value === undefined || Number.isNaN(value)) return '0%';
    return `${value.toFixed(2)}%`;
  }

  formatDuration(minutes: number | null | undefined): string {
    if (minutes === null || minutes === undefined || Number.isNaN(minutes)) {
      return '-';
    }

    return `${minutes.toFixed(1)} min`;
  }
}
