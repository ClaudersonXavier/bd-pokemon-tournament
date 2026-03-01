package com.ufape.projetobanquinhobd.entities;

public enum StatusTorneio {
    ABERTO("Inscrições abertas"),
    EM_ANDAMENTO("Torneio em andamento"),
    ENCERRADO("Torneio encerrado");

    private final String descricao;

    StatusTorneio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
