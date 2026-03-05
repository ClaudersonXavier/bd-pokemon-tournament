import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TorneioService } from '../../core/services/torneio.service';
import { TreinadorService } from '../../core/services/treinador.service';
import { EstatisticasService } from '../../core/services/estatisticas.service';
import { AuthService } from '../../core/services/auth.service';
import { Torneio, Batalha, Treinador, Time, BracketRound, BracketMatch } from '../../core/models';
import {
  TreinadorDesempenho,
  TimePokemonDetalhado,
  ResumoBatalhaTorneio,
} from '../../core/models/estatisticas';
import { AppRoutes } from '../../core/constants';

type TournamentTab = 'bracket' | 'trainers';

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

  // Admin controls
  isAdmin = false;
  showWinnerModal = false;
  selectedBatalha: Batalha | null = null;
  selectedTime: Time | null = null;
  settingWinner = false;
  generatingRound = false;
  rodadasCompletas = new Map<number, boolean>();

  // Modal de seleção de modo de geração
  showGenerationModeModal = false;
  currentRodadaToGenerate: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private torneioService: TorneioService,
    private estatisticasService: EstatisticasService,
    private treinadorService: TreinadorService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    const torneioId = this.route.snapshot.paramMap.get('id');

    // Verificar se é admin
    const currentUser = this.authService.currentUser();
    this.isAdmin = currentUser?.admin || false;

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
        console.log('🏆 Torneio carregado:', torneio);
        console.log('📋 Times no torneio:', torneio.times);
        console.log('🔢 Quantidade de times:', torneio.times?.length || 0);
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

        // Verificar quais rodadas estão completas (apenas se for admin)
        if (this.isAdmin && this.torneio) {
          const rodadas = [...new Set(batalhas.map((b) => b.rodada))];
          rodadas.forEach((rodada) => this.verificarRodadaCompleta(rodada));
        }
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

    // Se não houver batalhas, criar estrutura mock APENAS para torneios ABERTOS
    // (para torneios EM_ANDAMENTO, mostrar empty-state com botão de gerar)
    if (
      this.bracketRounds.length === 0 &&
      this.torneio.maxParticipantes &&
      this.torneio.status === 'ABERTO'
    ) {
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

    // Verificar se é realmente a final (apenas 1 batalha na rodada)
    // e se essa batalha tem um vencedor
    if (finalRound.matches.length === 1 && finalMatch?.winner) {
      return finalMatch.winner;
    }

    return null;
  }

  isTournamentFinished(): boolean {
    if (!this.torneio) return false;

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    const fim = new Date(this.torneio.dataFim);
    fim.setHours(0, 0, 0, 0);

    return fim < hoje;
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
    this.router.navigate([AppRoutes.HOME]);
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

    // Usar o campo status do banco, não cálculos de datas
    switch (this.torneio.status) {
      case 'ABERTO':
        return '📝 Inscrições Abertas';
      case 'EM_ANDAMENTO':
        return '⚔️ Em Andamento';
      case 'ENCERRADO':
        return '🏆 Encerrado';
      default:
        return '';
    }
  }

  getTournamentStatusClass(): string {
    if (!this.torneio) return '';

    // Usar o campo status do banco, não cálculos de datas
    switch (this.torneio.status) {
      case 'ABERTO':
        return 'status-open';
      case 'EM_ANDAMENTO':
        return 'status-progress';
      case 'ENCERRADO':
        return 'status-closed';
      default:
        return '';
    }
  }

  // ─── Admin Functions ─────────────────────────────────────────────────────────

  isTournamentInProgress(): boolean {
    if (!this.torneio) return false;
    return this.torneio.status === 'EM_ANDAMENTO';
  }

  canManageBattle(): boolean {
    return this.isAdmin && this.isTournamentInProgress();
  }

  openWinnerModal(batalha: Batalha, time: Time): void {
    if (!this.canManageBattle() || batalha.timeVencedor) return;

    this.selectedBatalha = batalha;
    this.selectedTime = time;
    this.showWinnerModal = true;
  }

  closeWinnerModal(): void {
    this.showWinnerModal = false;
    this.selectedBatalha = null;
    this.selectedTime = null;
  }

  confirmWinner(): void {
    if (!this.selectedBatalha || !this.selectedTime) return;

    this.settingWinner = true;

    this.torneioService
      .definirVencedor(this.selectedBatalha.id, this.selectedTime.id)
      .subscribe({
        next: (batalhaAtualizada) => {
          // Atualizar a batalha no array local
          const index = this.batalhas.findIndex(
            (b) => b.id === batalhaAtualizada.id,
          );
          if (index !== -1) {
            this.batalhas[index] = batalhaAtualizada;
          }

          // Recriar o bracket com os dados atualizados
          this.generateBracket();

          // Verificar se a rodada ficou completa
          if (this.torneio) {
            this.verificarRodadaCompleta(this.selectedBatalha!.rodada);
          }

          this.settingWinner = false;
          this.closeWinnerModal();
        },
        error: (error) => {
          console.error('Erro ao definir vencedor:', error);
          alert('Erro ao definir vencedor da batalha');
          this.settingWinner = false;
        },
      });
  }

  verificarRodadaCompleta(rodada: number): void {
    if (!this.torneio) return;

    this.torneioService
      .verificarRodadaCompleta(this.torneio.id, rodada)
      .subscribe({
        next: (completa) => {
          this.rodadasCompletas.set(rodada, completa);
        },
        error: (error) => {
          console.error('Erro ao verificar rodada:', error);
        },
      });
  }

  isRodadaCompleta(rodada: number): boolean {
    return this.rodadasCompletas.get(rodada) || false;
  }

  proximaRodadaJaExiste(rodada: number): boolean {
    // Verificar se já existe a rodada seguinte (rodada + 1)
    const proximaRodada = rodada + 1;
    return this.batalhas.some((b) => b.rodada === proximaRodada);
  }

  gerarProximaRodada(rodada: number): void {
    if (!this.torneio || !this.isRodadaCompleta(rodada)) return;

    // Abrir modal para escolher o modo de geração
    this.currentRodadaToGenerate = rodada;
    this.showGenerationModeModal = true;
  }

  closeGenerationModeModal(): void {
    this.showGenerationModeModal = false;
    this.currentRodadaToGenerate = null;
  }

  confirmarGeracaoRodada(random: boolean): void {
    if (!this.torneio || this.currentRodadaToGenerate === null) return;

    const rodada = this.currentRodadaToGenerate;
    this.closeGenerationModeModal();
    this.generatingRound = true;

    this.torneioService
      .gerarProximaRodada(this.torneio.id, rodada, random)
      .subscribe({
        next: (novasBatalhas) => {
          console.log('Novas batalhas criadas:', novasBatalhas);

          // Adicionar as novas batalhas ao array
          this.batalhas.push(...novasBatalhas);

          // Recriar o bracket
          this.generateBracket();

          this.generatingRound = false;

          const modo = random ? 'aleatório' : 'ordem atual';
          alert(
            `${novasBatalhas.length} batalha(s) da próxima rodada criada(s) com sucesso (modo ${modo})!`,
          );
        },
        error: (error) => {
          console.error('Erro ao gerar próxima rodada:', error);
          alert(
            'Erro ao gerar próxima rodada. Verifique se todas as batalhas têm vencedor.',
          );
          this.generatingRound = false;
        },
      });
  }

  gerarPrimeiraRodada(): void {
    if (!this.torneio || !this.isTournamentInProgress()) return;

    // Para a primeira rodada, usar rodada 0 como base (será criada rodada 1)
    this.currentRodadaToGenerate = 0;
    this.showGenerationModeModal = true;
  }
}
