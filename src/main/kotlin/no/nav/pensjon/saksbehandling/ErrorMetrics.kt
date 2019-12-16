package no.nav.pensjon.saksbehandling

internal object ErrorMetrics {
    internal val cantQueryDbCounter = counter {
        name("sum_errors_connecting_to_db")
        help("Antall tilkoblingsfeil mot pensjonsdatabasen")
    }
}

