package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeRepository extends JpaRepository<Time, Long> {
    List<Time> findByTreinadorId(Long treinadorId);
    Optional<Time> findByIdAndTreinadorId(Long id, Long treinadorId);
    boolean existsByPokemons_Id(Long pokemonId);
}
