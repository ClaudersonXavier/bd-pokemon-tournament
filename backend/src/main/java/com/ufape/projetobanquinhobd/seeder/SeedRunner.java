package com.ufape.projetobanquinhobd.seeder;

import com.ufape.projetobanquinhobd.seeder.especies.EspecieSeeder;
import com.ufape.projetobanquinhobd.seeder.tipos.TipoSeeder;
import com.ufape.projetobanquinhobd.seeder.ataques.AtaqueSeeder;
import com.ufape.projetobanquinhobd.seeder.treinadores.TreinadorSeeder;
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
            
            System.out.println("\n");
            
            // 3. Seed de Ataques
            System.out.println(">>> 3. Executando seed de Ataques...\n");
            AtaqueSeeder ataqueSeeder = context.getBean(AtaqueSeeder.class);
            ataqueSeeder.seed(50); // Quantidade de ataques
            
            System.out.println("\n");
            
            // 4. Seed de Treinadores
            System.out.println(">>> 4. Executando seed de Treinadores...\n");
            TreinadorSeeder treinadorSeeder = context.getBean(TreinadorSeeder.class);
            treinadorSeeder.seed();
            
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   ✓ SEED GERAL CONCLUÍDO!            ║");
            System.out.println("╚══════════════════════════════════════╝\n");
            
        } catch (Exception e) {
            System.err.println("\n✗ Erro durante o seed geral: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
