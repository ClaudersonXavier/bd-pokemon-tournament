package com.ufape.projetobanquinhobd.pokemonCatalogo.implPokeApi.dtos;

public record PokeApiType(Name type) {
    public record Name(String name) {
    }
}
