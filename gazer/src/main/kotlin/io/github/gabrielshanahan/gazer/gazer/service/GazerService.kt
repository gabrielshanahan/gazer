package io.github.gabrielshanahan.gazer.gazer.service

import io.github.gabrielshanahan.gazer.gazer.model.MonitoredEndpoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.SendChannel

interface GazerService {
    fun launchGazer(endpoint: MonitoredEndpoint, persistor: SendChannel<PersistMsg>): Job
}
