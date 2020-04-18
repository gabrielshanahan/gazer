package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.data.repository.UserRepository

abstract class AbstractController(val userRepository: UserRepository)
