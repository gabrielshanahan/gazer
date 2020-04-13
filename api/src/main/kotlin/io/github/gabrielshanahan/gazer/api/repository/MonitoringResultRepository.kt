package io.github.gabrielshanahan.gazer.api.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoringResult
import org.springframework.data.repository.CrudRepository
import java.util.*

interface MonitoringResultRepository: CrudRepository<MonitoringResult, UUID>
