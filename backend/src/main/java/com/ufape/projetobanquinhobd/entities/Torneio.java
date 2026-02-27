package com.ufape.projetobanquinhobd.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Torneio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private int  maxParticipantes;

    @Column(nullable = false)
    private Date dataAberturaInscricoes;

    @Column(nullable = false)
    private Date dataEncerramentoInscricoes;

    @Column(nullable = false)
    private Date dataInicio;

    @Column(nullable = false)
    private Date dataFim;

    @OneToMany(mappedBy = "torneio")
    @JsonIgnoreProperties({"torneio", "time1", "time2"})
    private Set<Batalha> batalhas;

    @ManyToMany
    @JsonIgnoreProperties({"torneios", "treinador", "pokemons"})
    private Set<Time> times = new HashSet<>();

    public Torneio(String nome, int maxParticipantes, Date dataAberturaInscricoes, 
                   Date dataEncerramentoInscricoes, Date dataInicio, Date dataFim) {
        this.setNome(nome);
        this.setMaxParticipantes(maxParticipantes);
        this.setDataAberturaInscricoes(dataAberturaInscricoes);
        this.setDataEncerramentoInscricoes(dataEncerramentoInscricoes);
        this.setDataInicio(dataInicio);
        this.setDataFim(dataFim);
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do torneio é obrigatório");
        }
        this.nome = nome;
    }

    public void setMaxParticipantes(int maxParticipantes) {
        if (maxParticipantes <= 0) {
            throw new IllegalArgumentException("Número máximo de participantes deve ser positivo");
        }
        this.maxParticipantes = maxParticipantes;
    }

    public void setDataAberturaInscricoes(Date dataAberturaInscricoes) {
        if (dataAberturaInscricoes == null) {
            throw new IllegalArgumentException("Data de abertura de inscrições é obrigatória");
        }
        this.dataAberturaInscricoes = dataAberturaInscricoes;
    }

    public void setDataEncerramentoInscricoes(Date dataEncerramentoInscricoes) {
        if (dataEncerramentoInscricoes == null) {
            throw new IllegalArgumentException("Data de encerramento de inscrições é obrigatória");
        }
        if (dataAberturaInscricoes != null && normalizeToDay(dataEncerramentoInscricoes).before(normalizeToDay(dataAberturaInscricoes))) {
            throw new IllegalArgumentException("Data de encerramento deve ser após a abertura");
        }
        this.dataEncerramentoInscricoes = dataEncerramentoInscricoes;
    }

    public void setDataInicio(Date dataInicio) {
        if (dataInicio == null) {
            throw new IllegalArgumentException("Data de início é obrigatória");
        }
        if (dataEncerramentoInscricoes != null && normalizeToDay(dataInicio).before(normalizeToDay(dataEncerramentoInscricoes))) {
            throw new IllegalArgumentException("Data de início deve ser após o encerramento das inscrições");
        }
        this.dataInicio = dataInicio;
    }

    public void setDataFim(Date dataFim) {
        if (dataFim == null) {
            throw new IllegalArgumentException("Data de fim é obrigatória");
        }
        if (dataInicio != null && normalizeToDay(dataFim).before(normalizeToDay(dataInicio))) {
            throw new IllegalArgumentException("Data de fim deve ser após a data de início");
        }
        this.dataFim = dataFim;
    }

    public Set<Time> getTimes() {
        return times;
    }

    @Transient
    public String getStatusAtual() {
        Date hoje = normalizeToDay(new Date());
        Date fim = dataFim == null ? null : normalizeToDay(dataFim);
        Date inicio = dataInicio == null ? null : normalizeToDay(dataInicio);

        if (fim != null && hoje.after(fim)) {
            return "ENCERRADO";
        }

        if (inicio != null && !hoje.before(inicio)) {
            return "EM_ANDAMENTO";
        }

        return "ABERTO";
    }

    @Transient
    public boolean isInscricoesAbertas() {
        if (dataAberturaInscricoes == null || dataEncerramentoInscricoes == null) {
            return false;
        }

        Date hoje = normalizeToDay(new Date());
        Date abertura = normalizeToDay(dataAberturaInscricoes);
        Date encerramento = normalizeToDay(dataEncerramentoInscricoes);
        return !hoje.before(abertura) && !hoje.after(encerramento);
    }

    private Date normalizeToDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
