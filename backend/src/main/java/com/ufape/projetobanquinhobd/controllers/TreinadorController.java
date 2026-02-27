package com.ufape.projetobanquinhobd.controllers;

import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Pokemon;
import com.ufape.projetobanquinhobd.facade.Fachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/treinadores")
public class TreinadorController {
    @Autowired
    private Fachada fachada;

    // Treinador endpoints
    @GetMapping
    public List<Treinador> listarTreinadores() {
        return fachada.getTreinadorService().listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Treinador> buscarTreinador(@PathVariable("id") Long id) {
        Optional<Treinador> treinador = fachada.getTreinadorService().buscarPorId(id);
        return treinador.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Treinador criarTreinador(@RequestBody Treinador treinador) {
        return fachada.getTreinadorService().salvar(treinador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Treinador> atualizarTreinador(@PathVariable("id") Long id, @RequestBody Treinador treinador) {
        try {
            Treinador treinadorAtualizado = fachada.getTreinadorService().atualizar(id, treinador);
            return ResponseEntity.ok(treinadorAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTreinador(@PathVariable("id") Long id) {
        fachada.getTreinadorService().deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // Time endpoints
    @GetMapping("/times")
    public List<Time> listarTimes() {
        return fachada.getTimeService().listarTodos();
    }

    @GetMapping("/times/{id}")
    public ResponseEntity<Time> buscarTime(@PathVariable("id") Long id) {
        Optional<Time> time = fachada.getTimeService().buscarPorId(id);
        return time.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/times")
    public Time criarTime(@RequestBody Time time) {
        return fachada.getTimeService().salvar(time);
    }

    @PutMapping("/times/{id}")
    public ResponseEntity<Time> atualizarTime(@PathVariable("id") Long id, @RequestBody Time time) {
        try {
            Time timeAtualizado = fachada.getTimeService().atualizar(id, time);
            return ResponseEntity.ok(timeAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deletarTime(@PathVariable("id") Long id) {
        fachada.getTimeService().deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // Pokemon endpoints
    @GetMapping("/pokemons")
    public List<Pokemon> listarPokemons() {
        return fachada.getPokemonService().listarTodos();
    }

    @GetMapping("/pokemons/{id}")
    public ResponseEntity<Pokemon> buscarPokemon(@PathVariable("id") Long id) {
        Optional<Pokemon> pokemon = fachada.getPokemonService().buscarPorId(id);
        return pokemon.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/pokemons")
    public Pokemon criarPokemon(@RequestBody Pokemon pokemon) {
        return fachada.getPokemonService().salvar(pokemon);
    }

    @PutMapping("/pokemons/{id}")
    public ResponseEntity<Pokemon> atualizarPokemon(@PathVariable("id") Long id, @RequestBody Pokemon pokemon) {
        try {
            Pokemon pokemonAtualizado = fachada.getPokemonService().atualizar(id, pokemon);
            return ResponseEntity.ok(pokemonAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/pokemons/{id}")
    public ResponseEntity<Void> deletarPokemon(@PathVariable("id") Long id) {
        fachada.getPokemonService().deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
