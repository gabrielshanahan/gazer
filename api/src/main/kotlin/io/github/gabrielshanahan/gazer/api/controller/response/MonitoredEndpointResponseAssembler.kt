package io.github.gabrielshanahan.gazer.api.controller.response

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointCollectionModel
import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointModel
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/** Space-saver */
internal typealias MonitoredEndpointModelResponse = ResponseEntity<MonitoredEndpointModel>

/** Space-saver */
internal typealias MonitoredEndpointCollectionResponse = ResponseEntity<MonitoredEndpointCollectionModel>

/**
 * Responsible for constructing the response for MonitoredEndpoint endpoints. A response contains a
 * [resource][io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler] along with
 * optional headers, HTTP status code, etc.
 *
 * @see io.github.gabrielshanahan.gazer.api.controller.MonitoredEndpointController
 * @see io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler
 */
@Component
class MonitoredEndpointResponseAssembler {

    /**
     * Adds appropriate Location header and 201 HTTP status
     */
    fun toCreatedResponse(endpointModel: MonitoredEndpointModel): MonitoredEndpointModelResponse =
        endpointModel into {
            ResponseEntity
                .created(it.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(it)
        }

    /**
     * Adds appropriate Location header and 200 HTTP status
     */
    fun toUpdatedResponse(endpointModel: MonitoredEndpointModel): MonitoredEndpointModelResponse =
        endpointModel into {
            ResponseEntity.ok()
                .location(it.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(it)
        }

    /**
     * Adds 200 HTTP status
     */
    fun toOkResponse(endpointModel: MonitoredEndpointModel): MonitoredEndpointModelResponse =
        endpointModel into {
            ResponseEntity.ok().body(it)
        }

    /**
     * Adds 200 HTTP status
     */
    fun toOkResponse(endpointModels: MonitoredEndpointCollectionModel): MonitoredEndpointCollectionResponse =
        endpointModels into {
            ResponseEntity.ok().body(it)
        }

    /**
     * Adds 204 HTTP status
     */
    fun noContentResponse(): MonitoredEndpointModelResponse = ResponseEntity.noContent().build()
}
