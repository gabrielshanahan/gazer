package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.api.model.User
import io.github.gabrielshanahan.gazer.api.model.asModel
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class TokenAuthenticationServiceImpl(private val userRepository: UserRepository) : TokenAuthenticationService {
    override fun getUser(token: String): User? = userRepository.getByToken(token)?.asModel()
}
