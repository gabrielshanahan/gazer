package io.github.gabrielshanahan.gazer.gazer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class GazerApplication

fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
