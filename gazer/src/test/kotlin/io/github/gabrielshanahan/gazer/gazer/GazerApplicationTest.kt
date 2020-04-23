package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.func.suspInto
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.github.gabrielshanahan.gazer.gazer.service.GazerService
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExperimentalCoroutinesApi
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfiguration::class])
class GazerApplicationTest(@Autowired private val userRepo: UserRepository) {

    private val sharedData = DataSamples(userRepo)

    @Test
    fun `Creating gazers for added endpoints works`() = runBlockingTest {
        val createdEndpoint = sharedData.googleEndpoint.asModel()
        val changes = Changes(create = listOf(createdEndpoint))
        val gazers: GazerMap = mutableMapOf()

        spyk(GazerApplication(mockk(), mockk(), mockk())).apply {
            coroutineScope {
                coEvery {
                    launchGazer(createdEndpoint)
                } returns(Job())

                changes suspInto update(gazers)

                coVerify {
                    launchGazer(createdEndpoint)
                }

                Assertions.assertEquals(createdEndpoint, gazers[createdEndpoint.id]!!.endpoint)
            }
        }
    }

    @Test
    fun `Creating gazers for updated endpoints works`() = runBlockingTest {
        val updatedEndpoint = sharedData.yahooEndpoint.asModel()
        val changes = Changes(update = listOf(updatedEndpoint))
        val mockJob = spyk<Job>()

        val gazers: GazerMap = mutableMapOf(
            updatedEndpoint.id to Gazer(updatedEndpoint, mockJob)
        )

        spyk(GazerApplication(mockk(), mockk(), mockk())).apply {
            coroutineScope {
                coEvery {
                    launchGazer(updatedEndpoint)
                } returns(Job())

                changes suspInto update(gazers)

                coVerify {
                    mockJob.cancel()
                    launchGazer(updatedEndpoint)
                }
            }
        }
    }

    @Test
    fun `Removing gazers for removed endpoints works`() = runBlockingTest {
        val removedEndpoint = sharedData.googleEndpoint.asModel()
        val changes = Changes(delete = setOf(removedEndpoint.id))
        val mockJob = spyk<Job>()

        val gazers: GazerMap = mutableMapOf(
            removedEndpoint.id to Gazer(removedEndpoint, mockJob)
        )

        spyk(GazerApplication(mockk(), mockk(), mockk())).apply {
            coroutineScope {

                changes suspInto update(gazers)

                coVerify {
                    mockJob.cancel()
                }

                Assertions.assertFalse(removedEndpoint.id in gazers)
            }
        }
    }

    @Test
    fun `Updated are constructed correctly`() = runBlockingTest {
        val endpointRepo: MonitoredEndpointRepository = mockk()
        val gazerService: GazerService = mockk(relaxUnitFun = true)
        val persistor: SendChannel<PersistMsg> = mockk()

        val modifiedGoogleEndpoint = MonitoredEndpointEntity(
            id = sharedData.googleEndpoint.id,
            name = sharedData.googleEndpoint.name,
            url = sharedData.googleEndpoint.url,
            created = sharedData.googleEndpoint.created,
            lastCheck = sharedData.googleEndpoint.lastCheck,
            monitoredInterval = sharedData.googleEndpoint.monitoredInterval + 10,
            user = sharedData.googleEndpoint.user
        )

        every {
            endpointRepo.findAll()
        } returns listOf(
            sharedData.googleEndpoint
        ) andThen listOf(
            modifiedGoogleEndpoint
        ) andThen listOf()

        val gazers: GazerMap = mutableMapOf()

        spyk(GazerApplication(endpointRepo, gazerService, persistor)).apply gazerApp@{
            coroutineScope coroutineScope@{
                val job = launch {
                    gaze(gazers, this@coroutineScope)
                }
                delay(3000)

                coVerify {
                    gazerService.gaze(sharedData.googleEndpoint.asModel(), persistor)
                    gazerService.gaze(modifiedGoogleEndpoint.asModel(), persistor)
                }

                Assertions.assertEquals(0, gazers.size)
                job.cancel()
            }
        }
    }
}
