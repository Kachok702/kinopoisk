package org.example.VKR.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FlywayRunner implements CommandLineRunner {
    private final Flyway flyway;

    @Autowired
    public FlywayRunner(Flyway flyway) {
        this.flyway = flyway;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Flyway migration start ===");
        System.out.println("Flyway version: " + Flyway.class.getPackage().getImplementationVersion());

        // Применяем миграции
        MigrateResult migrationsApplied = flyway.migrate();
        System.out.println("Applied " + migrationsApplied.migrationsExecuted + " migrations");

        // Выводим список применённых миграций
        System.out.println("=== Applied migrations ===");
        for (MigrationInfo info : flyway.info().applied()) {
            System.out.println(info.getVersion() + " - " + info.getDescription());
        }
    }
}
