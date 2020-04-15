package io.github.gabrielshanahan.gazer.data.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "monitoring_result")
class MonitoringResult(
    id: UUID? = null,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    var checked: Date,

    @Column(name = "http_status")
    var httpStatus: Int,

    @Lob
    var payload: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    var monitoredEndpoint: MonitoredEndpoint
) : AbstractEntity(id)
