package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Pokemon;
import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.repositories.PokemonRepository;
import com.ufape.projetobanquinhobd.repositories.TimeRepository;
import com.ufape.projetobanquinhobd.repositories.TorneioRepository;
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
public class TimeService {
    @Autowired
    private TimeRepository timeRepository;

    @Autowired
    private TreinadorRepository treinadorRepository;

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private TorneioRepository torneioRepository;

    public Time salvar(Time time) {
        return timeRepository.save(time);
    }

    public List<Time> listarTodos() {
        return timeRepository.findAll();
    }

    public List<Time> listarPorTreinador(Long treinadorId) {
        validarTreinadorExiste(treinadorId);
        return timeRepository.findByTreinadorId(treinadorId);
    }

    public Optional<Time> buscarPorId(Long id) {
        return timeRepository.findById(id);
    }

    public Time criarParaTreinador(Long treinadorId, String nome, List<Long> pokemonIds) {
        Treinador treinador = treinadorRepository.findById(treinadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Treinador não encontrado: " + treinadorId));

        Time time = new Time(nome, treinador);
        sincronizarPokemons(time, pokemonIds, treinador);
        return timeRepository.save(time);
    }

    public Time atualizar(Long id, Time timeAtualizado) {
        return timeRepository.findById(id)
            .map(time -> {
                time.setNome(timeAtualizado.getNome());
                time.setTreinador(timeAtualizado.getTreinador());
                return timeRepository.save(time);
            })
            .orElseThrow(() -> new RuntimeException("Time não encontrado com id: " + id));
    }

    public Time atualizarParaTreinador(Long treinadorId, Long timeId, String nome, List<Long> pokemonIds) {
        Time time = timeRepository.findByIdAndTreinadorId(timeId, treinadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Time não encontrado para este treinador"));

        if (nome != null) {
            if (nome.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do time é obrigatório");
            }
            time.setNome(nome);
        }

        Treinador treinador = time.getTreinador();
        sincronizarPokemons(time, pokemonIds, treinador);

        return timeRepository.save(time);
    }

    public void deletarPorId(Long id) {
        timeRepository.deleteById(id);
    }

    public void removerParaTreinador(Long treinadorId, Long timeId) {
        Time time = timeRepository.findByIdAndTreinadorId(timeId, treinadorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Time não encontrado para este treinador"));

        if (torneioRepository.existsByTimes_Id(timeId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível remover este time porque ele está inscrito em um torneio. " +
                            "Remova o time do torneio primeiro."
            );
        }

        timeRepository.delete(time);
    }

    private void validarTreinadorExiste(Long treinadorId) {
        if (!treinadorRepository.existsById(treinadorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Treinador não encontrado: " + treinadorId);
        }
    }

    private void sincronizarPokemons(Time time, List<Long> pokemonIds, Treinador treinador) {
        if (pokemonIds == null) {
            return;
        }

        if (pokemonIds.size() > 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Um time pode ter no máximo 6 pokémons");
        }

        Set<Pokemon> pokemons = pokemonRepository.findAllById(pokemonIds).stream()
                .peek(pokemon -> {
                    if (!pokemon.getTreinador().getId().equals(treinador.getId())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Pokémon " + pokemon.getId() + " não pertence ao treinador");
                    }
                })
                .collect(Collectors.toSet());

        time.getPokemons().clear();
        time.getPokemons().addAll(pokemons);
    }
}
