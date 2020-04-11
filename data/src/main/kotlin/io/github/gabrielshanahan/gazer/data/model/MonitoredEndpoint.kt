package io.github.gabrielshanahan.gazer.data.model

import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
@Table(name="monitored_endpoint")
class MonitoredEndpoint(
    id: UUID? = null,
    val name: String,
    val url: String,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    val created: Date,

    @Column(name="last_check")
    @Temporal(TemporalType.TIMESTAMP)
    val lastCheck: Date,

    @Column(name="monitored_interval")
    val monitoredInterval: Int,

    @ManyToOne(optional = false)
    @JoinColumn
    val user: User
) : AbstractEntity(id)
