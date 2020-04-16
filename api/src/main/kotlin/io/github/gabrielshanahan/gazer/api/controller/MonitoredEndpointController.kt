package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.dto.MonitoredEndpointDTO
import io.github.gabrielshanahan.gazer.api.dto.asDTO
import io.github.gabrielshanahan.gazer.api.exceptions.InvalidGazerTokenException
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointNotFoundException
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.data.model.User
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.func.into
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator
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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/monitoredEndpoints")
@Validated
class MonitoredEndpointController(
    val userRepository: UserRepository,
    val endpointRepository: MonitoredEndpointRepository,
    val validator: Validator
) {

    @GetMapping("")
    fun findAll(
        @RequestHeader(value = "GazerToken") token: String
    ): List<MonitoredEndpointDTO> = withToken(token) {
        endpointRepository
            .getAllByUser(it)
            .map(MonitoredEndpoint::asDTO)
    }

    @Validated(Default::class, OnCreate::class)
    @PostMapping("")
    fun create(
        @RequestHeader(value = "GazerToken") token: String,
        @Valid @RequestBody endpoint: MonitoredEndpointDTO
    ): MonitoredEndpointDTO = withToken(token) {

        MonitoredEndpoint(
            name = endpoint.name!!,
            url = endpoint.url!!,
            monitoredInterval = endpoint.monitoredInterval!!,
            user = it
        ) into endpointRepository::save into MonitoredEndpoint::asDTO
    }

    @GetMapping("/{id}")
    fun findById(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoredEndpointDTO = withToken(token) {
        endpointRepository.getByUserAndId(it, UUID.fromString(id)) ?: throw MonitoredEndpointNotFoundException(id)
    }.asDTO()

    @PutMapping("/{id}")
    fun replaceEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String,
        @RequestBody endpoint: MonitoredEndpointDTO
    ): MonitoredEndpointDTO = ensuringOwnership(token, id) { _, fetchedEndpoint ->
        endpoint transferTo fetchedEndpoint into endpointRepository::save into MonitoredEndpoint::asDTO
    } orWhenNoneFound {
        // Is there a better way?
        val violations = validator.validate(endpoint, Default::class.java, OnCreate::class.java)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
        create(token, endpoint)
    }

    @DeleteMapping("/{id}")
    fun deleteEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ) = ensuringOwnership(token, id) { _, _ ->
        endpointRepository.deleteById(UUID.fromString(id))
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }
}

private fun <R> MonitoredEndpointController.withToken(token: String, action: (User) -> R) =
    (userRepository.getByToken(token) ?: throw InvalidGazerTokenException())
        .let(action)

private fun <R> MonitoredEndpointController.ensuringOwnership(
    token: String,
    id: String,
    action: (User, MonitoredEndpoint) -> R
) = withToken(token) { user ->
        endpointRepository
            .findById(UUID.fromString(id))
            .orElse(null)
            ?.let { fetchedEndpoint ->
                if (fetchedEndpoint.user.id != user.id) {
                    throw MonitoredEndpointForbidden(id)
                }
                action(user, fetchedEndpoint)
            }
    }

private infix fun <R> R?.orWhenNoneFound(action: () -> R) = this ?: action()
