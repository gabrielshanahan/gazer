package io.github.gabrielshanahan.gazer.api.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoringResult
import java.util.*
import org.springframework.data.repository.CrudRepository

interface MonitoringResultRepository : CrudRepository<MonitoringResult, UUID>
