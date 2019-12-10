package no.nav.pensjon.saksbehandling

import no.nav.pensjon.saksbehandling.database.DatabaseTestUtils.createOracleDatasource
import no.nav.pensjon.saksbehandling.database.DatabaseTestUtils.setupOracleContainer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
    private const val DEFAULT_PORT = 8080
    private const val HTTP_OK = 200
    private const val QUERY_FREQUENCY = 0L
    private const val EXPECTED_METRICS_TEXT = "total_errors_from_psak 2.0"
    private val client = HttpClient.newHttpClient()

    @Container
    private val oracleContainer = setupOracleContainer()
    private lateinit var app: App
    private lateinit var datasource: DataSource

    @JvmStatic
    @BeforeAll
    internal fun setUp() {
        oracleContainer.start()
        datasource = createOracleDatasource(oracleContainer)
        app = App(DEFAULT_PORT, datasource)
        app.start(QUERY_FREQUENCY, loopForever = false)
    }

    @JvmStatic
    @AfterAll
    internal fun tearDown() = oracleContainer.stop()

    @Test
    internal fun `app gets error count from database and publishes it to error count metric`() =
        assertTrue(sendToEndpoint(METRICS).body().toString().contains(EXPECTED_METRICS_TEXT))

    @Test
    internal fun `isAlive returns 200 OK when server is running`() = testEndpoint(IS_ALIVE)

    @Test
    internal fun `isReady returns 200 OK when server is running`() = testEndpoint(IS_READY)

    @Test
    internal fun `metrics returns 200 OK when server is running`() = testEndpoint(METRICS)

    private fun testEndpoint(endpoint: String) = assertStatusCodeOK(sendToEndpoint(endpoint))
    private fun assertStatusCodeOK(response: HttpResponse<String>) =
        assertEquals(HTTP_OK, response.statusCode())

    private fun sendToEndpoint(endpoint: String) =
        client.send(createRequest(endpoint), HttpResponse.BodyHandlers.ofString())

    private fun createRequest(endpoint: String) =
        HttpRequest.newBuilder(URI("$LOCALHOST$DEFAULT_PORT/$endpoint")).build()

}
