package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.model.UserEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MonitoredEndpointRepository : JpaRepository<MonitoredEndpointEntity, UUID> {
    fun getAllByUser(user: UserEntity): List<MonitoredEndpointEntity>
    fun getByUserAndId(user: UserEntity, id: UUID): MonitoredEndpointEntity?
    fun removeByUserAndId(user: UserEntity, id: UUID): MonitoredEndpointEntity?
}
