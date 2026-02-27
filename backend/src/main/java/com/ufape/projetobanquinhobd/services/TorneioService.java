package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.repositories.TorneioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TorneioService {
    @Autowired
    private TorneioRepository torneioRepository;

    public Torneio salvar(Torneio torneio) {
        return torneioRepository.save(torneio);
    }

    public List<Torneio> listarTodos() {
        return torneioRepository.findAll();
    }

    public Optional<Torneio> buscarPorId(Long id) {
        return torneioRepository.findById(id);
    }

    public Torneio atualizar(Long id, Torneio torneioAtualizado) {
        return torneioRepository.findById(id)
            .map(torneio -> {
                torneio.setNome(torneioAtualizado.getNome());
                torneio.setMaxParticipantes(torneioAtualizado.getMaxParticipantes());
                torneio.setDataAberturaInscricoes(torneioAtualizado.getDataAberturaInscricoes());
                torneio.setDataEncerramentoInscricoes(torneioAtualizado.getDataEncerramentoInscricoes());
                torneio.setDataInicio(torneioAtualizado.getDataInicio());
                torneio.setDataFim(torneioAtualizado.getDataFim());
                return torneioRepository.save(torneio);
            })
            .orElseThrow(() -> new RuntimeException("Torneio não encontrado com id: " + id));
    }

    public void deletarPorId(Long id) {
        torneioRepository.deleteById(id);
    }
}
