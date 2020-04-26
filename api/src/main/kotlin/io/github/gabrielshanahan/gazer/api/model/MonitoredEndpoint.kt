package io.github.gabrielshanahan.gazer.api.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import io.github.gabrielshanahan.gazer.api.validation.NullOrNotBlank
import io.github.gabrielshanahan.gazer.api.validation.OnCreate
import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity
import java.util.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import org.hibernate.validator.constraints.URL

/** Adapter for [io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity]. */
data class MonitoredEndpoint(
    @field:JsonProperty(access = READ_ONLY)
    var id: UUID? = null,

    @field:NullOrNotBlank
    @field:NotNull(groups = [OnCreate::class])
    var name: String? = null,

    // Kudos https://stackoverflow.com/a/3809435 + added support for modern gTLD
    @field:URL(regexp = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}" +
        "\\.[a-z]{2,256}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
    @field:NotNull(groups = [OnCreate::class])
    var url: String? = null,

    @field:JsonProperty(access = READ_ONLY)
    var created: Date? = null,

    @field:JsonProperty(access = READ_ONLY)
    var lastCheck: Date? = null,

    // Is there a better way?
    @field:Min(10)
    @field:NotNull(groups = [OnCreate::class])
    var monitoredInterval: Int? = null,

    @field:JsonProperty(access = READ_ONLY)
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

/** Helper extension function for conversion from entity to model */
internal fun MonitoredEndpointEntity.asModel() = MonitoredEndpoint().apply { fromEntity(this@asModel) }
