package com.ufape.projetobanquinhobd.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Comment("Tabela de torneios e seus periodos de inscricao e execucao.")
public class Torneio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Identificador da competicao.")
    private long id;

    @Column(nullable = false)
    @Comment("Nome daquele torneio")
    private String nome;

    @Column(nullable = false)
    @Comment("Quantidade máximo de participanetes que o torneio poderá ter")
    private int  maxParticipantes;

    @Column(nullable = false)
    @Comment("Data de abertura das inscricoes do torneio.")
    private Date dataAberturaInscricoes;

    @Column(nullable = false)
    @Comment("Data de encerramento das inscricoes do torneio.")
    private Date dataEncerramentoInscricoes;

    @Column(nullable = false)
    @Comment("Data de inicio do torneio.")
    private Date dataInicio;

    @Column(nullable = false)
    @Comment("Data do fim do torneio.")
    private Date dataFim;

    @OneToMany(mappedBy = "torneio")
    @Comment("Batalhas que compoem o torneio.")
    private Set<Batalha> batalhas;

    @ManyToMany
    @Comment("Times inscritos no torneio.")
    private Set<Time> times;
}
