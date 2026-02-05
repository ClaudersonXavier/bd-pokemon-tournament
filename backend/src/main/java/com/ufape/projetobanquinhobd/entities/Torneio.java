package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
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
    private int  maxParticipantes;

    @Column(nullable = false)
    private Date dataAberturaInscricoes;

    @Column(nullable = false)
    private Date dataEncerramentoInscricoes;

    @Column(nullable = false)
    private Date dataInicio;

    @Column(nullable = false)
    private Date dataFim;

    @OneToMany(mappedBy = "torneio")
    private Set<Batalha> batalhas;

    @ManyToMany
    private Set<Time> times = new HashSet<>();

    public Torneio(String nome, int maxParticipantes, Date dataAberturaInscricoes, 
                   Date dataEncerramentoInscricoes, Date dataInicio, Date dataFim) {
        this.nome = nome;
        this.maxParticipantes = maxParticipantes;
        this.dataAberturaInscricoes = dataAberturaInscricoes;
        this.dataEncerramentoInscricoes = dataEncerramentoInscricoes;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public Set<Time> getTimes() {
        return times;
    }

}
