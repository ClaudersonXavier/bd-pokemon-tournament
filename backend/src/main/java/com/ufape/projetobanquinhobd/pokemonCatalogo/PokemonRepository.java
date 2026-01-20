package com.ufape.projetobanquinhobd.pokemonCatalogo;

import com.ufape.projetobanquinhobd.entities.Especie;

public interface PokemonRepository {
    Especie findByNome(String nome);
}
