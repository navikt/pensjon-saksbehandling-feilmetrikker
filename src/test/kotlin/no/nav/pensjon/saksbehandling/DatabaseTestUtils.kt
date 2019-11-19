package no.nav.pensjon.saksbehandling

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.testcontainers.containers.OracleContainer
import javax.sql.DataSource

internal object DatabaseTestUtils {
    fun setupOracleContainer(): OracleContainer {
        return OracleContainer("oracleinanutshell/oracle-xe-11g")
    }

    fun createOracleDatasource(oracleContainer: OracleContainer): HikariDataSource {
        return with(HikariConfig()) {
            maxLifetime = 100000
            connectionTimeout = 25000
            jdbcUrl = oracleContainer.jdbcUrl
            username = oracleContainer.username
            password = oracleContainer.password
            HikariDataSource(this)
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