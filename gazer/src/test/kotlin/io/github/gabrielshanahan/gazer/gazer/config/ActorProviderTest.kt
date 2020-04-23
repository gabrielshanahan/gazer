package io.github.gabrielshanahan.gazer.gazer.config

import com.ninjasquad.springmockk.MockkBean
import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.gazer.TestConfiguration
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.github.gabrielshanahan.gazer.gazer.service.PersistMsg
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfiguration::class])
class ActorProviderTest(@Autowired private val userRepo: UserRepository) {

    private val sharedData = DataSamples(userRepo)

    @MockkBean
    lateinit var endpointRepo: MonitoredEndpointRepository

    @MockkBean
    lateinit var resultRepo: MonitoringResultRepository

    @Test
    fun `Actor works as expected`() = runBlockingTest {

        val resultEntity = sharedData.googleResults[0]

        every {
            resultRepo.saveAndFlush(resultEntity)
        } returns(resultEntity)

        every {
            endpointRepo.saveAndFlush(resultEntity.monitoredEndpoint)
        } returns(resultEntity.monitoredEndpoint)

        val actor = ActorProvider(endpointRepo, resultRepo).getActor()

        actor.send(PersistMsg(resultEntity.asModel()))

        verify {
            resultRepo.saveAndFlush(resultEntity)
        }

        verify {
            endpointRepo.saveAndFlush(resultEntity.monitoredEndpoint)
        }
    }
}
