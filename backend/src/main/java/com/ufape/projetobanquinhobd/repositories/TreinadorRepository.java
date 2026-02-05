package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Treinador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreinadorRepository extends JpaRepository<Treinador, Long> {
}
