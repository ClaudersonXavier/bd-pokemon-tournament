package com.ufape.projetobanquinhobd.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufape.projetobanquinhobd.entities.Batalha;
import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.facade.Fachada;

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
    public ResponseEntity<Torneio> buscarTorneio(@PathVariable("id") Long id) {
        Optional<Torneio> torneio = fachada.getTorneioService().buscarPorId(id);
        return torneio.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Torneio criarTorneio(@RequestBody Torneio torneio) {
        return fachada.getTorneioService().salvar(torneio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Torneio> atualizarTorneio(@PathVariable("id") Long id, @RequestBody Torneio torneio) {
        try {
            Torneio torneioAtualizado = fachada.getTorneioService().atualizar(id, torneio);
            return ResponseEntity.ok(torneioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTorneio(@PathVariable("id") Long id) {
        fachada.getTorneioService().deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/inscricoes")
    public ResponseEntity<Torneio> inscreverTimeNoTorneio(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Long> body
    ) {
        Long timeId = body.get("timeId");
        if (timeId == null) {
            return ResponseEntity.badRequest().build();
        }

        Torneio torneioAtualizado = fachada.getTorneioService().inscreverTime(id, timeId);
        return ResponseEntity.ok(torneioAtualizado);
    }

    // Endpoint para listar batalhas de um torneio específico
    @GetMapping("/{id}/batalhas")
    public ResponseEntity<List<Batalha>> listarBatalhasPorTorneio(@PathVariable("id") Long id) {
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
    public ResponseEntity<Batalha> buscarBatalha(@PathVariable("id") Long id) {
        Optional<Batalha> batalha = fachada.getBatalhaService().buscarPorId(id);
        return batalha.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/batalhas")
    public Batalha criarBatalha(@RequestBody Batalha batalha) {
        return fachada.getBatalhaService().salvar(batalha);
    }

    @PutMapping("/batalhas/{id}")
    public ResponseEntity<Batalha> atualizarBatalha(@PathVariable("id") Long id, @RequestBody Batalha batalha) {
        try {
            Batalha batalhaAtualizada = fachada.getBatalhaService().atualizar(id, batalha);
            return ResponseEntity.ok(batalhaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/batalhas/{id}")
    public ResponseEntity<Void> deletarBatalha(@PathVariable("id") Long id) {
        fachada.getBatalhaService().deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para definir vencedor de uma batalha
    @PutMapping("/batalhas/{batalhaId}/vencedor/{timeId}")
    public ResponseEntity<Batalha> definirVencedor(
            @PathVariable("batalhaId") Long batalhaId,
            @PathVariable("timeId") Long timeId) {
        try {
            Batalha batalhaAtualizada = fachada.getBatalhaService().definirVencedor(batalhaId, timeId);
            return ResponseEntity.ok(batalhaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint para gerar batalhas da próxima rodada
    @PostMapping("/{torneioId}/gerar-proxima-rodada")
    public ResponseEntity<List<Batalha>> gerarProximaRodada(
            @PathVariable("torneioId") Long torneioId,
            @RequestParam("rodadaAtual") int rodadaAtual,
            @RequestParam(value = "random", defaultValue = "false") boolean random) {
        try {
            Optional<Torneio> torneioOpt = fachada.getTorneioService().buscarPorId(torneioId);
            if (torneioOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            List<Batalha> novasBatalhas = fachada.getBatalhaService()
                    .gerarProximaRodada(torneioId, rodadaAtual, torneioOpt.get(), random);
            return ResponseEntity.ok(novasBatalhas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint para verificar se todas batalhas da rodada têm vencedor
    @GetMapping("/{torneioId}/rodada/{rodada}/completa")
    public ResponseEntity<Boolean> verificarRodadaCompleta(
            @PathVariable("torneioId") Long torneioId,
            @PathVariable("rodada") int rodada) {
        boolean completa = fachada.getBatalhaService().todasBatalhasComVencedor(torneioId, rodada);
        return ResponseEntity.ok(completa);
    }
}
