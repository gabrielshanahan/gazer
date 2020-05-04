package io.github.gabrielshanahan.gazer.api.security

import io.github.gabrielshanahan.gazer.api.model.User

/**
 * Defines the [user] which is set by [io.github.gabrielshanahan.gazer.api.security.AuthenticateByGazerToken].
 */
interface UserAuthentication {
    var user: User
}
