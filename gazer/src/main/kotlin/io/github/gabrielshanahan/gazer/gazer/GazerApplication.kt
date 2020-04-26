package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.github.gabrielshanahan.gazer.gazer.model.toShortStr
import io.github.gabrielshanahan.gazer.gazer.properties.GazerProperties
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

/**
 * Represents a set of changes that need to be made to the current gazers.
 *
 * @see GazerApplication.collectChangesFrom
 * @see GazerApplication.update
 */
data class Changes(
    val create: List<MonitoredEndpoint> = emptyList(),
    val update: List<MonitoredEndpoint> = emptyList(),
    val delete: Set<UUID> = emptySet()
)

/**
 * The endpoints need to be stored with the jobs (gazers) that represent them, to be able to check for updates (such as
 * changed URLs).
 */
data class GazerPair(
    val endpoint: MonitoredEndpoint,
    val job: Job
)

/** The type used to store the list of currently running gazers along with all necessary information */
typealias GazerMap = MutableMap<UUID, GazerPair>

/**
 * Runs the gazing functionality as a CommandLineRunner. Actual gazing is delegated to an instance of [GazerService].
 *
 * The provided [gazerScope] is used to run all the coroutines.
 *
 * @see GazerConfiguration
 * @see GazerService
 */
@SpringBootApplication
class GazerApplication(
    private val endpointRepo: MonitoredEndpointRepository,
    private val gazerService: GazerService,
    private val persistor: SendChannel<PersistMsg>,
    private val gazerScope: CoroutineScope,
    private val properties: GazerProperties
) : CommandLineRunner, CoroutineScope by gazerScope {
    private val log: Logger = LoggerFactory.getLogger(GazerApplication::class.java)

    /** Constructs list of newly created/updated/removed endpoints with respect to the currently running gazers. */
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

    /**
     * Launches a coroutine representing a gazer for the given [endpoint]. The actual gazing is left to the
     * implementation of GazerService.
     */
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

    /**
     * Acts on the data constructed by [collectChangesFrom].
     */
    fun update(gazers: GazerMap): Changes.() -> Unit = {
        create.forEach {
            log.info("Creating gazer for ${it.toShortStr()}")
            gazers[it.id] = GazerPair(it, launchGazer(it))
        }

        update.forEach {
            log.info("Updating gazer for ${gazers[it.id]?.endpoint?.toShortStr()}")
            gazers[it.id]?.job?.cancel()
            gazers[it.id] = GazerPair(it, launchGazer(it))
        }

        delete.forEach {
            log.info("Removing gazer for ${gazers[it]?.endpoint?.toShortStr()}")
            gazers[it]?.job?.cancel()
            gazers.remove(it)
        }
    }

    /**
     * The main application loop. Periodically fetches all MonitoredEndpoints from the database, and
     * creates/updates/removes gazers as necessary.
     *
     * @see collectChangesFrom
     * @see update
     */
    suspend fun gaze(gazers: GazerMap) {
        while (isActive) {
            val fetchedEndpoints = endpointRepo.findAll().map { it.asModel() }
            gazers collectChangesFrom fetchedEndpoints into update(gazers)
            delay(properties.syncRate)
        }
    }

    /**
     * Delegates to [gaze].
     */
    override fun run(vararg args: String) {
        runBlocking {
            gaze(mutableMapOf())
        }
    }
}

fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
