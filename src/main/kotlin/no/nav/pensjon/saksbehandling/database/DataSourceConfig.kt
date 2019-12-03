package no.nav.pensjon.saksbehandling.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.pensjon.saksbehandling.ApplicationProperties.getFromEnvironment
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal object DataSourceConfig {

    private val LOG: Logger = LoggerFactory.getLogger(DataSourceConfig::class.java)

    private const val minIdle = 0
    private const val maxLifetimeMs = 30001L
    private const val maxPoolSize = 3
    private const val connectionTimeoutMs = 250L
    private const val idleTimeoutMs = 10001L

    fun createDatasource(env: Map<String, String>): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = getFromEnvironment(env, "JDBC_URL")
        config.username = getFromEnvironment(env, "USERNAME")
        config.password = getFromEnvironment(env, "PASSWORD")
        config.minimumIdle = minIdle
        config.maxLifetime = maxLifetimeMs
        config.maximumPoolSize = maxPoolSize
        config.idleTimeout = idleTimeoutMs
        config.connectionTimeout = connectionTimeoutMs
        LOG.info("Datasource created with JDBC_URL: ${config.jdbcUrl} and USERNAME: ${config.username}")
        return HikariDataSource(config)
    }
}