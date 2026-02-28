package com.ufape.projetobanquinhobd.seeder.torneios;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.services.TimeService;
import com.ufape.projetobanquinhobd.services.TorneioService;

@Component
public class TorneioSeeder {

    @Autowired
    private TorneioService torneioService;

    @Autowired
    private TimeService timeService;

    // Hoje: 27 de fevereiro de 2026
    private static final List<TorneioConfig> TORNEIOS = new ArrayList<>();

    static {
        // === TORNEIOS ENCERRADOS (6) - Com todas as batalhas ===
        
        // Encerrado 1 - 16 participantes
        TORNEIOS.add(new TorneioConfig(
                "Liga Kanto Regional",
                16,
                createDate(2026, Calendar.JANUARY, 1),
                createDate(2026, Calendar.JANUARY, 10),
                createDate(2026, Calendar.JANUARY, 15),
                createDate(2026, Calendar.JANUARY, 25),
                "ENCERRADO"
        ));

        // Encerrado 2 - 8 participantes
        TORNEIOS.add(new TorneioConfig(
                "Copa dos Mestres",
                8,
                createDate(2026, Calendar.JANUARY, 5),
                createDate(2026, Calendar.JANUARY, 12),
                createDate(2026, Calendar.JANUARY, 16),
                createDate(2026, Calendar.JANUARY, 23),
                "ENCERRADO"
        ));

        // Encerrado 3 - 16 participantes
        TORNEIOS.add(new TorneioConfig(
                "Taça Johto",
                16,
                createDate(2025, Calendar.DECEMBER, 10),
                createDate(2025, Calendar.DECEMBER, 20),
                createDate(2026, Calendar.JANUARY, 2),
                createDate(2026, Calendar.JANUARY, 12),
                "ENCERRADO"
        ));

        // Encerrado 4 - 8 participantes
        TORNEIOS.add(new TorneioConfig(
                "Campeonato Hoenn",
                8,
                createDate(2025, Calendar.DECEMBER, 1),
                createDate(2025, Calendar.DECEMBER, 10),
                createDate(2025, Calendar.DECEMBER, 15),
                createDate(2025, Calendar.DECEMBER, 28),
                "ENCERRADO"
        ));

        // Encerrado 5 - 16 participantes
        TORNEIOS.add(new TorneioConfig(
                "Torneio Sinnoh Elite",
                16,
                createDate(2025, Calendar.NOVEMBER, 15),
                createDate(2025, Calendar.NOVEMBER, 25),
                createDate(2025, Calendar.DECEMBER, 1),
                createDate(2025, Calendar.DECEMBER, 15),
                "ENCERRADO"
        ));

        // Encerrado 6 - 8 participantes
        TORNEIOS.add(new TorneioConfig(
                "Copa Unova",
                8,
                createDate(2025, Calendar.NOVEMBER, 1),
                createDate(2025, Calendar.NOVEMBER, 10),
                createDate(2025, Calendar.NOVEMBER, 15),
                createDate(2025, Calendar.NOVEMBER, 28),
                "ENCERRADO"
        ));

        // === TORNEIOS EM ANDAMENTO (2) - Com algumas batalhas ===
        
        // Em andamento 1 - 16 participantes
        TORNEIOS.add(new TorneioConfig(
                "Liga Kalos Champions",
                16,
                createDate(2026, Calendar.FEBRUARY, 1),
                createDate(2026, Calendar.FEBRUARY, 15),
                createDate(2026, Calendar.FEBRUARY, 20),
                createDate(2026, Calendar.MARCH, 10),
                "EM_ANDAMENTO"
        ));

        // Em andamento 2 - 8 participantes
        TORNEIOS.add(new TorneioConfig(
                "Batalha de Alola",
                8,
                createDate(2026, Calendar.FEBRUARY, 5),
                createDate(2026, Calendar.FEBRUARY, 18),
                createDate(2026, Calendar.FEBRUARY, 22),
                createDate(2026, Calendar.MARCH, 5),
                "EM_ANDAMENTO"
        ));

        // === TORNEIOS ABERTOS (2) - Sem batalhas ===
        
        // Aberto 1 - 16 participantes
        TORNEIOS.add(new TorneioConfig(
                "Grande Torneio Galar",
                16,
                createDate(2026, Calendar.FEBRUARY, 20),
                createDate(2026, Calendar.MARCH, 5),
                createDate(2026, Calendar.MARCH, 10),
                createDate(2026, Calendar.MARCH, 25),
                "ABERTO"
        ));

        // Aberto 2 - 8 participantes
        TORNEIOS.add(new TorneioConfig(
                "Desafio Paldea",
                8,
                createDate(2026, Calendar.FEBRUARY, 25),
                createDate(2026, Calendar.MARCH, 8),
                createDate(2026, Calendar.MARCH, 12),
                createDate(2026, Calendar.MARCH, 22),
                "ABERTO"
        ));
    }

    @Transactional
    public void seed() {
        System.out.println("\n=== Populando Torneios ===");

        // Verificar se já existem torneios para evitar duplicação
        List<Torneio> torneiosExistentes = torneioService.listarTodos();
        if (!torneiosExistentes.isEmpty()) {
            System.out.println("⚠️ Já existem " + torneiosExistentes.size() + " torneios no banco.");
            System.out.println("✓ Pulando seed de torneios para evitar duplicação.");
            return;
        }

        List<Time> todosOsTimes = timeService.listarTodos();
        if (todosOsTimes.isEmpty()) {
            System.out.println("⚠️ Nenhum time encontrado. Execute TimeSeeder primeiro.");
            return;
        }

        System.out.println("✓ Encontrados " + todosOsTimes.size() + " times disponíveis");
        System.out.println("✓ Criando 10 torneios: 6 encerrados, 2 em andamento, 2 abertos");
        System.out.println();

        int torneiosCriados = 0;

        for (TorneioConfig config : TORNEIOS) {
            try {
                torneiosCriados++;
                
                System.out.print("[" + torneiosCriados + "/10] " + config.nome + 
                               " (" + config.estado + ", " + config.maxParticipantes + " slots)... ");
                
                Torneio torneio = new Torneio(
                    config.nome,
                    config.maxParticipantes,
                    config.dataAberturaInscricoes,
                    config.dataEncerramentoInscricoes,
                    config.dataInicio,
                    config.dataFim
                );
                
                // Adicionar times diretamente ao Set (permite duplicação entre torneios)
                List<Time> timesEmbaralhados = new ArrayList<>(todosOsTimes);
                Collections.shuffle(timesEmbaralhados);
                
                int timesAdicionados = 0;
                for (Time time : timesEmbaralhados) {
                    if (timesAdicionados >= config.maxParticipantes) {
                        break;
                    }
                    torneio.getTimes().add(time);
                    timesAdicionados++;
                }
                
                if (torneio.getTimes().size() < config.maxParticipantes) {
                    System.err.print("⚠ Apenas " + torneio.getTimes().size() + " times adicionados... ");
                }

                torneioService.salvar(torneio);
                System.out.println("✓ (" + torneio.getTimes().size() + "/" + config.maxParticipantes + " times)");
                
            } catch (Exception e) {
                System.err.println("✗ Erro: " + e.getMessage());
            }
        }

        System.out.println();
        System.out.println("✓ " + torneiosCriados + " torneios criados com sucesso!");
        System.out.println("  - 6 Encerrados (com todas as batalhas)");
        System.out.println("  - 2 Em Andamento (com algumas batalhas)");
        System.out.println("  - 2 Abertos (sem batalhas)");
    }

    private static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static class TorneioConfig {
        String nome;
        int maxParticipantes;
        Date dataAberturaInscricoes;
        Date dataEncerramentoInscricoes;
        Date dataInicio;
        Date dataFim;
        String estado;

        TorneioConfig(String nome, int maxParticipantes, Date dataAbertura, Date dataEncerramento,
                     Date dataInicio, Date dataFim, String estado) {
            this.nome = nome;
            this.maxParticipantes = maxParticipantes;
            this.dataAberturaInscricoes = dataAbertura;
            this.dataEncerramentoInscricoes = dataEncerramento;
            this.dataInicio = dataInicio;
            this.dataFim = dataFim;
            this.estado = estado;
        }
    }
}
