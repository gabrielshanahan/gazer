package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.User
import java.util.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, UUID> {
    fun getByToken(token: String): User?
}
