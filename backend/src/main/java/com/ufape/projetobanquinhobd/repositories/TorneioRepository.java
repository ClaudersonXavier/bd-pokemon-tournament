package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Torneio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorneioRepository extends JpaRepository<Torneio, Long> {
}
