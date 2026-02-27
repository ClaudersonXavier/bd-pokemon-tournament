import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TorneioService } from '../../core/services/torneio.service';
import { TreinadorService } from '../../core/services/treinador.service';
import { Torneio, Batalha, Treinador, Time } from '../../core/models';

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
  styleUrl: './tournament-view.component.css'
})
export class TournamentViewComponent implements OnInit {
  activeTab: TournamentTab = 'bracket';
  torneio: Torneio | null = null;
  treinadores: Treinador[] = [];
  times: Time[] = [];
  batalhas: Batalha[] = [];
  loading = true;
  error = '';

  // Chaveamento
  bracketRounds: BracketRound[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private torneioService: TorneioService,
    private treinadorService: TreinadorService
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
      },
      error: (error: any) => {
        this.error = 'Não foi possível carregar os dados do torneio';
        this.loading = false;
        console.error('Erro ao carregar torneio:', error);
      }
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
      }
    });
  }

  loadParticipants(torneioId: number): void {
    // Por enquanto, vamos usar dados mock
    // TODO: Implementar endpoint para buscar participantes do torneio
    this.treinadorService.listarTreinadores().subscribe({
      next: (treinadores) => {
        // Filtrar apenas treinadores que participam deste torneio
        this.treinadores = treinadores.slice(0, 8); // Mock: pega os primeiros 8
        this.loadTeamsForTrainers();
      },
      error: (error) => {
        console.error('Erro ao carregar treinadores:', error);
        this.loading = false;
      }
    });
  }

  loadTeamsForTrainers(): void {
    // Carregar times de cada treinador
    if (!this.torneio || !this.torneio.times) {
      this.loading = false;
      return;
    }

    // Usar os times do torneio diretamente
    this.times = this.torneio.times;
    this.loading = false;
  }

  generateBracket(): void {
    if (!this.torneio) return;

    // Organizar batalhas por rodada
    const rounds = new Map<number, Batalha[]>();
    
    this.batalhas.forEach(batalha => {
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
          position: index
        }))
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
      const roundName = roundNames[numRounds - round - 1] || `Rodada ${round + 1}`;
      
      const matches: BracketMatch[] = [];
      for (let i = 0; i < matchesInRound; i++) {
        matches.push({
          id: round * 100 + i,
          position: i
        });
      }
      
      this.bracketRounds.push({
        name: roundName,
        matches
      });
    }
  }

  getTeamsForTrainer(treinadorId: number): Time[] {
    return this.times.filter(time => time.treinador.id === treinadorId);
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
      year: 'numeric' 
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
