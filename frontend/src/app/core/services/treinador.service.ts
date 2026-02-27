import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Treinador, Time, Pokemon } from '../models';

@Injectable({
  providedIn: 'root'
})
export class TreinadorService {
  private apiUrl = 'http://localhost:8080/api/treinadores';

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

  // Métodos para Times
  listarTimes(): Observable<Time[]> {
    return this.http.get<Time[]>(`${this.apiUrl}/times`);
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

  // Métodos para Pokemons
  listarPokemons(): Observable<Pokemon[]> {
    return this.http.get<Pokemon[]>(`${this.apiUrl}/pokemons`);
  }

  buscarPokemon(id: number): Observable<Pokemon> {
    return this.http.get<Pokemon>(`${this.apiUrl}/pokemons/${id}`);
  }

  criarPokemon(pokemon: Pokemon): Observable<Pokemon> {
    return this.http.post<Pokemon>(`${this.apiUrl}/pokemons`, pokemon);
  }

  atualizarPokemon(id: number, pokemon: Pokemon): Observable<Pokemon> {
    return this.http.put<Pokemon>(`${this.apiUrl}/pokemons/${id}`, pokemon);
  }

  deletarPokemon(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/pokemons/${id}`);
  }
}
