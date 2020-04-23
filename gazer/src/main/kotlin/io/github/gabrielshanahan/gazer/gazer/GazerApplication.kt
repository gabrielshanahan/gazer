package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.func.suspInto
import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.github.gabrielshanahan.gazer.gazer.model.toShortStr
import io.github.gabrielshanahan.gazer.gazer.service.GazerService
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
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
    private val persistor: SendChannel<PersistMsg>
) : CommandLineRunner {

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

    internal fun CoroutineScope.launchGazer(endpoint: MonitoredEndpoint): Job = launch {
        log.info("Gazer created for ${endpoint.toShortStr()}")
        gazerService.gaze(endpoint, persistor)
    }

    internal fun CoroutineScope.update(gazers: GazerMap): suspend Changes.() -> Unit = {
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

    internal suspend fun gaze(gazers: GazerMap, coroutineScope: CoroutineScope) = with(coroutineScope) {
        while (true) {
            val fetchedEndpoints = endpointRepo.findAll().map { it.asModel() }
            gazers collectChangesFrom fetchedEndpoints suspInto update(gazers)
            delay(1000)
        }
    }

    override fun run(vararg args: String) = runBlocking(Dispatchers.Default) {
        supervisorScope {
            gaze(mutableMapOf(), this)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
