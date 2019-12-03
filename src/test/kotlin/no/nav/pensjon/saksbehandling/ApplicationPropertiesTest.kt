package no.nav.pensjon.saksbehandling

import no.nav.pensjon.saksbehandling.ApplicationProperties.getFromEnvironment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*

internal object ApplicationPropertiesTest {
    private const val JDBC_URL = "jdbc_url"
    private const val JDBC_URL_KEY = "DB_URL"
    private val environment = HashMap<String, String>()

    @Test
    internal fun `get jdbc url from environment returns the url`() {
        environment[JDBC_URL_KEY] = JDBC_URL
        assertEquals(JDBC_URL, getFromEnvironment(environment, JDBC_URL_KEY))
    }

    @Test
    internal fun `MissingApplicationConfig is thrown when environment variable is missing`() {
        assertThrows(MissingApplicationConfig::class.java) { getFromEnvironment(environment, "invalid") }
    }

}