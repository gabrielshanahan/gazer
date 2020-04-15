package io.github.gabrielshanahan.gazer.data

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    properties = ["spring.jpa.hibernate.ddl-auto=create-drop"]
)
class DataTest {

    @Test
    fun contextLoads() {
    }
}
