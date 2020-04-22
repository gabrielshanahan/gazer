package io.github.gabrielshanahan.gazer.gazer.service

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.ident
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ActorProvider(
    val endpointRepo: MonitoredEndpointRepository,
    val resultRepo: MonitoringResultRepository
): CoroutineScope by CoroutineScope(Dispatchers.Unconfined) {

    private val log: Logger = LoggerFactory.getLogger(ActorProvider::class.java)

    @ObsoleteCoroutinesApi
    fun createActor() = actor<GazerMsg> {
        for(msg in channel) {
            when(msg) {
                is PersistMsg -> {
                    log.info("Persisting ${msg.result.ident()} for ${msg.result.monitoredEndpoint.ident()}...")

                    msg.result.monitoredEndpoint.lastCheck = msg.result.checked
                    msg.result.asEntity() into resultRepo::saveAndFlush
                    msg.result.monitoredEndpoint.asEntity() into endpointRepo::saveAndFlush

                    log.info("Persisting ${msg.result.ident()} for ${msg.result.monitoredEndpoint.ident()} finished")
                }
            }
        }
    }


}
