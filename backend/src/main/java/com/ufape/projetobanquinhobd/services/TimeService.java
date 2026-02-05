package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.repositories.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TimeService {
    @Autowired
    private TimeRepository timeRepository;

    public Time salvar(Time time) {
        return timeRepository.save(time);
    }

    public List<Time> listarTodos() {
        return timeRepository.findAll();
    }

    public Optional<Time> buscarPorId(Long id) {
        return timeRepository.findById(id);
    }

    public void deletarPorId(Long id) {
        timeRepository.deleteById(id);
    }
}
