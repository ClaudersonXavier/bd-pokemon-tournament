package com.ufape.projetobanquinhobd.seeder.times;

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
        List<Treinador> treinadores = treinadorService.listarTodos();
        
        if (treinadores.isEmpty()) {
            System.err.println("✗ Nenhum treinador encontrado! Execute o seed de treinadores primeiro.");
            return;
        }
        
        System.out.println("Encontrados " + treinadores.size() + " treinadores");
        System.out.println("Criando 1 time para cada treinador...");
        System.out.println();
        
        int count = 0;
        int salvos = 0;
        
        for (Treinador treinador : treinadores) {
            try {
                count++;
                
                // Gerar nome do time
                String sufixo = SUFIXOS_TIMES.getOrDefault(treinador.getNome(), "Desafiante");
                String nomeTime = "Time " + sufixo + " de " + treinador.getNome().split(" ")[0];
                
                System.out.println("[" + count + "/" + treinadores.size() + "] Criando: " + nomeTime);
                
                // Criar time
                Time time = new Time(nomeTime, treinador);
                
                // Buscar pokémons do treinador
                Set<Pokemon> pokemonsDoTreinador = treinador.getPokemons();
                
                if (pokemonsDoTreinador.isEmpty()) {
                    System.out.println("  ⚠ Treinador não possui pokémons, criando time vazio");
                } else {
                    System.out.println("  - Adicionando " + pokemonsDoTreinador.size() + " pokémons ao time");
                    
                    int pokemonCount = 0;
                    for (Pokemon pokemon : pokemonsDoTreinador) {
                        time.addPokemon(pokemon);
                        pokemon.addTime(time);
                        pokemonCount++;
                    }
                    
                    System.out.println("  - Pokémons: " + pokemonsDoTreinador.stream()
                            .map(Pokemon::getApelido)
                            .limit(3)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("") + 
                            (pokemonsDoTreinador.size() > 3 ? "..." : ""));
                }
                
                // Salvar time
                timeService.salvar(time);
                treinador.addTime(time);
                treinadorService.salvar(treinador);
                
                salvos++;
                System.out.println("  ✓ Salvo com sucesso!");
                
            } catch (Exception e) {
                System.err.println("  ✗ Erro ao criar time: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println();
        }
        
        System.out.println("=== Seed de Times finalizado! ===");
        System.out.println("Total processado: " + count + " times");
        System.out.println("Total salvos: " + salvos + " times");
    }
}
