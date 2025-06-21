package org.esc.serverportsmanager.config

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayConfig {

    @Bean
    fun flyway(dataSource: DataSource): Flyway {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .load()

        flyway.migrate()

        println("=== Applied migrations ===")
        for (info: MigrationInfo in flyway.info().applied()) {
            println("${info.version} - ${info.description}")
        }

        return flyway
    }
}