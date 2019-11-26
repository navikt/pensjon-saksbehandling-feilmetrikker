package no.nav.pensjon.saksbehandling

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.testcontainers.containers.OracleContainer
import java.sql.SQLException
import javax.sql.DataSource

internal object DatabaseTestUtils {

    private val log = LoggerFactory.getLogger(DatabaseTestUtils::class.java)

    fun setupOracleContainer() = OracleContainer("oracleinanutshell/oracle-xe-11g")
    fun createOracleDatasource(oracleContainer: OracleContainer): HikariDataSource {
        try {
            return with(HikariConfig()) {
                maxLifetime = 30001L
                connectionTimeout = 2500L
                jdbcUrl = oracleContainer.jdbcUrl
                username = oracleContainer.username
                password = oracleContainer.password
                HikariDataSource(this)
            }
        } catch (e: SQLException) {
            log.error(e.message, e)
            throw e
        }
    }

    fun populateT_AVVIKSINFORMASJON(dataSource: DataSource) {
        setDatabaseUserForSession(dataSource)
        createTableT_AVVIKSINFORMASJON(dataSource)
        insertDataInT_AVVIKSINFORMASJON(dataSource)
    }

    private fun setDatabaseUserForSession(dataSource: DataSource) {
        dataSource.connection.createStatement().execute("CREATE USER PEN IDENTIFIED BY opensourcedPassword")
        dataSource.connection.createStatement().execute("GRANT UNLIMITED TABLESPACE TO PEN")
        dataSource.connection.createStatement().execute("ALTER SESSION SET CURRENT_SCHEMA = PEN")
    }

    private fun createTableT_AVVIKSINFORMASJON(dataSource: DataSource) {
        dataSource.connection.createStatement()
            .executeQuery("CREATE TABLE PEN.T_AVVIKSINFORMASJON (AVVIKSINFORMASJON_ID NUMBER, APPLIKASJON VARCHAR2(50 CHAR))");

    }

    private fun insertDataInT_AVVIKSINFORMASJON(dataSource: DataSource) {
        dataSource.connection.createStatement()
            .executeQuery("INSERT INTO PEN.T_AVVIKSINFORMASJON (AVVIKSINFORMASJON_ID, APPLIKASJON) VALUES(1, 'PSAK')");
        dataSource.connection.createStatement()
            .executeQuery("INSERT INTO PEN.T_AVVIKSINFORMASJON (AVVIKSINFORMASJON_ID, APPLIKASJON) VALUES(2, 'PSAK')");
        dataSource.connection.createStatement()
            .executeQuery("INSERT INTO PEN.T_AVVIKSINFORMASJON (AVVIKSINFORMASJON_ID, APPLIKASJON) VALUES(2, 'PSELV')");
    }

}