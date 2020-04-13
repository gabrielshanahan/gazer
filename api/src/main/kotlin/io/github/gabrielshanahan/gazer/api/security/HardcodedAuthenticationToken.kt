package io.github.gabrielshanahan.gazer.api.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

data class HardcodedAuthenticationToken(
    val token: String,
    val userDetails: UserDetails = User("NOT_SET", token, emptyList())
) : AbstractAuthenticationToken(null) {
    override fun getCredentials(): String = token
    override fun getPrincipal(): UserDetails = userDetails
}
