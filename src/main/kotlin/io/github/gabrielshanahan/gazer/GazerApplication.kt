package io.github.gabrielshanahan.gazer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GazerApplication

fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
