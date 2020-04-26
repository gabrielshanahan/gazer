package io.github.gabrielshanahan.gazer.gazer.actor

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.cancelAndBlock
import io.github.gabrielshanahan.gazer.gazer.model.toShortStr
import io.github.gabrielshanahan.gazer.gazer.properties.GazerProperties
import io.github.gabrielshanahan.gazer.gazer.service.GazerMsg
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.yield
import org.hibernate.TransientPropertyValueException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.InvalidDataAccessApiUsageException
import javax.annotation.PreDestroy

@Configuration
@ObsoleteCoroutinesApi
class ActorProvider(
    val endpointRepo: MonitoredEndpointRepository,
    val resultRepo: MonitoringResultRepository,
    properties: GazerProperties
) : CoroutineScope by CoroutineScope(Dispatchers.Unconfined) {

    private val log: Logger = LoggerFactory.getLogger(ActorProvider::class.java)

    private val bufferSize = if (properties.bufferSize < 0) Channel.UNLIMITED else properties.bufferSize

    val persistor: SendChannel<GazerMsg> = actor(capacity = bufferSize) {
        for (msg in channel) {
            when (msg) {
                is PersistMsg -> {
                    log.info("Persisting ${msg.result.toShortStr()} for " +
                        "${msg.result.monitoredEndpoint.toShortStr()}...")

                    msg.result.monitoredEndpoint.lastCheck = msg.result.checked
                    try {
                        msg.result.asEntity() into resultRepo::saveAndFlush
                        msg.result.monitoredEndpoint.asEntity() into endpointRepo::saveAndFlush
                    } catch (e: InvalidDataAccessApiUsageException) {
                        val cause = e.cause
                        if (cause is IllegalStateException && cause.cause is TransientPropertyValueException) {
                            log.info("Persist failed for ${msg.result.toShortStr()} because " +
                                "${msg.result.monitoredEndpoint.toShortStr()} was removed")
                        } else {
                            throw e
                        }
                    }

                    log.info("Persisting ${msg.result.toShortStr()} for " +
                        "${msg.result.monitoredEndpoint.toShortStr()} finished")

                    yield()
                }
            }
        }
    }

    @Bean
    fun getActor(): SendChannel<PersistMsg> = persistor

    @PreDestroy
    fun closePersistorChannel() {
        log.info("Closing persistor channel")
        persistor.close()

        log.info("Cancelling actor scope")
        cancelAndBlock()
    }
}
