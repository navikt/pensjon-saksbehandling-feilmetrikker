package no.nav.pensjon.saksbehandling

import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.prometheus.client.Counter
import no.nav.pensjon.saksbehandling.database.DataSourceConfig.createDatasource
import no.nav.pensjon.saksbehandling.database.Database
import no.nav.pensjon.saksbehandling.nais.nais
import java.lang.System.getenv
import javax.sql.DataSource

fun main() {
    val app = App(datasource = createDatasource(getenv()))
    app.start()
}

internal class App(private val defaultPort: Int = 8080, val datasource: DataSource) {
    private val totalErrorFromPsak = Counter.build()
        .name("total_errors_from_psak")
        .help("Antall feil registrert i T_AVVIKSINFORMASJON i PSAK")
        .register()

    private fun createApplicationEnvironment() = applicationEngineEnvironment {
        connector {
            port = defaultPort
        }
        module {
            nais()
        }
    }

    internal fun start() {
        embeddedServer(Netty, createApplicationEnvironment()).let { app ->
            app.start(wait = false)
            val database = Database(datasource)
            totalErrorFromPsak.clear()
            totalErrorFromPsak.inc(database.countTechnicalErrorsFromPsak())
        }
    }
}

