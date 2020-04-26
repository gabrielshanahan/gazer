package io.github.gabrielshanahan.gazer.gazer.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("gazer")
@Component
class GazerProperties {
    /**
     * Used to control the rate with which the DB is polled for new/updated/removed MonitoredEndpoints.
     *
     * @see io.github.gabrielshanahan.gazer.gazer.GazerApplication.gaze
     */
    var syncRate: Long = 1000

    /**
     * Used to control the the size of the actors channel buffer.
     *
     * @see io.github.gabrielshanahan.gazer.gazer.actor.ActorProvider
     */
    var bufferSize: Int = 1024
}
