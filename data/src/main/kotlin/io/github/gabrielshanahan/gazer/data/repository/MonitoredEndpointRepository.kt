package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.data.model.User
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MonitoredEndpointRepository : JpaRepository<MonitoredEndpoint, UUID> {
    fun getAllByUser(user: User): List<MonitoredEndpoint>
    fun getByUserAndId(user: User, id: UUID): MonitoredEndpoint?
    fun removeByUserAndId(user: User, id: UUID): MonitoredEndpoint?
}
