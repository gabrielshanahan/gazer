package io.github.gabrielshanahan.gazer.api.repository

import io.github.gabrielshanahan.gazer.data.model.User
import java.util.*
import org.springframework.data.repository.CrudRepository

// @RepositoryRestResource(exported = false)
interface UserRepository : CrudRepository<User, UUID> {
    fun getByToken(token: String): User?
}
