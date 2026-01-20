package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Ataque {
    @Id
    private String nome;

    @Column(nullable = false)
    private String categoria;

    private int poder;

    @ManyToOne
    private Tipo tipo;

    public Ataque(String nome, String categoria, int poder, Tipo tipo) {
        this.nome = nome;
        this.setCategoria(categoria);
        this.setPoder(poder);
        this.setTipo(tipo);
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("Categoria do ataque é obrigatória");
        }
        this.categoria = categoria;
    }

    public void setPoder(int poder) {
        if (poder < 0) {
            throw new IllegalArgumentException("Poder do ataque não pode ser negativo");
        }
        this.poder = poder;
    }

    public void setTipo(Tipo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo do ataque é obrigatório");
        }
        this.tipo = tipo;
    }
}
