package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Identificador unico do confronto.")
    private Long id;

    @Column(nullable = false)
    @Comment("Fase da competicao (1: Oitavas, 2: Quartas...).")
    private int rodada;

    @Column(nullable = false)
    @Comment("Horário do inicio da batalha")
    private LocalDateTime horarioInicio;

    @Column(nullable = false)
    @Comment("Horário do fim da batalha")
    private LocalDateTime horarioFim;

    @ManyToMany
    private final Set<Time> timesParticipantes = new HashSet<>();

    @ManyToOne
    @Comment("Torneio ao qual a batalha pertence.")
    private Torneio torneio;

    @OneToOne
    @Comment("Time que venceu este combate especifico.")
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
