package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Treinador {
    @Id
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "treinador")
    private final Set<Time> times = new HashSet<>();

    @OneToMany(mappedBy = "treinador")
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
