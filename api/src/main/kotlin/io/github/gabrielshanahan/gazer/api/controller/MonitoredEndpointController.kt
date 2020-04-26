package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler
import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultResourceAssembler
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoredEndpointCollectionResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoredEndpointModelResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoredEndpointResponseAssembler
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultCollectionResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultResponseAssembler
import io.github.gabrielshanahan.gazer.api.exceptions.InvalidGazerTokenException
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointNotFoundException
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity
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

/**
 * Contains endpoints pertaining to MonitoredEndpoints
 */
@RestController
@RequestMapping("/monitoredEndpoints")
@Validated
class MonitoredEndpointController(
    val endpointRepository: MonitoredEndpointRepository,
    val validator: Validator,
    val resultRepository: MonitoringResultRepository,
    val resourceAssembler: MonitoredEndpointResourceAssembler,
    val responseAssembler: MonitoredEndpointResponseAssembler,
    val resultResourceAssembler: MonitoringResultResourceAssembler,
    val resultResponseAssembler: MonitoringResultResponseAssembler,
    userRepository: UserRepository
) : AbstractController(userRepository) {

    /**
     * If [token] is valid, return all MonitoredEndpoints owned by given user, otherwise throw
     * [InvalidGazerTokenException].
     *
     * @see withAuthedUser
     */
    @GetMapping("")
    fun getAll(
        @RequestHeader(value = "GazerToken") token: String
    ): MonitoredEndpointCollectionResponse = withAuthedUser(token) { user ->
        endpointRepository
            .getAllByUser(user)
            .map(MonitoredEndpointEntity::asModel).toMutableList() into
            resourceAssembler::toCollectionModel into responseAssembler::toOkResponse
    }

    /**
     * Checks [token] validity, then returns MonitoredEndpoint given by [id]. Throws one of
     * [InvalidGazerTokenException], [io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden] or
     * [MonitoredEndpointNotFoundException], depending on the situation.
     *
     * @see withAuthedUser
     * @see authAndFind
     */
    @GetMapping("/{id}")
    fun getById(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoredEndpointModelResponse = authAndFind(token, id) { endpoint ->
        endpoint into resourceAssembler::toModel into responseAssembler::toOkResponse
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }

    /**
     * Checks [token] validity, then returns [limit] MonitoringResults related to MonitoredEndpoint given by [id],
     * with the most recent being returned first. If no [limit] is specified, returns all MonitoringResults.
     *
     * Throws one of [InvalidGazerTokenException],
     * [io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden] or
     * [MonitoredEndpointNotFoundException], depending on the situation.
     *
     * @see withAuthedUser
     * @see authAndFind
     */
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

    /**
     * Checks [token] validity, then creates new MonitoredEndpoint.
     *
     * @see withAuthedUser
     */
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

    /**
     * Checks [token] validity and either creates or updates the given [id]. If the MonitoredEndpoint ends up being
     * created, the ID is not preserved. Throws one of [InvalidGazerTokenException] or
     * [io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden], depending on the situation.
     *
     * @see withAuthedUser
     * @see authAndFind
     */
    @PutMapping("/{id}")
    fun replaceEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String,
        @Valid @RequestBody endpoint: MonitoredEndpoint
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

    /**
     * Checks [token] validity, then deletes the given [id].
     *
     * Throws one of [InvalidGazerTokenException],
     * [io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden] or
     * [MonitoredEndpointNotFoundException], depending on the situation.
     */
    @DeleteMapping("/{id}")
    fun deleteEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoredEndpointModelResponse = authAndFind(token, id) {
        endpointRepository.deleteById(UUID.fromString(id))
        responseAssembler.noContentResponse()
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }
}
