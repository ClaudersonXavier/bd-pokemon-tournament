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
@Comment("Tabela de treinadores cadastrados.")
public class Treinador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Identificador unico do treinador.")
    private Long id;

    @Column(nullable = false)
    @Comment("Nome completo do treinador.")
    private String nome;

    @OneToMany(mappedBy = "treinador")
    @Comment("Times registrados pelo treinador.")
    private final Set<Time> times = new HashSet<>();

    @OneToMany(mappedBy = "treinador")
    @Comment("Pokemons pertencentes ao treinador.")
    private final Set<Pokemon> pokemons = new HashSet<>();

    public Treinador(String nome) {
        this.setNome(nome);
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do treinador é obrigatório");
        }
        this.nome = nome;
    }

    public void addTime(Time time) {
        this.times.add(time);
    }

    public void removeTime(Time time) {
        this.times.remove(time);
    }

    public void addPokemon(Pokemon pokemon) {
        if (pokemon == null) {
            throw new IllegalArgumentException("Pokémon não pode ser nulo");
        }

        if (this.pokemons.contains(pokemon)) {
            throw new IllegalArgumentException("Pokémon já pertence a este treinador");
        }

        this.pokemons.add(pokemon);
    }

    public void removePokemon(Pokemon pokemon) {
        this.pokemons.remove(pokemon);
    }
}
