// Interfaces para entidades do backend

export interface Tipo {
  nome: string;
}

export interface Especie {
  nome: string;
  imagemUrl: string;
  tipos: Tipo[];
}

export interface Ataque {
  nome: string;
  categoria: string;
  poder: number;
  tipo: Tipo;
}

export interface CredenciaisUsuario {
  email: string;
  senha: string;
}

export interface Pokemon {
  id: number;
  apelido: string;
  especie: Especie;
  ataques: Ataque[];
  treinador: Treinador;
  times: Time[];
}

export interface Time {
  id: number;
  nome: string;
  treinador: Treinador;
  pokemons: Pokemon[];
  torneios: Torneio[];
}

export interface Treinador {
  id: number;
  nome: string;
  credenciais: CredenciaisUsuario;
  times: Time[];
  pokemons: Pokemon[];
}

export interface Batalha {
  id: number;
  rodada: number;
  horarioInicio: string; // LocalDateTime será string no JSON
  horarioFim: string; // LocalDateTime será string no JSON
  timesParticipantes: Time[];
  torneio: Torneio;
  timeVencedor?: Time;
}

export interface Torneio {
  id: number;
  nome: string;
  statusAtual?: 'ABERTO' | 'EM_ANDAMENTO' | 'ENCERRADO';
  inscricoesAbertas?: boolean;
  maxParticipantes: number;
  dataAberturaInscricoes: string; // Date será string no JSON
  dataEncerramentoInscricoes: string;
  dataInicio: string;
  dataFim: string;
  batalhas: Batalha[];
  times: Time[];
}

// Estatísticas
export * from './estatisticas';
