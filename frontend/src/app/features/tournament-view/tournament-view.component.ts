import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TorneioService } from '../../core/services/torneio.service';
import { TreinadorService } from '../../core/services/treinador.service';
import { EstatisticasService } from '../../core/services/estatisticas.service';
import { Torneio, Batalha, Treinador, Time } from '../../core/models';
import {
  TreinadorDesempenho,
  TimePokemonDetalhado,
  ResumoBatalhaTorneio,
} from '../../core/models/estatisticas';

type TournamentTab = 'bracket' | 'trainers';

interface BracketRound {
  name: string;
  matches: BracketMatch[];
}

interface BracketMatch {
  id: number;
  team1?: Time;
  team2?: Time;
  winner?: Time;
  batalha?: Batalha;
  position: number;
}

@Component({
  selector: 'app-tournament-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tournament-view.component.html',
  styleUrl: './tournament-view.component.css',
})
export class TournamentViewComponent implements OnInit {
  activeTab: TournamentTab = 'bracket';
  torneio: Torneio | null = null;
  treinadores: Treinador[] = [];
  treinadoresDesempenho: TreinadorDesempenho[] = [];
  times: Time[] = [];
  batalhas: Batalha[] = [];
  loading = true;
  error = '';

  // Chaveamento
  bracketRounds: BracketRound[] = [];

  // Pokémons dos times (cache)
  timePokemonsMap = new Map<number, TimePokemonDetalhado[]>();
  timeExpandido: number | null = null;

  // Resumo de batalhas do torneio
  resumoBatalhas: ResumoBatalhaTorneio[] = [];
  estatisticasTorneio = {
    totalBatalhas: 0,
    duracaoMedia: 0,
    batalhasFinalizadas: 0,
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private torneioService: TorneioService,
    private estatisticasService: EstatisticasService,
    private treinadorService: TreinadorService,
  ) {}

  ngOnInit(): void {
    const torneioId = this.route.snapshot.paramMap.get('id');
    if (torneioId) {
      this.loadTournamentData(parseInt(torneioId, 10));
    } else {
      this.error = 'ID do torneio não encontrado';
      this.loading = false;
    }
  }

  loadTournamentData(id: number): void {
    this.loading = true;

    // Carregar dados do torneio
    this.torneioService.buscarTorneio(id).subscribe({
      next: (torneio: Torneio) => {
        this.torneio = torneio;
        this.loadBattles(id);
        this.loadParticipants(id);
        this.loadResumoBatalhas(id);
      },
      error: (error: any) => {
        this.error = 'Não foi possível carregar os dados do torneio';
        this.loading = false;
        console.error('Erro ao carregar torneio:', error);
      },
    });
  }

  loadBattles(torneioId: number): void {
    this.torneioService.listarBatalhasPorTorneio(torneioId).subscribe({
      next: (batalhas: Batalha[]) => {
        this.batalhas = batalhas;
        this.generateBracket();
      },
      error: (error: any) => {
        console.error('Erro ao carregar batalhas:', error);
      },
    });
  }

  loadParticipants(torneioId: number): void {
    // Carregar desempenho dos treinadores usando a view
    this.estatisticasService.obterDesempenhoTreinadores(torneioId).subscribe({
      next: (desempenho) => {
        console.log(
          '🔍 Dados brutos da view v_treinador_desempenho_torneio:',
          desempenho,
        );
        this.treinadoresDesempenho = desempenho;

        // Se tem desempenho, extrair treinadores e construir times
        if (desempenho && desempenho.length > 0) {
          const treinadoresMap = new Map<number, Treinador>();
          const timesMap = new Map<number, Time>();

          // Primeiro, criar os treinadores
          desempenho.forEach((d) => {
            if (!treinadoresMap.has(d.treinadorId)) {
              const treinador: Treinador = {
                id: d.treinadorId,
                nome: d.treinadorNome,
                credenciais: { email: '', senha: '' },
                times: [],
                pokemons: [],
              };
              treinadoresMap.set(d.treinadorId, treinador);
            }

            // Criar os times com referência ao treinador
            if (d.timeId && !timesMap.has(d.timeId)) {
              const treinador = treinadoresMap.get(d.treinadorId)!;
              const time: Time = {
                id: d.timeId,
                nome: d.timeNome,
                treinador: treinador,
                pokemons: [],
                torneios: [],
              };
              timesMap.set(d.timeId, time);
            }
          });

          this.treinadores = Array.from(treinadoresMap.values());
          this.times = Array.from(timesMap.values());

          console.log('Treinadores carregados:', this.treinadores);
          console.log('Times carregados:', this.times);
        } else if (this.torneio && this.torneio.times) {
          // Fallback: extrair treinadores dos times do torneio
          const treinadoresMap = new Map<number, Treinador>();
          this.torneio.times.forEach((time) => {
            if (time.treinador && !treinadoresMap.has(time.treinador.id)) {
              treinadoresMap.set(time.treinador.id, time.treinador);
            }
          });
          this.treinadores = Array.from(treinadoresMap.values());
          this.times = this.torneio.times;
        }

        this.loading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar desempenho dos treinadores:', error);

        // Fallback: tentar extrair dos times do torneio
        if (
          this.torneio &&
          this.torneio.times &&
          this.torneio.times.length > 0
        ) {
          const treinadoresMap = new Map<number, Treinador>();
          this.torneio.times.forEach((time) => {
            if (time.treinador && !treinadoresMap.has(time.treinador.id)) {
              treinadoresMap.set(time.treinador.id, time.treinador);
            }
          });
          this.treinadores = Array.from(treinadoresMap.values());
          this.times = this.torneio.times;
        }

        this.loading = false;
      },
    });
  }

  generateBracket(): void {
    if (!this.torneio) return;

    // Organizar batalhas por rodada
    const rounds = new Map<number, Batalha[]>();

    this.batalhas.forEach((batalha) => {
      const rodada = batalha.rodada || 1;
      if (!rounds.has(rodada)) {
        rounds.set(rodada, []);
      }
      rounds.get(rodada)!.push(batalha);
    });

    // Converter para estrutura de bracket
    this.bracketRounds = Array.from(rounds.entries())
      .sort((a, b) => a[0] - b[0])
      .map(([rodada, batalhas]) => ({
        name: `Rodada ${rodada}`,
        matches: batalhas.map((batalha, index) => ({
          id: batalha.id,
          team1: batalha.timesParticipantes[0],
          team2: batalha.timesParticipantes[1],
          winner: batalha.timeVencedor,
          batalha: batalha,
          position: index,
        })),
      }));

    // Se não houver batalhas, criar estrutura mock baseada no número de participantes
    if (this.bracketRounds.length === 0 && this.torneio.maxParticipantes) {
      this.generateMockBracket(this.torneio.maxParticipantes);
    }
  }

  generateMockBracket(maxParticipants: number): void {
    // Determinar número de rodadas baseado no número de participantes
    const numRounds = Math.ceil(Math.log2(maxParticipants));
    const roundNames = ['Oitavas', 'Quartas', 'Semifinais', 'Final'];

    this.bracketRounds = [];

    for (let round = 0; round < numRounds; round++) {
      const matchesInRound = Math.pow(2, numRounds - round - 1);
      const nameIndex = roundNames.length - numRounds + round;
      const roundName = roundNames[nameIndex] || `Rodada ${round + 1}`;

      const matches: BracketMatch[] = [];
      for (let i = 0; i < matchesInRound; i++) {
        matches.push({
          id: round * 100 + i,
          position: i,
        });
      }

      this.bracketRounds.push({
        name: roundName,
        matches,
      });
    }
  }

  getTeamsForTrainer(treinadorId: number): Time[] {
    return this.times.filter((time) => time.treinador.id === treinadorId);
  }

  getDesempenhoTreinador(treinadorId: number): TreinadorDesempenho | undefined {
    return this.treinadoresDesempenho.find(
      (d) => d.treinadorId === treinadorId,
    );
  }

  getTotalVitorias(treinadorId: number): number {
    // Somar vitórias de todos os times do treinador
    const registros = this.treinadoresDesempenho.filter(
      (d) => d.treinadorId === treinadorId,
    );
    return registros.reduce((total, d) => total + (d.totalVitorias || 0), 0);
  }

  getPercentualVitorias(treinadorId: number): number {
    // Calcular percentual baseado no total agregado
    const totalBatalhas = this.getTotalBatalhas(treinadorId);
    const totalVitorias = this.getTotalVitorias(treinadorId);

    if (totalBatalhas === 0) return 0;

    return Math.round((totalVitorias / totalBatalhas) * 100 * 100) / 100;
  }

  getTotalBatalhas(treinadorId: number): number {
    // Somar batalhas de todos os times do treinador
    const registros = this.treinadoresDesempenho.filter(
      (d) => d.treinadorId === treinadorId,
    );
    return registros.reduce((total, d) => total + (d.totalBatalhas || 0), 0);
  }

  getCampeao(): Time | null {
    if (this.bracketRounds.length === 0) return null;

    // O campeão é o vencedor da última rodada (Final)
    const finalRound = this.bracketRounds[this.bracketRounds.length - 1];
    const finalMatch = finalRound.matches[0];

    return finalMatch?.winner || null;
  }

  loadResumoBatalhas(torneioId: number): void {
    this.estatisticasService.obterResumoBatalhasTorneio(torneioId).subscribe({
      next: (resumo) => {
        this.resumoBatalhas = resumo;
        // Calcular estatísticas gerais
        this.estatisticasTorneio.totalBatalhas = resumo.length;
        this.estatisticasTorneio.batalhasFinalizadas = resumo.filter(
          (b) => b.timeVencedorId !== null,
        ).length;

        const totalDuracao = resumo
          .filter((b) => b.duracaoMinutos > 0)
          .reduce((acc, b) => acc + b.duracaoMinutos, 0);
        const batalhesComDuracao = resumo.filter(
          (b) => b.duracaoMinutos > 0,
        ).length;
        this.estatisticasTorneio.duracaoMedia =
          batalhesComDuracao > 0
            ? Math.round(totalDuracao / batalhesComDuracao)
            : 0;
      },
      error: (error) => {
        console.error('Erro ao carregar resumo de batalhas:', error);
      },
    });
  }

  toggleTimeExpansion(timeId: number): void {
    if (this.timeExpandido === timeId) {
      // Se já está expandido, recolhe
      this.timeExpandido = null;
    } else {
      // Expande e carrega pokémons se ainda não carregou
      this.timeExpandido = timeId;
      if (!this.timePokemonsMap.has(timeId)) {
        this.loadPokemonsDoTime(timeId);
      }
    }
  }

  loadPokemonsDoTime(timeId: number): void {
    this.estatisticasService.obterPokemonsDoTime(timeId).subscribe({
      next: (pokemons) => {
        this.timePokemonsMap.set(timeId, pokemons);
      },
      error: (error) => {
        console.error('Erro ao carregar pokémons do time:', error);
      },
    });
  }

  getPokemonsDoTime(timeId: number): TimePokemonDetalhado[] {
    return this.timePokemonsMap.get(timeId) || [];
  }

  isTimeExpandido(timeId: number): boolean {
    return this.timeExpandido === timeId;
  }

  setActiveTab(tab: TournamentTab): void {
    this.activeTab = tab;
  }

  isTabActive(tab: TournamentTab): boolean {
    return this.activeTab === tab;
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }

  formatDate(date: Date | string): string {
    const d = new Date(date);
    return d.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }

  getTournamentStatus(): string {
    if (!this.torneio) return '';

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    const inicio = new Date(this.torneio.dataInicio);
    const fim = new Date(this.torneio.dataFim);

    if (inicio > hoje) return '📝 Inscrições Abertas';
    if (inicio <= hoje && hoje <= fim) return '⚔️ Em Andamento';
    return '🏆 Encerrado';
  }

  getTournamentStatusClass(): string {
    if (!this.torneio) return '';

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    const inicio = new Date(this.torneio.dataInicio);
    const fim = new Date(this.torneio.dataFim);

    if (inicio > hoje) return 'status-open';
    if (inicio <= hoje && hoje <= fim) return 'status-progress';
    return 'status-closed';
  }
}