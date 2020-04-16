package io.github.gabrielshanahan.gazer.api.dto

import io.github.gabrielshanahan.gazer.data.model.User
import java.util.*

data class UserDTO(
    var id: UUID? = null,
    var username: String? = null,
    var email: String? = null,
    var token: String? = null
) : AbstractDTO<User>() {

    override fun fromEntity(entity: User) {
        id = entity.id
        username = entity.username
        email = entity.email
        token = entity.token
    }

    override infix fun transferTo(entity: User): User = entity
}

fun User.asDTO() = UserDTO().apply { fromEntity(this@asDTO) }
