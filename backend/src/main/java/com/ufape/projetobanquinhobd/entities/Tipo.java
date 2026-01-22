package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Tipo {
    @Id
    private String nome;

    public Tipo(String nome) {
        this.setNome(nome);
    }

    // Privada porque o nome é o Id e por isso não deve ser alterado
    private void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do tipo é obrigatório");
        }
        this.nome = nome;
    }
}
