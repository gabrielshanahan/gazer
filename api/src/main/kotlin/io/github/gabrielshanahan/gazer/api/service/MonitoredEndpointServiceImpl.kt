package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.MonitoringResult
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.api.security.UserAuthentication
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.func.into
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.Validator
import javax.validation.groups.Default
import org.springframework.stereotype.Service

@Service
class MonitoredEndpointServiceImpl(
    val endpointRepository: MonitoredEndpointRepository,
    val resultService: MonitoringResultService,
    val validator: Validator
) : MonitoredEndpointService {
    private fun <R> UserAuthentication.withOwn(id: String, action: (MonitoredEndpointEntity) -> R): R? =
        endpointRepository
            .findById(UUID.fromString(id))
            .map { fetchedEndpoint ->
                if (fetchedEndpoint.user.id != user.id) {
                    throw MonitoredEndpointForbidden(id)
                }
                action(fetchedEndpoint)
            }.orElse(null)

    override fun UserAuthentication.findAll(): List<MonitoredEndpoint> = endpointRepository
        .getAllByUser(user.asEntity())
        .map(MonitoredEndpointEntity::asModel)

    override fun UserAuthentication.findOwn(id: String): MonitoredEndpoint? = withOwn(id) { it.asModel() }

    override fun UserAuthentication.findRelatedTo(id: String, limit: Int?): List<MonitoringResult>? = withOwn(id) {
        resultService.findRelatedTo(it, limit)
    }

    override fun UserAuthentication.create(endpoint: MonitoredEndpoint): MonitoredEndpoint = MonitoredEndpointEntity(
        name = endpoint.name!!,
        url = endpoint.url!!,
        monitoredInterval = endpoint.monitoredInterval!!,
        user = user.asEntity()
    ) into endpointRepository::save into MonitoredEndpointEntity::asModel

    override fun UserAuthentication.updateIfFound(id: String, endpoint: MonitoredEndpoint): MonitoredEndpoint? =
        withOwn(id) { fetchedEndpoint ->
            endpoint transferTo fetchedEndpoint into endpointRepository::save into MonitoredEndpointEntity::asModel
        }

    override fun validateForCreation(endpoint: MonitoredEndpoint) {
        val result = validator.validate(
            endpoint,
            Default::class.java,
            OnCreate::class.java
        )

        if (result.isNotEmpty()) {
            throw ConstraintViolationException(result)
        }
    }

    override fun UserAuthentication.delete(id: String): Unit? = withOwn(id) {
        endpointRepository.deleteById(UUID.fromString(id))
    }
}
