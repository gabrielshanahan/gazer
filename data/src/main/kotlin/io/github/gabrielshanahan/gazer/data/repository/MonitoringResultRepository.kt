package io.github.gabrielshanahan.gazer.data.repository

import io.github.gabrielshanahan.gazer.data.model.MonitoringResult
import java.util.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MonitoringResultRepository : CrudRepository<MonitoringResult, UUID>
