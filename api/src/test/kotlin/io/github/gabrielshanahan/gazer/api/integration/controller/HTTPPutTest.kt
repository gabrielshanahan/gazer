package io.github.gabrielshanahan.gazer.api.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.asDTO
import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class HTTPPutTest(
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
    fun `Updating an existing monitored endpoint works`() {
        val endpoint = sharedData.applifting.endpoints.first()
        val payload = MonitoredEndpoint()
        payload.name = null
        payload.monitoredInterval = 100

        val json = jacksonObjectMapper.writeValueAsString(payload)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/monitoredEndpoints/${endpoint.id}")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.applifting.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(HAL_JSON_VALUE))
            .andExpect(
                MockMvcResultMatchers.jsonPath("\$.user.username").value(sharedData.applifting.user.username)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("\$.name").value(endpoint.name)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("\$.monitoredInterval").value(payload.monitoredInterval!!)
            )
    }

    @Test
    fun `Updating a non-existing monitored endpoint works`() {
        val endpoint = sharedData.batman.endpoints.first()

        val json = jacksonObjectMapper.writeValueAsString(endpoint)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/monitoredEndpoints/${endpoint.id}")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.batman.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(HAL_JSON_VALUE))
            .andExpect(
                MockMvcResultMatchers.jsonPath("\$.user.username").value(sharedData.batman.user.username)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("\$.name").value(endpoint.name)
            )
    }

    @Test
    fun `Updating an existing monitored endpoint by a different user doesn't work`() {
        val endpoint = sharedData.applifting.endpoints.first()

        val json = jacksonObjectMapper.writeValueAsString(endpoint)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/monitoredEndpoints/${endpoint.id}")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.batman.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `Creating invalid monitored endpoints doesn't work`() {
        val endpoint = sharedData.batman.endpoints.first().asDTO()
        endpoint.name = ""
        endpoint.monitoredInterval = null

        val json = jacksonObjectMapper.writeValueAsString(endpoint)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/monitoredEndpoints/${endpoint.id}")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.batman.user.token)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}
