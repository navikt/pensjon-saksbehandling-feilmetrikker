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

    private val totalErrorFromPsak = Counter.build()
        .name("total_errors_from_psak")
        .help("Antall feil registrert i T_AVVIKSINFORMASJON i PSAK").register()

    internal fun query(database: Database) {
        val sumErrorsFromPsak = database.countTechnicalErrorsFromPsak()
        log.info("Sum technical errors from PSAK: $sumErrorsFromPsak")
        totalErrorFromPsak.clear()
        totalErrorFromPsak.inc(sumErrorsFromPsak)
    }
}