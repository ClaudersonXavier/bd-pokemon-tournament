package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Torneio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private int qtdRodadas;

    @Column(nullable = false)
    private int qtdTimes;

    @Column(nullable = false)
    private Date dataInicio;

    @Column(nullable = false)
    private Date dataFim;

    @OneToMany(mappedBy = "torneio")
    private Set<Batalha> batalhas;

    @ManyToMany
    private Set<Time> times;
}
