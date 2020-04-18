package io.github.gabrielshanahan.gazer.api.controller.response

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointCollectionModel
import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointModel
import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoredEndpointResourceAssembler
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

internal typealias ModelResponse = ResponseEntity<MonitoredEndpointModel>
internal typealias CollectionResponse = ResponseEntity<MonitoredEndpointCollectionModel>

@Component
internal class MonitoredEndpointResponseAssembler(
    val monitoredEndpointModelAssembler: MonitoredEndpointResourceAssembler
) {
    fun toCreatedResponse(endpointModel: MonitoredEndpointModel): ModelResponse =
        endpointModel into {
            ResponseEntity
                .created(it.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(it)
        }

    fun toUpdatedResponse(endpointModel: MonitoredEndpointModel): ModelResponse =
        endpointModel into {
            ResponseEntity.ok()
                .location(it.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(it)
        }

    fun toOkResponse(endpointModel: MonitoredEndpointModel): ModelResponse =
        endpointModel into {
            ResponseEntity.ok().body(it)
        }

    fun toOkResponse(endpointModels: MonitoredEndpointCollectionModel): CollectionResponse =
        endpointModels into {
            ResponseEntity.ok().body(it)
        }

    fun noContentResponse(): ModelResponse = ResponseEntity.noContent().build()
}
