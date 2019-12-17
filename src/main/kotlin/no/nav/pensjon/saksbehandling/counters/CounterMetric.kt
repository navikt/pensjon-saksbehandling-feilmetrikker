package no.nav.pensjon.saksbehandling.counters

import io.prometheus.client.Counter
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

internal fun counter(conf: Counter.Builder.() -> Unit) = Counter.build().apply(conf).register()

open class PrometheusCounter(private val name: String, help: String) {
    companion object {
        internal val log: Logger = getLogger(PrometheusCounter::class.java)
    }

    private val prometheusCounter = counter {
        name(name.toLowerCase().replace(' ', '_'))
        help(help)
    }

    fun setDouble(value: Double) {
        log.info("$name: $this")
        prometheusCounter.inc(value - prometheusCounter.get())
    }
}