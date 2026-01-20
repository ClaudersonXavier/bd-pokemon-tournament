package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Batalha {
    @Id
    private Long id;

    @Column(nullable = false)
    private String rodada; // Acho que essa não é o melhor tipo para representar isso, mas não sei qual seria, um enum talvez? um número?

    @Column(nullable = false)
    private int HoraFim;

    @Column(nullable = false)
    private int HoraInicio;

    @ManyToMany
    private Set<Time> timesParticipantes;

    @ManyToOne
    private Torneio torneio;

    @OneToOne
    private Time timeVencedor;
}

