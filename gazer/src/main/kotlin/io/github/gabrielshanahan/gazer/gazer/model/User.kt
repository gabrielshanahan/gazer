package io.github.gabrielshanahan.gazer.gazer.model

import io.github.gabrielshanahan.gazer.data.model.UserEntity
import java.util.*

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

internal fun UserEntity.asModel() = User(
    id = id,
    username = username,
    email = email,
    token = token
)
