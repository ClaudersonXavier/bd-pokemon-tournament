package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Comment("Tabela de times montados por treinadores.")
public class Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Identificador unico da equipe.")
    private long id;

    @Column(nullable = false)
    @Comment("Nome da equipe.")
    private String nome;

    @ManyToOne
    @Comment("Dono responsavel pelo time.")
    private Treinador treinador;

    @ManyToMany
    @Comment("Pokemons pertencentes ao time (maximo 6).")
    private final Set<Pokemon> pokemons = new HashSet<>();

    @ManyToMany
    @Comment("Torneios em que o time esta inscrito.")
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
}
