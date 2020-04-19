package io.github.gabrielshanahan.gazer.api.integration.controller

import io.github.gabrielshanahan.gazer.data.DataSamples
import io.github.gabrielshanahan.gazer.data.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.data.repository.MonitoringResultRepository
import io.github.gabrielshanahan.gazer.data.repository.UserRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes.HAL_FORMS_JSON
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HTTPGetTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userRepo: UserRepository,
    @Autowired private val endpointRepo: MonitoredEndpointRepository,
    @Autowired private val resultRepo: MonitoringResultRepository
) {
    val sharedData = DataSamples(userRepo)

    @BeforeAll
    fun setup() {
        endpointRepo.saveAll(sharedData.applifting.endpoints)
        resultRepo.saveAll(sharedData.applifting.results)
    }

    @AfterAll
    fun teardown() {
        resultRepo.deleteAll(sharedData.applifting.results)
        endpointRepo.deleteAll(sharedData.applifting.endpoints)
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
            .andExpect(MockMvcResultMatchers.jsonPath("\$._embedded.monitoredEndpointList[0].user.username")
                .value(sharedData.applifting.user.username))
            .andExpect(MockMvcResultMatchers.jsonPath("\$._embedded.monitoredEndpointList[0].url")
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
            .andExpect(MockMvcResultMatchers.content().contentType(HAL_FORMS_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.user.username")
                .value(sharedData.applifting.user.username))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.url")
                .value(sharedData.applifting.endpoints.first().url))
    }

    @Test
    fun `List monitoring results works`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/monitoringResults")
                .accept(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.applifting.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .jsonPath("\$._embedded.monitoringResultList[0].monitoredEndpoint.id")
                .value(sharedData.applifting.endpoints.first().id.toString()))
            .andExpect(MockMvcResultMatchers
                .jsonPath("\$._embedded.monitoringResultList[0].monitoredEndpoint.user.username")
                .value(sharedData.applifting.user.username))
    }

    @Test
    fun `Monitoring result by id works when token matches`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/monitoringResults/${sharedData.applifting.results.first().id}")
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.applifting.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(HAL_FORMS_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.monitoredEndpoint.id")
                .value(sharedData.applifting.endpoints.first().id.toString()))
            .andExpect(MockMvcResultMatchers
                .jsonPath("\$.monitoredEndpoint.user.username")
                .value(sharedData.applifting.user.username))
    }

    @Test
    fun `Related monitoring results work`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get(
                "/monitoredEndpoints/${sharedData.applifting.endpoints.first().id}/monitoringResults"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.applifting.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(HAL_FORMS_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("\$._embedded.monitoringResultList[0].monitoredEndpoint.id")
                .value(sharedData.applifting.endpoints.first().id.toString()))
    }

    @Test
    fun `Related monitoring results work with limit`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get(
                "/monitoredEndpoints/${sharedData.applifting.endpoints.first().id}/monitoringResults?limit=1"
            )
                .contentType(MediaType.APPLICATION_JSON)
                .header("GazerToken", sharedData.applifting.user.token)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(HAL_FORMS_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("\$._embedded.monitoringResultList[1]")
                .doesNotExist())
    }
}
