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
public class Treinador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Embedded
    private CredenciaisUsuario credenciais;

    @OneToMany(mappedBy = "treinador")
    private final Set<Time> times = new HashSet<>();

    @OneToMany(mappedBy = "treinador")
    private final Set<Pokemon> pokemons = new HashSet<>();

    public Treinador(String nome, CredenciaisUsuario credenciais) {
        this.setNome(nome);
        this.setCredenciais(credenciais);
    }

    public void setCredenciais(CredenciaisUsuario credenciais) {
        if (credenciais == null) {
            throw new IllegalArgumentException("Credenciais são obrigatórias");
        }
        this.credenciais = credenciais;
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
