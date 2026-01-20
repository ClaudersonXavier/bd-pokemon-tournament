package com.ufape.projetobanquinhobd.pokemonCatalogo.implPokeApi;

import com.ufape.projetobanquinhobd.entities.Especie;
import com.ufape.projetobanquinhobd.entities.Tipo;
import com.ufape.projetobanquinhobd.pokemonCatalogo.PokemonRepository;
import com.ufape.projetobanquinhobd.pokemonCatalogo.implPokeApi.dtos.PokemonDTO;
import com.ufape.projetobanquinhobd.pokemonCatalogo.implPokeApi.dtos.PokeApiType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class PokemonRepositoryImpl implements PokemonRepository {
    private final WebClient pokeApiClient;

    public PokemonRepositoryImpl(WebClient pokeApiClient) {
        this.pokeApiClient = pokeApiClient;
    }

    @Override
    public Especie findByNome(String nome) {
        PokemonDTO pokemonDTO = pokeApiClient.get()
                .uri("/pokemon/{name}", nome)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Pokemon não encontrado")
                ))
                .bodyToMono(PokemonDTO.class)
                .block();

        Especie especie = new Especie(
                pokemonDTO.name(),
                pokemonDTO.sprites().front_default()
        );

        for (PokeApiType types : pokemonDTO.types()) {
            Tipo tipo = new Tipo(types.type().name());
            especie.addTipo(tipo);
        }

        return especie;
    }
}
