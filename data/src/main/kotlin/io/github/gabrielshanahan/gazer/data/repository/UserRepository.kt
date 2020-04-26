package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.entity.UserEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/** Used to validate user token */
@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun getByToken(token: String): UserEntity?
}
