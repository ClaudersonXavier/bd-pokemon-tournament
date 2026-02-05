package com.ufape.projetobanquinhobd.seeder.torneios;

import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.services.TimeService;
import com.ufape.projetobanquinhobd.services.TorneioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Component
public class TorneioSeeder {

    @Autowired
    private TorneioService torneioService;

    @Autowired
    private TimeService timeService;

    private static final Map<String, TorneioConfig> TORNEIOS = new LinkedHashMap<>();

    static {
        TORNEIOS.put("Liga Kanto Regional", new TorneioConfig(
                4,
                createDate(2026, Calendar.JANUARY, 1),
                createDate(2026, Calendar.JANUARY, 10),
                createDate(2026, Calendar.JANUARY, 15),
                createDate(2026, Calendar.JANUARY, 25)
        ));

        TORNEIOS.put("Copa dos Mestres", new TorneioConfig(
                4,
                createDate(2026, Calendar.FEBRUARY, 1),
                createDate(2026, Calendar.FEBRUARY, 5),
                createDate(2026, Calendar.FEBRUARY, 10),
                createDate(2026, Calendar.FEBRUARY, 20)
        ));
    }

    @Transactional
    public void seed() {
        System.out.println("\n=== Populando Torneios ===");

        List<Time> todosOsTimes = timeService.listarTodos();
        if (todosOsTimes.isEmpty()) {
            System.out.println("⚠️ Nenhum time encontrado. Execute TimeSeeder primeiro.");
            return;
        }

        System.out.println("✓ Encontrados " + todosOsTimes.size() + " times");

        for (Map.Entry<String, TorneioConfig> entry : TORNEIOS.entrySet()) {
            String nome = entry.getKey();
            TorneioConfig config = entry.getValue();

            Torneio torneio = new Torneio(
                nome,
                config.maxParticipantes,
                config.dataAberturaInscricoes,
                config.dataEncerramentoInscricoes,
                config.dataInicio,
                config.dataFim
            );
            
            // Adicionar todos os times ao torneio usando reflexão
            try {
                Field timesField = Torneio.class.getDeclaredField("times");
                timesField.setAccessible(true);
                Set<Time> times = new HashSet<>(todosOsTimes);
                timesField.set(torneio, times);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.err.println("Erro ao configurar times do torneio: " + e.getMessage());
            }

            Torneio torneioSalvo = torneioService.salvar(torneio);
            System.out.println("✓ Torneio criado: " + torneioSalvo.getNome() + 
                             " (" + torneioSalvo.getTimes().size() + " times)");
        }

        System.out.println("✓ " + TORNEIOS.size() + " torneios criados com sucesso!");
    }

    private static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static class TorneioConfig {
        int maxParticipantes;
        Date dataAberturaInscricoes;
        Date dataEncerramentoInscricoes;
        Date dataInicio;
        Date dataFim;

        TorneioConfig(int maxParticipantes, Date dataAbertura, Date dataEncerramento, 
                     Date dataInicio, Date dataFim) {
            this.maxParticipantes = maxParticipantes;
            this.dataAberturaInscricoes = dataAbertura;
            this.dataEncerramentoInscricoes = dataEncerramento;
            this.dataInicio = dataInicio;
            this.dataFim = dataFim;
        }
    }
}
