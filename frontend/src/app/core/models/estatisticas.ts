export interface TreinadorDesempenho {
  treinadorId: number;
  treinadorNome: string;
  torneioId: number;
  torneioNome: string;
  timeId: number;
  timeNome: string;
  totalBatalhas: number;
  totalVitorias: number;
  totalDerrotas: number;
  percentualVitorias: number;
}

export interface TimePokemonDetalhado {
  timeId: number;
  timeNome: string;
  treinadorId: number;
  treinadorNome: string;
  pokemonId: number;
  pokemonApelido: string;
  especieNome: string;
  especieImagemUrl: string;
  tipos: string;
}

export interface ResumoBatalhaTorneio {
  torneioId: number;
  torneioNome: string;
  batalhaId: number;
  rodada: number;
  horarioInicio: string;
  horarioFim: string;
  duracaoMinutos: number;
  timeVencedorId: number | null;
  timeVencedorNome: string | null;
}

export interface EstatisticasGerais {
  totalTreinadores: number;
  totalTorneios: number;
  totalBatalhas: number;
}

export interface TopTreinador {
  treinador_id: number;
  treinador_nome: string;
  total_batalhas: number;
  total_vitorias: number;
  total_derrotas: number;
  percentual_vitorias: number;
}

export interface TorneioResumo {
  torneio_id: number;
  torneio_nome: string;
  data_inicio: string;
  data_fim: string;
  total_participantes: number;
  total_batalhas: number;
}
