package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.exceptions.InvalidGazerTokenException
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.model.UserEntity
import java.util.*

internal fun <R> MonitoredEndpointController.withAuthedUser(token: String, action: (UserEntity) -> R) =
    (userRepository.getByToken(token) ?: throw InvalidGazerTokenException())
        .let(action)

internal fun <R> MonitoredEndpointController.authAndFind(
    token: String,
    id: String,
    action: (MonitoredEndpointEntity) -> R
): R? = withAuthedUser(token) { user ->
    endpointRepository
        .findById(UUID.fromString(id))
        .map { fetchedEndpoint ->
            if (fetchedEndpoint.user.id != user.id) {
                throw MonitoredEndpointForbidden(id)
            }
            action(fetchedEndpoint)
        }.orElse(null)
}

internal infix fun <R> R?.orWhenNoneFound(action: () -> R): R = this ?: action()
