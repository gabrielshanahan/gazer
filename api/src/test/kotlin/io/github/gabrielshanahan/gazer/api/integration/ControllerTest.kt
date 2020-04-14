package io.github.gabrielshanahan.gazer.api.integration

import com.ninjasquad.springmockk.MockkBean
import io.github.gabrielshanahan.gazer.api.repository.MonitoredEndpointRepository
import io.github.gabrielshanahan.gazer.api.repository.UserRepository
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class ControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var userRepo: UserRepository

    @MockkBean
    private lateinit var endpointRepo: MonitoredEndpointRepository

    @BeforeEach
    fun setupMocks() {
        every { endpointRepo.getAllByUser(SharedData.mockUser) } returns SharedData.mock.endpoints
        every {
            endpointRepo.getByUserAndId(SharedData.mockUser, SharedData.mockEndpoint.id)
        } returns SharedData.mockEndpoint

        every { userRepo.getByToken(SharedData.validMockToken) } returns SharedData.mockUser
        every { userRepo.getByToken(SharedData.invalidMockToken) } returns null
    }

    @Test
    fun `List monitored endpoints works`() {

        mockMvc.perform(
            get("/monitoredEndpoints")
                .accept(MediaType.APPLICATION_JSON)
                .header("GazerToken", SharedData.validMockToken)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].user.username").value(SharedData.mock.user.username))
            .andExpect(jsonPath("\$.[0].url").value(SharedData.mockEndpoint.url))
    }

    @Test
    fun `Monitored endpoint by id works when token matches`() {

        mockMvc.perform(
            get("/monitoredEndpoints/${SharedData.mockEndpoint.id}")
                .accept(MediaType.APPLICATION_JSON)
                .header("GazerToken", SharedData.validMockToken)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.user.username").value(SharedData.mock.user.username))
            .andExpect(jsonPath("\$.url").value(SharedData.mockEndpoint.url))
    }

    @Test
    fun `Missing token returns 400`() {

        mockMvc.perform(
            get("/monitoredEndpoints")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `Invalid token returns 401`() {

        mockMvc.perform(
            get("/monitoredEndpoints")
                .accept(MediaType.APPLICATION_JSON)
                .header("GazerToken", SharedData.invalidMockToken)
        ).andExpect(status().isUnauthorized)
    }
}
