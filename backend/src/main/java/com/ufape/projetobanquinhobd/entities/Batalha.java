package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Batalha {
    @Id
    private Long id;

    @Column(nullable = false)
    private int rodada;

    @Column(nullable = false)
    private LocalDateTime horarioFim;

    @Column(nullable = false)
    private LocalDateTime horarioInicio;

    @ManyToMany
    private final Set<Time> timesParticipantes = new HashSet<>();

    @ManyToOne
    private Torneio torneio;

    @OneToOne
    private Time timeVencedor;

    private static final int MAX_TIMES_PARTICIPANTES = 2;

    public Batalha(int rodada, LocalDateTime horarioFim, LocalDateTime horarioInicio, Torneio torneio) {
        // O torneio deve ser setado primeiro para as validações dos outros atributos funcionarem
        this.setTorneio(torneio);

        this.setRodada(rodada);

        // A ordem dos setters de horário é importante para a validação
        this.setHorarioInicio(horarioInicio);
        this.setHorarioFim(horarioFim);
    }

    public void setRodada(int rodada) {
        if (rodada < 0) {
            throw new IllegalArgumentException("Rodada não pode ser negativa");
        }

        if (rodada > torneio.getQtdRodadas()) {
            throw new IllegalArgumentException("Rodada não pode ser maior que a quantidade de rodadas do torneio");
        }

        this.rodada = rodada;
    }

    public void setHorarioFim(LocalDateTime horarioFim) {
        if (horarioFim == null) {
            throw new IllegalArgumentException("Horário de fim é obrigatório");
        }

        if (horarioFim.isBefore(horarioInicio)) {
            throw new IllegalArgumentException("Horário de fim não pode ser antes do horário de início");
        }

        this.horarioFim = horarioFim;
    }

    public void setHorarioInicio(LocalDateTime horarioInicio) {
        if (horarioInicio == null) {
            throw new IllegalArgumentException("Horário de início é obrigatório");
        }

        this.horarioInicio = horarioInicio;
    }

    public void addTimeParticipante(Time time) {
        if (time == null) {
            throw new IllegalArgumentException("Time participante não pode ser nulo");
        }

        if (timesParticipantes.size() >= MAX_TIMES_PARTICIPANTES) {
            throw new IllegalArgumentException("Número máximo de times participantes atingido");
        }

        if (!torneio.getTimes().contains(time)) {
            throw new IllegalArgumentException("O time participante deve estar inscrito no torneio da batalha");
        }

        this.timesParticipantes.add(time);
    }

    public void setTorneio(Torneio torneio) {
        if (torneio == null) {
            throw new IllegalArgumentException("Torneio é obrigatório");
        }

        this.torneio = torneio;
    }

    public void setTimeVencedor(Time timeVencedor) {
        if (!timesParticipantes.contains(timeVencedor)) {
            throw new IllegalArgumentException("O time vencedor deve ser um dos times participantes da batalha");
        }

        this.timeVencedor = timeVencedor;
    }
}

