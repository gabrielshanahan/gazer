package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.entity.UserEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/** Used to access MonitoredEndpoint based on the user that is "logged in" */
@Repository
interface MonitoredEndpointRepository : JpaRepository<MonitoredEndpointEntity, UUID> {
    fun getAllByUser(user: UserEntity): List<MonitoredEndpointEntity>
    fun getByUserAndId(user: UserEntity, id: UUID): MonitoredEndpointEntity?
}
