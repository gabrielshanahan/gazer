package io.github.gabrielshanahan.gazer.api.controller.resource

import io.github.gabrielshanahan.gazer.api.controller.MonitoringResultController
import io.github.gabrielshanahan.gazer.api.controller.RootController
import io.github.gabrielshanahan.gazer.api.controller.hyperlinks
import io.github.gabrielshanahan.gazer.api.model.MonitoringResult
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

/** Space-saver */
internal typealias MonitoringResultModel = EntityModel<MonitoringResult>

/** Space-saver */
internal typealias MonitoringResultCollectionModel = CollectionModel<MonitoringResultModel>

/**
 * Responsible for constructing the resource for MonitoredEndpoint endpoints. A resource is understood to be the data
 * computed by an endpoint enriched by links to relevant related endpoints.
 *
 * @see io.github.gabrielshanahan.gazer.api.controller.MonitoringResultController
 * @see io.github.gabrielshanahan.gazer.api.controller.response.MonitoringResultResponseAssembler
 */
@Component
class MonitoringResultResourceAssembler :
    RepresentationModelAssembler<MonitoringResult, MonitoringResultModel> {

    /**
     * Adds links in situations where the data being returned represents a single MonitoredEndpoint
     */
    override fun toModel(result: MonitoringResult): MonitoringResultModel =
        EntityModel.of(result).apply {
            hyperlinks<MonitoringResultController> {
                add(
                    selfLink { getById("", result.id.toString()) },
                    link { "monitoredEndpoints" to getAll("") }
                )
            }
        }

    /**
     * An overload for data represented as an entity.
     */
    fun toModel(endpointEntity: MonitoringResultEntity): EntityModel<MonitoringResult> =
        endpointEntity into MonitoringResultEntity::asModel into ::toModel

    /**
     * Adds links in situations where the data being returned represents a collection of MonitoredEndpoints
     */
    override fun toCollectionModel(endpoints: MutableIterable<MonitoringResult>): MonitoringResultCollectionModel =
        CollectionModel.of(endpoints.map(::toModel)).apply {
            hyperlinks<MonitoringResultController> {
                add(selfLink { getAll("") })
            }

            hyperlinks<RootController> {
                add(link { "root" to root() })
            }
        }
}
