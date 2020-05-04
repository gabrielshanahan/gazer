package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.api.model.MonitoredEndpoint
import io.github.gabrielshanahan.gazer.api.model.MonitoringResult
import io.github.gabrielshanahan.gazer.api.security.UserAuthentication

interface MonitoredEndpointService {
    fun UserAuthentication.findAll(): List<MonitoredEndpoint>

    fun UserAuthentication.findOwn(id: String): MonitoredEndpoint?

    fun UserAuthentication.findRelatedTo(id: String, limit: Int?): List<MonitoringResult>?

    fun UserAuthentication.create(endpoint: MonitoredEndpoint): MonitoredEndpoint

    fun UserAuthentication.updateIfFound(id: String, endpoint: MonitoredEndpoint): MonitoredEndpoint?

    fun validateForCreation(endpoint: MonitoredEndpoint)

    fun UserAuthentication.delete(id: String): Unit?
}
