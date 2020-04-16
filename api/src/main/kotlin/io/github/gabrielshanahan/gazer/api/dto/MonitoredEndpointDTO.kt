package io.github.gabrielshanahan.gazer.api.dto

import io.github.gabrielshanahan.gazer.api.validation.NullOrNotBlank
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import org.hibernate.validator.constraints.URL

data class MonitoredEndpointDTO(
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
}

fun MonitoredEndpoint.asDTO() = MonitoredEndpointDTO().apply { fromEntity(this@asDTO) }
