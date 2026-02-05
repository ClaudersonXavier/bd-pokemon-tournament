package com.ufape.projetobanquinhobd.seeder.pokemons;

import com.ufape.projetobanquinhobd.entities.Ataque;
import com.ufape.projetobanquinhobd.entities.Especie;
import com.ufape.projetobanquinhobd.entities.Pokemon;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.services.AtaqueService;
import com.ufape.projetobanquinhobd.services.EspecieService;
import com.ufape.projetobanquinhobd.services.PokemonService;
import com.ufape.projetobanquinhobd.services.TreinadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class PokemonSeeder {
    
    @Autowired
    private PokemonService pokemonService;
    
    @Autowired
    private TreinadorService treinadorService;
    
    @Autowired
    private EspecieService especieService;
    
    @Autowired
    private AtaqueService ataqueService;
    
    private final Random random = new Random();
    private static final int POKEMONS_POR_TREINADOR = 6;
    private static final int MIN_ATAQUES = 2;
    private static final int MAX_ATAQUES = 4;
    
    @Transactional
    public void seed() {
        System.out.println("=== Iniciando seed de Pokémons ===");
        
        // Buscar dados necessários
        List<Treinador> treinadores = treinadorService.listarTodos();
        List<Especie> especies = especieService.listarTodos();
        List<Ataque> ataques = ataqueService.listarTodos();
        
        if (treinadores.isEmpty()) {
            System.err.println("✗ Nenhum treinador encontrado! Execute o seed de treinadores primeiro.");
            return;
        }
        
        if (especies.isEmpty()) {
            System.err.println("✗ Nenhuma espécie encontrada! Execute o seed de espécies primeiro.");
            return;
        }
        
        if (ataques.isEmpty()) {
            System.err.println("✗ Nenhum ataque encontrado! Execute o seed de ataques primeiro.");
            return;
        }
        
        System.out.println("Encontrados " + treinadores.size() + " treinadores");
        System.out.println("Encontradas " + especies.size() + " espécies disponíveis");
        System.out.println("Encontrados " + ataques.size() + " ataques disponíveis");
        System.out.println();
        
        int totalPokemons = treinadores.size() * POKEMONS_POR_TREINADOR;
        int count = 0;
        int salvos = 0;
        
        // Embaralhar espécies para distribuição aleatória
        List<Especie> especiesEmbaralhadas = new ArrayList<>(especies);
        Collections.shuffle(especiesEmbaralhadas);
        
        int especieIndex = 0;
        
        for (Treinador treinador : treinadores) {
            System.out.println(">>> Criando pokémons para: " + treinador.getNome());
            
            for (int i = 1; i <= POKEMONS_POR_TREINADOR; i++) {
                try {
                    count++;
                    
                    // Pegar próxima espécie (com wraparound)
                    Especie especie = especiesEmbaralhadas.get(especieIndex % especiesEmbaralhadas.size());
                    especieIndex++;
                    
                    // Gerar apelido único
                    String apelido = gerarApelido(especie, treinador, i);
                    
                    System.out.println("[" + count + "/" + totalPokemons + "] Criando: " + apelido + " (" + especie.getNome() + ")");
                    
                    // Criar pokémon
                    Pokemon pokemon = new Pokemon(apelido, especie, treinador);
                    
                    // Adicionar ataques compatíveis
                    int numAtaques = MIN_ATAQUES + random.nextInt(MAX_ATAQUES - MIN_ATAQUES + 1);
                    List<Ataque> ataquesCompativeis = buscarAtaquesCompativeis(especie, ataques);
                    
                    if (ataquesCompativeis.isEmpty()) {
                        System.out.println("  ⚠ Nenhum ataque compatível encontrado, usando ataques aleatórios");
                        ataquesCompativeis = new ArrayList<>(ataques);
                    }
                    
                    Collections.shuffle(ataquesCompativeis);
                    int ataquesAdicionados = 0;
                    
                    for (Ataque ataque : ataquesCompativeis) {
                        if (ataquesAdicionados >= numAtaques) break;
                        pokemon.addAtaque(ataque);
                        ataquesAdicionados++;
                    }
                    
                    System.out.println("  - Ataques: " + pokemon.getAtaques().stream()
                            .map(Ataque::getNome)
                            .collect(Collectors.joining(", ")));
                    
                    // Salvar
                    pokemonService.salvar(pokemon);
                    treinador.addPokemon(pokemon);
                    treinadorService.salvar(treinador);
                    
                    salvos++;
                    System.out.println("  ✓ Salvo com sucesso!");
                    
                } catch (Exception e) {
                    System.err.println("  ✗ Erro ao criar pokémon: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println();
        }
        
        System.out.println("=== Seed de Pokémons finalizado! ===");
        System.out.println("Total processado: " + count + " pokémons");
        System.out.println("Total salvos: " + salvos + " pokémons");
    }
    
    /**
     * Gera apelido único baseado na espécie e treinador
     */
    private String gerarApelido(Especie especie, Treinador treinador, int numero) {
        String nomeEspecie = capitalizeFirst(especie.getNome());
        String sufixo = treinador.getNome().split(" ")[0]; // Primeiro nome do treinador
        return nomeEspecie + " de " + sufixo + " #" + numero;
    }
    
    /**
     * Busca ataques compatíveis com os tipos da espécie
     */
    private List<Ataque> buscarAtaquesCompativeis(Especie especie, List<Ataque> todosAtaques) {
        // Ataques cujo tipo é um dos tipos da espécie
        return todosAtaques.stream()
                .filter(ataque -> especie.getTipos().stream()
                        .anyMatch(tipo -> tipo.getNome().equals(ataque.getTipo().getNome())))
                .collect(Collectors.toList());
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
