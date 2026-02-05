package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Tipo;
import com.ufape.projetobanquinhobd.repositories.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoService {
    @Autowired
    private TipoRepository tipoRepository;

    public Tipo salvar(Tipo tipo) {
        return tipoRepository.save(tipo);
    }

    public List<Tipo> listarTodos() {
        return tipoRepository.findAll();
    }

    public Optional<Tipo> buscarPorNome(String nome) {
        return tipoRepository.findById(nome);
    }

    public void deletarPorNome(String nome) {
        tipoRepository.deleteById(nome);
    }
}
