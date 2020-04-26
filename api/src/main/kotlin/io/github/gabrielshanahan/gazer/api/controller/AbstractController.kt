package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.data.repository.UserRepository

/**
 * Parent for controllers, to allow a single place to define [withAuthedUser].
 */
abstract class AbstractController(val userRepository: UserRepository)
