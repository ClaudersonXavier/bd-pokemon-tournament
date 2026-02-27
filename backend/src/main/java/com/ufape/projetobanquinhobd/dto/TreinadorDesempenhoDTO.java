package com.ufape.projetobanquinhobd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TreinadorDesempenhoDTO {
    private Long treinadorId;
    private String treinadorNome;
    private Long torneioId;
    private String torneioNome;
    private Long timeId;
    private String timeNome;
    private Integer totalBatalhas;
    private Integer totalVitorias;
    private Integer totalDerrotas;
    private Double percentualVitorias;
}
