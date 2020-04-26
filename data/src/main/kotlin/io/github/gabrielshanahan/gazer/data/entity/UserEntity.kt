package io.github.gabrielshanahan.gazer.data.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/** Represents a User */
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
