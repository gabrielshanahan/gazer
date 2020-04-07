package io.github.gabrielshanahan.gazer.model

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class User(
    id: UUID? = null,
    val username: String,
    val email: String,
    @Column(name = "token ", length = 16, unique = true, nullable = false)
    val token: String
) : AbstractEntity(id)
