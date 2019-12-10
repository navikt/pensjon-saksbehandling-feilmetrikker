package no.nav.pensjon.saksbehandling.database

import java.sql.SQLException
import javax.sql.DataSource

internal class Database(private val dataSource: DataSource) {
    private companion object {
        private const val COUNT_AVVIKSINFORMASJON_PSAK = """SELECT COUNT(1) FROM PEN.T_AVVIKSINFORMASJON WHERE APPLIKASJON = 'PSAK'"""
        private const val COUNT_AVVIKSTILFELLER_PSAK = """ SELECT COUNT(1) FROM PEN.T_AVVIKSINFORMASJON i
                INNER JOIN PEN.T_AVVIKSGRUPPE g on g.AVVIKSINFORMASJON_ID = i.AVVIKSINFORMASJON_ID
                INNER JOIN PEN.T_AVVIKSTILFELLE t on t.AVVIKSGRUPPE_ID = g.AVVIKSGRUPPE_ID
                WHERE i.APPLIKASJON = 'PSAK'"""
    }

    internal fun countAvviksinformasjonFromPsak() = countTechnicalErrorsFromPsak(COUNT_AVVIKSINFORMASJON_PSAK)

    internal fun countAvvikstilfellerFromPsak() = countTechnicalErrorsFromPsak(COUNT_AVVIKSTILFELLER_PSAK)

    private fun countTechnicalErrorsFromPsak(query: String): Double = try {
        dataSource.connection.use {
            val response = it.prepareStatement(query).executeQuery()
            response.next()
            return response.getDouble(1)
        }
    } catch (e: SQLException) {
        throw CantQueryPenDatabase("Error contacting pen database", e)
    }
}