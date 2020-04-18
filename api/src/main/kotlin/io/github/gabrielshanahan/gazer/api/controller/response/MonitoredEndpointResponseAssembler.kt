package io.github.gabrielshanahan.gazer.api.controller.response

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointCollectionModel
import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointModel
import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

internal typealias MonitoredEndpointModelResponse = ResponseEntity<MonitoredEndpointModel>
internal typealias MonitoredEndpointCollectionResponse = ResponseEntity<MonitoredEndpointCollectionModel>

@Component
internal class MonitoredEndpointResponseAssembler(
    val monitoredEndpointModelAssembler: MonitoredEndpointResourceAssembler
) {
    fun toCreatedResponse(endpointModel: MonitoredEndpointModel): MonitoredEndpointModelResponse =
        endpointModel into {
            ResponseEntity
                .created(it.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(it)
        }

    fun toUpdatedResponse(endpointModel: MonitoredEndpointModel): MonitoredEndpointModelResponse =
        endpointModel into {
            ResponseEntity.ok()
                .location(it.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(it)
        }

    fun toOkResponse(endpointModel: MonitoredEndpointModel): MonitoredEndpointModelResponse =
        endpointModel into {
            ResponseEntity.ok().body(it)
        }

    fun toOkResponse(endpointModels: MonitoredEndpointCollectionModel): MonitoredEndpointCollectionResponse =
        endpointModels into {
            ResponseEntity.ok().body(it)
        }

    fun noContentResponse(): MonitoredEndpointModelResponse = ResponseEntity.noContent().build()
}
