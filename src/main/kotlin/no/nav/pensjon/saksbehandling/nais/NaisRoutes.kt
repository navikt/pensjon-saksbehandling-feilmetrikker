package no.nav.pensjon.saksbehandling.nais

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

fun Application.nais(
    isAliveCheck: () -> Boolean = { true },
    isReadyCheck: () -> Boolean = { true },
    collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry
) {

    install(MicrometerMetrics) {
        registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }

    routing {
        isAliveRouting(isAliveCheck)
        isReadyRouting(isReadyCheck)
        metricsRouting(collectorRegistry)
    }
}

private fun Routing.metricsRouting(collectorRegistry: CollectorRegistry) {
    get("/metrics") {
        val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: emptySet()
        call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
            TextFormat.write004(this, collectorRegistry.filteredMetricFamilySamples(names))
        }
    }
}

private fun Routing.isReadyRouting(isReadyCheck: () -> Boolean) = probeRouting(isReadyCheck, "/isReady", "READY")

private fun Routing.isAliveRouting(isAliveCheck: () -> Boolean) = probeRouting(isAliveCheck, "/isAlive", "ALIVE")

private fun Routing.probeRouting(check: () -> Boolean, path: String, state: String) {
   get(path) {
      with(
         if (check()) "" to HttpStatusCode.OK
         else "NOT " to HttpStatusCode.ServiceUnavailable
      ) { call.respondText(first+state, ContentType.Text.Plain, second) }
   }
}