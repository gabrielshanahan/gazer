package io.github.gabrielshanahan.gazer.api.service.resource

import io.github.gabrielshanahan.gazer.api.controller.MonitoredEndpointController
import io.github.gabrielshanahan.gazer.api.controller.RootController
import io.github.gabrielshanahan.gazer.api.controller.hyperlinks
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

/** Space-saver */
internal typealias MonitoredEndpointModel = EntityModel<MonitoredEndpoint>

/** Space-saver */
internal typealias MonitoredEndpointCollectionModel = CollectionModel<MonitoredEndpointModel>

/**
 * Responsible for constructing the resource for MonitoredEndpoint endpoints. A resource is understood to be the data
 * computed by an endpoint enriched by links to relevant related endpoints.
 *
 * @see io.github.gabrielshanahan.gazer.api.controller.MonitoredEndpointController
 * @see io.github.gabrielshanahan.gazer.api.controller.response.MonitoredEndpointResponseAssembler
 */
@Component
class MonitoredEndpointResourceAssembler :
    RepresentationModelAssembler<MonitoredEndpoint, MonitoredEndpointModel> {

    /**
     * Adds links in situations where the data being returned represents a single MonitoredEndpoint
     */
    override fun toModel(endpoint: MonitoredEndpoint): MonitoredEndpointModel =
        EntityModel.of(endpoint).apply {
            hyperlinks<MonitoredEndpointController> {
                add(
                    selfLink {
                        getById(endpoint.id.toString())
                    } andAfford {
                        replaceEndpoint("", endpoint.id.toString(), MonitoredEndpoint())
                    } andAfford {
                        deleteEndpoint("", endpoint.id.toString())
                    },
                    link {
                        "monitoringResults" to getRelatedResults(endpoint.id.toString())
                    },
                    link {
                        "monitoredEndpoints" to getAll()
                    }
                )
            }
        }

    /**
     * An overload for data represented as an entity.
     */
    fun toModel(endpointEntity: MonitoredEndpointEntity): EntityModel<MonitoredEndpoint> =
        endpointEntity into MonitoredEndpointEntity::asModel into ::toModel

    /**
     * Adds links in situations where the data being returned represents a collection of MonitoredEndpoints
     */
    override fun toCollectionModel(endpoints: MutableIterable<MonitoredEndpoint>): MonitoredEndpointCollectionModel =
        CollectionModel.of(endpoints.map(::toModel)).apply {
            hyperlinks<MonitoredEndpointController> {
                add(selfLink {
                    getAll()
                } andAfford {
                    createEndpoint(MonitoredEndpoint())
                })
            }

            hyperlinks<RootController> {
                add(link {
                    "root" to root()
                })
            }
        }
}
