package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.exceptions.InvalidGazerTokenException
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointForbidden
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoringResultForbidden
import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.entity.UserEntity
import java.util.*
import kotlin.reflect.KClass
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

/**
 * Runs [action] if [token] represents a valid user, otherwise throws [InvalidGazerTokenException].
 *
 * @see MonitoredEndpointController
 * @See MonitoringResultController
 */
internal fun <R> AbstractController.withAuthedUser(token: String, action: (UserEntity) -> R) =
    (userRepository.getByToken(token) ?: throw InvalidGazerTokenException())
        .let(action)

/**
 * Runs [action] if [withAuthedUser] confirms [token] and [id] represents a MonitoredEndpoint owned by the given user.
 * Returns null if no such id is found. Throws [MonitoredEndpointForbidden] if endpoint is found, but is owned by
 * different user.
 *
 * @see orWhenNoneFound
 * @see MonitoredEndpointController
 */
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

/**
 * Runs [action] if [withAuthedUser] confirms [token] and [id] represents a MonitoringResult owned by the given user.
 * Returns null if no such id is found. Throws [MonitoringResultForbidden] if endpoint is found, but is owned by
 * different user.
 *
 * @see orWhenNoneFound
 * @See MonitoringResultController
 */
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

/**
 * This action simply adds semantic meaning to the elvis operator in the context of this DSL. It is intended to be
 * used in conjunction with the authAndFind functions.
 *
 * @see MonitoredEndpointController.authAndFind
 * @see MonitoringResultController.authAndFind
 */
internal infix fun <R> R?.orWhenNoneFound(action: () -> R): R = this ?: action()

/**
 * Exposes DSL methods specific to links and affordances, used when constructing resources.
 *
 * @see io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler
 * @see io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultResourceAssembler
 */
internal class HyperlinkBuilder<C : Any>(val klass: KClass<C>) {

    internal inline fun <R : Any> selfLink(action: C.() -> R): Link =
        linkTo(methodOn(klass.java).action()).withSelfRel()

    internal inline fun <R : Any> link(action: C.() -> Pair<String, R>): Link =
        methodOn(klass.java).action().run { linkTo(second).withRel(first) }

    internal inline infix fun <R : Any> Link.andAfford(action: C.() -> R): Link =
        andAffordance(afford(methodOn(klass.java).action()))
}

/**
 * Allows the links and affordances dsl to be used in [block].
 *
 * @see io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler
 * @see io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultResourceAssembler
 */
internal inline fun <reified C : Any> hyperlinks(block: HyperlinkBuilder<C>.() -> Unit) =
    HyperlinkBuilder(C::class).block()
