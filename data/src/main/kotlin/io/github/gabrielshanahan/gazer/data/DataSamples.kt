package io.github.gabrielshanahan.gazer.data

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.model.UserEntity
import io.github.gabrielshanahan.gazer.data.repository.UserRepository

class DataSamples(userRepo: UserRepository) {

    data class UserData(val user: UserEntity, val endpoints: List<MonitoredEndpointEntity>)

    val appliftingUser = userRepo.getByToken("93f39e2f-80de-4033-99ee-249d92736a25")!!
    val batmanUser = userRepo.getByToken("dcb20f8a-5657-4f1b-9f7f-ce65739b359e")!!

    val googleEndpoint = MonitoredEndpointEntity(
        name = "Google",
        url = "http://www.google.com",
        monitoredInterval = 10,
        user = appliftingUser
    )

    val yahooEndpoint = MonitoredEndpointEntity(
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
        const val invalidMockToken = "invalid_token"
    }
}
