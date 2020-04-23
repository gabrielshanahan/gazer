package io.github.gabrielshanahan.gazer.gazer.config

import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.gazer.TestConfiguration
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfiguration::class])
class ActorProviderTest(
    @Autowired val endpointRepo: MonitoredEndpointRepository,
    @Autowired val resultRepo: MonitoringResultRepository
) {
}
