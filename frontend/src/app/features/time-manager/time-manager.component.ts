import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../core/services/auth.service';
import { TreinadorService } from '../../core/services/treinador.service';
import { Pokemon, Time } from '../../core/models';

@Component({
  selector: 'app-time-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './time-manager.component.html',
  styleUrls: ['./time-manager.component.css'],
})
export class TimeManagerComponent implements OnInit {
  currentUser: User | null = null;
  treinadorId?: number;

  times: Time[] = [];
  pokemons: Pokemon[] = [];

  loadingTimes = true;
  loadingPokemons = true;
  savingTime = false;
  deletingTimeId: number | null = null;
  errorMessage = '';

  searchTime = '';
  searchPokemon = '';

  newTime = {
    nome: '',
    pokemonIds: [] as number[],
  };

  editingTimeId: number | null = null;
  editForm = {
    nome: '',
    pokemonIds: [] as number[],
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private treinadorService: TreinadorService,
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUser();
    if (!this.currentUser) {
        this.router.navigate(['/login']);
        return;
    }

    this.treinadorId = this.currentUser.id;
    this.loadPokemons();
    this.loadTimes();
  }

  get filteredTimes(): Time[] {
    const term = this.searchTime.trim().toLowerCase();
    if (!term) return this.times;

    return this.times.filter((time) =>
      time.nome.toLowerCase().includes(term) ||
      (time.pokemons || []).some((p) =>
        (p.apelido || p.especie?.nome || '').toLowerCase().includes(term),
      ),
    );
  }

  get availablePokemons(): Pokemon[] {
    const term = this.searchPokemon.trim().toLowerCase();
    if (!term) return this.pokemons;
    return this.pokemons.filter((p) =>
      (p.apelido || '').toLowerCase().includes(term) ||
      (p.especie?.nome || '').toLowerCase().includes(term),
    );
  }

  loadPokemons(): void {
    if (!this.treinadorId) return;
    this.loadingPokemons = true;
    this.treinadorService
      .listarPokemonsDoTreinador(this.treinadorId)
      .subscribe({
        next: (lista) => {
          this.pokemons = lista;
          this.loadingPokemons = false;
        },
        error: () => {
          this.loadingPokemons = false;
          this.errorMessage = 'Não foi possível carregar seus pokémons.';
        },
      });
  }

  loadTimes(): void {
    if (!this.treinadorId) return;
    this.loadingTimes = true;
    this.errorMessage = '';

    this.treinadorService
      .listarTimesDoTreinador(this.treinadorId)
      .subscribe({
        next: (lista) => {
          this.times = lista;
          this.loadingTimes = false;
        },
        error: () => {
          this.loadingTimes = false;
          this.errorMessage = 'Não conseguimos carregar seus times agora.';
        },
      });
  }

  togglePokemonSelection(target: 'new' | 'edit', pokemonId: number): void {
    const list = target === 'new' ? this.newTime.pokemonIds : this.editForm.pokemonIds;

    const alreadySelected = list.includes(pokemonId);
    if (alreadySelected) {
      const updated = list.filter((id) => id !== pokemonId);
      if (target === 'new') this.newTime.pokemonIds = updated;
      else this.editForm.pokemonIds = updated;
      return;
    }

    if (list.length >= 6) {
      this.errorMessage = 'Um time pode ter no máximo 6 pokémons.';
      return;
    }

    const updated = [...list, pokemonId];
    if (target === 'new') this.newTime.pokemonIds = updated;
    else this.editForm.pokemonIds = updated;
  }

  isSelected(target: 'new' | 'edit', pokemonId: number): boolean {
    const list = target === 'new' ? this.newTime.pokemonIds : this.editForm.pokemonIds;
    return list.includes(pokemonId);
  }

  criarTime(): void {
    if (!this.treinadorId || this.savingTime) return;
    this.errorMessage = '';

    const nome = this.newTime.nome.trim();
    if (!nome) {
      this.errorMessage = 'O nome do time é obrigatório.';
      return;
    }

    this.savingTime = true;

    this.treinadorService
      .criarTimeParaTreinador(this.treinadorId, {
        nome,
        pokemonIds: this.newTime.pokemonIds,
      })
      .subscribe({
        next: (time) => {
          this.times = [time, ...this.times];
          this.newTime = { nome: '', pokemonIds: [] };
          this.savingTime = false;
        },
        error: (err) => {
          this.savingTime = false;
          this.errorMessage = this.extractErrorMessage(
            err,
            'Não foi possível criar o time agora.',
          );
        },
      });
  }

  iniciarEdicao(time: Time): void {
    this.editingTimeId = time.id;
    this.editForm = {
      nome: time.nome,
      pokemonIds: (time.pokemons || []).map((p) => p.id),
    };
  }

  cancelarEdicao(): void {
    this.editingTimeId = null;
    this.editForm = { nome: '', pokemonIds: [] };
  }

  salvarEdicao(): void {
    if (!this.treinadorId || this.editingTimeId === null) return;
    const nome = this.editForm.nome.trim();
    if (!nome) {
      this.errorMessage = 'O nome do time é obrigatório.';
      return;
    }

    if (this.editForm.pokemonIds.length > 6) {
      this.errorMessage = 'Um time pode ter no máximo 6 pokémons.';
      return;
    }

    this.savingTime = true;
    this.treinadorService
      .atualizarTimeDoTreinador(this.treinadorId, this.editingTimeId, {
        nome,
        pokemonIds: this.editForm.pokemonIds,
      })
      .subscribe({
        next: (timeAtualizado) => {
          this.times = this.times.map((t) =>
            t.id === timeAtualizado.id ? timeAtualizado : t,
          );
          this.savingTime = false;
          this.cancelarEdicao();
        },
        error: (err) => {
          this.savingTime = false;
          this.errorMessage = this.extractErrorMessage(
            err,
            'Não foi possível atualizar o time.',
          );
        },
      });
  }

  removerTime(time: Time): void {
    if (!this.treinadorId || this.deletingTimeId) return;

    const confirmDelete = confirm(`Liberar o time "${time.nome}"?`);
    if (!confirmDelete) return;

    this.deletingTimeId = time.id;
    this.treinadorService
      .deletarTimeDoTreinador(this.treinadorId, time.id)
      .subscribe({
        next: () => {
          this.times = this.times.filter((t) => t.id !== time.id);
          this.deletingTimeId = null;
        },
        error: (err) => {
          this.errorMessage = this.extractErrorMessage(
            err,
            'Não foi possível remover o time agora.',
            'Não é possível remover este time porque ele está em um torneio.',
          );
          this.deletingTimeId = null;
        },
      });
  }

  private extractErrorMessage(
    err: any,
    fallback: string,
    conflictFallback?: string,
  ): string {
    const backendMessage =
      err?.error?.message || err?.error?.detail || err?.error?.error;

    if (backendMessage) {
      return backendMessage;
    }

    if (err?.status === 409 && conflictFallback) {
      return conflictFallback;
    }

    return fallback;
  }

  sprite(pokemon: Pokemon): string {
    return (
      pokemon.especie?.imagemUrl || '/images/pokemon-placeholder.png'
    );
  }

  getEmptySlots(time: Time): number[] {
    const pokemonCount = time.pokemons?.length || 0;
    const emptyCount = Math.max(0, 6 - pokemonCount);
    return new Array(emptyCount);
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }
}
