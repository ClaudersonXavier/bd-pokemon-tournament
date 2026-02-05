package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
}