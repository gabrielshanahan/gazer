package io.github.gabrielshanahan.gazer.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import io.github.gabrielshanahan.gazer.data.model.MonitoringResultEntity
import java.util.*

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

    override fun fromEntity(entity: MonitoringResultEntity) {
        id = entity.id
        checked = entity.checked
        httpStatus = entity.httpStatus
        payload = entity.payload
        monitoredEndpoint = entity.monitoredEndpoint.asModel()
    }

    override fun transferTo(entity: MonitoringResultEntity): MonitoringResultEntity = entity
}

fun MonitoringResultEntity.asModel() = MonitoringResult().apply { fromEntity(this@asModel) }
