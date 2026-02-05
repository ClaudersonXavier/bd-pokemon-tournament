package com.ufape.projetobanquinhobd.pokemonCatalogo.implPokeApi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PokeApiClient {
    @Bean
    public WebClient webClient() {
        // Aumentar o limite de buffer para 10MB (os JSONs da PokeAPI são grandes)
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                .build();
        
        return WebClient.builder()
                .baseUrl("https://pokeapi.co/api/v2")
                .exchangeStrategies(strategies)
                .build();
    }
}
