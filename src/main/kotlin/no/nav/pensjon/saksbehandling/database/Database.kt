package no.nav.pensjon.saksbehandling.database

import java.sql.SQLException
import javax.sql.DataSource

internal class Database(private val dataSource: DataSource) {
    companion object {
        private const val QUERY = "SELECT COUNT(*) FROM PEN.T_AVVIKSINFORMASJON WHERE APPLIKASJON = 'PSAK'"
    }

    fun countTechnicalErrorsFromPsak(): Int {
        try {
            val response = dataSource.connection.prepareStatement(QUERY).executeQuery()
            response.next()
            return response.getInt(1)
        } catch (e: SQLException) {
            throw RuntimeException("Error contacting pen database", e)
        }
    }
}