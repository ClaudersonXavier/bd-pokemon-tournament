import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Torneio, Batalha } from '../models';

@Injectable({
  providedIn: 'root'
})
export class TorneioService {
  private apiUrl = 'http://localhost:8080/api/torneios';

  constructor(private http: HttpClient) {}

  // Métodos para Torneios
  listarTorneios(): Observable<Torneio[]> {
    return this.http.get<Torneio[]>(this.apiUrl);
  }

  buscarTorneio(id: number): Observable<Torneio> {
    return this.http.get<Torneio>(`${this.apiUrl}/${id}`);
  }

  criarTorneio(torneio: Torneio): Observable<Torneio> {
    return this.http.post<Torneio>(this.apiUrl, torneio);
  }

  atualizarTorneio(id: number, torneio: Torneio): Observable<Torneio> {
    return this.http.put<Torneio>(`${this.apiUrl}/${id}`, torneio);
  }

  deletarTorneio(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Métodos para Batalhas
  listarBatalhas(): Observable<Batalha[]> {
    return this.http.get<Batalha[]>(`${this.apiUrl}/batalhas`);
  }

  listarBatalhasPorTorneio(torneioId: number): Observable<Batalha[]> {
    return this.http.get<Batalha[]>(`${this.apiUrl}/${torneioId}/batalhas`);
  }

  buscarBatalha(id: number): Observable<Batalha> {
    return this.http.get<Batalha>(`${this.apiUrl}/batalhas/${id}`);
  }

  criarBatalha(batalha: Batalha): Observable<Batalha> {
    return this.http.post<Batalha>(`${this.apiUrl}/batalhas`, batalha);
  }

  atualizarBatalha(id: number, batalha: Batalha): Observable<Batalha> {
    return this.http.put<Batalha>(`${this.apiUrl}/batalhas/${id}`, batalha);
  }

  deletarBatalha(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/batalhas/${id}`);
  }
}
