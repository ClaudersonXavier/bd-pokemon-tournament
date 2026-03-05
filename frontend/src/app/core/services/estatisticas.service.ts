import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  TreinadorDesempenho,
  TimePokemonDetalhado,
  ResumoBatalhaTorneio,
  EstatisticasGerais,
  TopTreinador,
  TorneioResumo,
} from '../models/estatisticas';

export type { EstatisticasGerais, TopTreinador, TorneioResumo };

@Injectable({
  providedIn: 'root'
})
export class EstatisticasService {
  private apiUrl = `${environment.apiUrl}/estatisticas`;

  constructor(private http: HttpClient) {}

  obterEstatisticasGerais(): Observable<EstatisticasGerais> {
    return this.http.get<EstatisticasGerais>(`${this.apiUrl}/geral`);
  }

  obterTopTreinadores(): Observable<TopTreinador[]> {
    return this.http.get<TopTreinador[]>(`${this.apiUrl}/top-treinadores`);
  }

  obterResumoTorneios(): Observable<TorneioResumo[]> {
    return this.http.get<TorneioResumo[]>(`${this.apiUrl}/torneios-resumo`);
  }

  obterDesempenhoTreinadores(torneioId: number): Observable<TreinadorDesempenho[]> {
    return this.http.get<TreinadorDesempenho[]>(
      `${this.apiUrl}/torneio/${torneioId}/desempenho`
    );
  }

  obterPokemonsDoTime(timeId: number): Observable<TimePokemonDetalhado[]> {
    return this.http.get<TimePokemonDetalhado[]>(
      `${this.apiUrl}/time/${timeId}/pokemons`
    );
  }

  obterDesempenhoGeralTreinador(treinadorId: number): Observable<TreinadorDesempenho[]> {
    return this.http.get<TreinadorDesempenho[]>(
      `${this.apiUrl}/treinador/${treinadorId}/geral`
    );
  }

  obterResumoBatalhasTorneio(torneioId: number): Observable<ResumoBatalhaTorneio[]> {
    return this.http.get<ResumoBatalhaTorneio[]>(
      `${this.apiUrl}/torneio/${torneioId}/resumo-batalhas`
    );
  }
}
