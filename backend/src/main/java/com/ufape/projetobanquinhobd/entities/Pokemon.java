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

    @Column(nullable = false)
    private String apelido;

    @ManyToOne
    private Especie especie;

    @ManyToMany
    private Set<Ataque> ataques = new HashSet<>();

    @ManyToOne
    private Treinador treinador;

    @ManyToOne
    private Time time;

    public Pokemon(String apelido, Especie especie, Set<Ataque> ataques) {
        this.setApelido(apelido);
        this.setEspecie(especie);
        this.setAtaques(ataques);
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

    public void setAtaques(Set<Ataque> ataques) {
        if (ataques == null) {
            throw new IllegalArgumentException("Ataques não podem ser nulos");
        }
        this.ataques = ataques;
    }

    public void addAtaque(Ataque ataque) {
        if (ataque == null) {
            throw new IllegalArgumentException("Ataque não pode ser nulo");
        }
        this.ataques.add(ataque);
    }
}
