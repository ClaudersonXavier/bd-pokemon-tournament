import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Especie, Tipo, Ataque } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CatalogoService {
  private apiUrl = `${environment.apiUrl}/catalogo`;

  constructor(private http: HttpClient) {}

  // Métodos para Espécies
  listarEspecies(): Observable<Especie[]> {
    return this.http.get<Especie[]>(`${this.apiUrl}/especies`);
  }

  buscarEspecie(nome: string): Observable<Especie> {
    return this.http.get<Especie>(`${this.apiUrl}/especies/${nome}`);
  }

  criarEspecie(especie: Especie): Observable<Especie> {
    return this.http.post<Especie>(`${this.apiUrl}/especies`, especie);
  }

  deletarEspecie(nome: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/especies/${nome}`);
  }

  // Métodos para Tipos
  listarTipos(): Observable<Tipo[]> {
    return this.http.get<Tipo[]>(`${this.apiUrl}/tipos`);
  }

  buscarTipo(nome: string): Observable<Tipo> {
    return this.http.get<Tipo>(`${this.apiUrl}/tipos/${nome}`);
  }

  criarTipo(tipo: Tipo): Observable<Tipo> {
    return this.http.post<Tipo>(`${this.apiUrl}/tipos`, tipo);
  }

  deletarTipo(nome: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tipos/${nome}`);
  }

  // Métodos para Ataques
  listarAtaques(): Observable<Ataque[]> {
    return this.http.get<Ataque[]>(`${this.apiUrl}/ataques`);
  }

  buscarAtaque(nome: string): Observable<Ataque> {
    return this.http.get<Ataque>(`${this.apiUrl}/ataques/${nome}`);
  }

  criarAtaque(ataque: Ataque): Observable<Ataque> {
    return this.http.post<Ataque>(`${this.apiUrl}/ataques`, ataque);
  }

  deletarAtaque(nome: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/ataques/${nome}`);
  }
}
