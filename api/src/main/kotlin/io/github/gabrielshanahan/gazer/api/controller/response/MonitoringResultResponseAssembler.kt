package io.github.gabrielshanahan.gazer.api.controller.response

import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultCollectionModel
import io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultModel
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

internal typealias MonitoringResultModelResponse = ResponseEntity<MonitoringResultModel>
internal typealias MonitoringResultCollectionResponse = ResponseEntity<MonitoringResultCollectionModel>

@Component
class MonitoringResultResponseAssembler {
    fun toOkResponse(endpointModel: MonitoringResultModel): MonitoringResultModelResponse =
        endpointModel into {
            ResponseEntity.ok().body(it)
        }

    fun toOkResponse(endpointModels: MonitoringResultCollectionModel): MonitoringResultCollectionResponse =
        endpointModels into {
            ResponseEntity.ok().body(it)
        }
}
