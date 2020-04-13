package io.github.gabrielshanahan.gazer.api.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import java.util.*
import org.springframework.data.repository.CrudRepository

interface MonitoredEndpointRepository : CrudRepository<MonitoredEndpoint, UUID>
