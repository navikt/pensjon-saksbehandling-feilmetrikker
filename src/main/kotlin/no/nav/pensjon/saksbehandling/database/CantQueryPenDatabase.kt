package no.nav.pensjon.saksbehandling.database
import java.lang.RuntimeException

class CantQueryPenDatabase(message: String?, cause: Throwable?) : RuntimeException(message, cause)
