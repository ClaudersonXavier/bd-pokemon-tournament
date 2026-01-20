package com.ufape.projetobanquinhobd.pokemonCatalogo.implPokeApi.dtos;

import java.util.List;

public record PokemonDTO(List<PokeApiType> types, String name, Sprites sprites) {
    public record Sprites(String front_default) {
    }
}
