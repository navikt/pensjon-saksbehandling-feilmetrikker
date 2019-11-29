package no.nav.pensjon.saksbehandling

import no.nav.pensjon.saksbehandling.DatabaseTestUtils.createOracleDatasource
import no.nav.pensjon.saksbehandling.DatabaseTestUtils.populateT_AVVIKSINFORMASJON
import no.nav.pensjon.saksbehandling.DatabaseTestUtils.setupOracleContainer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.sql.DataSource

@Testcontainers
internal object ComponentTest {
    private const val LOCALHOST = "http://localhost:"
    private const val IS_ALIVE = "isAlive"
    private const val IS_READY = "isReady"
    private const val METRICS = "metrics"
    private const val HTTP_OK = 200

    private const val expectedMetricsText = """# HELP total_errors_from_psak Antall feil registrert i T_AVVIKSINFORMASJON i PSAK
# TYPE total_errors_from_psak counter
total_errors_from_psak 2.0
"""
    private val client = HttpClient.newHttpClient()

    @Container
    private val oracleContainer = setupOracleContainer()
    private lateinit var app: App
    private lateinit var datasource: DataSource

    @JvmStatic
    @BeforeAll
    internal fun startServer() {
        oracleContainer.start()
        datasource = createOracleDatasource(oracleContainer)
        app = App(datasource)
        populateT_AVVIKSINFORMASJON(datasource)
        app.start()
    }

    @JvmStatic
    @AfterAll
    internal fun stopServer() {
        oracleContainer.stop()
    }

    @Test
    internal fun `app gets error count from database and publishes it to error count metric`() {
        assertEquals(expectedMetricsText, sendToEndpoint(METRICS).body().toString())
    }

    @Test
    fun `isAlive returns 200 OK when server is running`() = testEndpoint(IS_ALIVE)

    @Test
    fun `isReady returns 200 OK when server is running`() = testEndpoint(IS_READY)

    @Test
    fun `metrics returns 200 OK when server is running`() = testEndpoint(METRICS)

    private fun testEndpoint(endpoint: String) = assertStatusCodeOK(sendToEndpoint(endpoint))
    private fun assertStatusCodeOK(response: HttpResponse<String>) =
        assertEquals(HTTP_OK, response.statusCode())

    private fun sendToEndpoint(endpoint: String) =
        client.send(createRequest(endpoint), HttpResponse.BodyHandlers.ofString())

    private fun createRequest(endpoint: String) =
        HttpRequest.newBuilder(URI("$LOCALHOST$DEFAULT_PORT/$endpoint")).build()

}
