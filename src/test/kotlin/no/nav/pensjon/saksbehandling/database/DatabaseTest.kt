package no.nav.pensjon.saksbehandling.database

import no.nav.pensjon.saksbehandling.database.DatabaseTestUtils.createOracleDatasource
import no.nav.pensjon.saksbehandling.database.DatabaseTestUtils.populateT_AVVIKSINFORMASJON
import no.nav.pensjon.saksbehandling.database.DatabaseTestUtils.setupOracleContainer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.testcontainers.containers.OracleContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import javax.sql.DataSource

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal object DatabaseTest {

    @Container
    private var oracleContainer: OracleContainer = setupOracleContainer()
    private const val SUM_TECHNICAL_ERRORS_FROM_PSAK = 2.00
    private lateinit var oracleDataSource: DataSource
    private lateinit var database: Database

    @JvmStatic
    @BeforeAll
    internal fun setup() {
        oracleContainer.start()
        oracleDataSource = createOracleDatasource(oracleContainer)
        database = Database(oracleDataSource)
    }

    @Test
    @Order(1)
    internal fun `given 2 errors from psak in T_AVVIKSINFORMASJON, 2 errors will be returned from countTechnicalErrorsFromPsak`() {
        populateT_AVVIKSINFORMASJON(oracleDataSource)
        assertEquals(SUM_TECHNICAL_ERRORS_FROM_PSAK, database.countTechnicalErrorsFromPsak())
    }

    @Test
    @Order(2)
    internal fun `when database is unavailable, throw CantQueryPenDatabase`() {
        breakDatabaseConnection()
        assertThrows<CantQueryPenDatabase> { database.countTechnicalErrorsFromPsak() }
    }

    private fun breakDatabaseConnection() {
        oracleContainer.stop()
    }
}