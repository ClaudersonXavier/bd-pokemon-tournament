package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.StatusTorneio;
import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.repositories.TimeRepository;
import com.ufape.projetobanquinhobd.repositories.TorneioRepository;
import com.ufape.projetobanquinhobd.repositories.TreinadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TorneioService {
    @Autowired
    private TorneioRepository torneioRepository;

    @Autowired
    private TimeRepository timeRepository;

    @Autowired
    private TreinadorRepository treinadorRepository;

    public Torneio salvar(Torneio torneio) {
        if (torneio.getStatus() == null) {
            torneio.setStatus(StatusTorneio.ABERTO);
        }
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
                if (torneioAtualizado.getStatus() != null) {
                    torneio.setStatus(torneioAtualizado.getStatus());
                }
                return torneioRepository.save(torneio);
            })
            .orElseThrow(() -> new RuntimeException("Torneio não encontrado com id: " + id));
    }

    public void deletarPorId(Long id) {
        torneioRepository.deleteById(id);
    }

    @Transactional
    public Torneio inscreverTime(Long torneioId, Long timeId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
        }

        String email = auth.getName();
        Treinador treinador = treinadorRepository.findByCredenciaisEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Treinador não autenticado"));

        Torneio torneio = torneioRepository.findById(torneioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Torneio não encontrado: " + torneioId));

        Time time = timeRepository.findById(timeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Time não encontrado: " + timeId));

        if (time.getTreinador() == null || !time.getTreinador().getId().equals(treinador.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você só pode inscrever seus próprios times");
        }

        if (torneio.getStatus() != StatusTorneio.ABERTO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "As inscrições para este torneio estão encerradas");
        }

        if (torneio.getTimes().stream().anyMatch(t -> t.getId() == time.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este time já está inscrito no torneio");
        }

        if (torneio.getTimes().stream().anyMatch(t -> t.getTreinador() != null
                && t.getTreinador().getId().equals(treinador.getId()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Você já possui um time inscrito neste torneio");
        }

        if (torneio.getTimes().size() >= torneio.getMaxParticipantes()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O torneio já atingiu o limite máximo de participantes");
        }

        torneio.getTimes().add(time);
        return torneioRepository.save(torneio);
    }
}
