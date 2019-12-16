package no.nav.pensjon.saksbehandling

import no.nav.pensjon.saksbehandling.database.Database

const val COUNT_AVVIKSTILFELLER_PSAK = """SELECT COUNT(1) FROM PEN.T_AVVIKSINFORMASJON i
                INNER JOIN PEN.T_AVVIKSGRUPPE g on g.AVVIKSINFORMASJON_ID = i.AVVIKSINFORMASJON_ID
                INNER JOIN PEN.T_AVVIKSTILFELLE t on t.AVVIKSGRUPPE_ID = g.AVVIKSGRUPPE_ID
                WHERE i.APPLIKASJON = 'PSAK'"""

internal class AvviksTilfeller(private val db:Database):Metric {
    companion object:PromethiusCounter("Sum avvikstilfeller from PSAK", "Antall feil registrert i T_AVVIKSTILFELLER i PSAK")

    override fun update() {
        set(db.queryForDouble(COUNT_AVVIKSTILFELLER_PSAK))
    }
}