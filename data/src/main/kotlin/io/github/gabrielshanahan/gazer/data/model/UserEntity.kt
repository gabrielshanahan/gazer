package io.github.gabrielshanahan.gazer.data.model

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "user")
class UserEntity(
    id: UUID? = null,

    @Column(unique = true)
    val username: String,

    val email: String,

    @Column(unique = true)
    val token: String
) : AbstractEntity(id)
