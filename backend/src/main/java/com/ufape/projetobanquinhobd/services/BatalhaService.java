package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Batalha;
import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.repositories.BatalhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class BatalhaService {
    @Autowired
    private BatalhaRepository batalhaRepository;

    public Batalha salvar(Batalha batalha) {
        return batalhaRepository.save(batalha);
    }

    public List<Batalha> listarTodos() {
        return batalhaRepository.findAll();
    }

    public Optional<Batalha> buscarPorId(Long id) {
        return batalhaRepository.findById(id);
    }

    public Batalha atualizar(Long id, Batalha batalhaAtualizada) {
        return batalhaRepository.findById(id)
            .map(batalha -> {
                batalha.setRodada(batalhaAtualizada.getRodada());
                batalha.setHorarioInicio(batalhaAtualizada.getHorarioInicio());
                batalha.setHorarioFim(batalhaAtualizada.getHorarioFim());
                batalha.setTorneio(batalhaAtualizada.getTorneio());
                if (batalhaAtualizada.getTimeVencedor() != null) {
                    batalha.setTimeVencedor(batalhaAtualizada.getTimeVencedor());
                }
                return batalhaRepository.save(batalha);
            })
            .orElseThrow(() -> new RuntimeException("Batalha não encontrada com id: " + id));
    }

    public void deletarPorId(Long id) {
        batalhaRepository.deleteById(id);
    }

    public List<Batalha> buscarPorTorneio(Long torneioId) {
        return batalhaRepository.findByTorneioId(torneioId);
    }

    public Batalha definirVencedor(Long batalhaId, Long timeVencedorId) {
        Batalha batalha = batalhaRepository.findById(batalhaId)
            .orElseThrow(() -> new RuntimeException("Batalha não encontrada com id: " + batalhaId));
        
        // Buscar o time vencedor entre os times participantes da batalha
        com.ufape.projetobanquinhobd.entities.Time timeVencedor = batalha.getTimesParticipantes().stream()
            .filter(time -> time.getId() == timeVencedorId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Time não está participando desta batalha"));
        
        batalha.setTimeVencedor(timeVencedor);
        return batalhaRepository.save(batalha);
    }

    public List<Batalha> buscarPorTorneioERodada(Long torneioId, int rodada) {
        return batalhaRepository.findByTorneioIdAndRodada(torneioId, rodada);
    }

    public boolean todasBatalhasComVencedor(Long torneioId, int rodada) {
        List<Batalha> batalhas = buscarPorTorneioERodada(torneioId, rodada);
        return !batalhas.isEmpty() && batalhas.stream().allMatch(b -> b.getTimeVencedor() != null);
    }

    public int obterProximaRodada(Long torneioId) {
        List<Batalha> todasBatalhas = buscarPorTorneio(torneioId);
        return todasBatalhas.stream()
            .mapToInt(Batalha::getRodada)
            .max()
            .orElse(0) + 1;
    }

    public List<Batalha> gerarProximaRodada(Long torneioId, int rodadaAtual, Torneio torneio, boolean random) {
        // Verificar se todas as batalhas da rodada atual têm vencedor
        if (!todasBatalhasComVencedor(torneioId, rodadaAtual)) {
            throw new RuntimeException("Nem todas as batalhas da rodada atual têm vencedor definido");
        }

        // Buscar vencedores da rodada atual
        List<Batalha> batalhasRodadaAtual = buscarPorTorneioERodada(torneioId, rodadaAtual);
        List<Time> vencedores = new ArrayList<>();
        
        for (Batalha batalha : batalhasRodadaAtual) {
            if (batalha.getTimeVencedor() != null) {
                vencedores.add(batalha.getTimeVencedor());
            }
        }
        
        // Se random for true, embaralhar os vencedores
        if (random) {
            Collections.shuffle(vencedores);
        }

        // Se houver apenas 1 vencedor, o torneio acabou
        if (vencedores.size() <= 1) {
            throw new RuntimeException("Torneio finalizado - campeão definido");
        }

        // Criar batalhas da próxima rodada
        List<Batalha> novasBatalhas = new ArrayList<>();
        int proximaRodada = rodadaAtual + 1;
        
        // Emparelar os vencedores (2 a 2)
        for (int i = 0; i < vencedores.size(); i += 2) {
            if (i + 1 < vencedores.size()) {
                // Criar horários para a batalha (pode ajustar conforme necessário)
                LocalDateTime horarioInicio = LocalDateTime.now().plusDays(proximaRodada);
                LocalDateTime horarioFim = horarioInicio.plusHours(1);
                
                Batalha novaBatalha = new Batalha(proximaRodada, horarioFim, horarioInicio, torneio);
                novaBatalha.addTimeParticipante(vencedores.get(i));
                novaBatalha.addTimeParticipante(vencedores.get(i + 1));
                
                Batalha batalhaSalva = batalhaRepository.save(novaBatalha);
                novasBatalhas.add(batalhaSalva);
            }
        }

        return novasBatalhas;
    }
}