package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.model.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.model.UserEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

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
