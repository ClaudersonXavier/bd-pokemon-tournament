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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTorneio(@PathVariable Long id) {
        fachada.getTorneioService().deletarPorId(id);
        return ResponseEntity.noContent().build();
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

    @DeleteMapping("/batalhas/{id}")
    public ResponseEntity<Void> deletarBatalha(@PathVariable Long id) {
        fachada.getBatalhaService().deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
