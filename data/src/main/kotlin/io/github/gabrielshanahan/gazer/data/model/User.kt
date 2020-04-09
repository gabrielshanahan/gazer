package io.github.gabrielshanahan.gazer.data.model

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class User(
    id: UUID? = null,
    val username: String,
    val email: String,
    val token: String
) : AbstractEntity(id)
