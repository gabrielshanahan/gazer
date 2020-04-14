package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.api.repository.UserRepository
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.data.model.User
import java.lang.RuntimeException
import java.util.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/monitoredEndpoints")
class MonitoredEndpointController(
    val userRepository: UserRepository,
    val endpointRepository: MonitoredEndpointRepository
) {

    class InvalidGazerTokenException : RuntimeException("Invalid GazerToken")
    class MonitoredEndpointNotFoundException(id: String) : RuntimeException("Monitored endpoint $id not found")

    private fun <R> withToken(token: String, action: (User) -> R) =
        userRepository.getByToken(token)?.let(action) ?: throw InvalidGazerTokenException()

    @GetMapping("")
    fun findAll(@RequestHeader(value = "GazerToken") token: String): List<MonitoredEndpoint> = withToken(token) {
        endpointRepository.getAllByUser(it)
    }

    @GetMapping("/{id}")
    fun findById(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): MonitoredEndpoint = withToken(token) {
        endpointRepository.getByUserAndId(it, UUID.fromString(id)) ?: throw MonitoredEndpointNotFoundException(id)
    }
}
