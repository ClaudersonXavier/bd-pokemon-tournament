package com.ufape.projetobanquinhobd.controllers;

import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.entities.Batalha;
import com.ufape.projetobanquinhobd.facade.Fachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/torneios")
public class TorneioController {
    @Autowired
    private Fachada fachada;

    // Torneio endpoints
    @GetMapping
    public List<Torneio> listarTorneios() {
        return fachada.getTorneioService().listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Torneio> buscarTorneio(@PathVariable Long id) {
        Optional<Torneio> torneio = fachada.getTorneioService().buscarPorId(id);
        return torneio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Torneio criarTorneio(@RequestBody Torneio torneio) {
        return fachada.getTorneioService().salvar(torneio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Torneio> atualizarTorneio(@PathVariable Long id, @RequestBody Torneio torneio) {
        try {
            Torneio torneioAtualizado = fachada.getTorneioService().atualizar(id, torneio);
            return ResponseEntity.ok(torneioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTorneio(@PathVariable Long id) {
        fachada.getTorneioService().deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para listar batalhas de um torneio específico
    @GetMapping("/{id}/batalhas")
    public ResponseEntity<List<Batalha>> listarBatalhasPorTorneio(@PathVariable Long id) {
        Optional<Torneio> torneio = fachada.getTorneioService().buscarPorId(id);
        if (torneio.isPresent()) {
            List<Batalha> batalhas = fachada.getBatalhaService().buscarPorTorneio(id);
            return ResponseEntity.ok(batalhas);
        }
        return ResponseEntity.notFound().build();
    }

    // Batalha endpoints (exemplo básico)
    @GetMapping("/batalhas")
    public List<Batalha> listarBatalhas() {
        return fachada.getBatalhaService().listarTodos();
    }

    @GetMapping("/batalhas/{id}")
    public ResponseEntity<Batalha> buscarBatalha(@PathVariable Long id) {
        Optional<Batalha> batalha = fachada.getBatalhaService().buscarPorId(id);
        return batalha.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/batalhas")
    public Batalha criarBatalha(@RequestBody Batalha batalha) {
        return fachada.getBatalhaService().salvar(batalha);
    }

    @PutMapping("/batalhas/{id}")
    public ResponseEntity<Batalha> atualizarBatalha(@PathVariable Long id, @RequestBody Batalha batalha) {
        try {
            Batalha batalhaAtualizada = fachada.getBatalhaService().atualizar(id, batalha);
            return ResponseEntity.ok(batalhaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/batalhas/{id}")
    public ResponseEntity<Void> deletarBatalha(@PathVariable Long id) {
        fachada.getBatalhaService().deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
