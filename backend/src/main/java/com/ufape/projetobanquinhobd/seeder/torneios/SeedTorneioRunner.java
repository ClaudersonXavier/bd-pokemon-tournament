package com.ufape.projetobanquinhobd.seeder.torneios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.ufape.projetobanquinhobd")
public class SeedTorneioRunner {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SeedTorneioRunner.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = app.run(args);

        TorneioSeeder seeder = context.getBean(TorneioSeeder.class);
        seeder.seed();

        context.close();
        System.exit(0);
    }
}
