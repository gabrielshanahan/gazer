package io.github.gabrielshanahan.gazer.gazer.service

import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.func.suspInto
import io.github.gabrielshanahan.gazer.gazer.ident
import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.gazer.model.MonitoringResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.util.date.toJvmDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.channels.UnresolvedAddressException
import java.util.*

@Service
class GazerServiceImpl(val client: HttpClient)
    : GazerService, CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob())
{
    private val log: Logger = LoggerFactory.getLogger(GazerServiceImpl::class.java)

    override fun launchGazer(endpoint: MonitoredEndpoint, persistor: SendChannel<PersistMsg>): Job = launch {
        log.info("Gazer created for ${endpoint.ident()}")
        try {
            while(true) {
                log.info("Gazing at ${endpoint.ident()}")
                try {
                    val response: HttpResponse = client.get(endpoint.url)

                    val result = MonitoringResult(
                        checked = response.responseTime.toJvmDate(),
                        httpStatus = response.status.value,
                        payload = response.readText(),
                        monitoredEndpoint = endpoint
                    )

                    log.info("Saw ${result.ident()} at ${endpoint.ident()}, sending result...")

                    PersistMsg(result) suspInto persistor::send

                } catch(e: UnresolvedAddressException) {
                    log.info("Unresolved address for ${endpoint.ident()}, sending result...")

                    PersistMsg(
                        MonitoringResult(
                            checked = Date(),
                            httpStatus = 404,
                            payload = "",
                            monitoredEndpoint = endpoint
                        )
                    ) suspInto persistor::send
                }

                log.info("Sending finished for ${endpoint.ident()}, shutting eyes for ${endpoint.monitoredInterval}")

                delay(endpoint.monitoredInterval * 1000L)
            }
        } finally {
            log.info("Gazing finished for ${endpoint.ident()}")
        }
    }
}
