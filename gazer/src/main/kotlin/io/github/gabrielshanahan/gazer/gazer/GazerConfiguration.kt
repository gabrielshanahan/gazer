package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.DataConfiguration
import io.github.gabrielshanahan.gazer.gazer.service.ActorProvider
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import io.ktor.client.HttpClient
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.annotation.PreDestroy

@Configuration
@EnableJpaRepositories(basePackageClasses = [DataConfiguration::class])
@EntityScan(basePackageClasses = [DataConfiguration::class])
class GazerConfiguration(
    actorPersistor: ActorProvider
) {

    private val log: Logger = LoggerFactory.getLogger(GazerConfiguration::class.java)

    @get:Bean
    val ktorClient = HttpClient() {
        expectSuccess = false
    }

    @ObsoleteCoroutinesApi
    @get:Bean
    val persistor: SendChannel<PersistMsg> = actorPersistor.createActor()

    @PreDestroy
    fun releaseKtorClient() {
        log.info("Releasing ktor client")
        ktorClient.close()
    }

    @PreDestroy
    fun closePersistorChannel() {
        log.info("Closing persistor channel")
        persistor.close()
    }
}
