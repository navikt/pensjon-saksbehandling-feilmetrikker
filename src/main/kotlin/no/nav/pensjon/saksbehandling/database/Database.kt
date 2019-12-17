package no.nav.pensjon.saksbehandling.database

import java.sql.Connection
import java.sql.SQLException

internal class Database(private val connectionProvider: () -> Connection) {
    internal fun queryForDouble(query: String): Double = try {
        connectionProvider().use {
            it.prepareStatement(query)
                .executeQuery()
                .apply { next() }
                .getDouble(1)
        }
    } catch (e: SQLException) {
        throw CantQueryPenDatabase("Error contacting pen database", e)
    }
}