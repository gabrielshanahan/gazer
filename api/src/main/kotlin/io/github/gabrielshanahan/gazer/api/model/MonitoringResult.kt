package io.github.gabrielshanahan.gazer.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity
import java.util.*

/** Adapter for [io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity]. */
data class MonitoringResult(
    @field:JsonProperty(access = READ_ONLY)
    var id: UUID? = null,

    @field:JsonProperty(access = READ_ONLY)
    var checked: Date? = null,

    @field:JsonProperty(access = READ_ONLY)
    var httpStatus: Int? = null,

    @field:JsonProperty(access = READ_ONLY)
    var payload: String? = null,

    @field:JsonProperty(access = READ_ONLY)
    var monitoredEndpoint: MonitoredEndpoint? = null
) : AbstractModel<MonitoringResultEntity>() {

    override fun transferTo(entity: MonitoringResultEntity): MonitoringResultEntity = entity

    override fun asEntity(): MonitoringResultEntity = MonitoringResultEntity(
        id = id,
        checked = checked!!,
        httpStatus = httpStatus!!,
        payload = payload!!,
        monitoredEndpoint = monitoredEndpoint!!.asEntity()
    )
}

/** Helper extension function for conversion from entity to model */
internal fun MonitoringResultEntity.asModel() = MonitoringResult(
    id = id,
    checked = checked,
    httpStatus = httpStatus,
    payload = payload,
    monitoredEndpoint = monitoredEndpoint.asModel()
)
