package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.github.gabrielshanahan.gazer.gazer.model.toShortStr
import io.github.gabrielshanahan.gazer.gazer.properties.GazerProperties
import io.github.gabrielshanahan.gazer.gazer.service.GazerService
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

data class Changes(
    val create: List<MonitoredEndpoint> = emptyList(),
    val update: List<MonitoredEndpoint> = emptyList(),
    val delete: Set<UUID> = emptySet()
)

data class Gazer(
    val endpoint: MonitoredEndpoint,
    val job: Job
)

typealias GazerMap = MutableMap<UUID, Gazer>

@SpringBootApplication
class GazerApplication(
    private val endpointRepo: MonitoredEndpointRepository,
    private val gazerService: GazerService,
    private val persistor: SendChannel<PersistMsg>,
    private val gazerScope: CoroutineScope,
    private val properties: GazerProperties
) : CommandLineRunner, CoroutineScope by gazerScope {
    private val log: Logger = LoggerFactory.getLogger(GazerApplication::class.java)

    infix fun GazerMap.collectChangesFrom(endpoints: List<MonitoredEndpoint>) = Changes(
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

    fun launchGazer(endpoint: MonitoredEndpoint): Job = launch {
        log.info("Gazer created for ${endpoint.toShortStr()}")
        try {
            while (isActive) {
                gazerService.gaze(endpoint, persistor)
                yield()
            }
        } finally {
            log.info("Gazer cancelled for ${endpoint.toShortStr()}")
        }
    }

    fun update(gazers: GazerMap): Changes.() -> Unit = {
        create.forEach {
            log.info("Creating gazer for ${it.toShortStr()}")
            gazers[it.id] = Gazer(it, launchGazer(it))
        }

        update.forEach {
            log.info("Updating gazer for ${gazers[it.id]?.endpoint?.toShortStr()}")
            gazers[it.id]?.job?.cancel()
            gazers[it.id] = Gazer(it, launchGazer(it))
        }

        delete.forEach {
            log.info("Removing gazer for ${gazers[it]?.endpoint?.toShortStr()}")
            gazers[it]?.job?.cancel()
            gazers.remove(it)
        }
    }

    suspend fun gaze(gazers: GazerMap) {
        while (isActive) {
            val fetchedEndpoints = endpointRepo.findAll().map { it.asModel() }
            gazers collectChangesFrom fetchedEndpoints into update(gazers)
            delay(properties.syncRate)
        }
    }

    override fun run(vararg args: String) {
        runBlocking {
            gaze(mutableMapOf())
        }
    }
}

fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
