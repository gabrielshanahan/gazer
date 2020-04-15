package io.github.gabrielshanahan.gazer.api.integration.controller

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
class HTTPDeleteTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val monitoredEndpointRepo: MonitoredEndpointRepository
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
    fun `Deleting an existing monitored endpoint works`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/monitoredEndpoints/${sharedData.applifting.endpoints.first().id}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.applifting.user.token)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `Deleting a non-existing monitored endpoint doesn't work`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/monitoredEndpoints/${sharedData.batman.endpoints.first().id}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.batman.user.token)
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `Deleting an existing monitored endpoint by a different user doesn't work`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/monitoredEndpoints/${sharedData.applifting.endpoints.first().id}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.batman.user.token)
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
    }
}
