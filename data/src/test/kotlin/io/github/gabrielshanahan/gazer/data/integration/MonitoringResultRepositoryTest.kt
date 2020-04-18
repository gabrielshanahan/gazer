package io.github.gabrielshanahan.gazer.data.integration

import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
final class MonitoringResultRepositoryTest(
    @Autowired var endpointRepo: MonitoredEndpointRepository,
    @Autowired var resultRepo: MonitoringResultRepository,
    @Autowired var userRepo: UserRepository
) {
    private val sharedData = DataSamples(userRepo)

    @BeforeEach
    fun setup() {
        endpointRepo.saveAll(listOf(sharedData.googleEndpoint, sharedData.yahooEndpoint))
        resultRepo.saveAll(sharedData.googleResults)
        resultRepo.saveAll(sharedData.yahooResults)
    }

    @Test
    fun `Filtering by user works as expected`() {

        val appliftingResults = resultRepo.getAllByMonitoredEndpointUser(sharedData.appliftingUser)
        val batmanResults = resultRepo.getAllByMonitoredEndpointUser(sharedData.batmanUser)

        Assertions.assertEquals(3, appliftingResults.size)
        Assertions.assertEquals(2, batmanResults.size)
    }
}
