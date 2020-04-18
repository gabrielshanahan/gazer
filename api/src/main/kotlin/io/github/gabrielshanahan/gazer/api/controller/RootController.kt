package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.api.model.AbstractModel
import io.github.gabrielshanahan.gazer.data.model.AbstractEntity
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.hateoas.server.mvc.add
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.hateoas.server.mvc.withRel


@RestController
class RootController {
    @GetMapping("/")
    fun root(): ResponseEntity<RepresentationModel<*>> {
        val model = RepresentationModel<RepresentationModel<*>>()

        linkTo<RootController> {
            root()
        } withRel IanaLinkRelations.SELF into model::add

        linkTo<MonitoredEndpointController> {
            getAll("")
        } withRel "monitoredEndpoints" into model::add

        return ResponseEntity.ok(model)
    }
}
