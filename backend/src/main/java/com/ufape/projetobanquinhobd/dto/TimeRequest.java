package com.ufape.projetobanquinhobd.dto;

import java.util.List;

public record TimeRequest(String nome, List<Long> pokemonIds) {
}
