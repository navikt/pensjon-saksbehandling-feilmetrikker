package no.nav.pensjon.saksbehandling.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.testcontainers.containers.OracleContainer
import org.testcontainers.utility.MountableFile

internal object DatabaseTestUtils {
    private const val INIT_DB_SCRIPT_FOLDER = "/docker-entrypoint-initdb.d/schema.sql"
    private const val INIT_SCRIPT = "schema.sql"

    internal fun setupOracleContainer(): OracleContainer {
        return OracleContainer("oracleinanutshell/oracle-xe-11g")
            .withCopyFileToContainer(MountableFile.forClasspathResource(INIT_SCRIPT), INIT_DB_SCRIPT_FOLDER)
    }

    internal fun createOracleDatasource(oracleContainer: OracleContainer) = HikariDataSource(
        HikariConfig().apply {
            addDataSourceProperty("oracle.jdbc.timezoneAsRegion", "false")
            maxLifetime = 30001L
            connectionTimeout = 2500L
            jdbcUrl = oracleContainer.jdbcUrl
            username = oracleContainer.username
            password = oracleContainer.password
        })
}