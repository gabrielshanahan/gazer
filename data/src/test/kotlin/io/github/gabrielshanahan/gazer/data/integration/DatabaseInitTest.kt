package io.github.gabrielshanahan.gazer.data.integration

import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class DatabaseInitTest(@Autowired var userRepository: UserRepository) {

    @Test
    fun `Users are correctly seeded`() {
        assertEquals(2, userRepository.findAll().toList().size)
    }
}
