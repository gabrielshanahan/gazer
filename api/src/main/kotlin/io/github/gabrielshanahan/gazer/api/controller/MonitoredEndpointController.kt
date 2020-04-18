package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler
import io.github.gabrielshanahan.gazer.api.controller.response.ModelResponse
import io.github.gabrielshanahan.gazer.api.controller.response.MonitoredEndpointResponseAssembler
import io.github.gabrielshanahan.gazer.api.exceptions.MonitoredEndpointNotFoundException
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asModel
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
    val resourceAssembler: MonitoredEndpointResourceAssembler,
    val responseAssembler: MonitoredEndpointResponseAssembler
) {

    @GetMapping("")
    fun findAll(
        @RequestHeader(value = "GazerToken") token: String
    ) = withAuthedUser(token) { user ->
        endpointRepository
            .getAllByUser(user)
            .map(MonitoredEndpointEntity::asModel).toMutableList() into
            resourceAssembler::toCollectionModel into responseAssembler::toOkResponse

//        endpointRepository
//            .getAllByUser(user)
//            .map { it.asModel() into resourceAssembler::toModel }
//    } into {
//        CollectionModel.of(
//            it,
//            WebMvcLinkBuilder.linkTo(
//                WebMvcLinkBuilder.methodOn(MonitoredEndpointController::class.java).findAll("")
//            ).withSelfRel()
//        )
    }

    @GetMapping("/{id}")
    fun findById(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): ModelResponse = authAndFind(token, id) { endpoint ->
        endpoint into resourceAssembler::toModel into responseAssembler::toOkResponse
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }

    @Validated(Default::class, OnCreate::class)
    @PostMapping("")
    fun create(
        @RequestHeader(value = "GazerToken") token: String,
        @Valid @RequestBody endpoint: MonitoredEndpoint
    ): ModelResponse = withAuthedUser(token) { user ->
        MonitoredEndpointEntity(
            name = endpoint.name!!,
            url = endpoint.url!!,
            monitoredInterval = endpoint.monitoredInterval!!,
            user = user
        ) into endpointRepository::save into resourceAssembler::toModel into responseAssembler::toCreatedResponse
    }

    @PutMapping("/{id}")
    fun replaceEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String,
        @RequestBody endpoint: MonitoredEndpoint
    ): ModelResponse = authAndFind(token, id) { fetchedEndpoint ->
        endpoint transferTo fetchedEndpoint into
            endpointRepository::save into resourceAssembler::toModel into responseAssembler::toUpdatedResponse
    } orWhenNoneFound {
        // Is there a better way?
        validator.validate(endpoint, Default::class.java, OnCreate::class.java) into {
            if (it.isNotEmpty()) {
                throw ConstraintViolationException(it)
            }
        }

        create(token, endpoint)
    }

    @DeleteMapping("/{id}")
    fun deleteEndpoint(
        @RequestHeader(value = "GazerToken") token: String,
        @PathVariable id: String
    ): ModelResponse = authAndFind(token, id) {
        endpointRepository.deleteById(UUID.fromString(id))
        responseAssembler.noContentResponse()
    } orWhenNoneFound { throw MonitoredEndpointNotFoundException(id) }
}
