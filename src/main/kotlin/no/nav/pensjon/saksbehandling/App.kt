package no.nav.pensjon.saksbehandling

import io.ktor.application.Application
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.pensjon.saksbehandling.nais.nais
import java.util.concurrent.TimeUnit

internal val server = embeddedServer(Netty, createApplicationEnvironment())
const val DEFAULT_PORT = 8080

fun createApplicationEnvironment(serverPort: Int = DEFAULT_PORT) = applicationEngineEnvironment {
    connector {
        port = serverPort
    }

    module {
        errorMetricApplication()
    }
}

fun Application.errorMetricApplication() = nais()

fun main() {
    server.start(wait = true)

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(1, 1, TimeUnit.SECONDS)
    })
}
