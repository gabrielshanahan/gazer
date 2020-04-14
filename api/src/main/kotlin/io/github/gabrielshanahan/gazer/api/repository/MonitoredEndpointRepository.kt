package io.github.gabrielshanahan.gazer.api.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.data.model.User
import java.util.*
import org.springframework.data.repository.CrudRepository

interface MonitoredEndpointRepository : CrudRepository<MonitoredEndpoint, UUID> {
    fun getAllByUser(user: User): List<MonitoredEndpoint>
    fun getByUserAndId(user: User, id: UUID): MonitoredEndpoint?
    fun removeByUserAndId(user: User, id: UUID): MonitoredEndpoint?
}
