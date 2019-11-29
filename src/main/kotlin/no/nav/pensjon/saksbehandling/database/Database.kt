package no.nav.pensjon.saksbehandling.database

import java.sql.SQLException
import javax.sql.DataSource

internal class Database(private val dataSource: DataSource) {
    companion object {
        private const val QUERY = "SELECT COUNT(*) FROM PEN.T_AVVIKSINFORMASJON WHERE APPLIKASJON = 'PSAK'"
    }

    fun countTechnicalErrorsFromPsak(): Double = try {
        dataSource.connection.use {
            val response = it.prepareStatement(QUERY).executeQuery()
            response.next()
            return response.getDouble(1)
        }
    } catch (e: SQLException) {
        throw CantQueryPenDatabase("Error contacting pen database", e)
    }

}