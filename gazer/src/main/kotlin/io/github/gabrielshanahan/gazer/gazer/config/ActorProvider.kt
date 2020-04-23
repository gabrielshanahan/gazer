package io.github.gabrielshanahan.gazer.gazer.config

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.model.toShortStr
import io.github.gabrielshanahan.gazer.gazer.service.GazerMsg
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import javax.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import org.hibernate.TransientPropertyValueException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.InvalidDataAccessApiUsageException

@Configuration
@ObsoleteCoroutinesApi
class ActorProvider(
    val endpointRepo: MonitoredEndpointRepository,
    val resultRepo: MonitoringResultRepository
) : CoroutineScope by CoroutineScope(Dispatchers.Unconfined) {

    private val log: Logger = LoggerFactory.getLogger(ActorProvider::class.java)

    val persistor: SendChannel<GazerMsg> = actor(capacity = 1024) {
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
    }
}
