package io.github.gabrielshanahan.gazer.api.model

import io.github.gabrielshanahan.gazer.api.validation.NullOrNotBlank
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import org.hibernate.validator.constraints.URL

data class MonitoredEndpoint(
    var id: UUID? = null,

    @get:NullOrNotBlank
    @get:NotBlank
    @get:NotNull(groups = [OnCreate::class])
    var name: String? = null,

    @get:URL
    @get:NotNull(groups = [OnCreate::class])
    var url: String? = null,

    var created: Date? = null,
    var lastCheck: Date? = null,

    // Is there a better way?
    @get:Min(10)
    @get:NotNull(groups = [OnCreate::class])
    var monitoredInterval: Int? = null,

    var user: User? = null
) : AbstractModel<MonitoredEndpointEntity>() {

    override fun fromEntity(entity: MonitoredEndpointEntity) {
        id = entity.id
        name = entity.name
        url = entity.url
        created = entity.created
        lastCheck = entity.lastCheck
        monitoredInterval = entity.monitoredInterval
        user = entity.user.asModel()
    }

    override fun transferTo(entity: MonitoredEndpointEntity): MonitoredEndpointEntity {
        entity.name = name ?: entity.name
        entity.url = url ?: entity.url
        entity.monitoredInterval = monitoredInterval ?: entity.monitoredInterval

        return entity
    }
}

fun MonitoredEndpointEntity.asModel() = MonitoredEndpoint().apply { fromEntity(this@asModel) }
