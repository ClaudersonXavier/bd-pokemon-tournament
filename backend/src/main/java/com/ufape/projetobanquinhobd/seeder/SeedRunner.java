package com.ufape.projetobanquinhobd.seeder;

import com.ufape.projetobanquinhobd.seeder.especies.EspecieSeeder;
import com.ufape.projetobanquinhobd.seeder.tipos.TipoSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

public class SeedRunner {
    
    public static void main(String[] args) {
        // Configurar para não iniciar servidor web
        SpringApplication app = new SpringApplication(SeedConfiguration.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        
        try (ConfigurableApplicationContext context = app.run(args)) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   SEED GERAL - BANCO DE DADOS        ║");
            System.out.println("╚══════════════════════════════════════╝\n");
            
            // 1. Seed de Tipos (executar primeiro pois Espécie depende de Tipo)
            System.out.println(">>> 1. Executando seed de Tipos...\n");
            TipoSeeder tipoSeeder = context.getBean(TipoSeeder.class);
            tipoSeeder.seed();
            
            System.out.println("\n");
            
            // 2. Seed de Espécies (Pokémons da PokeAPI)
            System.out.println(">>> 2. Executando seed de Espécies...\n");
            EspecieSeeder especieSeeder = context.getBean(EspecieSeeder.class);
            especieSeeder.seed(50); // Quantidade de espécies
            
            // TODO: Adicionar outros seeders aqui conforme forem criados
            // 3. Seed de Ataques
            // AtaqueSeeder ataqueSeeder = context.getBean(AtaqueSeeder.class);
            // ataqueSeeder.seed();
            
            // 4. Seed de Treinadores
            // TreinadorSeeder treinadorSeeder = context.getBean(TreinadorSeeder.class);
            // treinadorSeeder.seed();
            
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   ✓ SEED GERAL CONCLUÍDO!            ║");
            System.out.println("╚══════════════════════════════════════╝\n");
            
        } catch (Exception e) {
            System.err.println("\n✗ Erro durante o seed geral: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
