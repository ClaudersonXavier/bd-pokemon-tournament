package com.ufape.projetobanquinhobd.seeder.batalhas;

import com.ufape.projetobanquinhobd.entities.Batalha;
import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Torneio;
import com.ufape.projetobanquinhobd.services.BatalhaService;
import com.ufape.projetobanquinhobd.services.TorneioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class BatalhaSeeder {

    @Autowired
    private BatalhaService batalhaService;

    @Autowired
    private TorneioService torneioService;

    private final Random random = new Random();

    @Transactional
    public void seed() {
        System.out.println("\n=== Populando Batalhas ===");

        List<Torneio> torneios = torneioService.listarTodos();
        if (torneios.isEmpty()) {
            System.out.println("⚠️ Nenhum torneio encontrado. Execute TorneioSeeder primeiro.");
            return;
        }

        System.out.println("✓ Encontrados " + torneios.size() + " torneios");
        System.out.println();

        Date hoje = new Date();
        int totalBatalhasCriadas = 0;
        int torneiosProcessados = 0;

        for (Torneio torneio : torneios) {
            torneiosProcessados++;
            
            // Determinar estado do torneio
            String estado = determinarEstado(torneio, hoje);
            int numTimes = torneio.getTimes().size();
            
            System.out.print("[" + torneiosProcessados + "/" + torneios.size() + "] " + 
                           torneio.getNome() + " (" + estado + ", " + numTimes + " times)... ");

            if (estado.equals("ABERTO")) {
                System.out.println("⊘ Sem batalhas (aberto para inscrição)");
                continue;
            }

            if (numTimes < 2) {
                System.out.println("⚠ Times insuficientes");
                continue;
            }

            try {
                int batalhasCriadas = 0;
                
                if (estado.equals("ENCERRADO")) {
                    // Criar TODAS as batalhas
                    batalhasCriadas = criarBatalhasCompletas(torneio);
                } else if (estado.equals("EM_ANDAMENTO")) {
                    // Criar METADE das batalhas
                    batalhasCriadas = criarBatalhasParciais(torneio);
                }
                
                totalBatalhasCriadas += batalhasCriadas;
                System.out.println("✓ " + batalhasCriadas + " batalhas");
                
            } catch (Exception e) {
                System.out.println("✗ Erro: " + e.getMessage());
            }
        }

        System.out.println();
        System.out.println("✓ Total: " + totalBatalhasCriadas + " batalhas criadas!");
        System.out.println("  - Torneios encerrados: todas as rodadas completas");
        System.out.println("  - Torneios em andamento: apenas primeira rodada");
        System.out.println("  - Torneios abertos: sem batalhas");
    }

    private String determinarEstado(Torneio torneio, Date hoje) {
        if (hoje.before(torneio.getDataEncerramentoInscricoes())) {
            return "ABERTO";
        } else if (hoje.after(torneio.getDataFim())) {
            return "ENCERRADO";
        } else {
            return "EM_ANDAMENTO";
        }
    }

    private int criarBatalhasCompletas(Torneio torneio) {
        List<Time> times = new ArrayList<>(torneio.getTimes());
        Collections.shuffle(times);
        
        int numTimes = times.size();
        int batalhasCriadas = 0;
        
        // Calcular número de rodadas (log2)
        int numRodadas = (int) (Math.log(numTimes) / Math.log(2));
        
        LocalDateTime dataBase = torneio.getDataInicio().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        List<Time> timesAtivos = new ArrayList<>(times);
        
        for (int rodada = 1; rodada <= numRodadas; rodada++) {
            List<Time> vencedores = new ArrayList<>();
            int batalhasPorRodada = timesAtivos.size() / 2;
            
            for (int i = 0; i < batalhasPorRodada; i++) {
                Time time1 = timesAtivos.get(i * 2);
                Time time2 = timesAtivos.get(i * 2 + 1);
                
                // Escolher vencedor aleatório
                Time vencedor = random.nextBoolean() ? time1 : time2;
                vencedores.add(vencedor);
                
                // Criar batalha
                LocalDateTime horaBatalha = dataBase.plusDays(rodada * 2).plusHours(i * 2);
                Batalha batalha = new Batalha(
                        rodada,
                        horaBatalha,
                        horaBatalha.minusHours(1),
                        torneio
                );
                batalha.addTimeParticipante(time1);
                batalha.addTimeParticipante(time2);
                batalha.setTimeVencedor(vencedor);
                
                batalhaService.salvar(batalha);
                batalhasCriadas++;
            }
            
            timesAtivos = vencedores;
        }
        
        return batalhasCriadas;
    }

    private int criarBatalhasParciais(Torneio torneio) {
        // Criar apenas a primeira rodada (metade das batalhas)
        List<Time> times = new ArrayList<>(torneio.getTimes());
        Collections.shuffle(times);
        
        int batalhasCriadas = 0;
        int batalhasPorRodada = times.size() / 2;
        
        LocalDateTime dataBase = torneio.getDataInicio().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        
        for (int i = 0; i < batalhasPorRodada; i++) {
            Time time1 = times.get(i * 2);
            Time time2 = times.get(i * 2 + 1);
            
            LocalDateTime horaBatalha = dataBase.plusHours(i * 2);
            Batalha batalha = new Batalha(
                    1, // Rodada 1
                    horaBatalha,
                    horaBatalha.minusHours(1),
                    torneio
            );
            batalha.addTimeParticipante(time1);
            batalha.addTimeParticipante(time2);
            // Não definir vencedor ainda (batalha em andamento)
            
            batalhaService.salvar(batalha);
            batalhasCriadas++;
        }
        
        return batalhasCriadas;
    }
}
