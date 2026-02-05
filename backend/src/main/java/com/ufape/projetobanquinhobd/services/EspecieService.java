package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Especie;
import com.ufape.projetobanquinhobd.repositories.EspecieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspecieService {
    @Autowired
    private EspecieRepository especieRepository;

    public Especie salvar(Especie especie) {
        return especieRepository.save(especie);
    }

    public List<Especie> listarTodos() {
        return especieRepository.findAll();
    }

    public Optional<Especie> buscarPorNome(String nome) {
        return especieRepository.findById(nome);
    }

    public void deletarPorNome(String nome) {
        especieRepository.deleteById(nome);
    }
}