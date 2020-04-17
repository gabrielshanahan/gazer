package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.controller.resources.MonitoredEndpointModelAssembler
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointNotFoundException
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asDTO
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import io.github.gabrielshanahan.gazer.func.into
import java.util.UUID
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.Validator
import javax.validation.groups.Default
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
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
internal class MonitoredEndpointController(
    val userRepository: UserRepository,
    val endpointRepository: MonitoredEndpointRepository,
    val validator: Validator,
    val resourceAssembler: MonitoredEndpointModelAssembler
) {

    @GetMapping("")
    fun findAll(
        @RequestHeader(value = "GazerToken") token: String
    ): CollectionModel<EntityModel<MonitoredEndpoint>> = withToken(token) { user ->
        endpointRepository
            .getAllByUser(user)
            .map { it.asDTO() into resourceAssembler::toModel }
    } into {
        CollectionModel.of(
            it,
            WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(MonitoredEndpointController::class.java).findAll("")
            ).withSelfRel()
        )
    }

    @Validated(Default::class, OnCreate::class)
    @PostMapping("")
    fun create(
        @RequestHeader(value = "GazerToken") token: String,
        @Valid @RequestBody endpoint: MonitoredEndpoint
    ): EntityModel<MonitoredEndpoint> = withToken(token) {
        MonitoredEndpointEntity(
            name = endpoint.name!!,
            url = endpoint.url!!,
            monitoredInterval = endpoint.monitoredInterval!!,
            user = it
        ) into endpointRepository::save into MonitoredEndpointEntity::asDTO into resourceAssembler::toModel
    }

    @GetMapping("/{id}")
    fun findById(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): EntityModel<MonitoredEndpoint> = ensuringOwnership(token, id) { _, endpoint ->
        endpoint.asDTO() into resourceAssembler::toModel
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }

    @PutMapping("/{id}")
    fun replaceEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String,
        @RequestBody endpoint: MonitoredEndpoint
    ): EntityModel<MonitoredEndpoint> = ensuringOwnership(token, id) { _, fetchedEndpoint ->
        endpoint transferTo fetchedEndpoint into endpointRepository::save into
            MonitoredEndpointEntity::asDTO into resourceAssembler::toModel
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
