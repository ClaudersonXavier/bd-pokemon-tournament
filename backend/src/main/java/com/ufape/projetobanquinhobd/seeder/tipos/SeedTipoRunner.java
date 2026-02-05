package com.ufape.projetobanquinhobd.seeder.tipos;

import com.ufape.projetobanquinhobd.seeder.SeedConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

public class SeedTipoRunner {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SeedConfiguration.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        
        try (ConfigurableApplicationContext context = app.run(args)) {
            TipoSeeder seeder = context.getBean(TipoSeeder.class);
            
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   SEED DE TIPOS - PokeAPI            ║");
            System.out.println("╚══════════════════════════════════════╝\n");
            
            seeder.seed();
            
            System.out.println("\n✓ Seed de Tipos concluído com sucesso!");
        } catch (Exception e) {
            System.err.println("\n✗ Erro durante o seed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
