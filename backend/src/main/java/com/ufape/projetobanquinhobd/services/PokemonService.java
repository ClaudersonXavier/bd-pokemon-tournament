package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Pokemon;
import com.ufape.projetobanquinhobd.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {
    @Autowired
    private PokemonRepository pokemonRepository;

    public Pokemon salvar(Pokemon pokemon) {
        return pokemonRepository.save(pokemon);
    }

    public List<Pokemon> listarTodos() {
        return pokemonRepository.findAll();
    }

    public Optional<Pokemon> buscarPorId(Long id) {
        return pokemonRepository.findById(id);
    }

    public void deletarPorId(Long id) {
        pokemonRepository.deleteById(id);
    }
}