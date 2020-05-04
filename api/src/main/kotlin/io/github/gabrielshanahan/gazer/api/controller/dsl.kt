package io.github.gabrielshanahan.gazer.api.controller

import kotlin.reflect.KClass
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

/**
 * This action simply adds semantic meaning to the elvis operator in the context of this DSL. It is intended to be
 * used in conjunction with the authAndFind functions.
 *
 * @see MonitoredEndpointController.findOwned
 * @see MonitoringResultController.findOwned
 */
internal infix fun <R> R?.orWhenNotFound(action: () -> R): R = this ?: action()

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
