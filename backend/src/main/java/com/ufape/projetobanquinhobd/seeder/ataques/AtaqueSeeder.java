package com.ufape.projetobanquinhobd.seeder.ataques;


import com.ufape.projetobanquinhobd.entities.Ataque;
import com.ufape.projetobanquinhobd.entities.Tipo;
import com.ufape.projetobanquinhobd.services.AtaqueService;
import com.ufape.projetobanquinhobd.services.TipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Optional;
import com.ufape.projetobanquinhobd.seeder.dtos.MoveDetailResponse;

@Component
public class AtaqueSeeder {

    @Autowired
    private WebClient webClient;

    @Autowired
    private AtaqueService ataqueService;

    @Autowired
    private TipoService tipoService;

    /**
     * Seed dos ataques, buscando da PokéAPI.
     * @param limit Quantidade de ataques a buscar (ex: 50)
     */
    public void seed(int limit) {
        System.out.println("=== Iniciando seed de Ataques ===");
        System.out.println("Buscando " + limit + " ataques...");
        
        int count = 0;
        int salvos = 0;
        for (int i = 1; i <= limit; i++) {
            try {
                count++;
                System.out.println("[" + count + "/" + limit + "] Processando: move/" + i);
                
                MoveDetailResponse move = webClient.get()
                        .uri("/move/" + i)
                        .retrieve()
                        .bodyToMono(MoveDetailResponse.class)
                        .block();
                        
                if (move == null) {
                    System.err.println("  ✗ Não foi possível buscar move " + i);
                    continue;
                }
                
                String nome = capitalizeFirst(move.getName());
                String categoria = move.getDamage_class() != null ? move.getDamage_class().getName() : "";
                int poder = move.getPower() != null ? move.getPower() : 0;
                String tipoNome = move.getType() != null ? move.getType().getName() : "";
                
                if (nome.isEmpty() || categoria.isEmpty() || tipoNome.isEmpty()) {
                    System.err.println("  ✗ Dados incompletos para move " + i + ": " + nome);
                    continue;
                }
                
                // Capitalizar nome do tipo para padronização
                String tipoNomeFormatado = capitalizeFirst(tipoNome);
                
                if (persistAtaque(nome, categoria, poder, tipoNomeFormatado)) {
                    salvos++;
                    System.out.println("  ✓ Salvo com sucesso!");
                }
                
                Thread.sleep(100);
                
            } catch (WebClientResponseException e) {
                System.err.println("  ✗ Erro HTTP ao buscar move " + i + ": " + e.getStatusCode());
            } catch (Exception e) {
                System.err.println("  ✗ Erro ao processar move " + i + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== Seed de Ataques finalizado! ===");
        System.out.println("Total processado: " + count + " ataques");
        System.out.println("Total salvos: " + salvos + " ataques");
    }

    /**
     * Persiste o ataque se o tipo existir. Retorna true se persistiu, false caso contrário.
     */
    private boolean persistAtaque(String nome, String categoria, int poder, String tipoNome) {
        Optional<Tipo> tipoOpt = tipoService.buscarPorNome(tipoNome);
        if (tipoOpt.isEmpty()) {
            System.err.println("  ✗ Tipo não encontrado: " + tipoNome + ". Ataque: " + nome);
            return false;
        }
        if (ataqueService.buscarPorNome(nome).isEmpty()) {
            Ataque ataque = new Ataque(nome, categoria, poder, tipoOpt.get());
            ataqueService.salvar(ataque);
            return true;
        }
        return false;
    }

    private String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}