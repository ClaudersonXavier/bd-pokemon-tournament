package com.ufape.projetobanquinhobd.seeder.treinadores;

import com.ufape.projetobanquinhobd.entities.Treinador;
import com.ufape.projetobanquinhobd.services.TreinadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TreinadorSeeder {
    
    @Autowired
    private TreinadorService treinadorService;
    
    // Treinadores icônicos do universo Pokémon
    private static final List<String> TREINADORES = Arrays.asList(
        "Ash Ketchum",
        "Misty",
        "Brock",
        "Gary Oak"
    );
    
    public void seed() {
        System.out.println("=== Iniciando seed de Treinadores ===");
        System.out.println("Criando " + TREINADORES.size() + " treinadores...");
        
        int count = 0;
        int salvos = 0;
        
        for (String nome : TREINADORES) {
            try {
                count++;
                System.out.println("[" + count + "/" + TREINADORES.size() + "] Processando: " + nome);
                
                // Verificar se já existe (por enquanto cria sempre, mas pode adicionar validação)
                Treinador treinador = new Treinador(nome);
                treinadorService.salvar(treinador);
                salvos++;
                System.out.println("  ✓ Salvo com sucesso!");
                
            } catch (Exception e) {
                System.err.println("  ✗ Erro ao processar treinador " + nome + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("=== Seed de Treinadores finalizado! ===");
        System.out.println("Total processado: " + count + " treinadores");
        System.out.println("Total salvos: " + salvos + " treinadores");
    }
}
