package com.ufape.projetobanquinhobd.seeder.treinadores;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ufape.projetobanquinhobd.entities.CredenciaisUsuario;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.repositories.TreinadorRepository;
import com.ufape.projetobanquinhobd.services.TreinadorService;

@Component
public class TreinadorSeeder {
    
    @Autowired
    private TreinadorService treinadorService;

    @Autowired
    private TreinadorRepository treinadorRepository;
    
    // Treinadores mockados manualmente (NÃO REMOVER!)
    private static final List<TreinadorSeedData> TREINADORES_MOCKADOS = Arrays.asList(
        new TreinadorSeedData("Admin", "admin@pokemon.com", "123456"),
        new TreinadorSeedData("Ash Ketchum", "ash@pokemon.com", "ash123"),
        new TreinadorSeedData("Misty", "misty@pokemon.com", "misty123"),
        new TreinadorSeedData("Brock", "brock@pokemon.com", "brock123"),
        new TreinadorSeedData("Gary Oak", "gary@pokemon.com", "gary123")
    );
    
    // Nomes para gerar treinadores adicionais (personagens do universo Pokémon)
    private static final String[] NOMES_ADICIONAIS = {
        "Lance", "Cynthia", "Steven Stone", "Wallace", "Diantha", "Leon",
        "Iris", "Red", "Blue", "Green", "Gold", "Crystal", "Ruby", "Sapphire",
        "Dawn", "May", "Serena", "Clemont", "Cilan", "Tracey", "Paul",
        "Giovanni", "Archie", "Maxie", "Cyrus", "Ghetsis", "Lysandre",
        "Erika", "Sabrina", "Lt. Surge", "Blaine", "Koga", "Janine",
        "Whitney", "Morty", "Jasmine", "Pryce", "Clair", "Chuck",
        "Roxanne", "Brawly", "Wattson", "Flannery", "Norman", "Winona", "Tate", "Liza",
        "Roark", "Gardenia", "Maylene", "Crasher Wake", "Fantina", "Byron", "Candice", "Volkner",
        "Lenora", "Burgh", "Elesa", "Clay", "Skyla", "Drayden", "Marlon",
        "Viola", "Grant", "Korrina", "Ramos", "Valerie", "Olympia", "Wulfric",
        "Milo", "Nessa", "Kabu", "Bea", "Allister", "Opal", "Gordie", "Melony", "Piers", "Raihan", "Clau"
    };
    
    private static List<TreinadorSeedData> gerarTodosOsTreinadores() {
        List<TreinadorSeedData> todos = new ArrayList<>(TREINADORES_MOCKADOS);
        
        // Adicionar 45 treinadores gerados para totalizar 50
        int quantidade = 51 - TREINADORES_MOCKADOS.size();
        
        for (int i = 0; i < quantidade && i < NOMES_ADICIONAIS.length; i++) {
            String nome = NOMES_ADICIONAIS[i];
            String email = nome.toLowerCase()
                    .replace(" ", "")
                    .replace(".", "")
                    + "@pokemon.com";
            String senha = "senha123";
            
            todos.add(new TreinadorSeedData(nome, email, senha));
        }
        
        return todos;
    }
    
    private static final List<TreinadorSeedData> TREINADORES = gerarTodosOsTreinadores();
    
    public void seed() {
        System.out.println("=== Iniciando seed de Treinadores ===");
        System.out.println("Total de treinadores a criar: " + TREINADORES.size());
        System.out.println("  - Mockados manualmente: " + TREINADORES_MOCKADOS.size());
        System.out.println("  - Gerados automaticamente: " + (TREINADORES.size() - TREINADORES_MOCKADOS.size()));
        System.out.println();
        
        int count = 0;
        int salvos = 0;
        int atualizados = 0;
        
        for (TreinadorSeedData treinadorSeedData : TREINADORES) {
            try {
                count++;
                System.out.print("[" + count + "/" + TREINADORES.size() + "] " + treinadorSeedData.nome + "... ");
                
                boolean existe = treinadorRepository.findByCredenciaisEmail(treinadorSeedData.email).isPresent();
                
                Treinador treinador = treinadorRepository
                        .findByCredenciaisEmail(treinadorSeedData.email)
                        .map(existente -> {
                            existente.setNome(treinadorSeedData.nome);
                            existente.setCredenciais(new CredenciaisUsuario(
                                    treinadorSeedData.email,
                                    treinadorSeedData.senha
                            ));
                            return existente;
                        })
                        .orElseGet(() -> new Treinador(
                                treinadorSeedData.nome,
                                new CredenciaisUsuario(
                                        treinadorSeedData.email,
                                        treinadorSeedData.senha
                                )
                        ));

                treinadorService.salvar(treinador);
                salvos++;
                
                if (existe) {
                    atualizados++;
                    System.out.println("✓ Atualizado");
                } else {
                    System.out.println("✓ Criado");
                }
                
            } catch (Exception e) {
                System.err.println("✗ Erro: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println();
        System.out.println("=== Seed de Treinadores finalizado! ===");
        System.out.println("Total processado: " + count + " treinadores");
        System.out.println("Criados: " + (salvos - atualizados) + " | Atualizados: " + atualizados);
    }

    private static class TreinadorSeedData {
        private final String nome;
        private final String email;
        private final String senha;

        private TreinadorSeedData(String nome, String email, String senha) {
            this.nome = nome;
            this.email = email;
            this.senha = senha;
        }
    }
}
