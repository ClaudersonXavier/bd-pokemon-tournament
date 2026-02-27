package com.ufape.projetobanquinhobd.seeder.ataques;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import com.ufape.projetobanquinhobd.seeder.SeedConfiguration;

public class SeedAtaqueRunner {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SeedConfiguration.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        
        try (ConfigurableApplicationContext context = app.run(args)) {
            AtaqueSeeder seeder = context.getBean(AtaqueSeeder.class);
            
            int limit = 151;
            
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   SEED DE ATAQUES - PokeAPI         ║");
            System.out.println("╚══════════════════════════════════════╝\n");
            
            seeder.seed(limit);
            
            System.out.println("\n✓ Seed de Ataques concluído com sucesso!");
        } catch (Exception e) {
            System.err.println("\n✗ Erro durante o seed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}