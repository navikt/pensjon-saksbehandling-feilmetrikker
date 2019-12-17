package no.nav.pensjon.saksbehandling.gauge

import no.nav.pensjon.saksbehandling.Metric
import no.nav.pensjon.saksbehandling.database.Database

val ONE_DAY_AGO = "select count(1) FROM PEN.T_AVVIKSINFORMASJON i\n" +
        "INNER JOIN PEN.T_AVVIKSGRUPPE g on g.AVVIKSINFORMASJON_ID = i.AVVIKSINFORMASJON_ID\n" +
        "INNER JOIN PEN.T_AVVIKSTILFELLE t on t.AVVIKSGRUPPE_ID = g.AVVIKSGRUPPE_ID\n" +
        "WHERE i.APPLIKASJON = 'PSAK'\n" +
        "and t.TIDSPUNKT between (current_timestamp - interval '1' day) and current_timestamp;"


val ONE_WEEK_AGO = "select count(1) FROM PEN.T_AVVIKSINFORMASJON i\n" +
        "INNER JOIN PEN.T_AVVIKSGRUPPE g on g.AVVIKSINFORMASJON_ID = i.AVVIKSINFORMASJON_ID\n" +
        "INNER JOIN PEN.T_AVVIKSTILFELLE t on t.AVVIKSGRUPPE_ID = g.AVVIKSGRUPPE_ID\n" +
        "WHERE i.APPLIKASJON = 'PSAK'\n" +
        "and t.TIDSPUNKT between (current_timestamp - interval '8' day) and (current_timestamp - interval '7' day);"

internal class EndringFraForrigeUke(private val db: Database) : Metric {

    companion object :
        PrometheusGauge("Endringer i antall feil fra forrige uke til i dag", "Antall feil registrert i T_AVVIKSINFORMASJON i PSAK")

    override fun update() {
        val queryForOneDayAgo = db.queryForDouble(ONE_DAY_AGO)
        val queryForOneWeekAgo = db.queryForDouble(ONE_WEEK_AGO)
        setDouble(queryForOneDayAgo / queryForOneWeekAgo)
    }

}