package io.github.gabrielshanahan.gazer.api.integration.controller

import io.github.gabrielshanahan.gazer.data.DataSamples
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class HTTPErrorsTest(@Autowired private val mockMvc: MockMvc) {

    @Test
    fun `Missing token returns 400`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/monitoredEndpoints")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `Invalid token returns 401`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/monitoredEndpoints")
                .accept(MediaType.APPLICATION_JSON)
                .header("GazerToken", DataSamples.invalidMockToken)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }
}
