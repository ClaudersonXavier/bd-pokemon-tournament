package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Batalha;
import com.ufape.projetobanquinhobd.repositories.BatalhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}