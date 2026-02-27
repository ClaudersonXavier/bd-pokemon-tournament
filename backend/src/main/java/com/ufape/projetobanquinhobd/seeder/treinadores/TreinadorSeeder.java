package com.ufape.projetobanquinhobd.seeder.treinadores;

import com.ufape.projetobanquinhobd.entities.CredenciaisUsuario;
import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.repositories.TreinadorRepository;
import com.ufape.projetobanquinhobd.services.TreinadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TreinadorSeeder {
    
    @Autowired
    private TreinadorService treinadorService;

    @Autowired
    private TreinadorRepository treinadorRepository;
    
    // Treinadores icônicos do universo Pokémon
    private static final List<TreinadorSeedData> TREINADORES = Arrays.asList(
        new TreinadorSeedData("Admin", "admin@pokemon.com", "123456"),
        new TreinadorSeedData("Ash Ketchum", "ash@pokemon.com", "ash123"),
        new TreinadorSeedData("Misty", "misty@pokemon.com", "misty123"),
        new TreinadorSeedData("Brock", "brock@pokemon.com", "brock123"),
        new TreinadorSeedData("Gary Oak", "gary@pokemon.com", "gary123")
    );
    
    public void seed() {
        System.out.println("=== Iniciando seed de Treinadores ===");
        System.out.println("Criando " + TREINADORES.size() + " treinadores...");
        
        int count = 0;
        int salvos = 0;
        
        for (TreinadorSeedData treinadorSeedData : TREINADORES) {
            try {
                count++;
                System.out.println("[" + count + "/" + TREINADORES.size() + "] Processando: " + treinadorSeedData.nome);
                
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
                System.out.println("  ✓ Salvo com sucesso!");
                
            } catch (Exception e) {
                System.err.println("  ✗ Erro ao processar treinador " + treinadorSeedData.nome + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("=== Seed de Treinadores finalizado! ===");
        System.out.println("Total processado: " + count + " treinadores");
        System.out.println("Total salvos: " + salvos + " treinadores");
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
