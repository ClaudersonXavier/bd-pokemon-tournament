package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Treinador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TreinadorRepository extends JpaRepository<Treinador, Long> {
    Optional<Treinador> findByCredenciaisEmail(String email);
}
