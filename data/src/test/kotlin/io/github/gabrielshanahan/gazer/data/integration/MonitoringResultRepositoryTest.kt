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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

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

        val appliftingResults = resultRepo.getAllByMonitoredEndpointUserOrderByCheckedDesc(sharedData.appliftingUser)
        val batmanResults = resultRepo.getAllByMonitoredEndpointUserOrderByCheckedDesc(sharedData.batmanUser)

        Assertions.assertEquals(3, appliftingResults.size)
        Assertions.assertEquals(2, batmanResults.size)
    }

    @Test
    fun `Ordering works as expected`() {

        val appliftingResults = resultRepo.getAllByMonitoredEndpointUserOrderByCheckedDesc(sharedData.appliftingUser)

        Assertions.assertTrue(appliftingResults[0].checked > appliftingResults[1].checked)
        Assertions.assertTrue(appliftingResults[1].checked > appliftingResults[2].checked)
    }

    @Test
    fun `Pagination works as expected`() {

        val appliftingResults = resultRepo.getAllByMonitoredEndpoint(
            sharedData.applifting.endpoints[0],
            PageRequest.of(0, 2, Sort.by("checked").descending())
        )

        Assertions.assertEquals(2, appliftingResults.size)
        Assertions.assertTrue(appliftingResults[0].checked > appliftingResults[1].checked)
    }
}
