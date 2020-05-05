package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.api.model.User

interface TokenAuthenticationService {
    fun getUser(token: String): User?
}
