package io.github.gabrielshanahan.gazer.api.controller.resource

import io.github.gabrielshanahan.gazer.api.controller.MonitoredEndpointController
import io.github.gabrielshanahan.gazer.api.controller.RootController
import io.github.gabrielshanahan.gazer.api.controller.hyperlinks
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

internal typealias MonitoredEndpointModel = EntityModel<MonitoredEndpoint>
internal typealias MonitoredEndpointCollectionModel = CollectionModel<MonitoredEndpointModel>

@Component
class MonitoredEndpointResourceAssembler :
    RepresentationModelAssembler<MonitoredEndpoint, MonitoredEndpointModel> {

    override fun toModel(endpoint: MonitoredEndpoint): MonitoredEndpointModel =
        EntityModel.of(endpoint).apply {
            hyperlinks<MonitoredEndpointController> {
                add(
                    selfLink {
                        getById("", endpoint.id.toString())
                    } andAfford {
                        replaceEndpoint("", endpoint.id.toString(), MonitoredEndpoint())
                    } andAfford {
                        deleteEndpoint("", endpoint.id.toString())
                    },
                    link {
                        "monitoringResults" to getRelatedResults("", endpoint.id.toString())
                    },
                    link {
                        "monitoredEndpoints" to getAll("")
                    }
                )
            }
        }

    fun toModel(endpointEntity: MonitoredEndpointEntity): EntityModel<MonitoredEndpoint> =
        endpointEntity into MonitoredEndpointEntity::asModel into ::toModel

    override fun toCollectionModel(endpoints: MutableIterable<MonitoredEndpoint>): MonitoredEndpointCollectionModel =
        CollectionModel.of(endpoints.map(::toModel)).apply {
            hyperlinks<MonitoredEndpointController> {
                add(selfLink {
                    getAll("")
                } andAfford {
                    createEndpoint("", MonitoredEndpoint())
                })
            }

            hyperlinks<RootController> {
                add(link {
                    "root" to root()
                })
            }
        }
}
