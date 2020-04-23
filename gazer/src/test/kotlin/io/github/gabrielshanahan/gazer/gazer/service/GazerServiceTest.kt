package io.github.gabrielshanahan.gazer.gazer.service

import com.ninjasquad.springmockk.MockkBean
import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.gazer.TestConfiguration
import io.github.gabrielshanahan.gazer.gazer.model.asModel
import io.ktor.client.request.get
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
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
    @Autowired val gazerService: GazerServiceImpl
) {
    @MockkBean
    private lateinit var persistor: SendChannel<PersistMsg>

    private val sharedData = DataSamples(userRepo)

    @Test
    fun `Gazing works as expected`() = runBlockingTest {

        val endpoint = sharedData.googleEndpoint.asModel()

        coEvery {
            persistor.send(any())
        } returns(Unit)

        // Unfortunately can't mock HttpClient, because MockK can't deal with generic return types
        val job = launch {
            gazerService.gaze(endpoint, persistor)
        }

        coVerify(timeout = 1000) { persistor.send(any()) }

        job.cancel()
    }
}
