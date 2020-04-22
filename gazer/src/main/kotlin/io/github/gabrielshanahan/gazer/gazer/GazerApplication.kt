package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.model.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.util.date.toJvmDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*
import javax.annotation.PreDestroy

typealias Gazers = MutableMap<UUID, Pair<MonitoredEndpoint, Job>>
data class Changes(
    val create: List<MonitoredEndpoint>,
    val update: List<MonitoredEndpoint>,
    val delete: Set<UUID>
)

@SpringBootApplication
@EnableScheduling
class GazerApplication(
    val endpointRepo: MonitoredEndpointRepository,
    val resultRepo: MonitoringResultRepository
): CommandLineRunner {

    private val log: Logger = LoggerFactory.getLogger(GazerApplication::class.java)

    private lateinit var scope: CoroutineScope

    private infix fun Gazers.collectChangesIn(endpoints: List<MonitoredEndpoint>) = Changes(
        create = endpoints.filter {
            it.id !in keys
        },
        update = endpoints.filter {
            get(it.id)?.first ?: it != it
        },
        delete = keys - endpoints.map {
            it.id
        }
    )

    private fun CoroutineScope.update(gazers: Gazers): (Changes) -> Unit = { (create, update, delete) ->
        create.forEach {
            log.info("Creating gazer for ${it.id}")
            gazers[it.id] = it to gaze(it)
        }

        update.forEach {
            log.info("Updating gazer for ${it.id}")
            gazers[it.id]?.second?.cancel()
            gazers[it.id] = it to gaze(it)
        }

        delete.forEach {
            log.info("Removing gazer for $it")
            gazers[it]?.second?.cancel()
            gazers.remove(it)
        }
    }

    private fun CoroutineScope.gaze(endpoint: MonitoredEndpoint): Job = launch {
        HttpClient().use { client ->
            log.info("Gazer created for ${endpoint.id}")

            while(true) {
                log.info("Gazing for ${endpoint.id} (${endpoint.url})")
                val response: HttpResponse = client.get(endpoint.url)
                val payload = response.readText()
                val status = response.status.value
                val checked = response.responseTime.toJvmDate()

                log.info("Saw ${payload.take(25)}... for ${endpoint.id} (${endpoint.url}), persisting...")

                endpoint.lastCheck = checked

                val result = MonitoringResultEntity(
                    checked = checked,
                    httpStatus = status,
                    payload = payload,
                    monitoredEndpoint = endpoint.asEntity()
                )

                resultRepo.saveAndFlush(result)

                // Maybe not necessary if we use CascadeType.PERSIST?
                endpointRepo.saveAndFlush(endpoint.asEntity())
                log.info("Persist finished for ${endpoint.id} (${endpoint.url}), shutting eyes " +
                    "for ${endpoint.monitoredInterval}")
                delay(endpoint.monitoredInterval * 1000L)
            }
        }
        log.info("Gazing finished for ${endpoint.id}")
    }

    override fun run(vararg args: String) {
        HttpClient().use { client ->
            runBlocking {
                supervisorScope {
                    val gazers: Gazers = mutableMapOf()
                    scope = this

                    while(true) {
                        val endpoints = endpointRepo.findAll().map { it.asModel() }
                        gazers collectChangesIn endpoints into update(gazers)
                        delay(1000)
                    }
                }
            }
        }
    }

    @PreDestroy
    fun gracefulKill() = runBlocking {
        log.info("Preparing to kill all gazers")
        scope.coroutineContext[Job]?.cancelAndJoin()
        log.info("Gazers killed")
    }
}


fun main(args: Array<String>) {
    runApplication<GazerApplication>(*args)
}
