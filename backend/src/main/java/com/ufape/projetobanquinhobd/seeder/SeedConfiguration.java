package com.ufape.projetobanquinhobd.seeder;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.ufape.projetobanquinhobd",
    "com.ufape.projetobanquinhobd.seeder.especies",
    "com.ufape.projetobanquinhobd.seeder.tipos"
})
@EntityScan(basePackages = "com.ufape.projetobanquinhobd.entities")
@EnableJpaRepositories(basePackages = "com.ufape.projetobanquinhobd.repositories")
public class SeedConfiguration {
    // Configuração compartilhada para todos os seeders
}
