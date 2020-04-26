package io.github.gabrielshanahan.gazer.gazer.model

import io.github.gabrielshanahan.gazer.data.entity.UserEntity
import java.util.*

/** Adapter for [io.github.gabrielshanahan.gazer.data.entity.UserEntity]. */
data class User(
    val id: UUID,
    val username: String,
    val email: String,
    val token: String
) : AbstractModel<UserEntity>() {

    override fun asEntity(): UserEntity = UserEntity(
        id = id,
        username = username,
        email = email,
        token = token
    )
}

/** Helper extension function for conversion from entity to model */
internal fun UserEntity.asModel() = User(
    id = id,
    username = username,
    email = email,
    token = token
)
