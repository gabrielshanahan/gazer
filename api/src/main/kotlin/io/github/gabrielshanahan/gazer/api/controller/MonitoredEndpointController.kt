package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.service.response.MonitoredEndpointCollectionResponse
import io.github.gabrielshanahan.gazer.api.service.response.MonitoredEndpointModelResponse
import io.github.gabrielshanahan.gazer.api.service.response.MonitoringResultCollectionResponse
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointNotFoundException
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.User
import io.github.gabrielshanahan.gazer.api.security.Authenticated
import io.github.gabrielshanahan.gazer.api.security.UserAuthentication
import io.github.gabrielshanahan.gazer.api.service.MonitoredEndpointResponseService
import io.github.gabrielshanahan.gazer.api.service.MonitoredEndpointService
import io.github.gabrielshanahan.gazer.api.service.MonitoringResultResponseService
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.func.into
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.groups.Default
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
    val responseService: MonitoredEndpointResponseService,
    val resultResponseService: MonitoringResultResponseService,
    val monitoredEndpointService: MonitoredEndpointService
) : UserAuthentication {

    override lateinit var user: User

    /**
     * Return all MonitoredEndpoints owned by authenticated user.
     */
    @Authenticated
    @GetMapping("")
    fun getAll(): MonitoredEndpointCollectionResponse = with(monitoredEndpointService) {
        findAll() into responseService::ok
    }

    /**
     * Returns MonitoredEndpoint given by [id].
     *
     * Throws one of [io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden] or
     * [MonitoredEndpointNotFoundException], depending on the situation.
     */
    @Authenticated
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): MonitoredEndpointModelResponse = with(monitoredEndpointService) {
        findOwn(id) orWhenNotFound {
            throw MonitoredEndpointNotFoundException(id)
        } into responseService::ok
    }

    /**
     * Returns [limit] MonitoringResults related to MonitoredEndpoint given by [id],
     * with the most recent being returned first. If no [limit] is specified, returns all MonitoringResults.
     *
     * Throws one of [io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden] or
     * [MonitoredEndpointNotFoundException], depending on the situation.
     */
    @Authenticated
    @GetMapping("/{id}/monitoringResults")
    fun getRelatedResults(
        @PathVariable id: String,
        @RequestParam @Min(1) limit: Int? = null
    ): MonitoringResultCollectionResponse = with(monitoredEndpointService) {
        findRelatedTo(id, limit) orWhenNotFound {
            throw MonitoredEndpointNotFoundException(id)
        } into resultResponseService::buildOk
    }

    /**
     * Creates new MonitoredEndpoint.
     *
     * @see withAuthedUser
     */
    @Authenticated
    @Validated(Default::class, OnCreate::class)
    @PostMapping("")
    fun createEndpoint(
        @Valid @RequestBody endpoint: MonitoredEndpoint
    ): MonitoredEndpointModelResponse = with(monitoredEndpointService) {
        create(endpoint) into responseService::created
    }

    /**
     * Either creates or updates the given [id]. If the MonitoredEndpoint ends up being created, the ID is not
     * preserved.
     *
     * Throws [io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden].
     */
    @Authenticated
    @PutMapping("/{id}")
    fun replaceEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String,
        @Valid @RequestBody endpoint: MonitoredEndpoint
    ): MonitoredEndpointModelResponse = with(monitoredEndpointService) {
        updateIfFound(id, endpoint)?.let { updatedEndpoint ->
            updatedEndpoint into responseService::updated
        } orWhenNotFound {
            // Is there a better way?
            validate(endpoint)
            create(endpoint) into responseService::created
        }
    }

    /**
     * Deletes the given [id].
     *
     * Throws one of [io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden] or
     * [MonitoredEndpointNotFoundException], depending on the situation.
     */
    @Authenticated
    @DeleteMapping("/{id}")
    fun deleteEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoredEndpointModelResponse = with(monitoredEndpointService) {
        delete(id) orWhenNotFound { throw MonitoredEndpointNotFoundException(id) }
        responseService.noContent()
    }
}
