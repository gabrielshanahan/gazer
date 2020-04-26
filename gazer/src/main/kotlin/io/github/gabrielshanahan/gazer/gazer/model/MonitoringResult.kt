package io.github.gabrielshanahan.gazer.gazer.model

import io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity
import java.util.*

/** Adapter for [io.github.gabrielshanahan.gazer.data.entity.MonitoringResultEntity]. */
data class MonitoringResult(
    val id: UUID? = null,
    val checked: Date,
    val httpStatus: Int,
    val payload: String,
    val monitoredEndpoint: MonitoredEndpoint
) : AbstractModel<MonitoringResultEntity>() {

    override fun asEntity(): MonitoringResultEntity = MonitoringResultEntity(
        id = id,
        checked = checked,
        httpStatus = httpStatus,
        payload = payload,
        monitoredEndpoint = monitoredEndpoint.asEntity()
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
