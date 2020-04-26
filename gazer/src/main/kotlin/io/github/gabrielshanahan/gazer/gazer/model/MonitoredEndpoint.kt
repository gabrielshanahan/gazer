package io.github.gabrielshanahan.gazer.gazer.model

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.moroccode.compareUsingFields
import io.github.gabrielshanahan.moroccode.hash
import java.util.*

class MonitoredEndpoint(
    var id: UUID,
    var name: String,
    var url: String,
    var created: Date?,
    var lastCheck: Date?,
    var monitoredInterval: Int,
    var user: User
) : AbstractModel<MonitoredEndpointEntity>() {

    override fun hashCode() = hash(id, url, monitoredInterval)

    override fun equals(other: Any?) = compareUsingFields(other) {
        fields { id } and { url } and { monitoredInterval }
    }

    override fun asEntity(): MonitoredEndpointEntity = MonitoredEndpointEntity(
        id = id,
        name = name,
        url = url,
        created = created,
        lastCheck = lastCheck,
        monitoredInterval = monitoredInterval,
        user = user.asEntity()
    )
}

internal fun MonitoredEndpointEntity.asModel() = MonitoredEndpoint(
    id = id,
    name = name,
    url = url,
    created = created,
    lastCheck = lastCheck,
    monitoredInterval = monitoredInterval,
    user = user.asModel()
)
