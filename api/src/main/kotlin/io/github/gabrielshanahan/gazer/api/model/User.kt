package io.github.gabrielshanahan.gazer.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.gabrielshanahan.gazer.data.entity.UserEntity
import java.util.*

/** Adapter for [io.github.gabrielshanahan.gazer.data.entity.UserEntity]. */
data class User(
    var id: UUID? = null,
    var username: String? = null,
    var email: String? = null,
    @field:JsonIgnore
    var token: String? = null
) : AbstractModel<UserEntity>() {

    override infix fun transferTo(entity: UserEntity): UserEntity = entity

    override fun asEntity(): UserEntity = UserEntity(
        id = id,
        username = username!!,
        email = email!!,
        token = token!!
    )
}

/** Helper extension function for conversion from entity to model */
internal fun UserEntity.asModel() = User(
    id = id,
    username = username,
    email = email,
    token = token
)
