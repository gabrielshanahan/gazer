package io.github.gabrielshanahan.gazer.api.integration

import io.github.gabrielshanahan.gazer.api.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.api.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class RepositoryTest(
    @Autowired var endpointRepo: MonitoredEndpointRepository,
    @Autowired var userRepo: UserRepository
) {

    @Test
    fun `Filtering by user works as expected`() {

        val sharedData = SharedData(userRepo)

        endpointRepo.saveAll(listOf(sharedData.googleEndpoint, sharedData.yahooEndpoint))

        val appliftingEndpoints = endpointRepo.getAllByUser(sharedData.appliftingUser)
        val batmanEndpoints = endpointRepo.getAllByUser(sharedData.batmanUser)

        Assertions.assertEquals(1, appliftingEndpoints.size)
        Assertions.assertEquals(1, batmanEndpoints.size)

        Assertions.assertEquals(sharedData.googleEndpoint, appliftingEndpoints.single())
        Assertions.assertEquals(sharedData.yahooEndpoint, batmanEndpoints.single())
    }
}
