package com.ufape.projetobanquinhobd.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResumoBatalhaTorneioDTO {
    private Long torneioId;
    private String torneioNome;
    private Long batalhaId;
    private Integer rodada;
    private LocalDateTime horarioInicio;
    private LocalDateTime horarioFim;
    private Double duracaoMinutos;
    private Long timeVencedorId;
    private String timeVencedorNome;
}
