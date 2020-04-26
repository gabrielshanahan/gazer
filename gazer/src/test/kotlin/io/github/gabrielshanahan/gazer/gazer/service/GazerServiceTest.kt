package io.github.gabrielshanahan.gazer.gazer.service

import com.ninjasquad.springmockk.MockkBean
import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.gazer.TestConfiguration
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfiguration::class])
@ExperimentalCoroutinesApi
class GazerServiceTest(
    @Autowired private val userRepo: UserRepository,
    @Autowired val gazerService: SimpleGazerService
) {
    @MockkBean
    private lateinit var persistor: SendChannel<PersistMsg>

    private val sharedData = DataSamples(userRepo)

    /*
     * MockK has trouble mocking HttpClient.get, because it can't deal with generic return types,
     * so we actually have to make the HTTP call
     */
    @Test
    fun `Gazing works as expected`() = runBlocking {

        val endpoint = sharedData.googleEndpoint.asModel()
        endpoint.monitoredInterval = 0

        coEvery {
            persistor.send(any())
        } returns(Unit)

        gazerService.gaze(endpoint, persistor)
        coVerify { persistor.send(any()) }
    }
}
