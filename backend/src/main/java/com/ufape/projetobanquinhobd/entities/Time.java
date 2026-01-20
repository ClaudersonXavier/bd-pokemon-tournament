package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Time {
    @Id
    private long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne
    private Treinador treinador;

    @OneToMany(mappedBy = "time")
    private Set<Pokemon> pokemons;

    @ManyToOne
    private Torneio torneio;
}
