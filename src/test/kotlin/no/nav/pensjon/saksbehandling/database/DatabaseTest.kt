package no.nav.pensjon.saksbehandling.database

import no.nav.pensjon.saksbehandling.COUNT_AVVIKSINFORMASJON_PSAK
import no.nav.pensjon.saksbehandling.COUNT_AVVIKSTILFELLER_PSAK
import no.nav.pensjon.saksbehandling.database.DatabaseTestUtils.createOracleDatasource
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
    private const val SUM_AVVIKSINFORMASJON_FROM_PSAK = 2.00
    private const val SUM_AVVIKSTILFELLER_FROM_PSAK = 2.00
    private lateinit var oracleDataSource: DataSource
    private lateinit var database: Database

    @JvmStatic
    @BeforeAll
    internal fun setup() {
        oracleContainer.start()
        oracleDataSource = createOracleDatasource(oracleContainer)
        database = Database(oracleDataSource::getConnection)
    }

    @Test
    @Order(1)
    internal fun `given 2 errors from psak in T_AVVIKSINFORMASJON, 2 errors will be returned from countAvviksinformasjonFromPsak`() {
        assertEquals(SUM_AVVIKSINFORMASJON_FROM_PSAK, database.queryForDouble(COUNT_AVVIKSINFORMASJON_PSAK))
    }

    @Test
    @Order(2)
    internal fun `given 2 errors from psak in T_AVVIKSTILFELLER, 2 errors will be returned from countAvvikstilfellerFromPsak`() {
        assertEquals(SUM_AVVIKSTILFELLER_FROM_PSAK, database.queryForDouble(COUNT_AVVIKSTILFELLER_PSAK))
    }

    @Test
    @Order(3)
    internal fun `when database is unavailable, throw CantQueryPenDatabase`() {
        breakDatabaseConnection()
        assertThrows<CantQueryPenDatabase> { database.queryForDouble(COUNT_AVVIKSTILFELLER_PSAK) }
    }

    private fun breakDatabaseConnection() {
        oracleContainer.stop()
    }
}