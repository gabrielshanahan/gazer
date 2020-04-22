package io.github.gabrielshanahan.gazer.data.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
@Table(name = "monitoring_result")
class MonitoringResultEntity(
    id: UUID? = null,

    @Temporal(TemporalType.TIMESTAMP)
    var checked: Date,

    @Column(name = "http_status")
    var httpStatus: Int,

    @Lob
    var payload: String,

    @ManyToOne(optional = false)
    var monitoredEndpoint: MonitoredEndpointEntity
) : AbstractEntity(id)
