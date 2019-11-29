package no.nav.pensjon.saksbehandling.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.pensjon.saksbehandling.ApplicationProperties.getFromEnvironment

internal object DataSourceConfig {

    private const val minIdle = 0
    private const val maxLifetimeMs = 30001L
    private const val maxPoolSize = 3
    private const val connectionTimeoutMs = 250L
    private const val idleTimeoutMs = 10001L

    fun createDatasource(env: Map<String, String>): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = getFromEnvironment(env, "JDBC_URL")
        config.username = getFromEnvironment(env, "username")
        config.password = getFromEnvironment(env, "password")
        config.minimumIdle = minIdle
        config.maxLifetime = maxLifetimeMs
        config.maximumPoolSize = maxPoolSize
        config.idleTimeout = idleTimeoutMs
        config.connectionTimeout = connectionTimeoutMs
        return HikariDataSource(config)
    }

}