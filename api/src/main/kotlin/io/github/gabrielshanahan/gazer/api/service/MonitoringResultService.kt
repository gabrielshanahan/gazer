package io.github.gabrielshanahan.gazer.api.service

import io.github.gabrielshanahan.gazer.api.model.MonitoringResult
import io.github.gabrielshanahan.gazer.api.security.UserAuthentication
import io.github.gabrielshanahan.gazer.data.entity.MonitoredEndpointEntity

interface MonitoringResultService {

    fun UserAuthentication.findAll(): List<MonitoringResult>

    fun UserAuthentication.findOwn(id: String): MonitoringResult?

    fun findRelatedTo(endpoint: MonitoredEndpointEntity, limit: Int?): List<MonitoringResult>?
}
