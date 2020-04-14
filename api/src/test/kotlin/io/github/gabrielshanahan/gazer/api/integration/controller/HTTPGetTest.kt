package io.github.gabrielshanahan.gazer.api.integration.controller

import io.github.gabrielshanahan.gazer.api.integration.SharedData
import io.github.gabrielshanahan.gazer.api.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.api.repository.UserRepository
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
class HTTPGetTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val monitoredEndpointRepo: MonitoredEndpointRepository
) {
    val sharedData = SharedData(userRepo)

    @BeforeEach
    fun setup() {
        monitoredEndpointRepo.saveAll(sharedData.applifting.endpoints)
    }

    @AfterEach
    fun teardown() {
        monitoredEndpointRepo.deleteAll(sharedData.applifting.endpoints)
    }

    @Test
    fun `List monitored endpoints works`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/monitoredEndpoints")
                .accept(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.applifting.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.[0].user.username")
                .value(sharedData.applifting.user.username))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.[0].url")
                .value(sharedData.applifting.endpoints.first().url))
    }

    @Test
    fun `Monitored endpoint by id works when token matches`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/monitoredEndpoints/${sharedData.applifting.endpoints.first().id}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.applifting.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.user.username")
                .value(sharedData.applifting.user.username))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.url")
                .value(sharedData.applifting.endpoints.first().url))
    }
}
