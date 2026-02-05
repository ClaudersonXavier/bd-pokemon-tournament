package com.ufape.projetobanquinhobd.seeder.tipos;

import com.ufape.projetobanquinhobd.entities.Tipo;
import com.ufape.projetobanquinhobd.seeder.dtos.TypeListResponse;
import com.ufape.projetobanquinhobd.services.TipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TipoSeeder {
    
    @Autowired
    private WebClient webClient;
    
    @Autowired
    private TipoService tipoService;

    public void seed() {
        System.out.println("=== Iniciando seed de Tipos ===");
        System.out.println("Buscando lista de tipos da PokeAPI...");
        
        TypeListResponse listResponse = webClient.get()
                .uri("/type?limit=18")
                .retrieve()
                .bodyToMono(TypeListResponse.class)
                .block();

        if (listResponse == null || listResponse.getResults() == null) {
            System.err.println("Erro: não foi possível buscar lista de tipos");
            return;
        }

        System.out.println("Encontrados " + listResponse.getResults().size() + " tipos");
        
        int count = 0;
        for (TypeListResponse.TypeBasic typeBasic : listResponse.getResults()) {
            try {
                count++;
                // Capitalizar nome do tipo para padronização
                String nomeTipo = capitalizeFirst(typeBasic.getName());
                System.out.println("[" + count + "/" + listResponse.getResults().size() + "] Processando: " + nomeTipo);
                
                if (tipoService.buscarPorNome(nomeTipo).isPresent()) {
                    System.out.println("  ⊙ Tipo já existe, pulando...");
                } else {
                    Tipo tipo = new Tipo(nomeTipo);
                    tipoService.salvar(tipo);
                    System.out.println("  ✓ Salvo com sucesso!");
                }
                
            } catch (Exception e) {
                System.err.println("  ✗ Erro ao processar tipo: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("=== Seed de Tipos finalizado! ===");
        System.out.println("Total processado: " + count + " tipos");
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
