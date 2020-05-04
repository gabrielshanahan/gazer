package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.api.service.resource.MonitoredEndpointResourceAssembler
import io.github.gabrielshanahan.gazer.api.service.response.MonitoredEndpointResponseAssembler
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.func.into
import org.springframework.stereotype.Service

@Service
class MonitoredEndpointResponseService(
    val resourceAssembler: MonitoredEndpointResourceAssembler,
    val responseAssembler: MonitoredEndpointResponseAssembler
) {
    fun ok(endpoints: List<MonitoredEndpoint>) =
        endpoints.toMutableList() into resourceAssembler::toCollectionModel into responseAssembler::toOkResponse

    fun ok(endpoints: MonitoredEndpoint) =
        endpoints into resourceAssembler::toModel into responseAssembler::toOkResponse

    fun created(endpoints: MonitoredEndpoint) =
        endpoints into resourceAssembler::toModel into responseAssembler::toCreatedResponse

    fun updated(endpoints: MonitoredEndpoint) =
        endpoints into resourceAssembler::toModel into responseAssembler::toUpdatedResponse

    fun noContent() = responseAssembler.noContentResponse()
}
