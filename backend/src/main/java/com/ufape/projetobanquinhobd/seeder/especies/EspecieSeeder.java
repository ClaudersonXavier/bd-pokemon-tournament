package com.ufape.projetobanquinhobd.seeder.especies;

import com.ufape.projetobanquinhobd.entities.Especie;
import com.ufape.projetobanquinhobd.entities.Tipo;
import com.ufape.projetobanquinhobd.seeder.dtos.PokemonDetailResponse;
import com.ufape.projetobanquinhobd.seeder.dtos.PokemonListResponse;
import com.ufape.projetobanquinhobd.services.EspecieService;
import com.ufape.projetobanquinhobd.services.TipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Component
public class EspecieSeeder {
    
    @Autowired
    private WebClient webClient;
    
    @Autowired
    private EspecieService especieService;
    
    @Autowired
    private TipoService tipoService;

    public void seed(int limit) {
        System.out.println("=== Iniciando seed de Espécies ===");
        System.out.println("Buscando lista de " + limit + " pokémons...");
        
        PokemonListResponse listResponse = webClient.get()
                .uri("/pokemon?limit=" + limit)
                .retrieve()
                .bodyToMono(PokemonListResponse.class)
                .block();

        if (listResponse == null || listResponse.getResults() == null) {
            System.err.println("Erro: não foi possível buscar lista de pokémons");
            return;
        }

        System.out.println("Encontrados " + listResponse.getResults().size() + " pokémons");
        
        int count = 0;
        for (PokemonListResponse.PokemonBasic pokemon : listResponse.getResults()) {
            try {
                count++;
                System.out.println("[" + count + "/" + listResponse.getResults().size() + "] Processando: " + pokemon.getName());
                
                PokemonDetailResponse details = webClient.get()
                        .uri("/pokemon/" + pokemon.getName())
                        .retrieve()
                        .bodyToMono(PokemonDetailResponse.class)
                        .block();

                if (details != null) {
                    try {
                        Especie especie = createEspecieFromDetails(details);
                        especieService.salvar(especie);
                        System.out.println("  ✓ Salvo com sucesso!");
                    } catch (Exception ex) {
                        System.err.println("  ✗ Erro ao criar/salvar espécie: " + ex.getClass().getName() + " - " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    System.err.println("  ✗ Erro ao buscar detalhes");
                }
                
                Thread.sleep(100);
                
            } catch (Exception e) {
                System.err.println("  ✗ Erro ao processar " + pokemon.getName() + ": " + e.getClass().getName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("=== Seed de Espécies finalizado! ===");
        System.out.println("Total processado: " + count + " espécies");
    }

    private Especie createEspecieFromDetails(PokemonDetailResponse details) {
        String nome = details.getName();
        System.out.println("    - Extraindo imagem...");
        String imagemUrl = extractImageFromGen5(details);
        System.out.println("    - Imagem: " + imagemUrl);
        System.out.println("    - Extraindo tipos...");
        Set<Tipo> tipos = extractTypesFromGen6(details);
        System.out.println("    - Criando espécie...");
        
        return new Especie(nome, imagemUrl, tipos);
    }

    private String extractImageFromGen5(PokemonDetailResponse details) {
        try {
            Map<String, Object> versions = details.getSprites().getVersions();
            if (versions != null && versions.containsKey("generation-v")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> gen5 = (Map<String, Object>) versions.get("generation-v");
                
                if (gen5.containsKey("black-white")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> blackWhite = (Map<String, Object>) gen5.get("black-white");
                    
                    if (blackWhite.containsKey("front_default")) {
                        String url = (String) blackWhite.get("front_default");
                        if (url != null && !url.isEmpty()) {
                            return url;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("    Aviso: não foi possível extrair imagem da gen 5, usando padrão - " + e.getMessage());
        }
        
        String fallbackUrl = details.getSprites().getFrontDefault();
        if (fallbackUrl == null || fallbackUrl.isEmpty()) {
            throw new IllegalArgumentException("URL da imagem não pode ser nula ou vazia");
        }
        return fallbackUrl;
    }

    private Set<Tipo> extractTypesFromGen6(PokemonDetailResponse details) {
        Set<Tipo> tipos = new HashSet<>();
        
        List<PokemonDetailResponse.TypeSlot> typeSlots = null;
        
        if (details.getPastTypes() != null && !details.getPastTypes().isEmpty()) {
            for (PokemonDetailResponse.PastType pastType : details.getPastTypes()) {
                if (pastType.getGeneration() != null && 
                    "generation-vi".equals(pastType.getGeneration().getName())) {
                    typeSlots = pastType.getTypes();
                    System.out.println("      Usando tipos da geração 6");
                    break;
                }
            }
        }
        
        if (typeSlots == null) {
            typeSlots = details.getTypes();
            System.out.println("      Usando tipos atuais");
        }
        
        if (typeSlots == null || typeSlots.isEmpty()) {
            throw new IllegalArgumentException("Pokémon não possui tipos disponíveis");
        }
        
        for (PokemonDetailResponse.TypeSlot typeSlot : typeSlots) {
            // Capitalizar nome do tipo para padronização
            String nomeTipo = capitalizeFirst(typeSlot.getType().getName());
            
            Tipo tipo = tipoService.buscarPorNome(nomeTipo)
                    .orElseGet(() -> {
                        Tipo novoTipo = new Tipo(nomeTipo);
                        return tipoService.salvar(novoTipo);
                    });
            
            tipos.add(tipo);
        }
        
        if (tipos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum tipo válido foi extraído");
        }
        
        return tipos;
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
