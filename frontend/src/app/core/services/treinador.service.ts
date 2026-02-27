import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Treinador, Time, Pokemon, Ataque } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TreinadorService {
  private apiUrl = `${environment.apiUrl}/treinadores`;

  constructor(private http: HttpClient) {}

  // Métodos para Treinadores
  listarTreinadores(): Observable<Treinador[]> {
    return this.http.get<Treinador[]>(this.apiUrl);
  }

  buscarTreinador(id: number): Observable<Treinador> {
    return this.http.get<Treinador>(`${this.apiUrl}/${id}`);
  }

  criarTreinador(treinador: Treinador): Observable<Treinador> {
    return this.http.post<Treinador>(this.apiUrl, treinador);
  }

  atualizarTreinador(id: number, treinador: Treinador): Observable<Treinador> {
    return this.http.put<Treinador>(`${this.apiUrl}/${id}`, treinador);
  }

  deletarTreinador(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  atualizarNomeTreinador(id: number, nome: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/nome`, { nome });
  }

  // Métodos para Times
  listarTimes(): Observable<Time[]> {
    return this.http.get<Time[]>(`${this.apiUrl}/times`);
  }

  listarTimesDoTreinador(treinadorId: number): Observable<Time[]> {
    return this.http.get<Time[]>(`${this.apiUrl}/${treinadorId}/times`);
  }

  buscarTime(id: number): Observable<Time> {
    return this.http.get<Time>(`${this.apiUrl}/times/${id}`);
  }

  criarTime(time: Time): Observable<Time> {
    return this.http.post<Time>(`${this.apiUrl}/times`, time);
  }

  atualizarTime(id: number, time: Time): Observable<Time> {
    return this.http.put<Time>(`${this.apiUrl}/times/${id}`, time);
  }

  deletarTime(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/times/${id}`);
  }

  criarTimeParaTreinador(
    treinadorId: number,
    payload: { nome: string; pokemonIds?: number[] },
  ): Observable<Time> {
    return this.http.post<Time>(`${this.apiUrl}/${treinadorId}/times`, payload);
  }

  atualizarTimeDoTreinador(
    treinadorId: number,
    timeId: number,
    payload: { nome?: string; pokemonIds?: number[] },
  ): Observable<Time> {
    return this.http.put<Time>(
      `${this.apiUrl}/${treinadorId}/times/${timeId}`,
      payload,
    );
  }

  deletarTimeDoTreinador(treinadorId: number, timeId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${treinadorId}/times/${timeId}`);
  }

  // Métodos para Pokemons
  listarPokemonsDoTreinador(treinadorId: number): Observable<Pokemon[]> {
    return this.http.get<Pokemon[]>(`${this.apiUrl}/${treinadorId}/pokemons`);
  }

  criarPokemonParaTreinador(
    treinadorId: number,
    payload: { apelido?: string | null; especieNome: string },
  ): Observable<Pokemon> {
    return this.http.post<Pokemon>(
      `${this.apiUrl}/${treinadorId}/pokemons`,
      payload,
    );
  }

  atualizarPokemonDoTreinador(
    treinadorId: number,
    pokemonId: number,
    payload: { apelido?: string | null; especieNome?: string | null },
  ): Observable<Pokemon> {
    return this.http.put<Pokemon>(
      `${this.apiUrl}/${treinadorId}/pokemons/${pokemonId}`,
      payload,
    );
  }

  deletarPokemonDoTreinador(
    treinadorId: number,
    pokemonId: number,
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${treinadorId}/pokemons/${pokemonId}`,
    );
  }

  listarAtaquesDoPokemon(treinadorId: number, pokemonId: number): Observable<Ataque[]> {
    return this.http.get<Ataque[]>(
      `${this.apiUrl}/${treinadorId}/pokemons/${pokemonId}/ataques`,
    );
  }

  definirAtaquesDoPokemon(
    treinadorId: number,
    pokemonId: number,
    ataquesNomes: string[],
  ): Observable<Pokemon> {
    return this.http.put<Pokemon>(
      `${this.apiUrl}/${treinadorId}/pokemons/${pokemonId}/ataques`,
      { ataquesNomes },
    );
  }

  adicionarAtaqueAoPokemon(
    treinadorId: number,
    pokemonId: number,
    ataqueNome: string,
  ): Observable<Pokemon> {
    return this.http.post<Pokemon>(
      `${this.apiUrl}/${treinadorId}/pokemons/${pokemonId}/ataques/${encodeURIComponent(ataqueNome)}`,
      {},
    );
  }

  removerAtaqueDoPokemon(
    treinadorId: number,
    pokemonId: number,
    ataqueNome: string,
  ): Observable<Pokemon> {
    return this.http.delete<Pokemon>(
      `${this.apiUrl}/${treinadorId}/pokemons/${pokemonId}/ataques/${encodeURIComponent(ataqueNome)}`,
    );
  }
}
