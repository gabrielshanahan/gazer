package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.github.gabrielshanahan.gazer.gazer.model.toShortStr
import io.github.gabrielshanahan.gazer.gazer.service.GazerService
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import java.util.*
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

internal data class Changes(
    val create: List<MonitoredEndpoint> = emptyList(),
    val update: List<MonitoredEndpoint> = emptyList(),
    val delete: Set<UUID> = emptySet()
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
    private val persistor: SendChannel<PersistMsg>,
    private val gazerScope: CoroutineScope
) : CommandLineRunner, CoroutineScope by gazerScope {
    private val log: Logger = LoggerFactory.getLogger(GazerApplication::class.java)

    internal infix fun GazerMap.collectChangesFrom(endpoints: List<MonitoredEndpoint>) = Changes(
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

    internal fun launchGazer(endpoint: MonitoredEndpoint): Job = launch {
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

    internal fun update(gazers: GazerMap): Changes.() -> Unit = {
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

    internal suspend fun gaze(gazers: GazerMap) {
        while (isActive) {
            val fetchedEndpoints = endpointRepo.findAll().map { it.asModel() }
            gazers collectChangesFrom fetchedEndpoints into update(gazers)
            delay(1000)
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
