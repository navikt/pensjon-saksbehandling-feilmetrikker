package no.nav.pensjon.saksbehandling.gauge

import io.prometheus.client.Gauge
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

internal fun gauge(conf: Gauge.Builder.() -> Unit) = Gauge.build().apply(conf).register()

open class PrometheusGauge(private val name: String, help: String) {
    companion object {
        internal val log: Logger = getLogger(PrometheusGauge::class.java)
    }

    private val prometheusGauge = gauge {
        name(name.toLowerCase().replace(' ', '_'))
        help(help)
    }

    fun setDouble(value: Double) {
        log.info("$name: $this")
        prometheusGauge.set(value)
    }
}