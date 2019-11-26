package no.nav.pensjon.saksbehandling

import no.nav.pensjon.saksbehandling.DatabaseTestUtils.createOracleDatasource
import no.nav.pensjon.saksbehandling.DatabaseTestUtils.populateT_AVVIKSINFORMASJON
import no.nav.pensjon.saksbehandling.DatabaseTestUtils.setupOracleContainer
import no.nav.pensjon.saksbehandling.database.Database
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.OracleContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
internal object DatabaseTest {

    private const val SUM_TECHNICAL_ERRORS_FROM_PSAK = 2;

    @Container
    var oracleContainer: OracleContainer = setupOracleContainer()

    @JvmStatic
    @BeforeAll
    fun setup() = oracleContainer.start()

    @JvmStatic
    @AfterAll
    fun tearDown() = oracleContainer.stop()

    @Test
    fun `given 2 errors registered from psak, 2 errors will be posted to metrics`() {
        val oracleDataSource = createOracleDatasource(oracleContainer)
        populateT_AVVIKSINFORMASJON(oracleDataSource)
        val database = Database(oracleDataSource)
        assertEquals(SUM_TECHNICAL_ERRORS_FROM_PSAK, database.countTechnicalErrorsFromPsak())
    }
}