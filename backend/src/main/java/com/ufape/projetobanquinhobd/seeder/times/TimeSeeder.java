package com.ufape.projetobanquinhobd.seeder.times;

import com.ufape.projetobanquinhobd.auth.TreinadorUserDetailsService;
import com.ufape.projetobanquinhobd.entities.Pokemon;
import com.ufape.projetobanquinhobd.entities.Time;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.services.TimeService;
import com.ufape.projetobanquinhobd.services.TreinadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class TimeSeeder {
    
    @Autowired
    private TimeService timeService;
    
    @Autowired
    private TreinadorService treinadorService;
    
    // Sufixos temáticos para os nomes dos times
    private static final Map<String, String> SUFIXOS_TIMES = Map.of(
        "Ash Ketchum", "Campeão",
        "Misty", "Cascata",
        "Brock", "Pedra",
        "Gary Oak", "Elite"
    );
    
    @Transactional
    public void seed() {
        System.out.println("=== Iniciando seed de Times ===");
        
        // Buscar treinadores
        List<Treinador> treinadores = treinadorService.listarTodos().stream()
                .filter(treinador -> !TreinadorUserDetailsService.ADMIN_EMAIL
                        .equalsIgnoreCase(treinador.getCredenciais().getEmail()))
                .toList();
        
        if (treinadores.isEmpty()) {
            System.err.println("✗ Nenhum treinador encontrado! Execute o seed de treinadores primeiro.");
            return;
        }
        
        System.out.println("✓ Encontrados " + treinadores.size() + " treinadores (não-admin)");
        System.out.println("✓ Meta: 1 time por treinador");
        System.out.println();
        
        int criados = 0;
        int jaExistiam = 0;
        int processados = 0;
        
        for (Treinador treinador : treinadores) {
            try {
                processados++;
                
                // Verificar se o treinador já tem time
                if (!treinador.getTimes().isEmpty()) {
                    System.out.println("[" + processados + "/" + treinadores.size() + "] " + 
                                     treinador.getNome() + " - ✓ Já possui time");
                    jaExistiam++;
                    continue;
                }
                
                // Gerar nome do time
                String sufixo = SUFIXOS_TIMES.getOrDefault(treinador.getNome(), "Desafiante");
                String nomeTime = "Time " + sufixo + " de " + treinador.getNome().split(" ")[0];
                
                System.out.print("[" + processados + "/" + treinadores.size() + "] " + 
                               treinador.getNome() + " - Criando: " + nomeTime + "... ");
                System.out.print("[" + processados + "/" + treinadores.size() + "] " + 
                               treinador.getNome() + " - Criando: " + nomeTime + "... ");
                
                // Criar time
                Time time = new Time(nomeTime, treinador);
                
                // Buscar pokémons do treinador
                Set<Pokemon> pokemonsDoTreinador = treinador.getPokemons();
                
                if (!pokemonsDoTreinador.isEmpty()) {
                    for (Pokemon pokemon : pokemonsDoTreinador) {
                        time.addPokemon(pokemon);
                        pokemon.addTime(time);
                    }
                }
                
                // Salvar time
                timeService.salvar(time);
                treinador.addTime(time);
                treinadorService.salvar(treinador);
                
                criados++;
                System.out.println("✓ (" + pokemonsDoTreinador.size() + " pokémons)");
                
            } catch (Exception e) {
                System.err.println("✗ Erro: " + e.getMessage());
            }
        }
        
        System.out.println();
        System.out.println("=== Seed de Times finalizado! ===");
        System.out.println("Total de treinadores processados: " + processados);
        System.out.println("Times já existentes: " + jaExistiam);
        System.out.println("Times criados: " + criados);
        System.out.println("Total final: " + (jaExistiam + criados) + " times");
    }
}
