package no.nav.pensjon.saksbehandling

import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.pensjon.saksbehandling.counters.AvviksInformasjon
import no.nav.pensjon.saksbehandling.counters.AvviksTilfeller
import no.nav.pensjon.saksbehandling.database.DataSourceConfig.createDatasource
import no.nav.pensjon.saksbehandling.database.Database
import no.nav.pensjon.saksbehandling.database.CantQueryPenDatabase
import no.nav.pensjon.saksbehandling.gauge.EndringFraForrigeUke
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

internal class App(serverPort: Int = 8080, datasource: DataSource) {
    private val log: Logger = LoggerFactory.getLogger(App::class.java)
    private val database: Database = Database(datasource::getConnection)
    private val oneMinute = 60000L
    private val metrics =
        listOf(AvviksInformasjon(database), AvviksTilfeller(database), EndringFraForrigeUke(database))

    init {
        val server = embeddedServer(Netty, createApplicationEnvironment(serverPort))
        server.start(wait = false)
    }

    private fun createApplicationEnvironment(serverPort: Int) = applicationEngineEnvironment {
        connector { port = serverPort }
        module { nais() }
    }

    internal fun start(queryFrequency: Long = oneMinute, loopForever: Boolean = true) {
        do try {
            sleep(queryFrequency)
            metrics.forEach { it.update() }
        } catch (e: CantQueryPenDatabase) {
            log.error("Cant connect to db.", e)
            ErrorMetrics.cantQueryDbCounter.inc()
        } while (loopForever)
    }

}

interface Metric {
    fun update()
}
