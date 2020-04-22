package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.DataConfiguration
import io.github.gabrielshanahan.gazer.gazer.config.ActorProvider
import io.ktor.client.HttpClient
import javax.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackageClasses = [DataConfiguration::class])
@EntityScan(basePackageClasses = [DataConfiguration::class])
class GazerConfiguration {

    private val log: Logger = LoggerFactory.getLogger(GazerConfiguration::class.java)

    @get:Bean
    val ktorClient = HttpClient() {
        expectSuccess = false
    }

    @PreDestroy
    fun releaseKtorClient() {
        log.info("Releasing ktor client")
        ktorClient.close()
    }
}
