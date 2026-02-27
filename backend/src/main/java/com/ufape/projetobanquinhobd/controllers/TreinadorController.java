package com.ufape.projetobanquinhobd.controllers;

import com.ufape.projetobanquinhobd.dto.PokemonRequest;
import com.ufape.projetobanquinhobd.dto.PokemonAtaquesRequest;
import com.ufape.projetobanquinhobd.dto.TimeRequest;
import com.ufape.projetobanquinhobd.entities.Ataque;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Pokemon;
import com.ufape.projetobanquinhobd.facade.Fachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    // PUT /api/treinadores/{id}/nome - Admin atualiza nome de um treinador
    @PutMapping("/{id}/nome")
    public ResponseEntity<Treinador> atualizarNomeTreinador(
            @PathVariable("id") Long id,
            @RequestBody java.util.Map<String, String> body) {
        String novoNome = body.get("nome");
        if (novoNome == null || novoNome.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Optional<Treinador> treinadorOpt = fachada.getTreinadorService().buscarPorId(id);
            if (treinadorOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Treinador treinador = treinadorOpt.get();
            treinador.setNome(novoNome);
            Treinador salvo = fachada.getTreinadorService().salvar(treinador);
            return ResponseEntity.ok(salvo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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

    // Times por treinador
    @GetMapping("/{treinadorId}/times")
    public List<Time> listarTimesDoTreinador(@PathVariable("treinadorId") Long treinadorId) {
        return fachada.getTimeService().listarPorTreinador(treinadorId);
    }

    @PostMapping("/{treinadorId}/times")
    public ResponseEntity<Time> criarTimeParaTreinador(
            @PathVariable("treinadorId") Long treinadorId,
            @RequestBody TimeRequest request) {
        Time criado = fachada.getTimeService()
                .criarParaTreinador(treinadorId, request.nome(), request.pokemonIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{treinadorId}/times/{timeId}")
    public ResponseEntity<Time> atualizarTimeDoTreinador(
            @PathVariable("treinadorId") Long treinadorId,
            @PathVariable("timeId") Long timeId,
            @RequestBody TimeRequest request) {
        Time atualizado = fachada.getTimeService()
                .atualizarParaTreinador(treinadorId, timeId, request.nome(), request.pokemonIds());
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{treinadorId}/times/{timeId}")
    public ResponseEntity<Void> removerTimeDoTreinador(
            @PathVariable("treinadorId") Long treinadorId,
            @PathVariable("timeId") Long timeId) {
        fachada.getTimeService().removerParaTreinador(treinadorId, timeId);
        return ResponseEntity.noContent().build();
    }

    // Pokémon por treinador
    @GetMapping("/{treinadorId}/pokemons")
    public List<Pokemon> listarPokemonsDoTreinador(@PathVariable("treinadorId") Long treinadorId) {
        return fachada.getPokemonService().listarPorTreinador(treinadorId);
    }

    @PostMapping("/{treinadorId}/pokemons")
    public ResponseEntity<Pokemon> criarPokemonParaTreinador(
            @PathVariable("treinadorId") Long treinadorId,
            @RequestBody PokemonRequest request) {
        Pokemon criado = fachada.getPokemonService()
                .criarParaTreinador(treinadorId, request.apelido(), request.especieNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{treinadorId}/pokemons/{pokemonId}")
    public ResponseEntity<Pokemon> atualizarPokemonDoTreinador(
            @PathVariable("treinadorId") Long treinadorId,
            @PathVariable("pokemonId") Long pokemonId,
            @RequestBody PokemonRequest request) {
        Pokemon atualizado = fachada.getPokemonService()
                .atualizarParaTreinador(treinadorId, pokemonId, request.apelido(), request.especieNome());
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{treinadorId}/pokemons/{pokemonId}")
    public ResponseEntity<Void> removerPokemonDoTreinador(
            @PathVariable("treinadorId") Long treinadorId,
            @PathVariable("pokemonId") Long pokemonId) {
        fachada.getPokemonService().removerParaTreinador(treinadorId, pokemonId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{treinadorId}/pokemons/{pokemonId}/ataques")
    public ResponseEntity<Set<Ataque>> listarAtaquesDoPokemon(
            @PathVariable("treinadorId") Long treinadorId,
            @PathVariable("pokemonId") Long pokemonId) {
        Set<Ataque> ataques = fachada.getPokemonService().listarAtaquesDoPokemon(treinadorId, pokemonId);
        return ResponseEntity.ok(ataques);
    }

    @PutMapping("/{treinadorId}/pokemons/{pokemonId}/ataques")
    public ResponseEntity<Pokemon> definirAtaquesDoPokemon(
            @PathVariable("treinadorId") Long treinadorId,
            @PathVariable("pokemonId") Long pokemonId,
            @RequestBody PokemonAtaquesRequest request) {
        Pokemon atualizado = fachada.getPokemonService()
                .definirAtaquesDoPokemon(treinadorId, pokemonId, request.ataquesNomes());
        return ResponseEntity.ok(atualizado);
    }

    @PostMapping("/{treinadorId}/pokemons/{pokemonId}/ataques/{ataqueNome}")
    public ResponseEntity<Pokemon> adicionarAtaqueAoPokemon(
            @PathVariable("treinadorId") Long treinadorId,
            @PathVariable("pokemonId") Long pokemonId,
            @PathVariable("ataqueNome") String ataqueNome) {
        Pokemon atualizado = fachada.getPokemonService()
                .adicionarAtaqueAoPokemon(treinadorId, pokemonId, ataqueNome);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{treinadorId}/pokemons/{pokemonId}/ataques/{ataqueNome}")
    public ResponseEntity<Pokemon> removerAtaqueDoPokemon(
            @PathVariable("treinadorId") Long treinadorId,
            @PathVariable("pokemonId") Long pokemonId,
            @PathVariable("ataqueNome") String ataqueNome) {
        Pokemon atualizado = fachada.getPokemonService()
                .removerAtaqueDoPokemon(treinadorId, pokemonId, ataqueNome);
        return ResponseEntity.ok(atualizado);
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
