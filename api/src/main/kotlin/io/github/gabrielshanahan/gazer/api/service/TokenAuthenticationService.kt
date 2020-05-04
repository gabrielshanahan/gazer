package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.data.entity.UserEntity
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class TokenAuthenticationService(private val userRepository: UserRepository) {

    fun getUser(token: String): UserEntity? = userRepository.getByToken(token)
}
