package io.github.gabrielshanahan.gazer.api.integration

import io.github.gabrielshanahan.gazer.api.repository.UserRepository
import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.data.model.User
import java.util.*

class SharedData(userRepo: UserRepository) {

    data class UserData(val user: User, val endpoints: List<MonitoredEndpoint>)

    val appliftingUser = userRepo.getByToken("93f39e2f-80de-4033-99ee-249d92736a25")!!
    val batmanUser = userRepo.getByToken("dcb20f8a-5657-4f1b-9f7f-ce65739b359e")!!

    val googleEndpoint = MonitoredEndpoint(
        name = "Google",
        url = "http://www.google.com",
        monitoredInterval = 10,
        user = appliftingUser
    )

    val yahooEndpoint = MonitoredEndpoint(
        name = "Yahoo",
        url = "http://www.yahoo.com",
        monitoredInterval = 20,
        user = batmanUser
    )

    val applifting = UserData(
        user = appliftingUser,
        endpoints = listOf(googleEndpoint)
    )

    val batman = UserData(
        user = batmanUser,
        endpoints = listOf(yahooEndpoint)
    )

    companion object {
        val mockToken = "123-abc"
        val mockUser = User(UUID.randomUUID(), "abc", "abc@abc.com", mockToken)

        val mockEndpoint = MonitoredEndpoint(
            name = "MockMock",
            url = "http://www.mock.com",
            monitoredInterval = 15,
            user = mockUser
        )

        val mock = UserData(
            user = mockUser,
            endpoints = listOf(mockEndpoint)
        )
    }
}
