package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    List<Pokemon> findByTreinadorId(Long treinadorId);
    Optional<Pokemon> findByIdAndTreinadorId(Long id, Long treinadorId);
}
