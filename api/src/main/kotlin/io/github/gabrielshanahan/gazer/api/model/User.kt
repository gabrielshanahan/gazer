package io.github.gabrielshanahan.gazer.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.gabrielshanahan.gazer.data.model.UserEntity
import java.util.*

data class User(
    var id: UUID? = null,
    var username: String? = null,
    var email: String? = null,
    @field:JsonIgnore
    var token: String? = null
) : AbstractModel<UserEntity>() {

    override fun fromEntity(entity: UserEntity) {
        id = entity.id
        username = entity.username
        email = entity.email
        token = entity.token
    }

    override infix fun transferTo(entity: UserEntity): UserEntity = entity
}

fun UserEntity.asModel() = User().apply { fromEntity(this@asModel) }
