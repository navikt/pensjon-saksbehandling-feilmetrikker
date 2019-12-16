package no.nav.pensjon.saksbehandling

import no.nav.pensjon.saksbehandling.database.Database

const val COUNT_AVVIKSINFORMASJON_PSAK = "SELECT COUNT(1) FROM PEN.T_AVVIKSINFORMASJON WHERE APPLIKASJON = 'PSAK'"

internal class AvviksInformasjon(private val db:Database):Metric {
    companion object:PromethiusCounter("Sum avviksinformasjon from PSAK", "Antall feil registrert i T_AVVIKSINFORMASJON i PSAK")

    override fun update() {
        set(db.queryForDouble(COUNT_AVVIKSINFORMASJON_PSAK))
    }
}
