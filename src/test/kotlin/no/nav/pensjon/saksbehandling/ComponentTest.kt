package no.nav.pensjon.saksbehandling

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.TimeUnit.SECONDS

internal object ComponentTest {
    private const val LOCALHOST = "http://localhost:"
    private const val IS_ALIVE = "isAlive"
    private const val IS_READY = "isReady"
    private const val METRICS = "metrics"
    private const val HTTP_OK = 200

    private val client = HttpClient.newHttpClient()

    @JvmStatic
    @BeforeAll
    fun startServer() = server.start(wait = false).let { println("Server started.") }

    @JvmStatic
    @AfterAll
    fun stopServer() = server.stop(0, 0, SECONDS)

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
