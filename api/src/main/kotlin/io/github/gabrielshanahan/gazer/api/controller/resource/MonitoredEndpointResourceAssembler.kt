package io.github.gabrielshanahan.gazer.api.controller.resource

import io.github.gabrielshanahan.gazer.api.controller.MonitoredEndpointController
import io.github.gabrielshanahan.gazer.api.controller.link
import io.github.gabrielshanahan.gazer.api.controller.selfLink
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

internal typealias MonitoredEndpointModel = EntityModel<MonitoredEndpoint>
internal typealias MonitoredEndpointCollectionModel = CollectionModel<MonitoredEndpointModel>

@Component
class MonitoredEndpointResourceAssembler :
    RepresentationModelAssemblerSupport<MonitoredEndpoint, MonitoredEndpointModel>(
        MonitoredEndpointController::class.java,
        EntityModel.of(MonitoredEndpoint()).javaClass // Is there another way?
    ) {
    override fun instantiateModel(endpoint: MonitoredEndpoint): MonitoredEndpointModel = EntityModel.of(endpoint)

    override fun toModel(endpoint: MonitoredEndpoint): MonitoredEndpointModel =
        createModelWithId(endpoint.id!!, endpoint) into {
            it.add(
                link {
                    "monitoredEndpoints" to findAll("")
                }
            )
        }

    fun toModel(endpointEntity: MonitoredEndpointEntity): EntityModel<MonitoredEndpoint> =
        endpointEntity into MonitoredEndpointEntity::asModel into this::toModel

    override fun toCollectionModel(
        endpoints: MutableIterable<MonitoredEndpoint>
    ): CollectionModel<MonitoredEndpointModel> = super.toCollectionModel(endpoints) into {
        it.add(
            selfLink {
                findAll("")
            }
        )
    }
}
