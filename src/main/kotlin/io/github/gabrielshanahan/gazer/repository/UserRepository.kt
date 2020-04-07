package io.github.gabrielshanahan.gazer.repository

import io.github.gabrielshanahan.gazer.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User?, Int?>
