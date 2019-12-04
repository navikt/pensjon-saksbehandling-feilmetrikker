package no.nav.pensjon.saksbehandling

import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.prometheus.client.Counter
import no.nav.pensjon.saksbehandling.database.DataSourceConfig
import no.nav.pensjon.saksbehandling.database.DataSourceConfig.createDatasource
import no.nav.pensjon.saksbehandling.database.Database
import no.nav.pensjon.saksbehandling.nais.nais
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.System.getenv
import java.lang.Thread.sleep
import javax.sql.DataSource

fun main() {
    val app = App(datasource = createDatasource(getenv()))
    app.start()
}

internal class App(private val serverPort: Int = 8080, val datasource: DataSource) {
    private val LOG: Logger = LoggerFactory.getLogger(App::class.java)
    private val database: Database = Database(datasource)
    private val server = embeddedServer(Netty, createApplicationEnvironment())
    private val tenMinutes = 600000L

    private val totalErrorFromPsak = Counter.build()
        .name("total_errors_from_psak")
        .help("Antall feil registrert i T_AVVIKSINFORMASJON i PSAK")
        .register()

    private fun createApplicationEnvironment() = applicationEngineEnvironment {
        connector { port = serverPort }
        module { nais() }
    }

    internal fun start(queryFrequency: Long = tenMinutes, loopForever: Boolean = true) {
        server.let { app ->
            app.start(wait = false)
            do {
                val sumErrorsFromPsak = database.countTechnicalErrorsFromPsak()
                LOG.info("Sum technical errors from PSAK: $sumErrorsFromPsak")
                totalErrorFromPsak.clear()
                totalErrorFromPsak.inc(sumErrorsFromPsak)
                sleep(queryFrequency)
            } while (loopForever)
        }
    }
}

