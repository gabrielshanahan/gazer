package io.github.gabrielshanahan.gazer.data.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.PostLoad
import javax.persistence.PostPersist
import org.springframework.data.domain.Persistable

/**
 * Base class for all database entities. Implements functionality to generate binary UUIDs.
 *
 * Kudos to https://jivimberg.io/blog/2018/11/05/using-uuid-on-spring-data-jpa-entities/
 */
@MappedSuperclass
abstract class AbstractEntity(givenId: UUID? = null) : Persistable<UUID> {

    @Id
    @Column(name = "id", length = 16, unique = true, nullable = false)
    private val id: UUID = givenId ?: UUID.randomUUID()

    @Transient
    private var persisted: Boolean = givenId != null

    override fun getId(): UUID = id

    override fun isNew(): Boolean = !persisted

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            other !is AbstractEntity -> false
            else -> getId() == other.getId()
        }
    }

    @PostPersist
    @PostLoad
    private fun setPersisted() {
        persisted = true
    }
}
