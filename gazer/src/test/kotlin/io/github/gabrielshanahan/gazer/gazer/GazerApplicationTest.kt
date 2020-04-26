package io.github.gabrielshanahan.gazer.gazer

import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.func.into
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.github.gabrielshanahan.gazer.gazer.properties.GazerProperties
import io.github.gabrielshanahan.gazer.gazer.service.GazerService
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
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

    private val properties = GazerProperties()

    @Test
    fun `Creating gazers for added endpoints works`() = runBlockingTest {
        val createdEndpoint = sharedData.googleEndpoint.asModel()
        val changes = Changes(create = listOf(createdEndpoint))
        val gazers: GazerMap = mutableMapOf()

        val gazerApp = GazerApplication(mockk(), mockk(), mockk(), this, properties)

        with(spyk(gazerApp)) {
            coroutineScope {
                coEvery {
                    launchGazer(createdEndpoint)
                } returns(Job())

                changes into update(gazers)

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
            updatedEndpoint.id to GazerPair(updatedEndpoint, mockJob)
        )

        val gazerApp = GazerApplication(mockk(), mockk(), mockk(), this, properties)

        with(spyk(gazerApp)) {
            coroutineScope {
                coEvery {
                    launchGazer(updatedEndpoint)
                } returns(Job())

                changes into update(gazers)

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
            removedEndpoint.id to GazerPair(removedEndpoint, mockJob)
        )

        val gazerApp = GazerApplication(mockk(), mockk(), mockk(), this, properties)

        with(spyk(gazerApp)) {
            coroutineScope {

                changes into update(gazers)

                coVerify {
                    mockJob.cancel()
                }

                Assertions.assertFalse(removedEndpoint.id in gazers)
            }
        }
    }

    @Test
    fun `Gazer app works end-to-end`() = runBlockingTest {
        val endpointRepo: MonitoredEndpointRepository = mockk()
        val gazerService: GazerService = mockk()
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

        coEvery {
            gazerService.gaze(any(), any())
        } coAnswers {
            // This is necessary because for a TestCoroutineDispatcher, yield() doesn't actually yield
            delay(1)
        }

        val gazers: GazerMap = mutableMapOf()
        val gazerApp = GazerApplication(endpointRepo, gazerService, persistor, this, properties)

        with(spyk(gazerApp)) gazerApp@{
            val job = launch {
                gaze(gazers)
            }
            delay(3 * properties.syncRate)

            coVerify {
                gazerService.gaze(sharedData.googleEndpoint.asModel(), persistor)
                gazerService.gaze(modifiedGoogleEndpoint.asModel(), persistor)
            }

            Assertions.assertEquals(0, gazers.size)
            job.cancel()
        }
    }
}
