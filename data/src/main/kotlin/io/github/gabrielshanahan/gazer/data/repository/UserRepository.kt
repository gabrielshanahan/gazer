package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.User
import java.util.*
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, UUID>
