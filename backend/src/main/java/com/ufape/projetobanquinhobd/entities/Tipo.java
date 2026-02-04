package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Comment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Comment("Tabela de tipos elementais dos Pokemon.")
public class Tipo {
    @Id
    @Comment("Nome do elemento (ex: Fogo, Agua).")
    private String nome;

    public Tipo(String nome) {
        this.setNome(nome);
    }

    // Privada porque o nome é o ID e por isso não deve ser alterado
    private void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do tipo é obrigatório");
        }
        this.nome = nome;
    }
}
