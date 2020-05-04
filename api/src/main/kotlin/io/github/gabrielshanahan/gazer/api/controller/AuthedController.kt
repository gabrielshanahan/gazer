package io.github.gabrielshanahan.gazer.api.controller

import io.github.gabrielshanahan.gazer.data.entity.UserEntity

/**
 * Parent for controllers, defines the [user] which is set by
 * [io.github.gabrielshanahan.gazer.api.security.TokenAdvice].
 */
abstract class AuthedController {
    lateinit var user: UserEntity
}
