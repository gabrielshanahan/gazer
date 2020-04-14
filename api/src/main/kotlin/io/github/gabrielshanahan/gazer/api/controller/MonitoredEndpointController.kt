package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.api.repository.UserRepository
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/monitoredEndpoints")
class MonitoredEndpointController(
    val userRepository: UserRepository,
    val endpointRepository: MonitoredEndpointRepository
) {

    @GetMapping("")
    fun findAll(@RequestHeader(value = "GazerToken") token: String): List<MonitoredEndpoint> =
        userRepository.getByToken(token)?.let { endpointRepository.getAllByUser(it) } ?: emptyList()
}
