package io.github.gabrielshanahan.gazer.api.controller.resources

import io.github.gabrielshanahan.gazer.api.controller.MonitoredEndpointController
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Component

@Component
internal class MonitoredEndpointModelAssembler :
    RepresentationModelAssembler<MonitoredEndpoint, EntityModel<MonitoredEndpoint>> {

    override fun toModel(endpoint: MonitoredEndpoint): EntityModel<MonitoredEndpoint> {
        return EntityModel.of(endpoint,
            linkTo(
                WebMvcLinkBuilder
                    .methodOn(MonitoredEndpointController::class.java)
                    .findById("", endpoint.id.toString())
            ).withSelfRel(),
            linkTo(
                WebMvcLinkBuilder
                    .methodOn(MonitoredEndpointController::class.java)
                    .findAll("")
            ).withRel("monitoredEndpoints")
        )
    }
}
