package com.ufape.projetobanquinhobd.seeder.batalhas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.ufape.projetobanquinhobd")
public class SeedBatalhaRunner {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SeedBatalhaRunner.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        ConfigurableApplicationContext context = app.run(args);

        BatalhaSeeder seeder = context.getBean(BatalhaSeeder.class);
        seeder.seed();

        context.close();
        System.exit(0);
    }
}
