package io.github.gabrielshanahan.gazer.api.dto

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import java.util.*

data class MonitoredEndpointDTO(
    var id: UUID? = null,
    var name: String? = null,
    var url: String? = null,
    var created: Date? = null,
    var lastCheck: Date? = null,
    var monitoredInterval: Int? = null,
    var user: UserDTO? = null
) : AbstractDTO<MonitoredEndpoint>() {

    override fun fromEntity(entity: MonitoredEndpoint) {
        id = entity.id
        name = entity.name
        url = entity.url
        created = entity.created
        lastCheck = entity.lastCheck
        monitoredInterval = entity.monitoredInterval
        user = entity.user.asDTO()
    }

    override fun transferTo(entity: MonitoredEndpoint): MonitoredEndpoint {
        entity.name = name ?: entity.name
        entity.url = url ?: entity.url
        entity.monitoredInterval = monitoredInterval ?: entity.monitoredInterval

        return entity
    }

    override fun isValidEntity(): Boolean = null !in listOf(name, url, monitoredInterval)
}

fun MonitoredEndpoint.asDTO() = MonitoredEndpointDTO().apply { fromEntity(this@asDTO) }
