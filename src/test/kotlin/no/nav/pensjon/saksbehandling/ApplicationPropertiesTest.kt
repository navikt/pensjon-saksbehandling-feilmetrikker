package no.nav.pensjon.saksbehandling

import no.nav.pensjon.saksbehandling.ApplicationProperties.getFromEnvironment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*

object ApplicationPropertiesTest {

    private const val JDBC_URL = "jdbc_url"
    private const val JDBC_URL_KEY = "DB_URL"
    private val environment = HashMap<String, String>()

    @Test
    fun `get jdbc url from environment returns the url`() {
        environment[JDBC_URL_KEY] = JDBC_URL
        val jdbcUrl = getFromEnvironment(environment, JDBC_URL_KEY)
        assertEquals(JDBC_URL, jdbcUrl)
    }

    @Test
    fun getDbPropertyFromEnvironment_throws_MissingDatabaseConfig_when_property_has_no_value_in_environment() {
        @Suppress("UNCHECKED_CAST")
        environment[JDBC_URL_KEY] = ({ null } as () -> String)()
        assertThrows(MissingApplicationConfig::class.java) { getFromEnvironment(environment, JDBC_URL_KEY) }
    }

    @Test
    fun getDbPropertyFromEnvironment_throws_MissingDatabaseConfig_when_property_is_not_in_environment() {
        assertThrows(MissingApplicationConfig::class.java) { getFromEnvironment(environment, "invalid") }
    }

}