package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.github.gabrielshanahan.gazer.gazer.service.GazerService
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

internal data class Changes(
    val create: List<MonitoredEndpoint>,
    val update: List<MonitoredEndpoint>,
    val delete: Set<UUID>
)

internal data class Gazer(
    val endpoint: MonitoredEndpoint,
    val job: Job
)

internal typealias GazerMap = MutableMap<UUID, Gazer>

@SpringBootApplication
class GazerApplication(
    val endpointRepo: MonitoredEndpointRepository,
    private val gazerService: GazerService,
    private val persistor: SendChannel<PersistMsg>
): CommandLineRunner {

    private val log: Logger = LoggerFactory.getLogger(GazerApplication::class.java)

    private infix fun GazerMap.collectChangesFrom(endpoints: List<MonitoredEndpoint>) = Changes(
        create = endpoints.filter {
            it.id !in keys
        },
        update = endpoints.filter {
            get(it.id)?.endpoint ?: it != it
        },
        delete = keys - endpoints.map {
            it.id
        }
    )

    private fun update(gazers: GazerMap): Changes.() -> Unit = {
        create.forEach {
            log.info("Creating gazer for ${it.id}")
            gazers[it.id] = Gazer(it, gazerService.launchGazer(it, persistor))
        }

        update.forEach {
            log.info("Updating gazer for ${it.id}")
            gazers[it.id]?.job?.cancel()
            gazers[it.id] = Gazer(it, gazerService.launchGazer(it, persistor))
        }

        delete.forEach {
            log.info("Removing gazer for $it")
            gazers[it]?.job?.cancel()
            gazers.remove(it)
        }
    }


    override fun run(vararg args: String) = runBlocking {
        val gazers: GazerMap = mutableMapOf()

        while(true) {
            val fetchedEndpoints = endpointRepo.findAll().map { it.asModel() }
            gazers collectChangesFrom fetchedEndpoints into update(gazers)
            delay(1000)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
