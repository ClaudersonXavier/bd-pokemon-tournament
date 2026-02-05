package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne
    private Treinador treinador;

    @ManyToMany
    private final Set<Pokemon> pokemons = new HashSet<>();

    @ManyToMany
    private final Set<Torneio> torneios = new HashSet<>();

    private static final int MAX_POKEMONS_IN_TEAM = 6;

    public Time(String nome, Treinador treinador) {
        this.setNome(nome);
        this.setTreinador(treinador);
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do time é obrigatório");
        }
        this.nome = nome;
    }

    public void setTreinador(Treinador treinador) {
        if (treinador == null) {
            throw new IllegalArgumentException("Treinador do time é obrigatório");
        }
        this.treinador = treinador;
    }

    public void addPokemon(Pokemon pokemon) {
        if (this.pokemons.size() >= MAX_POKEMONS_IN_TEAM) {
            throw new IllegalArgumentException("Time não pode ter mais que " + MAX_POKEMONS_IN_TEAM + " pokémons");
        }
        this.pokemons.add(pokemon);
    }

    public void removePokemon(Pokemon pokemon) {
        this.pokemons.remove(pokemon);
    }

    public void addTorneio(Torneio torneio) {
        if (torneio == null) {
            throw new IllegalArgumentException("O torneio para adicionar não pode ser nulo");
        }

        this.torneios.add(torneio);
    }

    public void removeTorneio(Torneio torneio) {
        if (torneio == null) {
            throw new IllegalArgumentException("O torneio para remover não pode ser nulo");
        }

        this.torneios.remove(torneio);
    }

    public Treinador getTreinador() {
        return treinador;
    }
}
