package no.nav.pensjon.saksbehandling

import no.nav.pensjon.saksbehandling.counters.counter

internal object ErrorMetrics {
    internal val cantQueryDbCounter = counter {
        name("sum_errors_connecting_to_db")
        help("Antall tilkoblingsfeil mot pensjonsdatabasen")
    }
}

