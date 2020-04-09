package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, UUID>
