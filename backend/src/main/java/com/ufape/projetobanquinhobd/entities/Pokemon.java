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
public class Pokemon {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String apelido;

    @ManyToOne
    private Especie especie;

    @ManyToMany
    private final Set<Ataque> ataques = new HashSet<>();

    @ManyToOne
    private Treinador treinador;

    @ManyToMany
    private final Set<Time> times = new HashSet<>();

    private static final int MAX_ATAQUES = 4;

    public Pokemon(String apelido, Especie especie, Treinador treinador) {
        this.setApelido(apelido);
        this.setEspecie(especie);
        this.setTreinador(treinador);
    }

    public void setApelido(String apelido) {
        if (apelido == null || apelido.isBlank()) {
            throw new IllegalArgumentException("Apelido é obrigatório");
        }
        this.apelido = apelido;
    }

    public void setEspecie(Especie especie) {
        if (especie == null) {
            throw new IllegalArgumentException("Espécie é obrigatória");
        }
        this.especie = especie;
    }

    public void addAtaque(Ataque ataque) {
        if (ataque == null) {
            throw new IllegalArgumentException("Ataque não pode ser nulo");
        }

        if (this.ataques.size() >= MAX_ATAQUES) {
            throw new IllegalArgumentException("Pokémon não pode ter mais que " + MAX_ATAQUES + " ataques");
        }

        this.ataques.add(ataque);
    }

    public void removeAtaque(Ataque ataque) {
        this.ataques.remove(ataque);
    }

    public void setTreinador(Treinador treinador) {
        if (treinador == null) {
            throw new IllegalArgumentException("Treinador é obrigatório");
        }
        this.treinador = treinador;
    }

    public void addTime(Time time) {
        if (time == null) {
            throw new IllegalArgumentException("Time não pode ser nulo");
        }

        if (time.getTreinador() == this.treinador) {
            throw new IllegalArgumentException("O time não pode pertencer ao mesmo treinador do pokémon");
        }
        this.times.add(time);
    }

    public void removeTime(Time time) {
        this.times.remove(time);
    }
}
