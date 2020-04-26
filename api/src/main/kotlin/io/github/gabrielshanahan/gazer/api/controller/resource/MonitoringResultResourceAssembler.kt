package io.github.gabrielshanahan.gazer.api.controller.resource

import io.github.gabrielshanahan.gazer.api.controller.MonitoringResultController
import io.github.gabrielshanahan.gazer.api.controller.RootController
import io.github.gabrielshanahan.gazer.api.controller.hyperlinks
import io.github.gabrielshanahan.gazer.api.model.MonitoringResult
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.data.model.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.stereotype.Component

internal typealias MonitoringResultModel = EntityModel<MonitoringResult>
internal typealias MonitoringResultCollectionModel = CollectionModel<MonitoringResultModel>

@Component
class MonitoringResultResourceAssembler :
    RepresentationModelAssembler<MonitoringResult, MonitoringResultModel> {

    override fun toModel(result: MonitoringResult): MonitoringResultModel =
        EntityModel.of(result).apply {
            hyperlinks<MonitoringResultController> {
                add(
                    selfLink { getById("", result.id.toString()) },
                    link { "monitoredEndpoints" to getAll("") }
                )
            }
        }

    fun toModel(endpointEntity: MonitoringResultEntity): EntityModel<MonitoringResult> =
        endpointEntity into MonitoringResultEntity::asModel into ::toModel

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
