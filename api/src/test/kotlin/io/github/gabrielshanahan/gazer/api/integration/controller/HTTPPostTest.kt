package io.github.gabrielshanahan.gazer.api.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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
class HTTPPostTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val monitoredEndpointRepo: MonitoredEndpointRepository,
    @Autowired val jacksonObjectMapper: ObjectMapper
) {
    val sharedData = DataSamples(userRepo)

    @BeforeEach
    fun setup() {
        monitoredEndpointRepo.saveAll(sharedData.applifting.endpoints)
    }

    @AfterEach
    fun teardown() {
        monitoredEndpointRepo.deleteAll(sharedData.applifting.endpoints)
    }

    @Test
    fun `Saving a monitored endpoint works`() {
        val json = jacksonObjectMapper.writeValueAsString(sharedData.batman.endpoints.first())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/monitoredEndpoints")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.batman.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.user.username").value(sharedData.batman.user.username))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").isNotEmpty)
    }

    @Test
    fun `Saving something else doesn't works`() {
        val json = jacksonObjectMapper.writeValueAsString(sharedData.batman.user)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/monitoredEndpoints")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.batman.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}
