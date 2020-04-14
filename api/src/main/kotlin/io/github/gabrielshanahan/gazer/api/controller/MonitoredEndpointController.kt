package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.api.repository.UserRepository
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.data.model.User
import java.util.*
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
class MonitoredEndpointController(
    val userRepository: UserRepository,
    val endpointRepository: MonitoredEndpointRepository
) {

    class InvalidGazerTokenException :
        RuntimeException("Invalid GazerToken")
    class MonitoredEndpointNotFoundException(id: String) :
        RuntimeException("Monitored endpoint $id not found")
    class MonitoredEndpointForbidden(id: String) :
        RuntimeException("You do not have permission to access monitored endpoint $id")

    private fun <R> withToken(token: String, action: (User) -> R) =
    (userRepository.getByToken(token) ?: throw InvalidGazerTokenException()).let(action)

    private fun <R> ensuringOwnership(token: String, id: String, action: (User, MonitoredEndpoint) -> R) =
        withToken(token) { user ->
            endpointRepository.findById(UUID.fromString(id)).orElse(null)?.let { fetchedEndpoint ->
                if (fetchedEndpoint.user.id != user.id) {
                    throw MonitoredEndpointForbidden(id)
                }

                action(user, fetchedEndpoint)
            }
        }

    private infix fun <R> R?.orWhenNoneFound(action: () -> R) = this ?: action()

    @GetMapping("")
    fun findAll(
        @RequestHeader(value = "GazerToken") token: String
    ): List<MonitoredEndpoint> = withToken(token) { endpointRepository.getAllByUser(it) }

    /**
     * TODO: Add validation
     */
    @PostMapping("")
    fun create(
        @RequestHeader(value = "GazerToken") token: String,
        @RequestBody endpoint: MonitoredEndpoint
    ): MonitoredEndpoint = withToken(token) {
        endpoint.user = it
        endpointRepository.save(endpoint)
    }

    @GetMapping("/{id}")
    fun findById(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoredEndpoint = withToken(token) {
        endpointRepository.getByUserAndId(it, UUID.fromString(id)) ?: throw MonitoredEndpointNotFoundException(id)
    }

    @PutMapping("/{id}")
    fun replaceEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String,
        @RequestBody endpoint: MonitoredEndpoint
    ): MonitoredEndpoint = ensuringOwnership(token, id) { _, fetchedEndpoint ->
        fetchedEndpoint.copyFrom(endpoint)
        endpointRepository.save(fetchedEndpoint)
    } orWhenNoneFound { endpointRepository.save(endpoint) }

    @DeleteMapping("/{id}")
    fun deleteEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ) = ensuringOwnership(token, id) { _, _ ->
        endpointRepository.deleteById(UUID.fromString(id))
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }
}
