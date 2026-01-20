package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Date dataInicio;

    @Column(nullable = false)
    private Date dataFim;

    @OneToMany(mappedBy = "torneio")
    private Set<Batalha> batalhas;

    @OneToMany(mappedBy = "torneio")
    private Set<Time> times;
}
