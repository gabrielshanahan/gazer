package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.gazer.service.GazingService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GazerApplication(private val gazingService: GazingService): CommandLineRunner {
    override fun run(vararg args: String) = runBlocking { gazingService.run() }
}

fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
