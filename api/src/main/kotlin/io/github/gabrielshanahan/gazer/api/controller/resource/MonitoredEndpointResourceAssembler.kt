package io.github.gabrielshanahan.gazer.api.controller.resource

import io.github.gabrielshanahan.gazer.api.controller.MonitoredEndpointController
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.add
import org.springframework.hateoas.server.mvc.andAffordances
import org.springframework.stereotype.Component

internal typealias MonitoredEndpointModel = EntityModel<MonitoredEndpoint>
internal typealias MonitoredEndpointCollectionModel = CollectionModel<MonitoredEndpointModel>

@Component
class MonitoredEndpointResourceAssembler : RepresentationModelAssembler<MonitoredEndpoint, MonitoredEndpointModel> {

    override fun toModel(endpoint: MonitoredEndpoint): MonitoredEndpointModel =
        EntityModel.of(endpoint) into {
            it.add(MonitoredEndpointController::class) {
                linkTo {
                    findById("", endpoint.id.toString())
                } withRel IanaLinkRelations.SELF andAffordances {
                    afford<MonitoredEndpointController> {
                        replaceEndpoint("", endpoint.id.toString(), MonitoredEndpoint())
                    }
                    afford<MonitoredEndpointController> {
                        deleteEndpoint("", endpoint.id.toString())
                    }
                }

                linkTo {
                    findAll("")
                } withRel "monitoredEndpoints"
            }
        }

    fun toModel(endpointEntity: MonitoredEndpointEntity): EntityModel<MonitoredEndpoint> =
        endpointEntity into MonitoredEndpointEntity::asModel into ::toModel

    override fun toCollectionModel(endpoints: MutableIterable<MonitoredEndpoint>): MonitoredEndpointCollectionModel =
        CollectionModel.of(endpoints.map(::toModel)) into {
            it.add(MonitoredEndpointController::class) {
                linkTo {
                    findAll("")
                } withRel IanaLinkRelations.SELF
            }
        }
}
