package io.github.gabrielshanahan.gazer.data.integration

import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
final class RepositoryTest(
    @Autowired var endpointRepo: MonitoredEndpointRepository,
    @Autowired var userRepo: UserRepository
) {
    private val sharedData = DataSamples(userRepo)

    @BeforeEach
    fun setup() {
        endpointRepo.saveAll(listOf(sharedData.googleEndpoint, sharedData.yahooEndpoint))
    }

//    @AfterEach
//    fun teardown() {
//        endpointRepo.deleteAll()
//    }

    @Test
    fun `Filtering by user works as expected`() {

        val appliftingEndpoints = endpointRepo.getAllByUser(sharedData.appliftingUser)
        val batmanEndpoints = endpointRepo.getAllByUser(sharedData.batmanUser)

        Assertions.assertEquals(1, appliftingEndpoints.size)
        Assertions.assertEquals(1, batmanEndpoints.size)

        Assertions.assertEquals(sharedData.googleEndpoint, appliftingEndpoints.single())
        Assertions.assertEquals(sharedData.yahooEndpoint, batmanEndpoints.single())
    }

    @Test
    fun `Find by id works when users match`() {
        val yahooEndpoint =
            endpointRepo.getByUserAndId(sharedData.yahooEndpoint.user, sharedData.yahooEndpoint.id)

        Assertions.assertEquals(sharedData.yahooEndpoint, yahooEndpoint)
    }

    @Test
    fun `Find by id works when users don't match`() {
        val noEndpoint =
            endpointRepo.getByUserAndId(sharedData.googleEndpoint.user, sharedData.yahooEndpoint.id)

        Assertions.assertNull(noEndpoint)
    }
}
