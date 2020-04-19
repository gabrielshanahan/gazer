package io.github.gabrielshanahan.gazer.gazer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PreDestroy


@Component
class ScheduledTask : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val log: Logger = LoggerFactory.getLogger(ScheduledTask::class.java)

    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    suspend fun printStuff() {
        log.info("Inside coroutine, the time is now {}", dateFormat.format(Date()))
    }

    @PreDestroy
    fun doCancel() {
        log.info(" Running cancel")

        runBlocking {
            launch {
                log.info(" Delaying")
                delay(1000)
                this@ScheduledTask.coroutineContext[Job]?.cancelAndJoin()
                delay(1000)
                log.info(" Cancel called")
            }
        }
        log.info(" Finished running cancel")
    }

    @Scheduled(fixedRate = 3000)
    fun reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(Date()))
        log.info("Launching coroutine and setting x to 0")
        var x = 0
        launch {
            try {
                delay(2000)
                x++
                printStuff()
                log.info("x is $x")
            } finally {
                withContext(NonCancellable) {
                    log.info("Closing stuff")
                    delay(5000)
                    log.info("Closed")
                }
            }
        }
        log.info("Exiting reportCurrentTime")
    }
}
