package io.github.gabrielshanahan.gazer.gazer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TestConfiguration::class])
class GazerTest {

    @Test
    fun contextLoads() {
    }
}
