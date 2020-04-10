package io.github.gabrielshanahan.gazer.app

import io.github.gabrielshanahan.gazer.data.DataConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = [DataConfiguration::class])
@EntityScan(basePackageClasses = [DataConfiguration::class])
class GazerApplication

fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
