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
