package io.github.gabrielshanahan.gazer.data.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

/** Represents a MonitoredEndpoint */
@Entity
@Table(name = "monitored_endpoint")
class MonitoredEndpointEntity(
    id: UUID? = null,
    var name: String,
    var url: String,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    val created: Date? = null,

    @Column(name = "last_check")
    @Temporal(TemporalType.TIMESTAMP)
    var lastCheck: Date? = null,

    @OneToMany(
        mappedBy = "monitoredEndpoint",
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    val monitoringResults: List<MonitoringResultEntity> = emptyList(),

    @Column(name = "monitored_interval")
    var monitoredInterval: Int,

    @ManyToOne(optional = false)
    @JoinColumn
    val user: UserEntity
) : AbstractEntity(id)
