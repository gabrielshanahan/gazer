package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.api.exceptions.MonitoringResultForbidden
import io.github.gabrielshanahan.gazer.api.model.MonitoringResult
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.api.security.UserAuthentication
import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import java.util.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class MonitoringResultService(val resultRepository: MonitoringResultRepository) {

    fun UserAuthentication.findAll(): List<MonitoringResult> = resultRepository
        .getAllByMonitoredEndpointUserOrderByCheckedDesc(user.asEntity())
        .map(MonitoringResultEntity::asModel)

    private fun <R> UserAuthentication.withOwn(id: String, action: (MonitoringResultEntity) -> R): R? =
        resultRepository
            .findById(UUID.fromString(id))
            .map { fetchedResult ->
                if (fetchedResult.monitoredEndpoint.user.id != user.id) {
                    throw MonitoringResultForbidden(id)
                }
                action(fetchedResult)
            }.orElse(null)

    fun UserAuthentication.findOwn(id: String): MonitoringResult? = withOwn(id) { it.asModel() }

    fun findRelatedTo(endpoint: MonitoredEndpointEntity, limit: Int?): List<MonitoringResult>? {
        val results = if (limit != null) {
            resultRepository.getAllByMonitoredEndpoint(
                endpoint,
                PageRequest.of(0, limit, Sort.by("checked").descending())
            )
        } else {
            resultRepository.getAllByMonitoredEndpointOrderByCheckedDesc(endpoint)
        }

        return results.map(MonitoringResultEntity::asModel)
    }
}
