package io.github.gabrielshanahan.gazer.api.security.userdetails

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

interface UserDetailsByTokenService {

    @Throws(UsernameNotFoundException::class)
    fun loadUserByToken(token: String): UserDetails
}
