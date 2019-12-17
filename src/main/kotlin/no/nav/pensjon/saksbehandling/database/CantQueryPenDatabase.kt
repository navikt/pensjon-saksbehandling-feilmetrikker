package no.nav.pensjon.saksbehandling.database
import java.lang.RuntimeException

internal class CantQueryPenDatabase(message: String?, cause: Throwable?) : RuntimeException(message, cause)
