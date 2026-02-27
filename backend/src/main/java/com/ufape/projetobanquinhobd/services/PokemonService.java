package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Especie;
import com.ufape.projetobanquinhobd.entities.Pokemon;
import com.ufape.projetobanquinhobd.entities.Ataque;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.repositories.AtaqueRepository;
import com.ufape.projetobanquinhobd.repositories.EspecieRepository;
import com.ufape.projetobanquinhobd.repositories.PokemonRepository;
import com.ufape.projetobanquinhobd.repositories.TimeRepository;
import com.ufape.projetobanquinhobd.repositories.TreinadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PokemonService {
    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private TreinadorRepository treinadorRepository;

    @Autowired
    private EspecieRepository especieRepository;

    @Autowired
    private TimeRepository timeRepository;

    @Autowired
    private AtaqueRepository ataqueRepository;

    public Pokemon salvar(Pokemon pokemon) {
        return pokemonRepository.save(pokemon);
    }

    public List<Pokemon> listarTodos() {
        return pokemonRepository.findAll();
    }

    public List<Pokemon> listarPorTreinador(Long treinadorId) {
        validarTreinadorExiste(treinadorId);
        return pokemonRepository.findByTreinadorId(treinadorId);
    }

    public Optional<Pokemon> buscarPorId(Long id) {
        return pokemonRepository.findById(id);
    }

    public Pokemon criarParaTreinador(Long treinadorId, String apelido, String especieNome) {
        Treinador treinador = treinadorRepository.findById(treinadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Treinador não encontrado: " + treinadorId));

        Especie especie = buscarEspecie(especieNome);
        String apelidoDefinido = (apelido == null || apelido.isBlank()) ? especie.getNome() : apelido;

        Pokemon pokemon = new Pokemon(apelidoDefinido, especie, treinador);
        return pokemonRepository.save(pokemon);
    }

    public Pokemon atualizar(Long id, Pokemon pokemonAtualizado) {
        return pokemonRepository.findById(id)
            .map(pokemon -> {
                pokemon.setApelido(pokemonAtualizado.getApelido());
                pokemon.setEspecie(pokemonAtualizado.getEspecie());
                pokemon.setTreinador(pokemonAtualizado.getTreinador());
                return pokemonRepository.save(pokemon);
            })
            .orElseThrow(() -> new RuntimeException("Pokemon não encontrado com id: " + id));
    }

    public Pokemon atualizarParaTreinador(Long treinadorId, Long pokemonId, String apelido, String especieNome) {
        Pokemon pokemon = pokemonRepository.findByIdAndTreinadorId(pokemonId, treinadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pokémon não encontrado para este treinador"));

        if (apelido != null) {
            if (apelido.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apelido é obrigatório");
            }
            pokemon.setApelido(apelido);
        }

        if (especieNome != null) {
            Especie especie = buscarEspecie(especieNome);
            pokemon.setEspecie(especie);
        }

        return pokemonRepository.save(pokemon);
    }

    public void deletarPorId(Long id) {
        pokemonRepository.deleteById(id);
    }

    public void removerParaTreinador(Long treinadorId, Long pokemonId) {
        Pokemon pokemon = pokemonRepository.findByIdAndTreinadorId(pokemonId, treinadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pokémon não encontrado para este treinador"));

        if (timeRepository.existsByPokemons_Id(pokemonId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível remover este pokémon porque ele está em um time. " +
                            "Remova o pokémon do time primeiro."
            );
        }

        pokemonRepository.delete(pokemon);
    }

    public Set<Ataque> listarAtaquesDoPokemon(Long treinadorId, Long pokemonId) {
        Pokemon pokemon = buscarPokemonDoTreinador(treinadorId, pokemonId);
        return pokemon.getAtaques();
    }

    public Pokemon definirAtaquesDoPokemon(Long treinadorId, Long pokemonId, List<String> ataquesNomes) {
        Pokemon pokemon = buscarPokemonDoTreinador(treinadorId, pokemonId);

        if (ataquesNomes == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lista de ataques é obrigatória");
        }

        if (ataquesNomes.size() > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Um pokémon pode ter no máximo 4 ataques");
        }

        Set<Ataque> ataques = ataquesNomes.stream()
                .map(this::buscarAtaque)
                .collect(Collectors.toSet());

        pokemon.getAtaques().clear();
        pokemon.getAtaques().addAll(ataques);
        return pokemonRepository.save(pokemon);
    }

    public Pokemon adicionarAtaqueAoPokemon(Long treinadorId, Long pokemonId, String ataqueNome) {
        Pokemon pokemon = buscarPokemonDoTreinador(treinadorId, pokemonId);
        Ataque ataque = buscarAtaque(ataqueNome);

        if (pokemon.getAtaques().size() >= 4 && !pokemon.getAtaques().contains(ataque)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Um pokémon pode ter no máximo 4 ataques");
        }

        pokemon.getAtaques().add(ataque);
        return pokemonRepository.save(pokemon);
    }

    public Pokemon removerAtaqueDoPokemon(Long treinadorId, Long pokemonId, String ataqueNome) {
        Pokemon pokemon = buscarPokemonDoTreinador(treinadorId, pokemonId);
        Ataque ataque = buscarAtaque(ataqueNome);

        pokemon.getAtaques().remove(ataque);
        return pokemonRepository.save(pokemon);
    }

    private void validarTreinadorExiste(Long treinadorId) {
        if (!treinadorRepository.existsById(treinadorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Treinador não encontrado: " + treinadorId);
        }
    }

    private Especie buscarEspecie(String especieNome) {
        if (especieNome == null || especieNome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Espécie é obrigatória");
        }

        return especieRepository.findById(especieNome)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Espécie não encontrada: " + especieNome));
    }

    private Pokemon buscarPokemonDoTreinador(Long treinadorId, Long pokemonId) {
        validarTreinadorExiste(treinadorId);
        return pokemonRepository.findByIdAndTreinadorId(pokemonId, treinadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pokémon não encontrado para este treinador"));
    }

    private Ataque buscarAtaque(String ataqueNome) {
        if (ataqueNome == null || ataqueNome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do ataque é obrigatório");
        }

        return ataqueRepository.findById(ataqueNome)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ataque não encontrado: " + ataqueNome));
    }
}
