package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.hateoas.server.mvc.withRel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * The root endpoint, returning only links to supported operations. Intended for self-discovery purposes.
 */
@RestController
class RootController {

    /**
     * The root endpoint, returning only links to supported operations. Intended for self-discovery purposes.
     */
    @GetMapping("/")
    fun root(): ResponseEntity<RepresentationModel<*>> {
        val model = RepresentationModel<RepresentationModel<*>>()

        linkTo<RootController> {
            root()
        } withRel IanaLinkRelations.SELF into model::add

        linkTo<MonitoredEndpointController> {
            getAll()
        } withRel "monitoredEndpoints" into model::add

        linkTo<MonitoringResultController> {
            getAll()
        } withRel "monitoringResults" into model::add

        return ResponseEntity.ok(model)
    }
}
