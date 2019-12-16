package no.nav.pensjon.saksbehandling

import io.prometheus.client.Counter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun counter (conf:Counter.Builder.()->Unit) = Counter.build().apply(conf).register()

open class PromethiusCounter(private val name:String, help:String){
    companion object{
        internal val log: Logger = LoggerFactory.getLogger(PromethiusCounter::class.java)
    }
    val promethiusCounter = counter {
        name(name.toLowerCase().replace(' ', '_'))
        help(help)
    }

    fun set(value:Double) {
        log.info("$name: $this")
        promethiusCounter.inc(value - promethiusCounter.get())
    }
}