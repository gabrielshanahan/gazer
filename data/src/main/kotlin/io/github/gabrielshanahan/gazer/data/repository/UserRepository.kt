package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.User
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun getByToken(token: String): User?
}
