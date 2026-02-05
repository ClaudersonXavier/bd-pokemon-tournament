package com.ufape.projetobanquinhobd.repositories;

import com.ufape.projetobanquinhobd.entities.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeRepository extends JpaRepository<Time, Long> {
}
