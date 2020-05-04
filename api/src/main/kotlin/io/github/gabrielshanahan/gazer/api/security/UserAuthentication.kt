package io.github.gabrielshanahan.gazer.api.security

import io.github.gabrielshanahan.gazer.data.entity.UserEntity

/**
 * Defines the [user] which is set by [io.github.gabrielshanahan.gazer.api.security.AuthenticateByGazerToken].
 */
interface UserAuthentication {
    var user: UserEntity
}
