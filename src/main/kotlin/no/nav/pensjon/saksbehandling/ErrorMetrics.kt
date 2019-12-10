package no.nav.pensjon.saksbehandling

import io.prometheus.client.Counter
import no.nav.pensjon.saksbehandling.database.Database
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class ErrorMetrics {
    private val log: Logger = LoggerFactory.getLogger(ErrorMetrics::class.java)

    internal val cantQueryDbCounter = Counter.build()
        .name("sum_errors_connecting_to_db")
        .help("Antall tilkoblingsfeil mot pensjonsdatabasen").register()

    private val avviksinformasjonFromPsakCounter = Counter.build()
        .name("sum_avviksinformasjon_from_psak")
        .help("Antall feil registrert i T_AVVIKSINFORMASJON i PSAK").register()

    private val avvikstilfellerFromPsakCounter = Counter.build()
        .name("sum_avvikstilfeller_from_psak")
        .help("Antall feil registrert i T_AVVIKSINFORMASJON i PSAK").register()

    internal fun query(database: Database) {
        val sumAvviksinformasjonFromPsak = database.countAvviksinformasjonFromPsak()
        val sumAvvikstilfellerFromPsak = database.countAvviksinformasjonFromPsak()
        log.info("Sum avviksinformasjon from PSAK: $sumAvviksinformasjonFromPsak")
        log.info("Sum avvikstilfeller from PSAK: $sumAvviksinformasjonFromPsak")
        avviksinformasjonFromPsakCounter.clear()
        avvikstilfellerFromPsakCounter.clear()
        avviksinformasjonFromPsakCounter.inc(sumAvviksinformasjonFromPsak)
        avvikstilfellerFromPsakCounter.inc(sumAvvikstilfellerFromPsak)
    }
}