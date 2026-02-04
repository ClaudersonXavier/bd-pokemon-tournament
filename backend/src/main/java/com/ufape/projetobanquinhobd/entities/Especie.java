package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Especie {
    @Id
    private String nome;

    @Column(nullable = false)
    private String imagemUrl;

    @ManyToMany
    private final Set<Tipo> tipos = new HashSet<>();

    public Especie(String nome, String imagemUrl, Set<Tipo> tipos) {
        this.setNome(nome);
        this.setImagemUrl(imagemUrl);
        this.setTipos(tipos);
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da espécie é obrigatório");
        }
        this.nome = nome;
    }

    public void setImagemUrl(String imagemUrl) {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            throw new IllegalArgumentException("URL da imagem é obrigatória");
        }
        this.imagemUrl = imagemUrl;
    }

    public void addTipo(Tipo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo não pode ser nulo");
        }
        this.tipos.add(tipo);
    }

    public void removeTipo(Tipo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo não pode ser nulo");
        }

        if (this.tipos.size() <= 1) {
            throw new IllegalArgumentException("Espécie deve ter pelo menos um tipo");
        }
        
        this.tipos.remove(tipo);
    }

    private void setTipos(Set<Tipo> tipos) {
        if (tipos == null || tipos.isEmpty()) {
            throw new IllegalArgumentException("Espécie deve ter pelo menos um tipo");
        }

        // itera em cima do addTipo para garantir as validações
        for (Tipo tipo : tipos) {
            this.addTipo(tipo);
        }
    }
}
