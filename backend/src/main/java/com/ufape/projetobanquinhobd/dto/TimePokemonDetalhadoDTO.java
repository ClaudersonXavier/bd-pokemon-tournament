package com.ufape.projetobanquinhobd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimePokemonDetalhadoDTO {
    private Long timeId;
    private String timeNome;
    private Long treinadorId;
    private String treinadorNome;
    private Long pokemonId;
    private String pokemonApelido;
    private String especieNome;
    private String especieImagemUrl;
    private String tipos;
}
