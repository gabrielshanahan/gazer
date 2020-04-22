package io.github.gabrielshanahan.gazer.gazer.service

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
import io.ktor.utils.io.core.use
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PreDestroy
import java.nio.channels.UnresolvedAddressException

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

@Service
class GazingService(
    val endpointRepo: MonitoredEndpointRepository,
    val resultRepo: MonitoringResultRepository
): CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob())  {

    private val log: Logger = LoggerFactory.getLogger(GazingService::class.java)

    private fun MonitoredEndpoint.ident() = "$name(${id.toString().take(9)}...): $url"

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
            gazers[it.id] = Gazer(it, launchGazer(it))
        }

        update.forEach {
            log.info("Updating gazer for ${it.id}")
            gazers[it.id]?.job?.cancel()
            gazers[it.id] = Gazer(it, launchGazer(it))
        }

        delete.forEach {
            log.info("Removing gazer for $it")
            gazers[it]?.job?.cancel()
            gazers.remove(it)
        }
    }

    private fun launchGazer(endpoint: MonitoredEndpoint): Job = launch {
        HttpClient() {
            expectSuccess = false
        }.use { client ->
            log.info("Gazer created for ${endpoint.ident()}")

            while(true) {
                log.info("Gazing at ${endpoint.ident()}")
                try {
                    val response: HttpResponse = client.get(endpoint.url)
                    val checked = response.responseTime.toJvmDate()
                    val status = response.status.value
                    val payload = response.readText()

                    log.info("Saw ${payload.take(25)}... at ${endpoint.ident()}, persisting...")

                    persistResult(
                        endpoint = endpoint,
                        checked = checked,
                        status = status,
                        payload = payload
                    )

                } catch(e: UnresolvedAddressException) {
                    log.info("Unresolved address for ${endpoint.ident()}, persisting...")
                    persistResult(
                        endpoint = endpoint,
                        checked = Date(),
                        status = 404,
                        payload = ""
                    )
                }

                log.info("Persist finished for ${endpoint.ident()}, shutting eyes for ${endpoint.monitoredInterval}")

                delay(endpoint.monitoredInterval * 1000L)
            }
        }
        log.info("Gazing finished for ${endpoint.ident()}")
    }

    private fun persistResult(endpoint: MonitoredEndpoint, checked: Date, status: Int, payload: String) {
        MonitoringResultEntity(
            checked = checked,
            httpStatus = status,
            payload = payload,
            monitoredEndpoint = endpoint.asEntity()
        ) into resultRepo::saveAndFlush

        endpoint.lastCheck = checked
        endpoint.asEntity() into endpointRepo::saveAndFlush
    }

    suspend fun run() {
        val gazers: GazerMap = mutableMapOf()

        while(true) {
            val fetchedEndpoints = endpointRepo.findAll().map { it.asModel() }
            gazers collectChangesFrom fetchedEndpoints into update(gazers)
            delay(1000)
        }
    }
}
