package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Batalha {
    @Id
    private Long id;

    @Column(nullable = false)
    private int rodada;

    @Column(nullable = false)
    private LocalDateTime horarioFim;

    @Column(nullable = false)
    private LocalDateTime horarioInicio;

    @ManyToMany
    private Set<Time> timesParticipantes;

    @ManyToOne
    private Torneio torneio;

    @OneToOne
    private Time timeVencedor;
}

