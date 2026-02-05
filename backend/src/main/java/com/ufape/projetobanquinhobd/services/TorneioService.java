package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.repositories.TorneioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TorneioService {
    @Autowired
    private TorneioRepository torneioRepository;

    public Torneio salvar(Torneio torneio) {
        return torneioRepository.save(torneio);
    }

    public List<Torneio> listarTodos() {
        return torneioRepository.findAll();
    }

    public Optional<Torneio> buscarPorId(Long id) {
        return torneioRepository.findById(id);
    }

    public void deletarPorId(Long id) {
        torneioRepository.deleteById(id);
    }
}
