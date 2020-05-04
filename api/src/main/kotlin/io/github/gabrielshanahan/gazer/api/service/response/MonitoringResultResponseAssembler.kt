package io.github.gabrielshanahan.gazer.api.service.response

import io.github.gabrielshanahan.gazer.api.service.resource.MonitoringResultCollectionModel
import io.github.gabrielshanahan.gazer.api.service.resource.MonitoringResultModel
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

/** Space-saver */
internal typealias MonitoringResultModelResponse = ResponseEntity<MonitoringResultModel>

/** Space-saver */
internal typealias MonitoringResultCollectionResponse = ResponseEntity<MonitoringResultCollectionModel>

/**
 * Responsible for constructing the response for MonitoringResult endpoints. A response contains a
 * [resource][io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultResourceAssembler] along with
 * optional headers, HTTP status code, etc.
 *
 * @see io.github.gabrielshanahan.gazer.api.controller.MonitoringResultController
 * @see io.github.gabrielshanahan.gazer.api.controller.resource.MonitoringResultResourceAssembler
 */
@Component
class MonitoringResultResponseAssembler {

    /**
     * Adds 200 HTTP status
     */
    fun toOkResponse(endpointModel: MonitoringResultModel): MonitoringResultModelResponse =
        endpointModel into {
            ResponseEntity.ok().body(it)
        }

    /**
     * Adds 200 HTTP status
     */
    fun toOkResponse(endpointModels: MonitoringResultCollectionModel): MonitoringResultCollectionResponse =
        endpointModels into {
            ResponseEntity.ok().body(it)
        }
}
