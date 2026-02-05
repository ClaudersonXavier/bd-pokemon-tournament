package com.ufape.projetobanquinhobd.controllers;

import com.ufape.projetobanquinhobd.entities.Especie;
import com.ufape.projetobanquinhobd.entities.Tipo;
import com.ufape.projetobanquinhobd.entities.Ataque;
import com.ufape.projetobanquinhobd.facade.Fachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {
    @Autowired
    private Fachada fachada;

    // Especie endpoints
    @GetMapping("/especies")
    public List<Especie> listarEspecies() {
        return fachada.getEspecieService().listarTodos();
    }

    @GetMapping("/especies/{nome}")
    public ResponseEntity<Especie> buscarEspecie(@PathVariable String nome) {
        Optional<Especie> especie = fachada.getEspecieService().buscarPorNome(nome);
        return especie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/especies")
    public Especie criarEspecie(@RequestBody Especie especie) {
        return fachada.getEspecieService().salvar(especie);
    }

    @DeleteMapping("/especies/{nome}")
    public ResponseEntity<Void> deletarEspecie(@PathVariable String nome) {
        fachada.getEspecieService().deletarPorNome(nome);
        return ResponseEntity.noContent().build();
    }

    // Tipo endpoints
    @GetMapping("/tipos")
    public List<Tipo> listarTipos() {
        return fachada.getTipoService().listarTodos();
    }

    @GetMapping("/tipos/{nome}")
    public ResponseEntity<Tipo> buscarTipo(@PathVariable String nome) {
        Optional<Tipo> tipo = fachada.getTipoService().buscarPorNome(nome);
        return tipo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/tipos")
    public Tipo criarTipo(@RequestBody Tipo tipo) {
        return fachada.getTipoService().salvar(tipo);
    }

    @DeleteMapping("/tipos/{nome}")
    public ResponseEntity<Void> deletarTipo(@PathVariable String nome) {
        fachada.getTipoService().deletarPorNome(nome);
        return ResponseEntity.noContent().build();
    }

    // Ataque endpoints
    @GetMapping("/ataques")
    public List<Ataque> listarAtaques() {
        return fachada.getAtaqueService().listarTodos();
    }

    @GetMapping("/ataques/{nome}")
    public ResponseEntity<Ataque> buscarAtaque(@PathVariable String nome) {
        Optional<Ataque> ataque = fachada.getAtaqueService().buscarPorNome(nome);
        return ataque.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/ataques")
    public Ataque criarAtaque(@RequestBody Ataque ataque) {
        return fachada.getAtaqueService().salvar(ataque);
    }

    @DeleteMapping("/ataques/{nome}")
    public ResponseEntity<Void> deletarAtaque(@PathVariable String nome) {
        fachada.getAtaqueService().deletarPorNome(nome);
        return ResponseEntity.noContent().build();
    }
}
