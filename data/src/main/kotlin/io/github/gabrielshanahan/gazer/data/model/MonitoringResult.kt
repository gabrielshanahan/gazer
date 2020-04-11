package io.github.gabrielshanahan.gazer.data.model

import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
@Table(name="monitoring_result")
class MonitoringResult(
    id: UUID? = null,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    val checked: Date,

    @Column(name="http_status")
    val httpStatus: Int,

    @Lob
    val payload: String,

    @ManyToOne(optional = false)
    @JoinColumn
    val endpoint: MonitoredEndpoint
) : AbstractEntity(id)
