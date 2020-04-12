package io.github.gabrielshanahan.gazer.api

import io.github.gabrielshanahan.gazer.data.DataConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication
@EntityScan(basePackageClasses = [DataConfiguration::class])
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
