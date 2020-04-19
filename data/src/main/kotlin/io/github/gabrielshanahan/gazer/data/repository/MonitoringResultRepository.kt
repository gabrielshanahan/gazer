package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.model.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.model.UserEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MonitoringResultRepository : JpaRepository<MonitoringResultEntity, UUID> {
    fun getAllByMonitoredEndpointUser(user: UserEntity): List<MonitoringResultEntity>
    fun getAllByMonitoredEndpoint(monitoredEndpoint: MonitoredEndpointEntity): List<MonitoringResultEntity>
}
