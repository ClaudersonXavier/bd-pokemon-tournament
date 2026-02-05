package com.ufape.projetobanquinhobd.seeder.treinadores;

import com.ufape.projetobanquinhobd.seeder.SeedConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

public class SeedTreinadorRunner {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SeedConfiguration.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        
        try (ConfigurableApplicationContext context = app.run(args)) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   SEED DE TREINADORES               ║");
            System.out.println("╚══════════════════════════════════════╝\n");
            
            TreinadorSeeder seeder = context.getBean(TreinadorSeeder.class);
            seeder.seed();
            
            System.out.println("\n✓ Seed de Treinadores concluído com sucesso!");
        } catch (Exception e) {
            System.err.println("\n✗ Erro durante o seed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
