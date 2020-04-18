package io.github.gabrielshanahan.gazer.data

import io.github.gabrielshanahan.gazer.data.model.MonitoredEndpointEntity
import io.github.gabrielshanahan.gazer.data.model.MonitoringResultEntity
import io.github.gabrielshanahan.gazer.data.model.UserEntity
import io.github.gabrielshanahan.gazer.data.repository.UserRepository

class DataSamples(userRepo: UserRepository) {

    data class UserData(
        val user: UserEntity,
        val endpoints: List<MonitoredEndpointEntity>,
        val results: List<MonitoringResultEntity>
    )

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

    val googleResults = listOf(
        MonitoringResultEntity(
            httpStatus = 200,
            payload = "GooglePayload1",
            monitoredEndpoint = googleEndpoint
        ),
        MonitoringResultEntity(
            httpStatus = 200,
            payload = "GooglePayload2",
            monitoredEndpoint = googleEndpoint
        ),
        MonitoringResultEntity(
            httpStatus = 400,
            payload = "Bad request",
            monitoredEndpoint = googleEndpoint
        )
    )

    val yahooResults = listOf(
        MonitoringResultEntity(
            httpStatus = 500,
            payload = "Internal error",
            monitoredEndpoint = yahooEndpoint
        ),
        MonitoringResultEntity(
            httpStatus = 200,
            payload = "YahooPayload2",
            monitoredEndpoint = yahooEndpoint
        )
    )

    val applifting = UserData(
        user = appliftingUser,
        endpoints = listOf(googleEndpoint),
        results = googleResults
    )

    val batman = UserData(
        user = batmanUser,
        endpoints = listOf(yahooEndpoint),
        results = yahooResults
    )

    companion object {
        const val invalidMockToken = "invalid_token"
    }
}
