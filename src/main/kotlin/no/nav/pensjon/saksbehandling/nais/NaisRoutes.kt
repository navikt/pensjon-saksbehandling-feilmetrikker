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
import io.prometheus.client.exporter.common.TextFormat.write004 as write0041

internal fun Application.nais(
    collectorRegistry: CollectorRegistry = CollectorRegistry.defaultRegistry
) {

    install(MicrometerMetrics) {
        registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }

    routing {
        isAliveRouting()
        isReadyRouting()
        metricsRouting(collectorRegistry)
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
private fun Routing.metricsRouting(collectorRegistry: CollectorRegistry) {
    get("/metrics") {
        val names = call.request.queryParameters.getAll("name[]")?.toSet() ?: emptySet()
        call.respondTextWriter(ContentType.parse(TextFormat.CONTENT_TYPE_004)) {
            write0041(this, collectorRegistry.filteredMetricFamilySamples(names))
        }
    }
}

private fun Routing.isReadyRouting() = probeRouting("/isReady")

private fun Routing.isAliveRouting() = probeRouting("/isAlive")

private fun Routing.probeRouting(path: String) {
    get(path) {
        with("" to HttpStatusCode.OK) {
            call.respondText(first, ContentType.Text.Plain, second)
        }
    }
}