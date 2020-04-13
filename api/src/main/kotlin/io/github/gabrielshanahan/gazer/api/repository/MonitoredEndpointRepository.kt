package io.github.gabrielshanahan.gazer.api.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import org.springframework.data.repository.CrudRepository
import java.util.*

interface MonitoredEndpointRepository: CrudRepository<MonitoredEndpoint, UUID>
