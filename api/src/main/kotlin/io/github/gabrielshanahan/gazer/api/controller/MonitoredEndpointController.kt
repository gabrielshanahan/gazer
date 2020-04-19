package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler
import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultResourceAssembler
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoredEndpointCollectionResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoredEndpointModelResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoredEndpointResponseAssembler
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultCollectionResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultResponseAssembler
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointNotFoundException
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.model.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.func.into
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator
import javax.validation.constraints.Min
import javax.validation.groups.Default
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/monitoredEndpoints")
@Validated
internal class MonitoredEndpointController(
    val endpointRepository: MonitoredEndpointRepository,
    val validator: Validator,
    val resourceAssembler: MonitoredEndpointResourceAssembler,
    val responseAssembler: MonitoredEndpointResponseAssembler,
    val resultRepository: MonitoringResultRepository,
    val resultResourceAssembler: MonitoringResultResourceAssembler,
    val resultResponseAssembler: MonitoringResultResponseAssembler,
    userRepository: UserRepository
) : AbstractController(userRepository) {

    @GetMapping("")
    fun getAll(
        @RequestHeader(value = "GazerToken") token: String
    ): MonitoredEndpointCollectionResponse = withAuthedUser(token) { user ->
        endpointRepository
            .getAllByUser(user)
            .map(MonitoredEndpointEntity::asModel).toMutableList() into
            resourceAssembler::toCollectionModel into responseAssembler::toOkResponse
    }

    @GetMapping("/{id}")
    fun getById(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoredEndpointModelResponse = authAndFind(token, id) { endpoint ->
        endpoint into resourceAssembler::toModel into responseAssembler::toOkResponse
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }

    @GetMapping("/{id}/monitoringResults")
    fun getRelatedResults(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String,
        @RequestParam @Min(1) limit: Int? = null
    ): MonitoringResultCollectionResponse = authAndFind(token, id) { endpoint ->
        val results = if (limit != null) {
            resultRepository.getAllByMonitoredEndpoint(
                endpoint,
                PageRequest.of(0, limit, Sort.by("checked").descending())
            )
        } else {
            resultRepository.getAllByMonitoredEndpointOrderByCheckedDesc(endpoint)
        }

        results.map(MonitoringResultEntity::asModel).toMutableList() into
            resultResourceAssembler::toCollectionModel into resultResponseAssembler::toOkResponse
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }

    @Validated(Default::class, OnCreate::class)
    @PostMapping("")
    fun createEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @Valid @RequestBody endpoint: MonitoredEndpoint
    ): MonitoredEndpointModelResponse = withAuthedUser(token) { user ->
        MonitoredEndpointEntity(
            name = endpoint.name!!,
            url = endpoint.url!!,
            monitoredInterval = endpoint.monitoredInterval!!,
            user = user
        ) into endpointRepository::save into resourceAssembler::toModel into responseAssembler::toCreatedResponse
    }

    @PutMapping("/{id}")
    fun replaceEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String,
        @RequestBody endpoint: MonitoredEndpoint
    ): MonitoredEndpointModelResponse = authAndFind(token, id) { fetchedEndpoint ->
        endpoint transferTo fetchedEndpoint into
            endpointRepository::save into resourceAssembler::toModel into responseAssembler::toUpdatedResponse
    } orWhenNoneFound {
        // Is there a better way?
        validator.validate(endpoint, Default::class.java, OnCreate::class.java) into {
            if (it.isNotEmpty()) {
                throw ConstraintViolationException(it)
            }
        }

        createEndpoint(token, endpoint)
    }

    @DeleteMapping("/{id}")
    fun deleteEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoredEndpointModelResponse = authAndFind(token, id) {
        endpointRepository.deleteById(UUID.fromString(id))
        responseAssembler.noContentResponse()
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }
}
