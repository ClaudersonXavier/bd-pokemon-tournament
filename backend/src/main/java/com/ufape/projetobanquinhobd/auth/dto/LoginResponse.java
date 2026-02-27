package com.ufape.projetobanquinhobd.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo;   //Bearer
    private Long   id;
    private String nome;
    private String email;
}
