package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.entity.UserEntity
import java.util.*
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/** Used to access MonitoringResults based on the user that is "logged in" and possibly a MonitoringEndpoint parent */
@Repository
interface MonitoringResultRepository : JpaRepository<MonitoringResultEntity, UUID> {
    fun getAllByMonitoredEndpointUserOrderByCheckedDesc(user: UserEntity): List<MonitoringResultEntity>

    fun getAllByMonitoredEndpointOrderByCheckedDesc(
        monitoredEndpoint: MonitoredEndpointEntity
    ): List<MonitoringResultEntity>

    fun getAllByMonitoredEndpoint(
        monitoredEndpoint: MonitoredEndpointEntity,
        pageable: Pageable
    ): List<MonitoringResultEntity>
}
