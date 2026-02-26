package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CredenciaisUsuario {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    public CredenciaisUsuario(String email, String senha) {
        this.setEmail(email);
        this.setSenha(senha);
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        this.email = email;
    }

    public void setSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        this.senha = senha;
    }
}
