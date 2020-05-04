package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.api.model.MonitoringResult
import io.github.gabrielshanahan.gazer.api.service.resource.MonitoringResultResourceAssembler
import io.github.gabrielshanahan.gazer.api.service.response.MonitoringResultResponseAssembler
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.stereotype.Service

@Service
class MonitoringResultResponseService(
    val resourceAssembler: MonitoringResultResourceAssembler,
    val responseAssembler: MonitoringResultResponseAssembler
) {
    fun buildOk(endpoints: List<MonitoringResult>) =
        endpoints.toMutableList() into resourceAssembler::toCollectionModel into responseAssembler::toOkResponse

    fun buildOk(endpoints: MonitoringResult) =
        endpoints into resourceAssembler::toModel into responseAssembler::toOkResponse
}
