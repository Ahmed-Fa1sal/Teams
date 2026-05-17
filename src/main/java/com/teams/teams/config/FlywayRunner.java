package com.teams.teams.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class FlywayRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(FlywayRunner.class);

    private final DataSource dataSource;

    public FlywayRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting programmatic Flyway migration...");
        Flyway flyway = Flyway.configure()
                .dataSource(this.dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        org.flywaydb.core.api.output.MigrateResult result = flyway.migrate();
        log.info("Flyway migration complete, applied {} migrations.", result.migrationsExecuted);
    }
}

