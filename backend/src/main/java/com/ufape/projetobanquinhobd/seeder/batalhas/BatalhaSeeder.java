package com.ufape.projetobanquinhobd.seeder.batalhas;

import com.ufape.projetobanquinhobd.entities.Batalha;
import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.services.BatalhaService;
import com.ufape.projetobanquinhobd.services.TimeService;
import com.ufape.projetobanquinhobd.services.TorneioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BatalhaSeeder {

    @Autowired
    private BatalhaService batalhaService;

    @Autowired
    private TorneioService torneioService;

    @Autowired
    private TimeService timeService;

    @Transactional
    public void seed() {
        System.out.println("\n=== Populando Batalhas ===");

        List<Torneio> torneios = torneioService.listarTodos();
        if (torneios.isEmpty()) {
            System.out.println("⚠️ Nenhum torneio encontrado. Execute TorneioSeeder primeiro.");
            return;
        }

        List<Time> todosOsTimes = timeService.listarTodos();
        if (todosOsTimes.size() < 4) {
            System.out.println("⚠️ Necessários 4 times. Encontrados: " + todosOsTimes.size());
            return;
        }

        System.out.println("✓ Encontrados " + torneios.size() + " torneios e " + todosOsTimes.size() + " times");

        // Organizar times por treinador (ordem alfabética para previsibilidade)
        List<Time> timesOrdenados = new ArrayList<>(todosOsTimes);
        timesOrdenados.sort((t1, t2) -> t1.getNome().compareTo(t2.getNome()));

        // Assumindo ordem: Time Campeão de Ash, Time Cascata de Misty, Time Elite de Gary, Time Pedra de Brock
        Time timeAsh = timesOrdenados.get(0);
        Time timeMisty = timesOrdenados.get(1);
        Time timeGary = timesOrdenados.get(2);
        Time timeBrock = timesOrdenados.get(3);

        int batalhasCriadas = 0;

        // Criar batalhas para cada torneio
        for (Torneio torneio : torneios) {
            System.out.println("\n>>> Criando batalhas para: " + torneio.getNome());

            if (torneio.getNome().contains("Liga Kanto")) {
                batalhasCriadas += criarBatalhasLigaKanto(torneio, timeAsh, timeMisty, timeGary, timeBrock);
            } else if (torneio.getNome().contains("Copa dos Mestres")) {
                batalhasCriadas += criarBatalhasCopaDosMestres(torneio, timeAsh, timeMisty, timeGary, timeBrock);
            }
        }

        System.out.println("\n✓ " + batalhasCriadas + " batalhas criadas com sucesso!");
    }

    private int criarBatalhasLigaKanto(Torneio torneio, Time timeAsh, Time timeMisty, 
                                        Time timeGary, Time timeBrock) {
        int count = 0;

        // Rodada 1 - Semifinais (15/01/2026)
        // Batalha 1: Ash vs Misty
        Batalha semi1 = new Batalha(
                1,
                LocalDateTime.of(2026, 1, 15, 11, 0),
                LocalDateTime.of(2026, 1, 15, 10, 0),
                torneio
        );
        semi1.addTimeParticipante(timeAsh);
        semi1.addTimeParticipante(timeMisty);
        semi1.setTimeVencedor(timeAsh); // Ash vence
        batalhaService.salvar(semi1);
        count++;
        System.out.println("  ✓ Rodada 1, Batalha 1: " + timeAsh.getNome() + " vs " + 
                         timeMisty.getNome() + " → Vencedor: " + timeAsh.getNome());

        // Batalha 2: Brock vs Gary
        Batalha semi2 = new Batalha(
                1,
                LocalDateTime.of(2026, 1, 15, 15, 0),
                LocalDateTime.of(2026, 1, 15, 14, 0),
                torneio
        );
        semi2.addTimeParticipante(timeBrock);
        semi2.addTimeParticipante(timeGary);
        semi2.setTimeVencedor(timeGary); // Gary vence
        batalhaService.salvar(semi2);
        count++;
        System.out.println("  ✓ Rodada 1, Batalha 2: " + timeBrock.getNome() + " vs " + 
                         timeGary.getNome() + " → Vencedor: " + timeGary.getNome());

        // Rodada 2 - Final (20/01/2026)
        // Batalha 3: Ash vs Gary
        Batalha finalBatalha = new Batalha(
                2,
                LocalDateTime.of(2026, 1, 20, 17, 0),
                LocalDateTime.of(2026, 1, 20, 16, 0),
                torneio
        );
        finalBatalha.addTimeParticipante(timeAsh);
        finalBatalha.addTimeParticipante(timeGary);
        finalBatalha.setTimeVencedor(timeAsh); // Ash vence o torneio
        batalhaService.salvar(finalBatalha);
        count++;
        System.out.println("  ✓ Rodada 2 (FINAL): " + timeAsh.getNome() + " vs " + 
                         timeGary.getNome() + " → CAMPEÃO: " + timeAsh.getNome());

        return count;
    }

    private int criarBatalhasCopaDosMestres(Torneio torneio, Time timeAsh, Time timeMisty, 
                                             Time timeGary, Time timeBrock) {
        int count = 0;

        // Rodada 1 - Semifinais (10/02/2026)
        // Batalha 1: Ash vs Brock
        Batalha semi1 = new Batalha(
                1,
                LocalDateTime.of(2026, 2, 10, 11, 0),
                LocalDateTime.of(2026, 2, 10, 10, 0),
                torneio
        );
        semi1.addTimeParticipante(timeAsh);
        semi1.addTimeParticipante(timeBrock);
        semi1.setTimeVencedor(timeBrock); // Brock vence
        batalhaService.salvar(semi1);
        count++;
        System.out.println("  ✓ Rodada 1, Batalha 1: " + timeAsh.getNome() + " vs " + 
                         timeBrock.getNome() + " → Vencedor: " + timeBrock.getNome());

        // Batalha 2: Misty vs Gary
        Batalha semi2 = new Batalha(
                1,
                LocalDateTime.of(2026, 2, 10, 15, 0),
                LocalDateTime.of(2026, 2, 10, 14, 0),
                torneio
        );
        semi2.addTimeParticipante(timeMisty);
        semi2.addTimeParticipante(timeGary);
        semi2.setTimeVencedor(timeMisty); // Misty vence
        batalhaService.salvar(semi2);
        count++;
        System.out.println("  ✓ Rodada 1, Batalha 2: " + timeMisty.getNome() + " vs " + 
                         timeGary.getNome() + " → Vencedor: " + timeMisty.getNome());

        // Rodada 2 - Final (15/02/2026)
        // Batalha 3: Brock vs Misty
        Batalha finalBatalha = new Batalha(
                2,
                LocalDateTime.of(2026, 2, 15, 17, 0),
                LocalDateTime.of(2026, 2, 15, 16, 0),
                torneio
        );
        finalBatalha.addTimeParticipante(timeBrock);
        finalBatalha.addTimeParticipante(timeMisty);
        finalBatalha.setTimeVencedor(timeMisty); // Misty vence o torneio
        batalhaService.salvar(finalBatalha);
        count++;
        System.out.println("  ✓ Rodada 2 (FINAL): " + timeBrock.getNome() + " vs " + 
                         timeMisty.getNome() + " → CAMPEÃ: " + timeMisty.getNome());

        return count;
    }
}
