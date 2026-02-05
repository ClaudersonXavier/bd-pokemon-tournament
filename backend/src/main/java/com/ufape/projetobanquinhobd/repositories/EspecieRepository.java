package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Especie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecieRepository extends JpaRepository<Especie, String> {
}