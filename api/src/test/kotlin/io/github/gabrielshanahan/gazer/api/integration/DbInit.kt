package io.github.gabrielshanahan.gazer.api.integration

import io.github.gabrielshanahan.gazer.api.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class DbInit {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `Users are correctly seeded`() {
        assertEquals(2, userRepository.findAll().toList().size)
    }
}
