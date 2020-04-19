package io.github.gabrielshanahan.gazer.gazer

import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class GazerApplication

fun main(args: Array<String>) {
    runBlocking {
        runApplication<GazerApplication>(*args)
    }
}
