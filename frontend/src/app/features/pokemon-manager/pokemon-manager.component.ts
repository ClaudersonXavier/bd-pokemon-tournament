import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, User } from '../../core/services/auth.service';
import { CatalogoService } from '../../core/services/catalogo.service';
import { TreinadorService } from '../../core/services/treinador.service';
import { Ataque, Especie, Pokemon } from '../../core/models';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-pokemon-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pokemon-manager.component.html',
  styleUrl: './pokemon-manager.component.css',
})
export class PokemonManagerComponent implements OnInit {
  currentUser: User | null = null;
  treinadorId?: number;

  pokemons: Pokemon[] = [];
  especies: Especie[] = [];
  ataquesCatalogo: Ataque[] = [];

  loadingPokemons = true;
  loadingEspecies = true;
  loadingAtaques = true;
  savingPokemon = false;
  deletingPokemonId: number | null = null;
  editingPokemonId: number | null = null;
  errorMessage = '';

  searchPokemon = '';

  newPokemon = {
    apelido: '',
    especieNome: '',
  };

  editForm = {
    apelido: '',
    especieNome: '',
    ataquesNomes: [] as string[],
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private treinadorService: TreinadorService,
    private catalogoService: CatalogoService,
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUser();
    if (!this.currentUser) {
      this.router.navigate(['/login']);
      return;
    }

    this.treinadorId = this.currentUser.id;
    this.loadEspecies();
    this.loadAtaquesCatalogo();
    this.loadPokemons();
  }

  get editingPokemon(): Pokemon | undefined {
    if (this.editingPokemonId === null) return undefined;
    return this.pokemons.find((pokemon) => pokemon.id === this.editingPokemonId);
  }

  get filteredPokemons(): Pokemon[] {
    const term = this.searchPokemon.trim().toLowerCase();
    if (!term) return this.pokemons;

    return this.pokemons.filter((pokemon) => {
      const apelido = (pokemon.apelido || '').toLowerCase();
      const especie = (pokemon.especie?.nome || '').toLowerCase();
      return apelido.includes(term) || especie.includes(term);
    });
  }

  loadEspecies(): void {
    this.loadingEspecies = true;
    this.catalogoService.listarEspecies().subscribe({
      next: (lista) => {
        this.especies = lista;
        this.loadingEspecies = false;

        if (!this.newPokemon.especieNome && lista.length > 0) {
          this.newPokemon.especieNome = lista[0].nome;
        }
      },
      error: () => {
        this.loadingEspecies = false;
        this.errorMessage = 'Nao foi possivel carregar o catalogo de especies.';
      },
    });
  }

  loadPokemons(): void {
    if (!this.treinadorId) return;

    this.loadingPokemons = true;
    this.errorMessage = '';

    this.treinadorService.listarPokemonsDoTreinador(this.treinadorId).subscribe({
      next: (lista) => {
        this.pokemons = lista;
        this.loadingPokemons = false;
      },
      error: () => {
        this.loadingPokemons = false;
        this.errorMessage = 'Nao foi possivel carregar seus pokemons.';
      },
    });
  }

  loadAtaquesCatalogo(): void {
    this.loadingAtaques = true;
    this.catalogoService.listarAtaques().subscribe({
      next: (lista) => {
        this.ataquesCatalogo = lista;
        this.loadingAtaques = false;
      },
      error: () => {
        this.loadingAtaques = false;
        this.errorMessage = 'Nao foi possivel carregar os ataques do catalogo.';
      },
    });
  }

  criarPokemon(): void {
    if (!this.treinadorId || this.savingPokemon) return;

    const especieNome = this.newPokemon.especieNome.trim();
    if (!especieNome) {
      this.errorMessage = 'Selecione uma especie para criar o pokemon.';
      return;
    }

    this.savingPokemon = true;
    this.errorMessage = '';

    this.treinadorService
      .criarPokemonParaTreinador(this.treinadorId, {
        apelido: this.newPokemon.apelido.trim() || null,
        especieNome,
      })
      .subscribe({
        next: (pokemon) => {
          this.pokemons = [pokemon, ...this.pokemons];
          this.newPokemon.apelido = '';
          this.savingPokemon = false;
        },
        error: (err) => {
          this.savingPokemon = false;
          this.errorMessage = this.extractErrorMessage(
            err,
            'Nao foi possivel criar o pokemon agora.',
          );
        },
      });
  }

  iniciarEdicao(event: Event, pokemon: Pokemon): void {
    event.stopPropagation();
    this.editingPokemonId = pokemon.id;
    this.editForm = {
      apelido: pokemon.apelido || '',
      especieNome: pokemon.especie?.nome || '',
      ataquesNomes: (pokemon.ataques || []).map((ataque) => ataque.nome),
    };
  }

  cancelarEdicao(): void {
    this.editingPokemonId = null;
    this.editForm = { apelido: '', especieNome: '', ataquesNomes: [] };
  }

  salvarEdicao(): void {
    if (!this.treinadorId || this.editingPokemonId === null || this.savingPokemon) return;

    const apelido = this.editForm.apelido.trim();
    const especieNome = this.editForm.especieNome.trim();

    if (!apelido) {
      this.errorMessage = 'O apelido do pokemon e obrigatorio.';
      return;
    }

    if (!especieNome) {
      this.errorMessage = 'Selecione uma especie valida.';
      return;
    }

    this.savingPokemon = true;
    this.errorMessage = '';

    this.treinadorService
      .atualizarPokemonDoTreinador(this.treinadorId, this.editingPokemonId, {
        apelido,
        especieNome,
      })
      .pipe(
        switchMap(() =>
          this.treinadorService.definirAtaquesDoPokemon(
            this.treinadorId!,
            this.editingPokemonId!,
            this.editForm.ataquesNomes,
          ),
        ),
      )
      .subscribe({
        next: (pokemonAtualizado) => {
          this.pokemons = this.pokemons.map((p) =>
            p.id === pokemonAtualizado.id ? pokemonAtualizado : p,
          );
          this.savingPokemon = false;
          this.cancelarEdicao();
        },
        error: (err) => {
          this.savingPokemon = false;
          this.errorMessage = this.extractErrorMessage(
            err,
            'Nao foi possivel atualizar o pokemon.',
          );
        },
      });
  }

  removerPokemon(event: Event, pokemon: Pokemon): void {
    event.stopPropagation();
    if (!this.treinadorId || this.deletingPokemonId) return;

    const confirmDelete = confirm(`Liberar o pokemon "${pokemon.apelido}"?`);
    if (!confirmDelete) return;

    this.deletingPokemonId = pokemon.id;
    this.errorMessage = '';

    this.treinadorService
      .deletarPokemonDoTreinador(this.treinadorId, pokemon.id)
      .subscribe({
        next: () => {
          this.pokemons = this.pokemons.filter((p) => p.id !== pokemon.id);
          this.deletingPokemonId = null;
        },
        error: (err) => {
          this.deletingPokemonId = null;
          this.errorMessage = this.extractErrorMessage(
            err,
            'Nao foi possivel remover o pokemon.',
            'Nao e possivel remover este pokemon porque ele esta em um time.',
          );
        },
      });
  }

  isAttackSelected(ataqueNome: string): boolean {
    return this.editForm.ataquesNomes.includes(ataqueNome);
  }

  toggleAttackSelection(event: Event, ataqueNome: string): void {
    event.stopPropagation();
    const alreadySelected = this.editForm.ataquesNomes.includes(ataqueNome);
    if (alreadySelected) {
      this.editForm.ataquesNomes = this.editForm.ataquesNomes.filter(
        (nome) => nome !== ataqueNome,
      );
      return;
    }

    if (this.editForm.ataquesNomes.length >= 4) {
      this.errorMessage = 'Um pokemon pode ter no maximo 4 ataques.';
      return;
    }

    this.editForm.ataquesNomes = [...this.editForm.ataquesNomes, ataqueNome];
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

  imageFor(pokemon: Pokemon): string {
    return pokemon.especie?.imagemUrl || '/images/pokemon-placeholder.png';
  }

  goHome(): void {
    this.router.navigate(['/home']);
  }
}
