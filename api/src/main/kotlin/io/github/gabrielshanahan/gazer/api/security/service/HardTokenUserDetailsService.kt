package io.github.gabrielshanahan.gazer.api.security.service

import io.github.gabrielshanahan.gazer.api.repository.UserRepository
import io.github.gabrielshanahan.gazer.api.security.userdetails.UserDetailsByTokenService
import java.util.*
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

class UserWithId(val id: UUID, username: String, password: String) : User(username, password, emptyList())

@Service
class HardTokenUserDetailsService(private val userRepository: UserRepository) : UserDetailsByTokenService {
    override fun loadUserByToken(token: String) = userRepository.getByToken(token)?.run {
        UserWithId(id, username, token)
    } ?: throw UsernameNotFoundException("User not found.")
}
