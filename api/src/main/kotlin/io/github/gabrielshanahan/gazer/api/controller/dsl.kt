package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.exceptions.InvalidGazerTokenException
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoringResultForbidden
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.model.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.model.UserEntity
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import java.util.*
import kotlin.reflect.KClass

internal fun <R> AbstractController.withAuthedUser(token: String, action: (UserEntity) -> R) =
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

internal fun <R> MonitoringResultController.authAndFind(
    token: String,
    id: String,
    action: (MonitoringResultEntity) -> R
): R? = withAuthedUser(token) { user ->
    resultRepository
        .findById(UUID.fromString(id))
        .map { fetchedResult ->
            if (fetchedResult.monitoredEndpoint.user.id != user.id) {
                throw MonitoringResultForbidden(id)
            }
            action(fetchedResult)
        }.orElse(null)
}

internal infix fun <R> R?.orWhenNoneFound(action: () -> R): R = this ?: action()

internal class HyperlinkBuilder<C : Any>(val klass: KClass<C>) {

    internal inline fun <R : Any> selfLink(action: C.() -> R): Link =
        linkTo(methodOn(klass.java).action()).withSelfRel()

    internal inline fun <R : Any> link(action: C.() -> Pair<String, R>): Link =
        methodOn(klass.java).action().run { linkTo(second).withRel(first) }

    internal inline infix fun <R : Any> Link.andAfford(action: C.() -> R): Link =
        andAffordance(afford(methodOn(klass.java).action()))
}

internal inline fun <reified C : Any> hyperlinks(action: HyperlinkBuilder<C>.() -> Unit) =
    HyperlinkBuilder(C::class).action()
