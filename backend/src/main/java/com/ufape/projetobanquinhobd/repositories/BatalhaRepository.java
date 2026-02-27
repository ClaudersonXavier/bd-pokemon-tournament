package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Batalha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BatalhaRepository extends JpaRepository<Batalha, Long> {
    @Query("SELECT b FROM Batalha b WHERE b.torneio.id = :torneioId ORDER BY b.rodada")
    List<Batalha> findByTorneioId(@Param("torneioId") Long torneioId);
}